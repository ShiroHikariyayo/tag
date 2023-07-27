package com.shirohikari.tag.main;

import com.shirohikari.tag.util.FileUtil;

import java.io.*;

/**
 * @author 20637
 */
public class DataStorage {
    private static final String TAG_TABLE = "tag_table";
    private static final String FILE_TABLE = "file_table";

    private File dir;
    private File tabTable;
    private File fileTable;

    private DataStorage(File dir,File tabTable,File fileTable) throws IOException {
        this.dir = dir;
        this.tabTable = tabTable;
        this.fileTable = fileTable;
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
    
}
