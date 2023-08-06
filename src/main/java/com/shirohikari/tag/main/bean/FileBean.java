package com.shirohikari.tag.main.bean;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * 代表需要添加标签的文件的信息
 * @author ShiroHikari
 */
public class FileBean {
    private Integer id;
    private String path;
    private String description;
    private HashSet<String> tagSet;

    private transient ArrayList<String> oldPaths;

    public FileBean(){
        this(null,"");
    }

    public FileBean(String path, String description) {
        this(path,description,new HashSet<>());
    }

    public FileBean(String path, String description, HashSet<String> tagSet) {
        this(null,path,description,tagSet);
    }

    public FileBean(Integer id, String path, String description, HashSet<String> tagSet) {
        this.id = id;
        this.path = path;
        this.description = description;
        this.tagSet = tagSet;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        if(path != null){
            oldPaths = oldPaths == null ? new ArrayList<>() : oldPaths;
            oldPaths.add(this.path);
        }
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashSet<String> getTagSet() {
        return tagSet;
    }

    public void setTagSet(HashSet<String> tagSet) {
        this.tagSet = tagSet;
    }

    public ArrayList<String> getOldPaths() {
        return oldPaths;
    }

    @Override
    public String toString() {
        return "FileBean{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", description='" + description + '\'' +
                ", tagSet=" + tagSet +
                ", oldPaths=" + oldPaths +
                '}';
    }
}
