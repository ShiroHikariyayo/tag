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

package com.shirohikari.tag.main.datastorage;

import com.shirohikari.tag.main.bean.FileBean;
import com.shirohikari.tag.main.bean.TagBean;

import java.io.IOException;
import java.util.Set;

/**
 * 存储标签和目录信息
 * @author ShiroHikariyayo
 */
public interface IDataStorage {

    /**
     * 对应id的路径是否含有标签
     * @param id
     * @return
     */
    boolean hasFile(int id);

    /**
     * 对应的路径是否含有标签
     * @param path
     * @return
     */
    boolean hasFile(String path);

    /**
     * 是否含有该标签
     * @param tag
     * @return
     */
    boolean hasTag(String tag);

    /**
     * 返回所有标签
     * @return
     */
    Set<String> getAllTags();

    /**
     * 根据指定的id返回FileBean
     * @param id
     * @return
     */
    FileBean getFileBean(int id);

    /**
     * 根据指定的路径返回FileBean
     * @param path
     * @return
     */
    FileBean getFileBean(String path);

    /**
     * 根据指定的标签返回TagBean
     * @param tag
     * @return
     */
    TagBean getTagBean(String tag);

    /**
     * 添加文件路径对应的标签信息
     * @param bean
     * @throws IOException
     */
    void addFileRecord(FileBean bean) throws IOException;

    /**
     * 更新文件路径对应的标签信息
     * @param bean
     * @throws IOException
     */
    void updateFileRecord(FileBean bean) throws IOException;

    /**
     * 删除文件路径对应的标签信息
     * @param bean
     * @throws IOException
     */
    void removeFileRecord(FileBean bean) throws IOException;

    /**
     * 添加标签信息
     * @param bean
     * @throws IOException
     */
    void addTagRecord(TagBean bean) throws IOException;

    /**
     * 更新标签信息
     * @param bean
     * @throws IOException
     */
    void updateTagRecord(TagBean bean) throws IOException;

    /**
     * 删除标签信息
     * @param bean
     * @throws IOException
     */
    void removeTagRecord(TagBean bean) throws IOException;

    /**
     * 备份当前文件信息，并命名为name
     * @param name
     * @throws IOException
     */
    void backup(String name) throws IOException;

    /**
     * 根据指定的name恢复备份
     * @param name
     * @throws IOException
     */
    void recover(String name) throws IOException;

    /**
     * 根据指定的name删除备份
     * @param name
     * @throws IOException
     */
    void removeBackup(String name) throws IOException;
}
