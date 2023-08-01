package com.shirohikari.tag.main.bean;

import java.util.ArrayList;

public class TagBean {
    private String tag;
    private ArrayList<Integer> idList;

    public TagBean(String tag) {
        this(tag,new ArrayList<>());
    }

    public TagBean(String tag, ArrayList<Integer> idList) {
        this.tag = tag;
        this.idList = idList;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public ArrayList<Integer> getIdList() {
        return idList;
    }

    public void setIdList(ArrayList<Integer> idList) {
        this.idList = idList;
    }

    @Override
    public String toString() {
        return "TagBean{" +
                "tag='" + tag + '\'' +
                ", idList=" + idList +
                '}';
    }
}
