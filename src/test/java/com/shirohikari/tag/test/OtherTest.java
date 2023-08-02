package com.shirohikari.tag.test;

import com.shirohikari.tag.main.DataStorage;
import com.shirohikari.tag.main.TagFile;
import com.shirohikari.tag.main.bean.FileBean;
import org.junit.Test;
import org.openjdk.jol.info.ClassLayout;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashSet;

public class OtherTest {

    @Test
    public void getInitMemoryUsed() throws IOException {
        TagFile t = new TagFile("E:\\test");
        MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = bean.getHeapMemoryUsage();
        System.out.println(memoryUsage.getUsed());
    }

    @Test
    public void genTable() throws IOException {
        long s1 = System.currentTimeMillis();
        TagFile t = new TagFile("E:\\test");
        for(int j = 1;j<=10;j++){
            for (int i = 1;i<=1000;i++){
                HashSet<String> l = new HashSet<>();
                if(i % 3 == 0) l.add("tag2");
                if(i % 7 == 0) l.add("tag3");
                if(i % 11 == 0) l.add("tag4");
                t.addTagToFile(new FileBean("E:\\Pictures\\comic\\蘑菇的擬態日常\\第0"+j+"话\\"+i+".webp","this is a bean"+j+":"+i,l),"tag1");
            }
            System.gc();
        }
        long s2 = System.currentTimeMillis();
        System.out.println("time consume:"+(s2-s1));
    }

    @Test
    public void fileBeanSize(){
        HashSet<String> l = new HashSet<>();
        l.add("tag1");
        l.add("tag2");
        l.add("tag3");
        FileBean f = new FileBean(0,"E:\\Pictures\\comic\\蘑菇的擬態日常\\第01话\\1.webp","只是一个普通的bean",l);
        System.out.println(ClassLayout.parseInstance(f).toPrintable());
    }
}
