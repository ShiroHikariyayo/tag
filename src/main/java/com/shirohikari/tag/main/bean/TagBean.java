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

import java.util.HashSet;

/**
 * 代表标签的信息
 * @author ShiroHikariyayo
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
        this.idSet = new HashSet<>(idSet);
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        if(tag != null && !tag.isEmpty()){
            this.tag = tag;
        }else {
            throw new RuntimeException("标签不应为null或空字符串");
        }
    }

    public HashSet<Integer> getIdSet() {
        return idSet;
    }

    public void setIdSet(HashSet<Integer> idSet) {
        this.idSet = new HashSet<>(idSet);
    }

    @Override
    public String toString() {
        return "TagBean{" +
                "tag='" + tag + '\'' +
                ", idSet=" + idSet +
                '}';
    }
}
