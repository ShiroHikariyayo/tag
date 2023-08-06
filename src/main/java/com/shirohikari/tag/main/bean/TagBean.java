package com.shirohikari.tag.main.bean;

import java.util.HashSet;

/**
 * 代表标签的信息
 * @author ShiroHikari
 */
public class TagBean {
    private String tag;
    private HashSet<Integer> idSet;

    public TagBean(){
        this("");
    }

    public TagBean(String tag) {
        this(tag,new HashSet<>());
    }

    public TagBean(String tag, HashSet<Integer> idSet) {
        this.tag = tag;
        this.idSet = idSet;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public HashSet<Integer> getIdSet() {
        return idSet;
    }

    public void setIdSet(HashSet<Integer> idSet) {
        this.idSet = idSet;
    }

    @Override
    public String toString() {
        return "TagBean{" +
                "tag='" + tag + '\'' +
                ", idSet=" + idSet +
                '}';
    }
}
