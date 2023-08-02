package com.shirohikari.tag.main;

import com.shirohikari.tag.main.bean.FileBean;
import com.shirohikari.tag.main.bean.TagBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 将标签和文件做关联,并不会对文件进行修改
 * @author ShiroHikari
 */
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
        FileBean bean = dataStorage.getFileBean(filePath);
        bean = bean == null ? new FileBean(filePath,"",new HashSet<>()) : bean;
        addTagToFile(bean,new TagBean(tag));
    }

    public void addTagToFile(FileBean fileBean,String tag){
        addTagToFile(fileBean,new TagBean(tag));
    }

    public void addTagToFile(FileBean fileBean,TagBean tagBean){
        try {
            if(tagBean.getIdSet().size() != 0){
                throw new RuntimeException("添加标签时不可手动设置idList");
            }
            fileBean.getTagSet().add(tagBean.getTag());
            if (dataStorage.hasFile(fileBean.getPath())){
                //当同一个外部new出来的fileBean使用两次时,需要同步id
                fileBean.setId(dataStorage.getFileBean(fileBean.getPath()).getId());
                dataStorage.updateFileRecord(fileBean);
            }else {
                dataStorage.addFileRecord(fileBean);
                //如果是外部new出来的fileBean则将表中的fileBean与其同步
                fileBean.setId(dataStorage.getFileBean(fileBean.getPath()).getId());
            }
            //如果外部对tagSet进行了修改，则可以删除或添加tag_table中的id
            updateIdInExistTag(fileBean.getTagSet(),fileBean.getId());
            addIdInNotExistTag(fileBean);
            if(!dataStorage.hasTag(tagBean.getTag())){
                dataStorage.addTagRecord(tagBean);
            }
            fileBean.setId(fileBean.getId());
            //如果是外部new出来的tagBean则将表中的tagBean与其同步
            tagBean.setIdSet(dataStorage.getTagBean(tagBean.getTag()).getIdSet());
            tagBean.getIdSet().add(fileBean.getId());
            dataStorage.updateTagRecord(tagBean);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateFile(FileBean fileBean){
        if(fileBean == null || fileBean.getId() == null || dataStorage.getFileBean(fileBean.getId()) == null){
            throw new RuntimeException("传入的bean必须为已在表内的bean");
        }
        if(removeFileWhenNoTag(fileBean,0)){
            return;
        }
        try {
            dataStorage.updateFileRecord(fileBean);
            updateIdInExistTag(fileBean.getTagSet(),fileBean.getId());
            addIdInNotExistTag(fileBean);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteTagToFile(String path,String tag){
        deleteTagToFile(dataStorage.getFileBean(path),dataStorage.getTagBean(tag));
    }

    public void deleteTagToFile(FileBean fileBean,TagBean tagBean){
        if(fileBean == null || !dataStorage.hasFile(fileBean.getPath())){
            return;
        }
        if(removeFileWhenNoTag(fileBean,1)){
            return;
        }
        if(tagBean == null || !dataStorage.getAllTags().contains(tagBean.getTag())){
            return;
        }
        if(!fileBean.getTagSet().contains(tagBean.getTag())){
            return;
        }
        fileBean = dataStorage.getFileBean(fileBean.getPath());
        tagBean = dataStorage.getTagBean(tagBean.getTag());
        fileBean.getTagSet().remove(tagBean.getTag());
        tagBean.getIdSet().remove(fileBean.getId());
        try {
            dataStorage.updateFileRecord(fileBean);
            dataStorage.updateTagRecord(tagBean);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HashSet<Integer> getFileBeansId(String tag){
        return dataStorage.getTagBean(tag).getIdSet();
    }

    public HashSet<Integer> getFileBeansId(ArrayList<String> tags){
        TagBean tagBean;
        HashSet<Integer> intersection = null;
        for(String tag:tags){
            if((tagBean = dataStorage.getTagBean(tag)) != null){
                if(intersection == null){
                    intersection = new HashSet<>();
                    intersection.addAll(tagBean.getIdSet());
                }else {
                    intersection.retainAll(tagBean.getIdSet());
                }
            }
        }
        return intersection;
    }

    public ArrayList<FileBean> getFileBeans(ArrayList<String> tags){
        ArrayList<FileBean> beans = new ArrayList<>();
        HashSet<Integer> set = this.getFileBeansId(tags);
        for(int id:set){
            beans.add(dataStorage.getFileBean(id));
        }
        return beans;
    }

    public ArrayList<FileBean> getFileBeans(String tag){
        ArrayList<FileBean> beans = new ArrayList<>();
        TagBean tagBean;
        if((tagBean = dataStorage.getTagBean(tag)) != null){
            for(int id:tagBean.getIdSet()){
                beans.add(dataStorage.getFileBean(id));
            }
        }
        return beans;
    }

    private void updateIdInExistTag(HashSet<String> tagSet,Integer id) throws IOException {
        for(String tag:dataStorage.getAllTags()){
            TagBean tagBean = dataStorage.getTagBean(tag);
            //在tagSet中找到的tag，说明要在这些tag的idSet中增加id
            if(tagSet.contains(tag)){
                if(tagBean.getIdSet().add(id)){
                    dataStorage.updateTagRecord(tagBean);
                }
            //未在tagSet中找到这些tag，说明要在这些tag的idSet中删除id或者表中不存在这些tag
            }else if(tagBean != null && tagBean.getIdSet().contains(id)){
                tagBean.getIdSet().remove(id);
                dataStorage.updateTagRecord(tagBean);
            }
        }
    }

    private void addIdInNotExistTag(FileBean fileBean) throws IOException {
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
    }

    private boolean removeFileWhenNoTag(FileBean bean,int size){
        FileBean cacheBean = dataStorage.getFileBean(bean.getPath());
        if(cacheBean != null && bean.getTagSet().size() == size){
            try {
                for (String tag:dataStorage.getAllTags()){
                    TagBean tagBean = dataStorage.getTagBean(tag);
                    if(tagBean.getIdSet().remove(cacheBean.getId())){
                        dataStorage.updateTagRecord(tagBean);
                    }
                }
                dataStorage.removeFileRecord(cacheBean);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }else {
            return false;
        }
    }
}
