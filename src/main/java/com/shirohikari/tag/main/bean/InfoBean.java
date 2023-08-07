package com.shirohikari.tag.main.bean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author ShiroHikari
 */
public class InfoBean {
    private Integer tableVersion;
    private ArrayList<String> backups;

    public InfoBean() {
        this(null,new ArrayList<>());
    }

    public InfoBean(Integer tableVersion, ArrayList<String> backups) {
        this.tableVersion = tableVersion;
        this.backups = new ArrayList<>(backups);
    }

    public Integer getTableVersion() {
        return tableVersion;
    }

    public void setTableVersion(Integer tableVersion) {
        this.tableVersion = tableVersion;
    }

    public ArrayList<String> getBackups() {
        return backups;
    }

    public void setBackups(ArrayList<String> backups) {
        this.backups = new ArrayList<>(backups);
    }
}
