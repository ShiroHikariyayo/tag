/*
 * Copyright (C) 2023 ShiroHikariyayo
 * Copyright 2008 Google Inc.
 * used google/gson,see https://github.com/google/gson
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

import com.google.gson.Gson;
import com.shirohikari.tag.main.bean.FileBean;
import com.shirohikari.tag.main.bean.InfoBean;
import com.shirohikari.tag.main.bean.TagBean;
import com.shirohikari.tag.main.fileoperator.IFileOperator;
import com.shirohikari.tag.main.fileoperator.RafFileOperator;
import com.shirohikari.tag.util.FileUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 将标签和路径信息在本地进行存储，对tag_table和file_table文件进行直接操作
 * @author ShiroHikariyayo
 */
public class LocalDataStorage implements IDataStorage {

    private static final String TAG_TABLE = "tag_table";
    private static final String FILE_TABLE = "file_table";
    private static final String INFO = "info";
    private static final String BACKUP = "backup";

    private int nextId;
    private long tagEndOffset;
    private long fileEndOffset;
    private final Path dir;
    private final Path backup;
    private final Path tagTable;
    private final Path fileTable;
    private final Path info;
    private HashSet<String> tags;
    private LinkedHashMap<Integer,Long> idOffsetMap;
    private HashMap<Integer,FileBean> idFileBeanMap;
    private HashMap<String,Integer> pathIdMap;
    private HashMap<String,FileBean> pathFileBeanMap;
    private HashMap<String, Long> tagOffsetMap;
    private HashMap<String, TagBean> tagTagBeanMap;
    private IFileOperator tagOperator;
    private IFileOperator fileOperator;
    private Gson gson;

    private LocalDataStorage(Path dir, Path backup, Path tagTable, Path fileTable, Path info,IFileOperator tagOperator,IFileOperator fileOperator) throws IOException {
        this.dir = dir;
        this.backup = backup;
        this.tagTable = tagTable;
        this.fileTable = fileTable;
        this.info = info;
        this.tagOperator = tagOperator == null ? new RafFileOperator() : tagOperator;
        this.fileOperator = fileOperator == null ? new RafFileOperator() : fileOperator;
        this.tagOperator.load(tagTable);
        this.fileOperator.load(fileTable);
        init();
    }

    public static LocalDataStorage create(String dirPath) throws IOException {
        return create(dirPath,null,null);
    }

    public static LocalDataStorage create(String dirPath,IFileOperator tagOperator,IFileOperator fileOperator) throws IOException {
        Path dir = Paths.get(dirPath);
        Path backup = Paths.get(dirPath,BACKUP);
        Path tagTable = Paths.get(dirPath,TAG_TABLE);
        Path fileTable = Paths.get(dirPath,FILE_TABLE);
        Path info = Paths.get(dirPath,INFO);
        if(!FileUtil.isEmptyDirectory(dir) && !Files.exists(tagTable)
                && !Files.exists(fileTable) && !Files.exists(info) && !Files.exists(backup)){
            throw new IOException("文件夹不为空");
        }
        FileUtil.makeDirectory(backup);
        FileUtil.makeFile(tagTable);
        FileUtil.makeFile(fileTable);
        FileUtil.makeFile(info);
        return new LocalDataStorage(dir,backup,tagTable,fileTable,info,tagOperator,fileOperator);
    }

    private void init() throws IOException {
        gson = new Gson();
        if(!canRead()){
            throw new IOException("版本不统一");
        }
        tags = new HashSet<>();
        idOffsetMap = new LinkedHashMap<>();
        pathIdMap = new HashMap<>();
        idFileBeanMap = new HashMap<>();
        pathFileBeanMap = new HashMap<>();
        tagOffsetMap = new HashMap<>();
        tagTagBeanMap = new HashMap<>();
        //读取file_table信息
        fileOperator.position(0);
        while (fileOperator.position() < fileOperator.size()){
            long offset = fileOperator.position();
            String json = fileOperator.readNext();
            FileBean bean = gson.fromJson(json,FileBean.class);
            nextId = bean.getId() + 1;
            addToFileMaps(bean,offset);
        }
        System.gc();
        fileEndOffset = fileOperator.size();
        //读取tag_table信息
        tagOperator.position(0);
        while (tagOperator.position() < tagOperator.size()){
            long offset = tagOperator.position();
            String json = tagOperator.readNext();
            TagBean bean = gson.fromJson(json, TagBean.class);
            tags.add(bean.getTag());
            tagOffsetMap.put(bean.getTag(),offset);
            tagTagBeanMap.put(bean.getTag(),bean);
        }
        System.gc();
        tagEndOffset = tagOperator.size();
    }

