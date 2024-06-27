package com.nenu.service;

import com.nenu.pojo.FileSet;

import java.util.List;

public interface FileSetService {
    //获取未被引用的文件列表
    List<FileSet> getZeroReferenced();

    //删除未被引用的文件列表
    void deleteZeroReferenced();
}
