package com.shirohikari.tag.main.bean;

import java.util.HashSet;

public class FileBean {
    private Integer id;
    private String path;
    private String description;
    private HashSet<String> tagSet;

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

    @Override
    public String toString() {
        return "FileBean{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", description='" + description + '\'' +
                ", tagSet=" + tagSet +
                '}';
    }
}
