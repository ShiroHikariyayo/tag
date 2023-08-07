package com.shirohikari.tag.main.bean;

import java.util.ArrayList;

/**
 * @author ShiroHikari
 */
public class InfoBean {
    private Integer tableVersion;
    private ArrayList<String> backups;

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
        this.backups = backups;
    }
}
