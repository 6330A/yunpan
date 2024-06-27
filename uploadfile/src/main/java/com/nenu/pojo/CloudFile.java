package com.nenu.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
//只在上传成功后更新
public class CloudFile {
    private Integer fileId;                //文件id
    private Integer userId;                //用户id
    private Integer folderId;              //文件夹id
    private String fileName;               //文件名
    private Long fileSize;                 //文件大小
    private String fileType;               //文件类型
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime fileCt;          //文件创建时间，注解将返回的json字符串固定格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime fileUt;          //文件更新时间，注解将返回的json字符串固定格式
    private String fileMd5;                //文件唯一标识md5
    private String filePath;               //文件位置
}

