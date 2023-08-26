/*
 * Copyright (C) 2023 ShiroHikariyayo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shirohikari.tag.util;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author ShiroHikari
 */
public class FileUtil {

    private static Method invokeCleaner = null;
    private static Object unsafe = null;

    /**
     * 循环创建文件夹
     * @param dir
     */
    public static void makeDirectory(Path dir) throws IOException {
        if(Files.exists(dir)) {
            return;
        }
        Files.createDirectories(dir);
    }

    public static void makeFile(Path file) throws IOException {
        if(Files.exists(file)) {
            return;
        }
        makeDirectory(file.getParent());
        Files.createFile(file);
    }

    public static boolean isEmptyDirectory(Path file) throws IOException {
        if(Files.exists(file) && Files.isDirectory(file)){
            return Files.list(file).count() == 0;
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

    public static void saveAfterToTemp(RandomAccessFile raf,long start,long end,File file) throws IOException {
        FileChannel saveChannel = new FileOutputStream(file).getChannel();
        FileChannel rafChannel = raf.getChannel().position(0);
        rafChannel.transferTo(start,end-start,saveChannel);
        saveChannel.close();
    }

    public static void readAndCover(RandomAccessFile raf,long start,long end,File file) throws IOException {
        raf.setLength(end);
        FileChannel inChannel = new FileInputStream(file).getChannel();
        FileChannel rafChannel = raf.getChannel();
        MappedByteBuffer out = rafChannel.map(FileChannel.MapMode.READ_WRITE,start,end-start);
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        while (true) {
            int r = inChannel.read(buffer);
            if (r == -1) {
                break;
            }
            buffer.flip();
            out.put(buffer);
            buffer.clear();
        }
        out.force();
        closeMappedByteBuffer(out);
        inChannel.close();
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
        Path saveDir = Paths.get(savePath);
        makeDirectory(saveDir);
        Path file = Paths.get(savePath,fileName);
        FileChannel channel = FileChannel.open(file, StandardOpenOption.CREATE,StandardOpenOption.WRITE);
        ByteBuffer buffer = ByteBuffer.allocate(data.length);
        buffer.put(data);
        buffer.flip();
        channel.write(buffer);
        channel.force(false);
        channel.close();
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

    private static void closeMappedByteBuffer(MappedByteBuffer buffer) throws IOException {
        //关闭内存映射文件
        try {
            if(unsafe == null || invokeCleaner == null) {
                Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
                unsafeField.setAccessible(true);
                unsafe = unsafeField.get(null);
                invokeCleaner = unsafeClass.getMethod("invokeCleaner", ByteBuffer.class);
            }
            invokeCleaner.invoke(unsafe, buffer);
        } catch (Exception e) {
            throw new IOException("内存映射文件关闭失败",e);
        }
    }
}
