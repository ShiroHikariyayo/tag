package com.shirohikari.tag.test;

import com.shirohikari.tag.main.TagFile;
import com.shirohikari.tag.main.bean.FileBean;
import org.junit.Test;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
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
        for(int j = 1;j<=50;j++){
            for (int i = 1;i<=100;i++){
                HashSet<String> l = new HashSet<>();
                if(i % 6 == 0) l.add("tag1");
                if(i % 7 == 0) l.add("tag2");
                if(i % 8 == 0) l.add("tag3");
                if(i % 9 == 0) l.add("tag4");
                l.add(tags[OtherTest.getRandom(0,50)]);
                l.add(tags[OtherTest.getRandom(0,50)]);
                l.add(tags[OtherTest.getRandom(0,50)]);
                t.addTagToFile(new FileBean("E:\\Pictures\\comic\\蘑菇的擬態日常\\第0"+j+"话\\"+i+".webp","this is a bean"+j+":"+i,l));
            }
            System.gc();
        }
        long s2 = System.currentTimeMillis();
        System.out.println("time consume:"+(s2-s1));
    }

    public static int getRandom(int min,int max){
        return min + (int)(Math.random() * (max-min+1));
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
        ArrayList<String> tags = new ArrayList<>();
        tags.add("标签1");
        t.removeTag(tags);
        long s2 = System.currentTimeMillis();
        System.out.println("time consume:"+(s2-s1));
    }

    @Test
    public void removeMultiTagTimeUse() throws IOException {
        TagFile t = new TagFile("E:\\test");
        ArrayList<FileBean> fileBeans = t.getFileBeans("标签1");
        fileBeans.addAll(t.getFileBeans("标签2"));
        System.out.println("标签数:"+fileBeans.size());
        long s1 = System.currentTimeMillis();
        ArrayList<String> tags = new ArrayList<>();
        tags.add("标签1");
        tags.add("标签2");
        t.removeTag(tags);
        long s2 = System.currentTimeMillis();
        System.out.println("time consume:"+(s2-s1));
    }

}
