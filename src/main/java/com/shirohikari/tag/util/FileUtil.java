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

import com.shirohikari.tag.main.fileoperator.IFileOperator;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author ShiroHikariyayo
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

    public static void remove(Path dir) throws IOException {
        if(dir != null && Files.exists(dir)){
            Files.walkFileTree(dir,new SimpleFileVisitor<>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    public static void saveAfterToTemp(IFileOperator operator, long start, long end, Path file) throws IOException {
        FileChannel saveChannel = FileChannel.open(file,StandardOpenOption.WRITE);
        FileChannel srcChannel = operator.getFileChannel();
        srcChannel.position(0);
        srcChannel.transferTo(start,end-start,saveChannel);
        saveChannel.close();
    }

    public static void readAndCover(IFileOperator operator, long start, long end, Path file) throws IOException {
        operator.truncate(end);
        FileChannel inChannel = FileChannel.open(file,StandardOpenOption.READ);
        FileChannel destChannel = operator.getFileChannel();
        inChannel.transferTo(0,inChannel.size(),destChannel);
        destChannel.position(start);
        inChannel.close();
    }

    public static void saveFile(byte[] data,String fileName,String savePath) throws IOException {
        Path saveDir = Paths.get(savePath);
        makeDirectory(saveDir);
        Path file = Paths.get(savePath,fileName);
        FileChannel channel = FileChannel.open(file, StandardOpenOption.CREATE,StandardOpenOption.WRITE,StandardOpenOption.TRUNCATE_EXISTING);
        ByteBuffer buffer = ByteBuffer.allocate(data.length);
        buffer.put(data);
        buffer.flip();
        channel.write(buffer);
        channel.force(false);
        channel.close();
    }

    public static void copyFile(Path source, Path dest) throws IOException {
        FileChannel inputChannel = FileChannel.open(source,StandardOpenOption.READ,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
        FileChannel outputChannel = FileChannel.open(dest,StandardOpenOption.WRITE,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
        outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        inputChannel.close();
        outputChannel.close();
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
