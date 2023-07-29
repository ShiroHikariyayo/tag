package com.shirohikari.tag.main.bean;

import java.util.ArrayList;

public class TagBean {
    private String tag;
    private ArrayList<Long> offsetList;

    public TagBean(String tag, ArrayList<Long> offsetList) {
        this.tag = tag;
        this.offsetList = offsetList;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public ArrayList<Long> getOffsetList() {
        return offsetList;
    }

    public void setOffsetList(ArrayList<Long> offsetList) {
        this.offsetList = offsetList;
    }

    @Override
    public String toString() {
        return "TagBean{" +
                "tag='" + tag + '\'' +
                ", offsetList=" + offsetList +
                '}';
    }
}
