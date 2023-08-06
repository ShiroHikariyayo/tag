package com.shirohikari.tag.main;

import com.google.gson.Gson;
import com.shirohikari.tag.main.bean.FileBean;
import com.shirohikari.tag.main.bean.TagBean;
import com.shirohikari.tag.util.FileUtil;

import java.io.*;
import java.util.*;

/**
 * 对tag_table和file_table文件进行直接操作
 * @author ShiroHikari
 */
public class DataStorage {
    private static final int ADD = 0;
    private static final int UPDATE = 1;
    private static final int REMOVE = 2;
    private static final String TAG_TABLE = "tag_table";
    private static final String FILE_TABLE = "file_table";

    private int nextId;
    private long tagEndOffset;
    private long fileEndOffset;
    private final File dir;
    private final File tagTable;
    private final File fileTable;
    private HashSet<String> tags;
    private LinkedHashMap<Integer,Long> idOffsetMap;
    private HashMap<Integer,FileBean> idFileBeanMap;
    private HashMap<String,Integer> pathIdMap;
    private HashMap<String,FileBean> pathFileBeanMap;
    private HashMap<String, Long> tagOffsetMap;
    private HashMap<String, TagBean> tagTagBeanMap;
    private RandomAccessFile tagRAF;
    private RandomAccessFile fileRAF;
    private Gson gson;

    private DataStorage(File dir,File tagTable,File fileTable) throws IOException {
        this.dir = dir;
        this.tagTable = tagTable;
        this.fileTable = fileTable;
        init();
    }

    public static DataStorage create(String dirPath) throws IOException {
        File dir = new File(dirPath);
        File tabTable = new File(dirPath,TAG_TABLE);
        File fileTable = new File(dirPath,FILE_TABLE);
        if(!FileUtil.isEmptyDirectory(dir) && !tabTable.exists() && !fileTable.exists()){
            throw new IOException("文件夹不为空");
        }
        FileUtil.makeDirectory(dir);
        FileUtil.makeFile(tabTable);
        FileUtil.makeFile(fileTable);
        return new DataStorage(dir,tabTable,fileTable);
    }

    private void init() throws IOException {
        gson = new Gson();
        tagRAF = new RandomAccessFile(tagTable,"rw");
        fileRAF = new RandomAccessFile(fileTable,"rw");
        tags = new HashSet<>();
        idOffsetMap = new LinkedHashMap<>();
        pathIdMap = new HashMap<>();
        idFileBeanMap = new HashMap<>();
        pathFileBeanMap = new HashMap<>();
        tagOffsetMap = new HashMap<>();
        tagTagBeanMap = new HashMap<>();
        //读取file_table信息
        while (fileRAF.getFilePointer() < fileRAF.length()){
            long offset = fileRAF.getFilePointer();
            String json = fileRAF.readUTF();
            FileBean bean = gson.fromJson(json,FileBean.class);
            nextId = bean.getId() + 1;
            addToFileMaps(bean,offset);
        }
        System.gc();
        fileEndOffset = fileRAF.length();
        //读取tag_table信息
        while (tagRAF.getFilePointer() < tagRAF.length()){
            long offset = tagRAF.getFilePointer();
            String json = tagRAF.readUTF();
            TagBean bean = gson.fromJson(json, TagBean.class);
            tags.add(bean.getTag());
            tagOffsetMap.put(bean.getTag(),offset);
            tagTagBeanMap.put(bean.getTag(),bean);
        }
        System.gc();
        tagEndOffset = tagRAF.length();
    }

    public boolean hasFile(int id){
        return idFileBeanMap.containsKey(id);
    }

    public boolean hasFile(String path){
        return pathFileBeanMap.containsKey(path);
    }

    public boolean hasTag(String tag){
        return tags.contains(tag);
    }

    public Set<String> getAllTags(){
        return Collections.unmodifiableSet(tags);
    }

    public FileBean getFileBean(int id){
        return idFileBeanMap.get(id);
    }

