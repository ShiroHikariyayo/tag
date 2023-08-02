package com.shirohikari.tag.main;

import com.shirohikari.tag.main.bean.FileBean;
import com.shirohikari.tag.main.bean.TagBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TagFile {
    private DataStorage dataStorage;

    public TagFile(String savePath) throws IOException {
        dataStorage = DataStorage.create(savePath);
    }

    public boolean hasFile(int id){
        return dataStorage.hasFile(id);
    }

    public boolean hasFile(String path){
        return dataStorage.hasFile(path);
    }

    public boolean hasTag(String tag){
        return dataStorage.hasTag(tag);
    }

    public Set<String> getAllTags(){
        return dataStorage.getAllTags();
    }

    public FileBean getFileBean(int id){
        return dataStorage.getFileBean(id);
    }

    public FileBean getFileBean(String path) {
        return dataStorage.getFileBean(path);
    }

    public TagBean getTagBean(String tag){
        return dataStorage.getTagBean(tag);
    }

    public void addTagToFile(String filePath,String tag){
        addTagToFile(new FileBean(filePath,"",new HashSet<>()),new TagBean(tag));
    }

    public void addTagToFile(FileBean fileBean,String tag){
        addTagToFile(fileBean,new TagBean(tag));
    }

    public void addTagToFile(FileBean fileBean,TagBean tagBean){
        try {
            if(tagBean.getIdSet().size() != 0){
                throw new RuntimeException("添加标签时不可手动设置idList");
            }
            if(!fileBean.getTagSet().contains(tagBean.getTag())){
                fileBean.getTagSet().add(tagBean.getTag());
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
            tagBean.getIdSet().add(fileBean.getId());
            dataStorage.updateTagRecord(tagBean);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateFile(FileBean fileBean){
        if(fileBean.getId() == null || dataStorage.getFileBean(fileBean.getId()) == null){
            throw new RuntimeException("传入的bean必须为已在表内的bean");
        }
        try {
            dataStorage.updateFileRecord(fileBean);
            updateIdInExistTag(fileBean.getTagSet(),fileBean.getId());
            Set<String> allTags = dataStorage.getAllTags();
            ArrayList<String> extractTags = new ArrayList<>();
            for(String tag:fileBean.getTagSet()){
                if(!allTags.contains(tag)){
                    extractTags.add(tag);
                }
            }
            TagBean tagBean;
            for(String tag:extractTags){
                tagBean = new TagBean(tag);
                dataStorage.addTagRecord(tagBean);
                tagBean.getIdSet().add(fileBean.getId());
                dataStorage.updateTagRecord(tagBean);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateIdInExistTag(HashSet<String> tagSet,Integer id) throws IOException {
        for(String tag:dataStorage.getAllTags()){
            TagBean tagBean = dataStorage.getTagBean(tag);
            if(tagSet.contains(tag) && !tagBean.getIdSet().contains(id)){//在tagList中找到的tag，说明要在这些tag的idList中增加id
                tagBean.getIdSet().add(id);
                dataStorage.updateTagRecord(tagBean);
            }else if(tagBean != null){//未在tagList中找到这些tag，说明要在这些tag的idList中删除id或者表中不存在这些tag
                tagBean.getIdSet().remove(id);
                dataStorage.updateTagRecord(tagBean);
            }
        }
    }
}
