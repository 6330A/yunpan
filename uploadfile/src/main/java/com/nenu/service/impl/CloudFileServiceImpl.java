package com.nenu.service.impl;

import com.nenu.mapper.CloudFileMapper;
import com.nenu.pojo.CloudFile;
import com.nenu.service.CloudFileService;
import com.nenu.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CloudFileServiceImpl implements CloudFileService {
    @Autowired
    private CloudFileMapper cloudFileMapper;

    @Override
    public List<CloudFile> getAllFiles() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = Integer.valueOf(claims.get("userId").toString());
        
        return cloudFileMapper.getAllFiles(userId);
    }



    //上传文件之前根据fileMd5查找是否已经存在该文件，实现秒传
    @Override
    public CloudFile existCloudFileByFileMd5(String fileMd5) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = Integer.valueOf(claims.get("userId").toString());

        return cloudFileMapper.existCloudFileByFileMd5(fileMd5, userId);
    }

    //在分片上传完之后合并文件（这里我只是改名，因为都写入同一个文件中了），然后添加文件信息到数据库
    @Override
    public void addFile(String fileName, String fileMd5, String fileType, long fileSize, String filePath) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = Integer.valueOf(claims.get("userId").toString());

        cloudFileMapper.addFile(userId, fileName, fileMd5, fileType, fileSize, filePath);
    }

    //用户使用的查询功能，要模糊查询
    @Override
    public List<CloudFile> findCloudFileByFileName(String fileName) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = Integer.valueOf(claims.get("userId").toString());

        return cloudFileMapper.findCloudFileByFileName(userId, fileName);
    }

    //删除文件操作，不实现磁盘的物理删除
    @Override
    public void deleteCloudFileByFileIdList(List<Integer> fileIdList) {
        cloudFileMapper.deleteCloudFileByFileIdList(fileIdList);
    }

    @Override
    public List<CloudFile> getFilesByType(List<String> types) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = Integer.valueOf(claims.get("userId").toString());

        return cloudFileMapper.getFilesByType(userId, types);
    }

    @Override
    public List<CloudFile> getFilesByNotType(List<String> types) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = Integer.valueOf(claims.get("userId").toString());
        return cloudFileMapper.getFilesByNotType(userId, types);
    }
}
