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

    public static void insert(RandomAccessFile raf,File dir, String tempFile,long overrideOffset,long saveOffset,Long fileEndOffset,String insertContent) throws IOException{
        File tmp=File.createTempFile(tempFile, null,dir);
        tmp.deleteOnExit();
        //使用临时文件保存插入点后的数据
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tmp));
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(tmp));
        //计算新文件长度
        if (fileEndOffset == null){
            fileEndOffset = raf.length();
        }
        raf.seek(overrideOffset);
        String oldJson = raf.readUTF();
        fileEndOffset = fileEndOffset - oldJson.getBytes().length + insertContent.getBytes().length;
        //----------下面代码将插入点后的内容读入临时文件中保存----------
        raf.seek(saveOffset);
        byte[] buffer=new byte[128];
        //用于保存实际读取的字节数
        int hasRead =0;
        //使用循环方式读取插入点后的数据
        while((hasRead=raf.read(buffer))>0){
            //将读取的数据写入临时文件
            out.write(buffer,0,hasRead);
        }
        out.flush();
        //-----------下面代码用于插入内容----------
        //把文件记录指针重写定位到pos位置
        raf.seek(overrideOffset);
        //追加需要插入的内容
        raf.writeUTF(insertContent);
        //追加临时文件中的内容
        while((hasRead=in.read(buffer))>0){
            raf.write(buffer,0, hasRead);
        }

        out.close();
        in.close();
        tmp.delete();
        raf.setLength(fileEndOffset);
    }

}
