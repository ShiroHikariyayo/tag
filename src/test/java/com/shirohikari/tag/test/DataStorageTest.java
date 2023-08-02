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
        d.updateFileRecord(new FileBean(896,"E:\\Pictures\\comic\\幸福观鸟\\1.第01话\\1.webp","更改后的数据1",l));
        l.add("t2");
        d.updateFileRecord(new FileBean(898,"E:\\Pictures\\comic\\幸福观鸟\\1.第01话\\2.webp","更改后的数据2",l));
    }

    @Test
    public void removeFileRecord() throws IOException{
        DataStorage d = DataStorage.create("E:\\test");
        d.removeFileRecord(d.getFileBean(0));
        d.removeFileRecord(d.getFileBean(2));
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
    }
}