    @Override
    public boolean hasFile(int id){
        return idFileBeanMap.containsKey(id);
    }

    @Override
    public boolean hasFile(String path){
        return pathFileBeanMap.containsKey(path);
    }

    @Override
    public boolean hasTag(String tag){
        return tags.contains(tag);
    }

    @Override
    public Set<String> getAllTags(){
        return Collections.unmodifiableSet(tags);
    }

    @Override
    public FileBean getFileBean(int id){
        return idFileBeanMap.get(id);
    }

    @Override
    public FileBean getFileBean(String path) {
        return pathFileBeanMap.get(path);
    }

    @Override
    public TagBean getTagBean(String tag){
        return tagTagBeanMap.get(tag);
    }

    @Override
    public void addFileRecord(FileBean bean) throws IOException {
        checkFileBean(bean,Operate.ADD);
        bean.setId(nextId++);
        addToFileMaps(bean,fileEndOffset);
        String json = gson.toJson(bean);
        fileOperator.position(fileEndOffset);
        fileOperator.write(json);
        fileEndOffset += json.getBytes().length + fileOperator.messageDefineLength();
    }

    @Override
    public void updateFileRecord(FileBean bean) throws IOException {
        checkFileBean(bean,Operate.UPDATE);
        long offset = idOffsetMap.get(bean.getId());
        fileOperator.position(offset);
        String oldJson = fileOperator.readNext();
        idFileBeanMap.replace(bean.getId(),bean);
        //可能会修改path
        if(bean.getNotStoredPaths() == null){
            pathFileBeanMap.replace(bean.getPath(),bean);
        }else {
            String oldPath = bean.getNotStoredPaths().get(0);
            bean.getNotStoredPaths().removeAll(bean.getNotStoredPaths());
            pathIdMap.remove(oldPath);
            pathFileBeanMap.remove(oldPath);
            pathIdMap.put(bean.getPath(),bean.getId());
            pathFileBeanMap.put(bean.getPath(),bean);
        }
        String newJson = gson.toJson(bean);
        updateOffset(oldJson,newJson,offset,true,fileOperator.messageDefineLength());
        insertOrRemoveFileRecord(oldJson,newJson,offset);
    }

    @Override
    public void removeFileRecord(FileBean bean) throws IOException {
        checkFileBean(bean,Operate.REMOVE);
        long offset = idOffsetMap.get(bean.getId());
        fileOperator.position(offset);
        String oldJson = fileOperator.readNext();
        idOffsetMap.remove(bean.getId());
        idFileBeanMap.remove(bean.getId());
        pathIdMap.remove(bean.getPath());
        pathFileBeanMap.remove(bean.getPath());
        updateOffset(oldJson,null,offset,true,fileOperator.messageDefineLength());
        insertOrRemoveFileRecord(oldJson,null,offset);
    }

    @Override
    public void addTagRecord(TagBean bean) throws IOException {
        checkTagBean(bean,Operate.ADD);
        tags.add(bean.getTag());
        tagOffsetMap.put(bean.getTag(),tagEndOffset);
        tagTagBeanMap.put(bean.getTag(),bean);
        String json = gson.toJson(bean);
        tagOperator.position(tagEndOffset);
        tagOperator.write(json);
        tagEndOffset += json.getBytes().length + tagOperator.messageDefineLength();
    }

    @Override
    public void updateTagRecord(TagBean bean) throws IOException {
        checkTagBean(bean,Operate.UPDATE);
        long offset = tagOffsetMap.get(bean.getTag());
        tagOperator.position(offset);
        String oldJson = tagOperator.readNext();
        tagTagBeanMap.replace(bean.getTag(),bean);
        String newJson = gson.toJson(bean);
        updateOffset(oldJson,newJson,offset,false,tagOperator.messageDefineLength());
        insertOrRemoveTagRecord(oldJson,newJson,offset);
    }

