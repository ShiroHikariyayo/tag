package com.shirohikari.tag.main.bean;

import java.util.ArrayList;

public class FileBean {
    private Long offset;
    private String path;
    private String description;
    private ArrayList<String> tagList;

    public FileBean(Long offset, String path, String description, ArrayList<String> tagList) {
        this.offset = offset;
        this.path = path;
        this.description = description;
        this.tagList = tagList;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
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
                "offset=" + offset +
                ", path='" + path + '\'' +
                ", description='" + description + '\'' +
                ", tagList=" + tagList +
                '}';
    }
}
