package com.nenu.controller;

import com.nenu.pojo.Folder;
import com.nenu.pojo.Result;
import com.nenu.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("folder")
public class FolderController {
    @Autowired
    private FolderService folderService;

    @GetMapping("/getAllFolder")
    public Result<List<Folder>> getAllFolder() {
        List<Folder> folderList = folderService.getAllFolder();
        System.out.println(folderList);
        return Result.success(folderList);
    }



}
