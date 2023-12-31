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

package com.shirohikari.tag.main.fileoperator;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * @author ShiroHikariyayo
 */
public class TextFileOperator implements IFileOperator {

    private static final int BUFFER_LENGTH = 1024;
    private static final String VERSION = TextFileOperator.class.getName();

    private final ByteBuffer buffer;
    private final StringBuilder jsonBuilder;
    private FileChannel channel;

    public TextFileOperator() {
        this.buffer = ByteBuffer.allocateDirect(BUFFER_LENGTH);
        this.jsonBuilder = new StringBuilder();
    }

    @Override
    public void load(Path file) throws IOException {
        this.channel = FileChannel.open(file,StandardOpenOption.READ,StandardOpenOption.WRITE);
    }

    @Override
    public void close() throws IOException {
        this.channel.close();
    }

    @Override
    public String readNext() throws IOException {
        long startPosition = channel.position();
        String json;
        int hasNext = channel.read(buffer);
        if (hasNext == -1) {
            return null;
        }
        buffer.flip();
        int len = buffer.getInt();
        if(len <= 1020){
            byte[] data = new byte[len];
            buffer.get(data,0,len);
            json = new String(data);
        }else {
            byte[] data = new byte[BUFFER_LENGTH];
            buffer.get(data,0,1020);
            jsonBuilder.append(new String(data,0,1020));
            buffer.clear();
            int end = len - 1020;
            int strLen;
            do {
                int r = channel.read(buffer);
                if (r == -1) {
                    break;
                }
                buffer.flip();
                strLen = Math.min(end, BUFFER_LENGTH);
                buffer.get(data,0,strLen);
                jsonBuilder.append(new String(data,0,strLen));
                buffer.clear();
                end -= BUFFER_LENGTH;
            }while (end > 0);
            json = jsonBuilder.toString();
        }
        buffer.clear();
        jsonBuilder.delete(0,jsonBuilder.length());
        channel.position(startPosition + len + Integer.BYTES);
        return json;
    }

    @Override
    public void write(String json) throws IOException {
        channel.position(channel.position());
        buffer.putInt(json.getBytes().length);
        int len = json.getBytes().length;
        if(len <= 1020){
            buffer.put(json.getBytes());
            buffer.flip();
            channel.write(buffer);
        }else if(len <= BUFFER_LENGTH){
            writePart(json.getBytes(),0,1020);
            writePart(json.getBytes(),1020,len - 1020);
        }else {
            writePart(json.getBytes(),0,1020);
            writePart(json.getBytes(),1020,4);
            int start = BUFFER_LENGTH;
            int end = len - BUFFER_LENGTH;
            while (start < len) {
                writePart(json.getBytes(),start,Math.min(end,BUFFER_LENGTH));
                start += BUFFER_LENGTH;
                end -= BUFFER_LENGTH;
            }
        }
        buffer.clear();
    }

    @Override
    public long position() throws IOException {
        return channel.position();
    }

    @Override
    public void position(long position) throws IOException {
        channel.position(position);
    }

    @Override
    public int messageDefineLength() {
        return Integer.BYTES;
    }

    @Override
    public FileChannel getFileChannel() {
        return channel;
    }

    @Override
    public void force() throws IOException {
        this.channel.force(true);
    }

    @Override
    public void truncate(long length) throws IOException {
        long position = channel.position();
        channel.truncate(length);
        channel.position(position);
    }

    @Override
    public long size() throws IOException {
        return channel.size();
    }

    @Override
    public String version() {
        return VERSION;
    }

    private void writePart(byte[] data,int offset,int length) throws IOException {
        buffer.put(data,offset,length);
        buffer.flip();
        channel.write(buffer);
        buffer.clear();
        channel.position(channel.position());
    }
}
