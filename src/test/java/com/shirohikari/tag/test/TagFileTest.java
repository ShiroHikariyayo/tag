package com.shirohikari.tag.test;

import com.shirohikari.tag.main.TagFile;
import com.shirohikari.tag.main.bean.FileBean;
import com.shirohikari.tag.main.bean.TagBean;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class TagFileTest {
    @Test
    public void addTagToFile() throws IOException {
        TagFile t = new TagFile("E:\\test");
        ArrayList<String> l = new ArrayList<>();
        l.add("t1");
        t.addTagToFile(new FileBean("E:\\Pictures\\comic\\幸福观鸟\\1.第01话\\1.webp","数据1",l),new TagBean("t4"));
        t.addTagToFile("E:\\Pictures\\comic\\幸福观鸟\\1.第01话\\2.webp","tag1");
    }
}
