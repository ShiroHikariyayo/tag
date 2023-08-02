package com.shirohikari.tag.test;

import com.shirohikari.tag.main.TagFile;
import com.shirohikari.tag.main.bean.FileBean;
import com.shirohikari.tag.main.bean.TagBean;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;

public class TagFileTest {
    @Test
    public void addTagToFile() throws IOException {
        TagFile t = new TagFile("E:\\test");
        HashSet<String> l = new HashSet<>();
        l.add("t1");
        t.addTagToFile(new FileBean("E:\\Pictures\\comic\\幸福观鸟\\1.第01话\\1.webp","数据1",l),new TagBean("t4"));
        t.addTagToFile("E:\\Pictures\\comic\\幸福观鸟\\1.第01话\\2.webp","tag1");
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
    }
}
