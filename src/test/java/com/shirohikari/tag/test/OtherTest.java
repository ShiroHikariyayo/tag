package com.shirohikari.tag.test;

import com.shirohikari.tag.main.DataStorage;
import com.shirohikari.tag.main.bean.FileBean;
import org.junit.Test;
import org.openjdk.jol.info.ClassLayout;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;

public class OtherTest {

    @Test
    public void getInitMemoryUsed() throws IOException {
        DataStorage d = DataStorage.create("E:\\test");
        MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = bean.getHeapMemoryUsage();
        System.out.println(memoryUsage.getUsed());
    }

    @Test
    public void genFileRecord() throws IOException {
        long s1 = System.currentTimeMillis();
        DataStorage d = DataStorage.create("E:\\test");
        for (int i = 1;i<=900;i++){
            ArrayList<String> l = new ArrayList<>();
            if(i % 3 == 0) l.add("tag1");
            if(i % 5 == 0) l.add("tag2");
            if(i % 7 == 0) l.add("tag3");
            d.addFileRecord(new FileBean("E:\\Pictures\\comic\\蘑菇的擬態日常\\第01话\\"+i+".webp","this is a bean"+i,l));
        }
        long s2 = System.currentTimeMillis();
        System.out.println("time consume:"+(s2-s1));
    }

    @Test
    public void fileBeanSize(){
        ArrayList<String> l = new ArrayList<>();
        l.add("tag1");
        l.add("tag2");
        l.add("tag3");
        FileBean f = new FileBean(0,"E:\\Pictures\\comic\\蘑菇的擬態日常\\第01话\\1.webp","只是一个普通的bean",l);
        System.out.println(ClassLayout.parseInstance(f).toPrintable());
    }
}
