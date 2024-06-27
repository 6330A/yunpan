package com.nenu.service;

import com.nenu.pojo.CloudFile;

import java.util.List;

public interface CloudFileService {
    List<CloudFile> getAllFiles();

    CloudFile existCloudFileByFileMd5(String fileMd5);

    void addFile(String fileName, String fileMd5, String fileType, long fileSize, String filePath);

    List<CloudFile> findCloudFileByFileName(String fileName);

    void deleteCloudFileByFileIdList(List<Integer> fileIdList);

    List<CloudFile> getFilesByType(List<String> types);

    List<CloudFile> getFilesByNotType(List<String> types);
}
