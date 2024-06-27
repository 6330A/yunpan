package com.nenu.pojo;
import lombok.Data;

@Data
//只在上传成功后更新
public class FileSet {
    private Integer userId;               //用户id
    private String fileMd5;                //文件唯一标识md5
    private Integer fileCount;               //用户id
    private String filePath;               //文件位置
    private Long fileSize;                 //文件大小
}
