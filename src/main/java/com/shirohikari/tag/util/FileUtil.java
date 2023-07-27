package com.shirohikari.tag.util;

import java.io.*;

public class FileUtil {

    /**
     * 循环创建文件夹
     * @param file
     */
    public static void makeDirectory(File file){
        if(file.exists())
            return;
        String[] paths = file.getPath().split("\\\\");
        StringBuilder curPath = new StringBuilder();
        for(String dir:paths){
            curPath.append(dir);
            File curFile = new File(curPath.toString());
            //System.out.println(curFile.getPath());
            if (!curFile.exists()) {
                curFile.mkdir();
            }
            curPath.append("\\");
        }
    }

    public static void makeFile(File file) throws IOException {
        if(file.exists())
            return;
        makeDirectory(new File(file.getParent()));
        file.createNewFile();
    }

    public static boolean isEmptyDirectory(File file){
        if(file.exists() && file.isDirectory()){
            return file.list().length == 0;
        } else {
            return true;
        }
    }

}
