/*
 * Copyright (C) 2023 ShiroHikariyayo
 * Copyright 2008 Google Inc.
 * used google/gson,see https://github.com/google/gson
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

package com.shirohikari.tag.test;

import com.google.gson.Gson;
import com.shirohikari.tag.main.bean.FileBean;
import com.shirohikari.tag.main.fileoperator.TextFileOperator;
import org.junit.Test;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.HashSet;

public class TextFileOperatorTest {

    @Test
    public void write() throws IOException {
        TextFileOperator operator = new TextFileOperator();
        operator.load(Paths.get("E:\\test\\1.txt"));
        Gson gson = new Gson();
        operator.write(gson.toJson(new FileBean(0,"E:\\test","bean1",new HashSet<>())));
        operator.write(gson.toJson(new FileBean(1,"E:\\test","bean2",new HashSet<>())));
        operator.write(gson.toJson(new FileBean(2,"E:\\test","bean3",new HashSet<>())));
        operator.position(0);
        System.out.println(operator.readNext());
        System.out.println(operator.readNext());
        System.out.println(operator.readNext());
    }

    @Test
    public void write2() throws IOException {
        TextFileOperator operator = new TextFileOperator();
        operator.load(Paths.get("E:\\test\\1.txt"));
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<=200;i++){
            sb.append(System.currentTimeMillis());
        }
        Gson gson = new Gson();
        operator.write(gson.toJson(new FileBean(1,"E:\\test",sb.toString(),new HashSet<>())));
        operator.write(gson.toJson(new FileBean(2,"E:\\test",sb.toString(),new HashSet<>())));
        operator.position(0);
        for (int i=0;i<=10;i++){
            System.out.println(operator.readNext());
        }

        String s = "后续添加";
        System.out.println("size:"+operator.size());
        System.out.println("position:"+operator.position());
        operator.truncate(operator.position() + s.getBytes().length + 4);
        System.out.println("size:"+operator.size());
        System.out.println("position:"+operator.position());

        MappedByteBuffer byteBuffer = operator.getFileChannel().map(
                FileChannel.MapMode.READ_WRITE,operator.position(), operator.size() - operator.position());
        byteBuffer.putInt(s.getBytes().length);
        byteBuffer.put(s.getBytes());
        System.out.println("size:"+operator.size());
        System.out.println("position:"+operator.position());
    }
}
