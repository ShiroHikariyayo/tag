package com.shirohikari.tag.main;

import com.shirohikari.tag.main.bean.FileBean;
import com.shirohikari.tag.main.bean.TagBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class TagFile {
    private DataStorage dataStorage;

    public TagFile(String savePath) throws IOException {
        dataStorage = DataStorage.create(savePath);
    }

    public void addTagToFile(String filePath,String tag){
        addTagToFile(new FileBean(filePath,"",new ArrayList<>()),new TagBean(tag));
    }

    public void addTagToFile(FileBean fileBean,String tag){
        addTagToFile(fileBean,new TagBean(tag));
    }

    public void addTagToFile(FileBean fileBean,TagBean tagBean){
        try {
            if(tagBean.getIdList().size() != 0){
                throw new RuntimeException("添加标签时不可手动设置idList");
            }
            if(!fileBean.getTagList().contains(tagBean.getTag())){
                fileBean.getTagList().add(tagBean.getTag());
            }
            if (dataStorage.hasFile(fileBean.getPath())){
                fileBean.setId(dataStorage.getFileBean(fileBean.getPath()).getId());
                dataStorage.updateFileRecord(fileBean);
            }else {
                dataStorage.addFileRecord(fileBean);
                fileBean.setId(dataStorage.getFileBean(fileBean.getPath()).getId());
            }
            if(!dataStorage.hasTag(tagBean.getTag())){
                dataStorage.addTagRecord(tagBean);
            }
            fileBean.setId(fileBean.getId());
            tagBean.getIdList().add(fileBean.getId());
            dataStorage.updateTagRecord(tagBean);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
