package com.shirohikari.tag.main.bean;

import java.util.ArrayList;

public class FileBean {
    private Integer id;
    private String path;
    private String description;
    private ArrayList<String> tagList;

    public FileBean(String path, String description, ArrayList<String> tagList) {
        this(null,path,description,tagList);
    }

    public FileBean(Integer id, String path, String description, ArrayList<String> tagList) {
        this.id = id;
        this.path = path;
        this.description = description;
        this.tagList = tagList;
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
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getTagList() {
        return tagList;
    }

    public void setTagList(ArrayList<String> tagList) {
        this.tagList = tagList;
    }

    @Override
    public String toString() {
        return "FileBean{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", description='" + description + '\'' +
                ", tagList=" + tagList +
                '}';
    }
}
