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

    public static void saveAfterToTemp(RandomAccessFile raf,long start,File file) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        raf.seek(start);
        byte[] buffer=new byte[128];
        //用于保存实际读取的字节数
        int hasRead =0;
        //使用循环方式读取插入点后的数据
        while((hasRead=raf.read(buffer))>0){
            //将读取的数据写入临时文件
            out.write(buffer,0,hasRead);
        }
        out.flush();
        out.close();
    }

    public static void readAndCover(RandomAccessFile raf,long start,File file) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        raf.seek(start);
        byte[] buffer=new byte[128];
        int hasRead =0;
        while((hasRead=in.read(buffer))>0){
            raf.write(buffer,0, hasRead);
        }
        in.close();
    }

}
