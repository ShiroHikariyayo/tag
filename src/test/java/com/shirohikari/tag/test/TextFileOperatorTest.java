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

package com.shirohikari.tag.test;

import com.shirohikari.tag.main.bean.FileBean;
import com.shirohikari.tag.main.interfaces.impl.TextFileOperator;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;

public class TextFileOperatorTest {

    @Test
    public void write() throws IOException {
        TextFileOperator<FileBean> operator = new TextFileOperator(Paths.get("E:\\test\\1.txt"),FileBean.class);
        operator.write(new FileBean(0,"E:\\test","bean1",new HashSet<>()));
        operator.write(new FileBean(1,"E:\\test","bean2",new HashSet<>()));
        operator.write(new FileBean(2,"E:\\test","bean3",new HashSet<>()));
        operator.position(0);
        System.out.println(operator.readNext());
        System.out.println(operator.readNext());
        System.out.println(operator.readNext());
    }

    @Test
    public void write2() throws IOException {
        TextFileOperator<FileBean> operator = new TextFileOperator(Paths.get("E:\\test\\1.txt"),FileBean.class);
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<=100;i++){
            sb.append(System.currentTimeMillis());
        }
        operator.write(new FileBean(1,"E:\\test",sb.toString(),new HashSet<>()));
        operator.write(new FileBean(2,"E:\\test",sb.toString(),new HashSet<>()));
        operator.position(0);
        System.out.println(operator.readNext());
        System.out.println(operator.readNext());
        System.out.println(operator.position());
    }
}
