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

package com.shirohikari.tag.test;

import com.shirohikari.tag.main.DataStorage;
import com.shirohikari.tag.main.bean.FileBean;
import com.shirohikari.tag.main.bean.TagBean;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;

public class DataStorageTest {

    @Test
    public void create() throws IOException {
        DataStorage.create("E:\\test");
    }

    @Test
    public void addFileRecord() throws IOException {
        DataStorage d = DataStorage.create("E:\\test");
        HashSet<String> l = new HashSet<>();
        l.add("t1");
        l.add("t2");
        d.addFileRecord(new FileBean("E:\\test\\1","bean1",l));
        d.addFileRecord(new FileBean("E:\\test\\2","bean2",l));
        d.addFileRecord(new FileBean("E:\\test\\3","bean3",l));
        d.addFileRecord(new FileBean("E:\\test\\4","bean4",l));
    }

//    @Test
//    public void getFileRecord() throws IOException {
//        DataStorage d = DataStorage.create("E:\\test");
//        d.getFileRecord(8335099);
//    }

    @Test
    public void updateFileRecord() throws IOException{
        DataStorage d = DataStorage.create("E:\\test");
        HashSet<String> l = new HashSet<>();
        l.add("t1");
        d.updateFileRecord(new FileBean(0,"E:\\Pictures\\comic\\幸福观鸟\\1.第01话\\1.webp","更改后的数据1",l));
        l.add("t2");
        d.updateFileRecord(new FileBean(1,"E:\\Pictures\\comic\\幸福观鸟\\1.第01话\\2.webp","更改后的数据2",l));
    }

    @Test
    public void removeFileRecord() throws IOException{
        DataStorage d = DataStorage.create("E:\\test");
        d.removeFileRecord(d.getFileBean(0));
        d.removeFileRecord(d.getFileBean(2));
        HashSet<String> l = new HashSet<>();
        l.add("t3");
        d.addFileRecord(new FileBean("E:\\Pictures\\comic\\星灵感应\\1.第01话\\1.webp","关闭内存映射后增加的",l));
    }

    @Test
    public void addTagRecord() throws IOException {
        DataStorage d = DataStorage.create("E:\\test");
        for (int i = 1;i<=5;i++){
            d.addTagRecord(new TagBean("tag"+i));
        }
    }

    @Test
    public void updateTagRecord() throws IOException {
        DataStorage d = DataStorage.create("E:\\test");
        HashSet<Integer> l = new HashSet<>();
        l.add(1);
        d.updateTagRecord(new TagBean("tag1",l));
        l.add(3);
        d.updateTagRecord(new TagBean("tag2",l));
    }

    @Test
    public void removeTagRecord() throws IOException {
        DataStorage d = DataStorage.create("E:\\test");
        d.removeTagRecord(new TagBean("tag2"));
        d.removeTagRecord(new TagBean("tag4"));
        d.addTagRecord(new TagBean("tag6"));
    }

    @Test
    public void backup() throws IOException {
        DataStorage d = DataStorage.create("E:\\test");
        d.backup("test1");
    }

    @Test
    public void recover() throws IOException {
        DataStorage d = DataStorage.create("E:\\test");
        d.recover("test1");
    }
}
