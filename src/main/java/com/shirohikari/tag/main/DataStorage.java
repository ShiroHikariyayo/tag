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

    private long fileOffset;
    private boolean fileRead;
    private boolean tagRead;
    private File dir;
    private File tabTable;
    private File fileTable;
    private HashMap<Long,String> offsetFileJSONMap;
    private HashMap<String,Long> fileJSONOffsetMap;
    private RandomAccessFile tabRAF;
    private RandomAccessFile fileRAF;
    private Gson gson;

    private DataStorage(File dir,File tabTable,File fileTable) throws IOException {
        this.tagRead = true;
        this.fileRead = true;
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
        offsetFileJSONMap = new HashMap<>();
        fileJSONOffsetMap = new HashMap<>();
        while (fileRAF.getFilePointer() < fileRAF.length()){
            long offset = fileRAF.getFilePointer();
            String json = fileRAF.readUTF();
            offsetFileJSONMap.put(offset,json);
            fileJSONOffsetMap.put(json,offset);
        }
        fileOffset = fileRAF.length();
    }

    public FileBean getFileRecord(long id) throws IOException {
        String json = offsetFileJSONMap.get(id);
        System.out.println(json);
        return gson.fromJson(json,FileBean.class);
    }

    public void addFileRecord(FileBean bean) throws IOException {
        bean.setOffset(fileOffset);
        String json = gson.toJson(bean);
        fileOffset += json.getBytes().length + 2;
        fileRAF.writeUTF(json);
    }

    public void updateFileRecord(FileBean bean) throws IOException {

    }
}
