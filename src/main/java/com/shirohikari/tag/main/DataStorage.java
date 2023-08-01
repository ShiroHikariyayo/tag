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
            setMaps(bean,offset);
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
        setMaps(bean,fileEndOffset);
        String json = gson.toJson(bean);
        fileEndOffset += json.getBytes().length + 2;
        fileRAF.writeUTF(json);
    }

    public void updateFileRecord(FileBean bean) throws IOException {
        if(bean == null || bean.getId() == null){
            throw new IOException("需要指定FileBean及其id");
        }else if(!idFileBeanMap.containsKey(bean.getId())){
            throw new IOException("未发现相应记录");
        }
        long offset = idOffsetMap.get(bean.getId());

        fileRAF.seek(offset);
        String oldJson = fileRAF.readUTF();
        String newJson = gson.toJson(bean);
        //更新idOffsetMap
        long len = newJson.getBytes().length - oldJson.getBytes().length;
        Iterator iter = idOffsetMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            if((long)entry.getValue() <= offset){
                continue;
            }
            entry.setValue((long)entry.getValue() + len);
        }

        FileUtil.insert(fileRAF,dir,"file_tmp",offset,fileRAF.getFilePointer(),fileEndOffset,newJson);
        fileEndOffset = fileEndOffset - oldJson.getBytes().length + newJson.getBytes().length;
        fileRAF.setLength(fileEndOffset);
    }

    private void setMaps(FileBean bean,long offset){
        idOffsetMap.put(bean.getId(),offset);
        idFileBeanMap.put(bean.getId(),bean);
        pathFileBeanMap.put(bean.getPath(),bean);
    }
}
