package com.shirohikari.tag.main;

import com.google.gson.Gson;
import com.shirohikari.tag.main.bean.FileBean;
import com.shirohikari.tag.main.bean.TagBean;
import com.shirohikari.tag.util.FileUtil;

import java.io.*;
import java.util.*;

/**
 * @author 20637
 */
public class DataStorage {
    private static final String TAG_TABLE = "tag_table";
    private static final String FILE_TABLE = "file_table";

    private int nextId;
    private long tagEndOffset;
    private long fileEndOffset;
    private File dir;
    private File tagTable;
    private File fileTable;
    private LinkedHashMap<Integer,Long> idOffsetMap;
    private HashMap<Integer,FileBean> idFileBeanMap;
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
        idOffsetMap = new LinkedHashMap<>();
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
            tagOffsetMap.put(bean.getTag(),offset);
            tagTagBeanMap.put(bean.getTag(),bean);
        }
        System.gc();
        tagEndOffset = tagRAF.length();
    }

    public boolean hasTag(int id){
        return idFileBeanMap.containsKey(id);
    }

    public boolean hasTag(String path){
        return pathFileBeanMap.containsKey(path);
    }

    public FileBean getFileBean(int id){
        return idFileBeanMap.get(id);
    }

    public FileBean getFileBean(String path) throws IOException {
        return pathFileBeanMap.get(path);
    }

    public void addFileRecord(FileBean bean) throws IOException {
        checkFileBean(bean,true);
        bean.setId(nextId++);
        addToFileMaps(bean,fileEndOffset);
        String json = gson.toJson(bean);
        fileEndOffset += json.getBytes().length + 2;
        fileRAF.writeUTF(json);
    }

    public void updateFileRecord(FileBean bean) throws IOException {
        checkFileBean(bean,false);
        long offset = idOffsetMap.get(bean.getId());
        fileRAF.seek(offset);
        String oldJson = fileRAF.readUTF();
        idFileBeanMap.replace(bean.getId(),bean);
        pathFileBeanMap.replace(bean.getPath(),bean);
        String newJson = gson.toJson(bean);
        updateOffset(oldJson,newJson,offset,true);
        insertOrRemoveFileRecord(oldJson,newJson,offset);
    }

    public void removeFileRecord(FileBean bean) throws IOException {
        checkFileBean(bean,false);
        long offset = idOffsetMap.get(bean.getId());
        fileRAF.seek(offset);
        String oldJson = fileRAF.readUTF();
        idOffsetMap.remove(bean.getId());
        idFileBeanMap.remove(bean.getId());
        pathFileBeanMap.remove(bean.getPath());
        updateOffset(oldJson,null,offset,true);
        insertOrRemoveFileRecord(oldJson,null,offset);
    }

    public void addTagRecord(TagBean bean) throws IOException {
        checkTagBean(bean,true);
        tagOffsetMap.put(bean.getTag(),tagEndOffset);
        tagTagBeanMap.put(bean.getTag(),bean);
        String json = gson.toJson(bean);
        tagEndOffset += json.getBytes().length + 2;
        tagRAF.writeUTF(json);
    }

    public void updateTagRecord(TagBean bean) throws IOException {
        checkTagBean(bean,false);
        long offset = tagOffsetMap.get(bean.getTag());
        tagRAF.seek(offset);
        String oldJson = tagRAF.readUTF();
        tagTagBeanMap.replace(bean.getTag(),bean);
        String newJson = gson.toJson(bean);
        updateOffset(oldJson,newJson,offset,false);
        insertOrRemoveTagRecord(oldJson,newJson,offset);
    }

    public void removeTagRecord(TagBean bean) throws IOException {
        checkTagBean(bean,false);
        checkTagBean(bean,false);
        long offset = tagOffsetMap.get(bean.getTag());
        tagRAF.seek(offset);
        String oldJson = tagRAF.readUTF();
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

    private void checkFileBean(FileBean bean,boolean add) throws IOException {
        if(bean == null){
            throw new IOException("FileBean为null");
        }
        if(bean.getTagList() == null || bean.getTagList().isEmpty()){
            throw new IOException("FileBean为必须至少含有一个tag");
        }
        if(add){
            if(bean.getId() != null){
                throw new IOException("插入时不允许手动设置FileBean的id");
            }
        }else {
            if(bean.getId() == null){
                throw new IOException("需要指定FileBean及其id");
            }else if(!idFileBeanMap.containsKey(bean.getId())){
                throw new IOException("未发现相应记录");
            }
        }
    }

    private void checkTagBean(TagBean bean,boolean add) throws IOException {
        if(bean == null){
            throw new IOException("TagBean为null");
        }
        if(add){
            if(tagTagBeanMap.containsKey(bean.getTag())){
                throw new IOException("不可添加已有的tag");
            }
            if(!bean.getIdList().isEmpty()){
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
        idFileBeanMap.put(bean.getId(),bean);
        pathFileBeanMap.put(bean.getPath(),bean);
    }
}
