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

import com.shirohikari.tag.main.TagFile;
import com.shirohikari.tag.main.bean.FileBean;
import com.shirohikari.tag.main.bean.TagBean;
import com.shirohikari.tag.main.datastorage.LocalDataStorage;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagFileTest {
    @Test
    public void addTagToFile() throws IOException {
        TagFile t = new TagFile(LocalDataStorage.create("E:\\test"));
        HashSet<String> l = new HashSet<>();
        l.add("t1");
        l.add("t2");
        t.addTagToFile(new FileBean("E:\\Pictures\\comic\\幸福观鸟\\1.第01话\\1.webp","数据1",l),new TagBean("t4"));
        l.remove("t2");
        t.addTagToFile(new FileBean("E:\\Pictures\\comic\\幸福观鸟\\1.第01话\\1.webp","数据1",l),new TagBean("t4"));
        t.addTagToFile(new FileBean("E:\\Pictures\\comic\\幸福观鸟\\1.第01话\\1.webp","数据1",l),new TagBean("t5"));
        t.addTagToFile("E:\\Pictures\\comic\\幸福观鸟\\1.第01话\\2.webp","tag1");
        t.addTagToFile("E:\\Pictures\\comic\\幸福观鸟\\1.第01话\\2.webp","tag2");
        t.addTagToFile(t.getFileBean(0),new TagBean("t6"));
    }

    @Test
    public void addTagToFile2() throws IOException {
        TagFile t = new TagFile(LocalDataStorage.create("E:\\test"));
        HashSet<String> l = new HashSet<>();
        l.add("t1");
        l.add("t2");
        t.addTagToFile(new FileBean("E:\\test\\1","desc",l));
        //t.addTagToFile(new FileBean("E:\\test\\1","desc",l));
    }

    @Test
    public void addTagToFile3() throws IOException {
        TagFile t = new TagFile(LocalDataStorage.create("E:\\test"));
        ArrayList<TagBean> l = new ArrayList<>();
        l.add(new TagBean("t1"));
        TagBean b = new TagBean("t2");
        l.add(b);
        //t.addTagsToFile(new FileBean(0,"E:\\test\\1","desc",new HashSet<>()),l);
        t.addTagsToFile(new FileBean("E:\\test\\1","desc"),l);
        t.addTagsToFile(new FileBean("E:\\test\\1","desc"),l);
        l.remove(b);
        l.add(new TagBean("t3"));
        t.addTagsToFile(new FileBean("E:\\test\\1","desc"),l);

//        ArrayList<String> arr = new ArrayList<>();
//        arr.add("t3");
//        arr.add("t4");
//        t.addTagsToFile("E:\\test\\2",arr);
//        t.addTagsToFile("E:\\test\\2",arr);
//        arr.remove("t4");
//        arr.add("t5");
//        t.addTagsToFile("E:\\test\\2",arr);
    }

    @Test
    public void updateFile() throws IOException {
        TagFile t = new TagFile(LocalDataStorage.create("E:\\test"));
        t.addTagToFile("E:\\test\\1","tag1");
        t.addTagToFile("E:\\test\\2","tag2");
        FileBean fileBean = t.getFileBean(0);
        fileBean.getTagSet().remove("tag1");
        fileBean.getTagSet().add("tag3");
        t.updateFile(fileBean);
        t.updateFile(fileBean);
    }

    @Test
    public void updateFile2() throws IOException {
        TagFile t = new TagFile(LocalDataStorage.create("E:\\test"));
        FileBean bean = new FileBean("E:\\test\\1","this is a bean1");
        FileBean bean2 = new FileBean("E:\\test\\2","this is a bean2");
        bean.getTagSet().add("tag1");
        bean2.getTagSet().add("tag2");
        t.addTagToFile(bean);
        t.addTagToFile(bean2);
        //bean.setId(1);
        bean.setPath("E:\\test\\2");
        bean.setPath("E:\\test\\3");
        t.updateFile(bean);
        System.out.println(t.hasFile("E:\\test\\1"));
        System.out.println(t.hasFile("E:\\test\\2"));
    }

    @Test
    public void deleteTagToFile() throws IOException {
        TagFile t = new TagFile(LocalDataStorage.create("E:\\test"));
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
    public void deleteTagToFile2() throws IOException {
        TagFile t = new TagFile(LocalDataStorage.create("E:\\test"));
        t.addTagToFile("E:\\test\\1","tag1");
        t.addTagToFile("E:\\test\\1","tag2");
        t.addTagToFile("E:\\test\\1","tag3");
        FileBean f = t.getFileBean(0);
        f.getTagSet().remove("tag1");
        f.getTagSet().add("tag4");
        t.deleteTagToFile(f,new TagBean("tag2"));
    }

    @Test
    public void getFileBeans() throws IOException {
        long s1 = System.currentTimeMillis();
        TagFile t = new TagFile(LocalDataStorage.create("E:\\test"));
        ArrayList<String> l = new ArrayList<>();
        l.add("标签1");
        l.add("标签2");
        l.add("标签3");
        Set<Integer> set = t.getFileBeansId(l);
        System.out.println(set);
        System.out.println("===================");
        List<FileBean> arr = t.getFileBeans(l);
        System.out.println(arr);
        long s2 = System.currentTimeMillis();
        System.out.println("耗时:"+(s2 - s1));
    }

    @Test
    public void removeTag() throws IOException {
        TagFile t = new TagFile(LocalDataStorage.create("E:\\test"));
        HashSet<String> set = new HashSet<>();
        set.add("t1");
        t.addTagToFile(new FileBean("E:\\test\\1","desc1",set));
        set.add("t2");
        t.addTagToFile(new FileBean("E:\\test\\2","desc2",set));
        set.add("t3");
        t.addTagToFile(new FileBean("E:\\test\\3","desc3",set));
        t.removeTag("t1");
    }

    @Test
    public void removeTag2() throws IOException {
        TagFile t = new TagFile(LocalDataStorage.create("E:\\test"));
        HashSet<String> set = new HashSet<>();
        set.add("t1");
        t.addTagToFile(new FileBean("E:\\test\\1","desc1",set));
        set.add("t2");
        t.addTagToFile(new FileBean("E:\\test\\2","desc2",set));
        set.add("t3");
        t.addTagToFile(new FileBean("E:\\test\\3","desc3",set));
        ArrayList<String> l = new ArrayList<>();
        l.add("t1");
        l.add("t2");
        t.removeTag(l);
    }

    @Test
    public void addTagInDirectory() throws IOException {
        TagFile t = new TagFile(LocalDataStorage.create("E:\\test"));
//        t.addTagInDirectory(new File("E:\\test"),new TagBean("tag1"),true);
//        t.addTagInDirectory(new File("E:\\test"),new TagBean("tag1"),false);
//        t.addTagInDirectory(new File("E:\\test"),new TagBean("tag2"),false);
        ArrayList<String> l = new ArrayList<>();
        l.add("t1");
        t.addTagsInDirectory(Paths.get("E:\\test"),l,true);
        t.addTagsInDirectory(Paths.get("E:\\test"),l,false);
        l.add("t2");
        t.addTagsInDirectory(Paths.get("E:\\test"),l,false);
    }

    @Test
    public void backup() throws IOException {
        TagFile t = new TagFile(LocalDataStorage.create("E:\\test"));
        t.backup("备份1");
        System.out.println("开始除所有标签");
        ArrayList<String> tags = new ArrayList<>();
        for (int i=1;i<=50;i++){
            tags.add("标签"+i);
        }
        t.removeTag(tags);
        System.out.println("已移除所有标签");
        t.recover("备份1");
        System.out.println(t.hasTag("标签1"));
        System.out.println(t.hasFile(0));
        t.removeBackup("备份1");
    }

    @Test
    public void getFilePaths() throws IOException {
        TagFile t = new TagFile(LocalDataStorage.create("E:\\test"));
        System.out.println("标签数:"+t.getFileBeansId("标签1").size());
        ArrayList<String> tags = new ArrayList<>();
        tags.add("标签1");
        tags.add("标签2");
        System.out.println(t.getFilePaths("标签1"));
        System.out.println("========================================");
        System.out.println(t.getFilePaths(tags));
    }
}
