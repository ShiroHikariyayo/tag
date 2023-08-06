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
        System.out.println(memoryUsage.getUsed() + "===" + memoryUsage.getUsed() / (1024*1024.0));
    }

    @Test
    public void genTable() throws IOException {
        long s1 = System.currentTimeMillis();
        TagFile t = new TagFile("E:\\test");
        String[] tags = new String[100];
        for(int i=0;i<100;i++){
            tags[i] = "标签"+i;
        }
        for(int j = 1;j<=900;j++){
            for (int i = 1;i<=100;i++){
                HashSet<String> l = new HashSet<>();
                if(i % 13 == 0) l.add("tag1");
                if(i % 14 == 0) l.add("tag2");
                if(i % 15 == 0) l.add("tag3");
                if(i % 16 == 0) l.add("tag4");
                l.add(tags[OtherTest.getRandom()]);
                l.add(tags[OtherTest.getRandom()]);
                t.addTagToFile(new FileBean("E:\\Pictures\\comic\\蘑菇的擬態日常\\第0"+j+"话\\"+i+".webp","this is a bean"+j+":"+i,l),tags[OtherTest.getRandom()]);
            }
            System.gc();
        }
        long s2 = System.currentTimeMillis();
        System.out.println("time consume:"+(s2-s1));
    }

    public static int getRandom(){
        return (int)(Math.random() * 100);
    }

    @Test
    public void updateTimeUse() throws IOException {
        TagFile t = new TagFile("E:\\test");
        long s1 = System.currentTimeMillis();
        HashSet<String> l = new HashSet<>();
        l.add("tag5");
        for(int i = 99;i>0;i--){
            t.updateFile(new FileBean(i, "E:\\Pictures\\comic\\幸福观鸟\\第0"+i+"话\\"+i+".webp","this is a updated bean"+i,l));
        }
        long s2 = System.currentTimeMillis();
        System.out.println("time consume:"+(s2-s1));
    }

    @Test
    public void removeTagTimeUse() throws IOException {
        TagFile t = new TagFile("E:\\test");
        System.out.println("标签数:"+t.getFileBeans("标签1").size());
        long s1 = System.currentTimeMillis();
        t.removeTag("标签1");
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
