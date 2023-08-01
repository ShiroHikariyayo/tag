package com.shirohikari.tag.main;

import com.google.gson.Gson;
import com.shirohikari.tag.main.bean.FileBean;
import com.shirohikari.tag.util.FileUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 20637
 */
public class DataStorage {
    private static final String TAG_TABLE = "tag_table";
    private static final String FILE_TABLE = "file_table";

    private int nextId;
    private long fileEndOffset;
    private File dir;
    private File tabTable;
    private File fileTable;
    private LinkedHashMap<Integer,Long> idOffsetMap;
    private HashMap<Integer,FileBean> idFileBeanMap;
    private HashMap<String,FileBean> pathFileBeanMap;
    private RandomAccessFile tabRAF;
    private RandomAccessFile fileRAF;
    private Gson gson;

    private DataStorage(File dir,File tabTable,File fileTable) throws IOException {
        this.dir = dir;
        this.tabTable = tabTable;
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
        tabRAF = new RandomAccessFile(tabTable,"rw");
        fileRAF = new RandomAccessFile(fileTable,"rw");
        idOffsetMap = new LinkedHashMap<>();
        idFileBeanMap = new HashMap<>();
        pathFileBeanMap = new HashMap<>();
        //读取file_table信息
        while (fileRAF.getFilePointer() < fileRAF.length()){
            long offset = fileRAF.getFilePointer();
            String json = fileRAF.readUTF();
            FileBean bean = gson.fromJson(json,FileBean.class);
            nextId = bean.getId() + 1;
            addToMaps(bean,offset);
        }
        System.gc();
        fileEndOffset = fileRAF.length();
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
        addToMaps(bean,fileEndOffset);
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
        updateOffset(oldJson,newJson,offset);
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
        updateOffset(oldJson,null,offset);
        insertOrRemoveFileRecord(oldJson,null,offset);
    }

    private void updateOffset(String oldJson,String newJson,long offset) throws IOException {
        long len;
        if(newJson != null){
            len = newJson.getBytes().length - oldJson.getBytes().length;
        }else {
            len = -oldJson.getBytes().length - 2;
        }
        Iterator iter = idOffsetMap.entrySet().iterator();
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

    private void checkFileBean(FileBean bean,boolean add) throws IOException {
        if(add){
            if(bean == null){
                throw new IOException("不允许插入null数据");
            } else if(bean.getId() != null){
                throw new IOException("插入时不允许手动设置FileBean的id");
            }
        }else {
            if(bean == null || bean.getId() == null){
                throw new IOException("需要指定FileBean及其id");
            }else if(!idFileBeanMap.containsKey(bean.getId())){
                throw new IOException("未发现相应记录");
            }
        }
    }

    private void addToMaps(FileBean bean,long offset){
        idOffsetMap.put(bean.getId(),offset);
        idFileBeanMap.put(bean.getId(),bean);
        pathFileBeanMap.put(bean.getPath(),bean);
    }
}
