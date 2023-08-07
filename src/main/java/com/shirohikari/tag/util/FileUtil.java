package com.shirohikari.tag.util;

import java.io.*;
import java.nio.channels.FileChannel;

/**
 * @author ShiroHikari
 */
public class FileUtil {

    /**
     * 循环创建文件夹
     * @param file
     */
    public static void makeDirectory(File file){
        if(file.exists()) {
            return;
        }
        String[] paths = file.getPath().split("\\\\");
        StringBuilder curPath = new StringBuilder();
        for(String dir:paths){
            curPath.append(dir);
            File curFile = new File(curPath.toString());
            if (!curFile.exists()) {
                curFile.mkdir();
            }
            curPath.append("\\");
        }
    }

    public static void makeFile(File file) throws IOException {
        if(file.exists()) {
            return;
        }
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

    public static void remove(File dir){
        if(dir != null && dir.exists()){
            for(File file:dir.listFiles()){
                if(file.isDirectory()){
                    remove(file);
                }else {
                    file.delete();
                }
            }
            dir.delete();
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

    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[4096];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static void saveFile(byte[] data,String fileName,String savePath) throws IOException {
        File saveDir = new File(savePath);
        makeDirectory(saveDir);
        File file = new File(saveDir + File.separator + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data);
        if (fos != null) {
            fos.close();
        }
    }

    public static void copyFile(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }
}