    @Override
    public void removeTagRecord(TagBean bean) throws IOException {
        checkTagBean(bean,Operate.REMOVE);
        long offset = tagOffsetMap.get(bean.getTag());
        tagOperator.position(offset);
        String oldJson = tagOperator.readNext();
        tags.remove(bean.getTag());
        tagOffsetMap.remove(bean.getTag());
        tagTagBeanMap.remove(bean.getTag());
        updateOffset(oldJson,null,offset,false,tagOperator.messageDefineLength());
        insertOrRemoveTagRecord(oldJson,null,offset);
    }

    @Override
    public void backup(String name) throws IOException {
        byte[] bytes = Files.readAllBytes(info);
        InfoBean infoBean = gson.fromJson(new String(bytes),InfoBean.class);
        if(infoBean.getBackups().contains(name)){
            throw new IOException("备份名称已被占用");
        }
        Path saveFolder = Paths.get(backup.toString(),name);
        FileUtil.makeDirectory(saveFolder);
        Path backupTagTable = Paths.get(saveFolder.toString(),TAG_TABLE);
        Path backupFileTable = Paths.get(saveFolder.toString(),FILE_TABLE);
        FileUtil.makeFile(tagTable);
        FileUtil.makeFile(fileTable);
        FileUtil.copyFile(tagTable,backupTagTable);
        FileUtil.copyFile(fileTable,backupFileTable);
        infoBean.getBackups().add(name);
        FileUtil.saveFile(gson.toJson(infoBean).getBytes(),INFO,info.getParent().toString());
    }

    @Override
    public void recover(String name) throws IOException {
        byte[] bytes = Files.readAllBytes(info);
        InfoBean infoBean = gson.fromJson(new String(bytes),InfoBean.class);
        if(!infoBean.getBackups().contains(name)){
            throw new IOException("未找到该备份");
        }
        tagOperator.force();
        fileOperator.force();
        tagOperator.close();
        fileOperator.close();
        Path folder = Paths.get(backup.toString(),name);
        Path backupTagTable = Paths.get(folder.toString(),TAG_TABLE);
        Path backupFileTable = Paths.get(folder.toString(),FILE_TABLE);
        FileUtil.copyFile(backupTagTable,tagTable);
        FileUtil.copyFile(backupFileTable,fileTable);
        tagOperator.load(tagTable);
        fileOperator.load(fileTable);
        init();
    }

    @Override
    public void removeBackup(String name) throws IOException {
        byte[] bytes = Files.readAllBytes(info);
        InfoBean infoBean = gson.fromJson(new String(bytes),InfoBean.class);
        FileUtil.remove(Paths.get(backup.toString(),name));
        infoBean.getBackups().remove(name);
        FileUtil.saveFile(gson.toJson(infoBean).getBytes(),INFO,info.getParent().toString());
    }