    public FileBean getFileBean(String path) {
        return pathFileBeanMap.get(path);
    }

    public TagBean getTagBean(String tag){
        return tagTagBeanMap.get(tag);
    }

    public void addFileRecord(FileBean bean) throws IOException {
        checkFileBean(bean,ADD);
        bean.setId(nextId++);
        addToFileMaps(bean,fileEndOffset);
        String json = gson.toJson(bean);
        fileEndOffset += json.getBytes().length + 2;
        fileRAF.writeUTF(json);
    }

    public void updateFileRecord(FileBean bean) throws IOException {
        checkFileBean(bean,UPDATE);
        long offset = idOffsetMap.get(bean.getId());
        fileRAF.seek(offset);
        String oldJson = fileRAF.readUTF();
        idFileBeanMap.replace(bean.getId(),bean);
        //可能会修改path
        if(bean.getOldPaths() == null){
            pathFileBeanMap.replace(bean.getPath(),bean);
        }else {
            for(String oldPath:bean.getOldPaths()){
                pathIdMap.remove(oldPath);
                pathFileBeanMap.remove(oldPath);
            }
            pathIdMap.put(bean.getPath(),bean.getId());
            pathFileBeanMap.put(bean.getPath(),bean);
        }
        String newJson = gson.toJson(bean);
        updateOffset(oldJson,newJson,offset,true);
        insertOrRemoveFileRecord(oldJson,newJson,offset);
    }

    public void removeFileRecord(FileBean bean) throws IOException {
        checkFileBean(bean,REMOVE);
        long offset = idOffsetMap.get(bean.getId());
        fileRAF.seek(offset);
        String oldJson = fileRAF.readUTF();
        idOffsetMap.remove(bean.getId());
        idFileBeanMap.remove(bean.getId());
        pathIdMap.remove(bean.getPath());
        pathFileBeanMap.remove(bean.getPath());
        updateOffset(oldJson,null,offset,true);
        insertOrRemoveFileRecord(oldJson,null,offset);
    }

    public void addTagRecord(TagBean bean) throws IOException {
        checkTagBean(bean,ADD);
        tags.add(bean.getTag());
        tagOffsetMap.put(bean.getTag(),tagEndOffset);
        tagTagBeanMap.put(bean.getTag(),bean);
        String json = gson.toJson(bean);
        tagEndOffset += json.getBytes().length + 2;
        tagRAF.writeUTF(json);
    }

    public void updateTagRecord(TagBean bean) throws IOException {
        checkTagBean(bean,UPDATE);
        long offset = tagOffsetMap.get(bean.getTag());
        tagRAF.seek(offset);
        String oldJson = tagRAF.readUTF();
        tagTagBeanMap.replace(bean.getTag(),bean);
        String newJson = gson.toJson(bean);
        updateOffset(oldJson,newJson,offset,false);
        insertOrRemoveTagRecord(oldJson,newJson,offset);
    }

    public void removeTagRecord(TagBean bean) throws IOException {
        checkTagBean(bean,REMOVE);
        long offset = tagOffsetMap.get(bean.getTag());
        tagRAF.seek(offset);
        String oldJson = tagRAF.readUTF();
        tags.remove(bean.getTag());
        tagOffsetMap.remove(bean.getTag());
        tagTagBeanMap.remove(bean.getTag());
        updateOffset(oldJson,null,offset,false);
        insertOrRemoveTagRecord(oldJson,null,offset);
    }

