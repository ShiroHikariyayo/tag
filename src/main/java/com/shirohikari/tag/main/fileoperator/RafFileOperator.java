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
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

/**
 * @author ShiroHikariyayo
 */
public class RafFileOperator implements IFileOperator {

    private static final String VERSION = "RAFFileOperator-1";
    private RandomAccessFile raf;

    @Override
    public void load(Path path) throws IOException {
        raf = new RandomAccessFile(path.toFile(),"rwd");
    }

    @Override
    public void close() throws IOException {
        raf.close();
    }

    @Override
    public String readNext() throws IOException {
        return raf.readUTF();
    }

    @Override
    public void write(String json) throws IOException {
        raf.writeUTF(json);
    }

    @Override
    public long position() throws IOException {
        return raf.getFilePointer();
    }

    @Override
    public void position(long position) throws IOException {
        raf.seek(position);
    }

    @Override
    public int messageDefineLength() {
        return 2;
    }

    @Override
    public FileChannel getFileChannel() throws IOException {
        return raf.getChannel();
    }

    @Override
    public void force() throws IOException {

    }

    @Override
    public void truncate(long length) throws IOException {
        if(length < raf.length()){
            raf.setLength(length);
        }
    }

    @Override
    public long size() throws IOException {
        return raf.length();
    }

    @Override
    public String version() {
        return VERSION;
    }
}
