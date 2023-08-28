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

import com.shirohikari.tag.main.fileoperator.IFileOperator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 当原table使用的IFileOperator的version为全类名时，可以自动转换为新的table
 * @author ShiroHikariyayo
 */
public class DefaultTableConverter implements ITableConverter {

    private final IFileOperator tagOperator;
    private final IFileOperator fileOperator;

    public DefaultTableConverter(IFileOperator tagOperator,IFileOperator fileOperator){
        this.tagOperator = tagOperator;
        this.fileOperator = fileOperator;
    }

    @Override
    public boolean convert(String tagVersion, String fileVersion, Path tagTable, Path fileTable) throws IOException {
        try {
            Class<?> tagClazz = Class.forName(tagVersion);
            Class<?> fileClazz = Class.forName(fileVersion);
            IFileOperator srcTagOperator = (IFileOperator) tagClazz.getConstructor().newInstance();
            IFileOperator srcFileOperator = (IFileOperator) fileClazz.getConstructor().newInstance();
            Path tempTagTable = Files.createFile(Paths.get(tagTable.getParent().toString(),"tag_table_temp"));
            Path tempFileTable = Files.createFile(Paths.get(fileTable.getParent().toString(),"file_table_temp"));
            srcTagOperator.load(tagTable);
            srcFileOperator.load(fileTable);
            tagOperator.load(tempTagTable);
            fileOperator.load(tempFileTable);
            String tagJson;
            String fileJson;
            while (true){
                tagJson = srcTagOperator.readNext();
                srcTagOperator.position(srcTagOperator.position());
                if(tagJson == null){
                    break;
                }
                tagOperator.write(tagJson);
                tagOperator.position(tagOperator.position());
            }
            while (true){
                fileJson = srcFileOperator.readNext();
                srcFileOperator.position(srcFileOperator.position());
                if(fileJson == null){
                    break;
                }
                fileOperator.write(fileJson);
                fileOperator.position(fileOperator.position());
            }
            srcTagOperator.close();
            srcFileOperator.close();
            tagOperator.close();
            fileOperator.close();
            Files.delete(tagTable);
            Files.delete(fileTable);
            Files.move(tempTagTable,tagTable);
            Files.move(tempFileTable,fileTable);
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException("table转换失败",e);
        }
        return true;
    }
}