    private void updateOffset(String oldJson,String newJson,long offset,boolean file) throws IOException {
        long len;
        if(newJson != null){
            len = newJson.getBytes().length - oldJson.getBytes().length;
        }else {
            len = -oldJson.getBytes().length - 2;
        }
        Iterator iter;
        if(file){
            iter = idOffsetMap.entrySet().iterator();
        }else{
            iter = tagOffsetMap.entrySet().iterator();
        }
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            if((long)entry.getValue() <= offset){
                continue;
            }
            entry.setValue((long)entry.getValue() + len);
        }
    }

    /**
     * 更新或删除需要改变的FileBean记录
     * @param oldJson 需要进行更改的记录
     * @param newJson 新的记录，如果为null则删除记录
     * @param offset 旧记录在file_table中的偏移量
     * @throws IOException
     */
    private void insertOrRemoveFileRecord(String oldJson,String newJson,long offset) throws IOException {
        File tmp=File.createTempFile("file_tmp", null,dir);
        tmp.deleteOnExit();
        FileUtil.saveAfterToTemp(fileRAF,fileRAF.getFilePointer(),tmp);
        fileRAF.seek(offset);
        if(newJson != null){
            fileRAF.writeUTF(newJson);
            fileEndOffset = fileEndOffset - oldJson.getBytes().length + newJson.getBytes().length;
        }else {
            fileEndOffset = fileEndOffset - oldJson.getBytes().length - 2;
        }
        FileUtil.readAndCover(fileRAF,fileRAF.getFilePointer(),tmp);
        fileRAF.setLength(fileEndOffset);
        tmp.delete();
    }

    private void insertOrRemoveTagRecord(String oldJson,String newJson,long offset) throws IOException {
        File tmp=File.createTempFile("tag_tmp", null,dir);
        tmp.deleteOnExit();
        FileUtil.saveAfterToTemp(tagRAF,tagRAF.getFilePointer(),tmp);
        tagRAF.seek(offset);
        if(newJson != null){
            tagRAF.writeUTF(newJson);
            tagEndOffset = tagEndOffset - oldJson.getBytes().length + newJson.getBytes().length;
        }else {
            tagEndOffset = tagEndOffset - oldJson.getBytes().length - 2;
        }
        FileUtil.readAndCover(tagRAF,tagRAF.getFilePointer(),tmp);
        tagRAF.setLength(tagEndOffset);
        tmp.delete();
    }

    private void checkFileBean(FileBean bean,int operate) throws IOException {
        if(bean == null){
            throw new IOException("FileBean为null");
        }else if(bean.getPath() == null){
            throw new IOException("文件路径不应为null");
        }
        if(operate == ADD){
            if(bean.getTagSet() == null || bean.getTagSet().isEmpty()){
                throw new IOException("FileBean为必须至少含有一个tag");
            }else if(bean.getId() != null){
                throw new IOException("插入时不允许手动设置FileBean的id");
            }
            return;
        }
        if(bean.getId() == null){
            throw new IOException("需要指定FileBean及其id");
        }else if(!idFileBeanMap.containsKey(bean.getId())){
            throw new IOException("未发现相应记录");
        }
        //找不到id时则更改了path
        Integer id = pathIdMap.get(bean.getPath());
        if(operate == UPDATE){
            if(id != null && !bean.getId().equals(id)){
                throw new IOException("禁止修改id");
            }else if(!bean.getId().equals(id)){
                throw new IOException("禁止修改id和path");
            }
        }else if(operate == REMOVE){
            if(!bean.getId().equals(id)){
                throw new IOException("禁止修改id或path");
            }
        }
    }

    private void checkTagBean(TagBean bean,int operate) throws IOException {
        if(bean == null){
            throw new IOException("TagBean为null");
        }
        if(operate == ADD){
            if(tagTagBeanMap.containsKey(bean.getTag())){
                throw new IOException("不可添加已有的tag");
            }
            if(!bean.getIdSet().isEmpty()){
                throw new IOException("创建新标签时不可手动指定idList");
            }
        }else{
            if(!tagTagBeanMap.containsKey(bean.getTag())){
                throw new IOException("未发现指定tag");
            }
        }
    }

    private void addToFileMaps(FileBean bean,long offset){
        idOffsetMap.put(bean.getId(),offset);
        pathIdMap.put(bean.getPath(),bean.getId());
        idFileBeanMap.put(bean.getId(),bean);
        pathFileBeanMap.put(bean.getPath(),bean);
    }
}
