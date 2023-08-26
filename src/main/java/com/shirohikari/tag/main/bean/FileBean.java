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

package com.shirohikari.tag.main.bean;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * 代表需要添加标签的文件的信息,不应手动修改id
 * @author ShiroHikari
 */
public class FileBean {
    private Integer id;
    private String path;
    private String description;
    private HashSet<String> tagSet;

    private transient ArrayList<String> notStoredPaths;

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
        this.tagSet = new HashSet<>(tagSet);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if(this.id == null || this.id.equals(id)){
            this.id = id;
        }else{
            throw new RuntimeException("禁止手动修改id");
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        if(path != null){
            notStoredPaths = notStoredPaths == null ? new ArrayList<>() : notStoredPaths;
            notStoredPaths.add(this.path);
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
        this.tagSet = new HashSet<>(tagSet);
    }

    public ArrayList<String> getNotStoredPaths() {
        return notStoredPaths;
    }

    @Override
    public String toString() {
        return "FileBean{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", description='" + description + '\'' +
                ", tagSet=" + tagSet +
                ", notStoredPaths=" + notStoredPaths +
                '}';
    }
}