    private void updateOffset(String oldJson,String newJson,long offset,boolean file,int messageDefineLength) {
        long len;
        if(newJson != null){
            len = newJson.getBytes().length - oldJson.getBytes().length;
        }else {
            len = -oldJson.getBytes().length - messageDefineLength;
        }
        Iterator iter;
        if(file){
            iter = idOffsetMap.entrySet().iterator();
        }else{
            iter = tagOffsetMap.entrySet().iterator();
        }
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            if((long)entry.getValue() <= offset){
                continue;
            }
            entry.setValue((long)entry.getValue() + len);
        }
    }

    /**
     * 更新或删除需要改变的FileBean记录
     * @param oldJson 需要进行更改的记录
     * @param newJson 新的记录，如果为null则删除记录
     * @param offset 旧记录在file_table中的偏移量
     * @throws IOException
     */
    private void insertOrRemoveFileRecord(String oldJson,String newJson,long offset) throws IOException {
        Path tmp=Files.createTempFile(dir,"file_tmp", null);
        FileUtil.saveAfterToTemp(fileOperator,fileOperator.position(),fileEndOffset,tmp);
        fileOperator.position(offset);
        if(newJson != null){
            fileOperator.write(newJson);
            fileEndOffset = fileEndOffset - oldJson.getBytes().length + newJson.getBytes().length;
        }else {
            fileEndOffset = fileEndOffset - oldJson.getBytes().length - fileOperator.messageDefineLength();
        }
        FileUtil.readAndCover(fileOperator,fileOperator.position(),fileEndOffset,tmp);
        Files.delete(tmp);
    }

    private void insertOrRemoveTagRecord(String oldJson,String newJson,long offset) throws IOException {
        Path tmp=Files.createTempFile(dir,"tag_tmp", null);
        FileUtil.saveAfterToTemp(tagOperator,tagOperator.position(),tagEndOffset,tmp);
        tagOperator.position(offset);
        if(newJson != null){
            tagOperator.write(newJson);
            tagEndOffset = tagEndOffset - oldJson.getBytes().length + newJson.getBytes().length;
        }else {
            tagEndOffset = tagEndOffset - oldJson.getBytes().length - tagOperator.messageDefineLength();
        }
        FileUtil.readAndCover(tagOperator,tagOperator.position(),tagEndOffset,tmp);
        Files.delete(tmp);
    }

    private void checkFileBean(FileBean bean,int operate) throws IOException {
        if(bean == null){
            throw new IOException("FileBean为null");
        }else if(bean.getPath() == null){
            throw new IOException("文件路径不应为null");
        }
        if(operate == Operate.ADD){
            if(bean.getTagSet() == null || bean.getTagSet().isEmpty()){
                throw new IOException("FileBean为必须至少含有一个tag");
            }else if(bean.getId() != null){
                throw new IOException("插入时不允许手动设置FileBean的id");
            }
            return;
        }
        if(bean.getId() == null){
            throw new IOException("需要指定FileBean及其id");
        }else if(!idFileBeanMap.containsKey(bean.getId())){
            throw new IOException("未发现相应记录");
        }
        //找不到id时则更改了path
        Integer id = pathIdMap.get(bean.getPath());
        if(operate == Operate.UPDATE){
            if(id != null && !bean.getId().equals(id)){
                throw new IOException("禁止修改id或修改path为已存在的path");
            }
        }else if(operate == Operate.REMOVE){
            if(!bean.getId().equals(id)){
                throw new IOException("禁止修改id或path");
            }
        }
    }

    private void checkTagBean(TagBean bean,int operate) throws IOException {
        if(bean == null){
            throw new IOException("TagBean为null");
        }
        if(operate == Operate.ADD){
            if(tagTagBeanMap.containsKey(bean.getTag())){
                throw new IOException("不可添加已有的tag");
            }
            if(!bean.getIdSet().isEmpty()){
                throw new IOException("创建新标签时不可手动指定idList");
            }
        }else{
            if(!tagTagBeanMap.containsKey(bean.getTag())){
                throw new IOException("未发现指定tag");
            }
        }
    }

    private void addToFileMaps(FileBean bean,long offset){
        idOffsetMap.put(bean.getId(),offset);
        pathIdMap.put(bean.getPath(),bean.getId());
        idFileBeanMap.put(bean.getId(),bean);
        pathFileBeanMap.put(bean.getPath(),bean);
    }

    private boolean canRead() throws IOException {
        byte[] bytes = Files.readAllBytes(info);
        String json = new String(bytes);
        if("".equals(json)){
            InfoBean infoBean = new InfoBean();
            infoBean.setTagVersion(tagOperator.version());
            infoBean.setFileVersion(fileOperator.version());
            FileUtil.saveFile(gson.toJson(infoBean).getBytes(),INFO,info.getParent().toString());
            return true;
        }else {
            InfoBean infoBean = gson.fromJson(json,InfoBean.class);
            return tagOperator.version().equals(infoBean.getTagVersion()) && fileOperator.version().equals(infoBean.getFileVersion());
        }
    }

    private static final class Operate{
        private static final int ADD = 0;
        private static final int UPDATE = 1;
        private static final int REMOVE = 2;
    }
}
