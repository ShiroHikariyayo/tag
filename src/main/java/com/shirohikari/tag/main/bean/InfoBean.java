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

/**
 * @author ShiroHikariyayo
 */
public class InfoBean {
    private String tagVersion;
    private String fileVersion;
    private ArrayList<String> backups;

    public InfoBean() {
        this(null,null,new ArrayList<>());
    }

    public InfoBean(String tagVersion,String fileVersion, ArrayList<String> backups) {
        this.tagVersion = tagVersion;
        this.fileVersion = fileVersion;
        this.backups = new ArrayList<>(backups);
    }

    public String getTagVersion() {
        return tagVersion;
    }

    public void setTagVersion(String tagVersion) {
        this.tagVersion = tagVersion;
    }

    public String getFileVersion() {
        return fileVersion;
    }

    public void setFileVersion(String fileVersion) {
        this.fileVersion = fileVersion;
    }

    public ArrayList<String> getBackups() {
        return backups;
    }

    public void setBackups(ArrayList<String> backups) {
        this.backups = new ArrayList<>(backups);
    }
}
