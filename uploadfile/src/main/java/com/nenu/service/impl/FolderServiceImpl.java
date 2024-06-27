package com.nenu.service.impl;

import com.nenu.mapper.FolderMapper;
import com.nenu.pojo.Folder;
import com.nenu.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FolderServiceImpl implements FolderService {
    @Autowired
    private FolderMapper folderMapper;

    @Override
    public List<Folder> getAllFolder() {
        return folderMapper.getAllFolder();
    }
}
