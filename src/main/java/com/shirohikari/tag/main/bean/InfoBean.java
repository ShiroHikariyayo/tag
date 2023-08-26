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
