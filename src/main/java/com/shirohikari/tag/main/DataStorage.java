package com.shirohikari.tag.main;

import com.shirohikari.tag.util.FileUtil;

import java.io.*;

/**
 * @author 20637
 */
public class DataStorage {
    private static final String TAG_TABLE = "tag_table";
    private static final String FILE_TABLE = "file_table";

    private String dirPath;
    private BufferedReader tabReader;
    private BufferedReader fileReader;
    private BufferedWriter tabWriter;
    private BufferedWriter fileWriter;

    private DataStorage(String dirPath,File tabTable,File fileTable) throws IOException {
        this.dirPath = dirPath;
        tabReader = new BufferedReader(new FileReader(tabTable));
        fileReader = new BufferedReader(new FileReader(fileTable));
        tabWriter = new BufferedWriter(new FileWriter(tabTable));
        fileWriter = new BufferedWriter(new FileWriter(fileTable));
    }

    public static DataStorage create(String dirPath) throws IOException {
        File dir = new File(dirPath);
        File tabTable = new File(dirPath,TAG_TABLE);
        File fileTable = new File(dirPath,FILE_TABLE);
        if(!FileUtil.isEmptyDirectory(dir)){
            throw new IOException("文件夹不为空");
        }
        FileUtil.makeDirectory(dir);
        FileUtil.makeFile(tabTable);
        FileUtil.makeFile(fileTable);
        return new DataStorage(dirPath,tabTable,fileTable);
    }
    
}
