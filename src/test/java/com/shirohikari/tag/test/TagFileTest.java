package com.shirohikari.tag.test;

import com.shirohikari.tag.main.TagFile;
import com.shirohikari.tag.main.bean.FileBean;
import com.shirohikari.tag.main.bean.TagBean;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class TagFileTest {
    @Test
    public void addTagToFile() throws IOException {
        TagFile t = new TagFile("E:\\test");
        HashSet<String> l = new HashSet<>();
        l.add("t1");
        l.add("t2");
        t.addTagToFile(new FileBean("E:\\Pictures\\comic\\幸福观鸟\\1.第01话\\1.webp","数据1",l),new TagBean("t4"));
        l.remove("t2");
        t.addTagToFile(new FileBean("E:\\Pictures\\comic\\幸福观鸟\\1.第01话\\1.webp","数据1",l),new TagBean("t4"));
        t.addTagToFile(new FileBean("E:\\Pictures\\comic\\幸福观鸟\\1.第01话\\1.webp","数据1",l),new TagBean("t5"));
        t.addTagToFile("E:\\Pictures\\comic\\幸福观鸟\\1.第01话\\2.webp","tag1");
        t.addTagToFile("E:\\Pictures\\comic\\幸福观鸟\\1.第01话\\2.webp","tag2");
    }

    @Test
    public void updateFile() throws IOException {
        TagFile t = new TagFile("E:\\test");
        t.addTagToFile("E:\\test\\1","tag1");
        t.addTagToFile("E:\\test\\2","tag2");
        FileBean fileBean = t.getFileBean(0);
        fileBean.getTagSet().remove("tag1");
        fileBean.getTagSet().add("tag3");
        t.updateFile(fileBean);
        t.updateFile(fileBean);
    }

    @Test
    public void deleteTagToFile() throws IOException {
        TagFile t = new TagFile("E:\\test");
        t.addTagToFile("E:\\test\\1","tag1");
        t.addTagToFile("E:\\test\\1","tag2");
        t.addTagToFile("E:\\test\\2","tag2");
        t.deleteTagToFile("E:\\test\\1","tag1");
        t.deleteTagToFile("E:\\test\\1","tag1");
        t.deleteTagToFile("E:\\test\\3","tag1");
        t.deleteTagToFile("E:\\test\\1","tag3");
        t.deleteTagToFile("E:\\test\\1","tag2");
    }

    @Test
    public void getFileBeans() throws IOException {
        long s1 = System.currentTimeMillis();
        TagFile t = new TagFile("E:\\test");
        ArrayList<String> l = new ArrayList<>();
        l.add("tag2");
        l.add("tag3");
        l.add("tag4");
        HashSet<Integer> set = t.getFileBeansId(l);
        System.out.println(set);
        System.out.println("===================");
        ArrayList<FileBean> arr = t.getFileBeans(l);
        System.out.println(arr);
        long s2 = System.currentTimeMillis();
        System.out.println("耗时:"+(s2 - s1));
    }
}
