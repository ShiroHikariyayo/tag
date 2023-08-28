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

package com.shirohikari.tag.main.tableconverter;


import java.io.IOException;
import java.nio.file.Path;

/**
 * 将使用不同版本的IFileOperator构建出来的tag_table和file_table进行转换
 * @author ShiroHikariyayo
 */
public interface ITableConverter {

    /**
     * 将原有的table转换成新的table,数据应存储在tagTable和fileTable路径
     * @param tagVersion 现存的tag_table使用的IFileOperator版本
     * @param fileVersion 现存的file_table使用的IFileOperator版本
     * @param tagTable 现存的tag_table的路径,包含所有存储的数据,应该将新的tag_table存储至该路径
     * @param fileTable 现存的file_table的路径,包含所有存储的数据,应该将新的file_table存储至该路径
     * @return true如果转换成功
     * @throws IOException
     */
    boolean convert(String tagVersion, String fileVersion, Path tagTable,Path fileTable) throws IOException;
}
