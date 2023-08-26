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

package com.shirohikari.tag.main.interfaces;

import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * 定义如何tag_table和file_table文件中的信息与TagBean和FileBean对应的json进行转换，
 * 并进行存储和读取，一条记录应在文件中连续
 * @author ShiroHikariyayo
 */
public interface FileOperator {

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
     */
    long position() throws IOException;

    /**
     * 移动到指定位置读取
     * @param position 读取位置
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
     */
    FileChannel getFileChannel() throws IOException;

    /**
     * 文件对应的FileChannel
     * @param position FileChannel的读取位置
     * @return 文件对应的FileChannel
     */
    FileChannel getFileChannel(long position) throws IOException;

    /**
     * 文件最大位置
     * @return
     */
    long size() throws IOException;
}
