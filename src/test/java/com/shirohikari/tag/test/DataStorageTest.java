package com.shirohikari.tag.test;

import com.shirohikari.tag.main.DataStorage;
import com.shirohikari.tag.main.bean.FileBean;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class DataStorageTest {

    @Test
    public void create() throws IOException {
        DataStorage.create("E:\\test");
    }

    @Test
    public void addFileRecord() throws IOException {
        DataStorage d = DataStorage.create("E:\\test");
        ArrayList<String> l = new ArrayList<>();
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
        ArrayList<String> l = new ArrayList<>();
        l.add("t1");
        d.updateFileRecord(new FileBean(896,"E:\\Pictures\\comic\\幸福观鸟\\1.第01话\\1.webp","更改后的数据1",l));
        l.add("t2");
        d.updateFileRecord(new FileBean(898,"E:\\Pictures\\comic\\幸福观鸟\\1.第01话\\2.webp","更改后的数据2",l));
    }
}
