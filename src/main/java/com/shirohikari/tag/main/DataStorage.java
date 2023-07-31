package com.shirohikari.tag.main;

import com.google.gson.Gson;
import com.shirohikari.tag.main.bean.FileBean;
import com.shirohikari.tag.util.FileUtil;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;

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
    private HashMap<Integer,Long> idOffsetMap;
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
        idOffsetMap = new HashMap<>();
        idFileBeanMap = new HashMap<>();
        pathFileBeanMap = new HashMap<>();
        while (fileRAF.getFilePointer() < fileRAF.length()){
            long offset = fileRAF.getFilePointer();
            String json = fileRAF.readUTF();
            FileBean bean = gson.fromJson(json,FileBean.class);
            nextId = bean.getId() + 1;
            idOffsetMap.put(bean.getId(),offset);
            idFileBeanMap.put(bean.getId(),bean);
            pathFileBeanMap.put(bean.getPath(),bean);
        }
        System.gc();
        fileEndOffset = fileRAF.length();
    }

    public boolean hasTag(String path){
        return pathFileBeanMap.containsKey(path);
    }

    public FileBean getFileBean(String path) throws IOException {
        return pathFileBeanMap.get(path);
    }

    public void addFileRecord(FileBean bean) throws IOException {
        if(bean == null){
            throw new IOException("不允许插入null数据");
        } else if(bean.getId() != null){
            throw new IOException("插入时不允许手动设置FileBean的id");
        }
        bean.setId(nextId++);
        String json = gson.toJson(bean);
        fileEndOffset += json.getBytes().length + 2;
        fileRAF.writeUTF(json);
    }

    public void updateFileRecord(FileBean bean) throws IOException {

    }
}
