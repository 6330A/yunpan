package com.nenu.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Folder {
    private Integer folderId;              //文件夹id
    private Integer userId;                //用户id
    private String folderName;             //文件夹名称
    private LocalDateTime folderCt;        //文件夹创建时间
    private LocalDateTime folderUt;        //文件夹更新时间
    private Long folderSize;               //文件夹大小
}
