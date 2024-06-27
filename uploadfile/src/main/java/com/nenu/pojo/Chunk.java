package com.nenu.pojo;

import lombok.Data;

@Data
public class Chunk {
    private Integer chunkId;        //分片id
    private Integer userId;         //用户id
    private Long chunkIndex;        //分片下标
    private String chunkMd5;        //分片md5
    private String fileMd5;         //文件md5
    private String fileName;        //文件名
}
