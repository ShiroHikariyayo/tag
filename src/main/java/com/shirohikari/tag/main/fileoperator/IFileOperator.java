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

package com.shirohikari.tag.main.fileoperator;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

/**
 * 定义如何tag_table和file_table文件中的信息与TagBean和FileBean对应的json进行转换，
 * 并进行存储和读取，一条记录应在文件中连续
 * @author ShiroHikariyayo
 */
public interface IFileOperator {

    /**
     * 当要操作其他文件时,重新加载文件
     * @param path
     * @return
     */
    void reload(Path path) throws IOException;

    /**
     * 读取下一个数据
     * @return 下一个数据
     * @throws IOException
     */
    String readNext() throws IOException;

    /**
     * 写入下一个数据
     * @param json 下一个数据
     * @throws IOException
     */
    void write(String json) throws IOException;

    /**
     * 返回当前位置
     * @return 当前位置
     * @throws IOException
     */
    long position() throws IOException;

    /**
     * 移动到指定位置读取
     * @param position 读取位置
     * @throws IOException
     */
    void position(long position) throws IOException;

    /**
     * 定义储存一条数据，除了数据本身所需的字节数以外的字节数
     * @return 额外字节数
     */
    int messageDefineLength();

    /**
     * 文件对应的FileChannel
     * @return 文件对应的FileChannel
     * @throws IOException
     */
    FileChannel getFileChannel() throws IOException;

    /**
     * 设置文件大小
     * @param length
     */
    void setLength(long length) throws IOException;

    /**
     * 文件最大位置
     * @return
     * @throws IOException
     */
    long size() throws IOException;

    /**
     * 版本号
     * @return
     */
    String version();
}
