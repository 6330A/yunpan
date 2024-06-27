package com.nenu.controller;

import com.nenu.pojo.Result;
import com.nenu.service.ChunkService;
import com.nenu.service.CloudFileService;
import com.nenu.service.UserService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;



@RestController
@RequestMapping("/chunk")
public class ChunkController {
    @Value("${slice.saveDir}")
    private String saveDir;

    @Autowired
    private ChunkService chunkService;

    @Autowired
    private CloudFileService cloudFileService;

    @Autowired
    private UserService userService;

    /**
     *
     * @param chunk                 分片数据
     * @param fileName              文件名称
     * @param fileMd5               文件指纹，唯一标识，hash值，md5前端生成
     * @param StrcurrentIndex       分片下标
     * @param StrchunkCount         每个分片大小
     * @return
     */
    @PostMapping("/uploadChunks")
    public Result uploadChunks(MultipartFile chunk, String fileMd5, String StrcurrentIndex, String StrchunkSize ,String StrchunkCount, String fileName) {
        Integer currentIndex = Integer.valueOf(StrcurrentIndex);
        Integer chunkSize = Integer.valueOf(StrchunkSize);
        Integer chunkCount = Integer.valueOf(StrchunkCount);

        String tempFilePath = saveDir + File.separator + fileMd5 + ".tmp";
        long offset = (long)currentIndex * chunkSize;

        File file = new File(saveDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        try (RandomAccessFile accessFile = new RandomAccessFile(tempFilePath, "rw")) {
            accessFile.seek(offset);
            accessFile.write(chunk.getBytes());

            chunkService.uploadChunks(currentIndex, chunkCount, fileMd5);
            //每个分片上传成功都更新用户空间大小
            userService.updateUsedSpace(chunk.getSize());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Result.success();
    }

    @PostMapping("/mergeChunks")
    public Result mergeChunks(String fileMd5, String fileName){
        System.out.println("---------------------------------------ChunkController.mergeChunks");
        //合并请求，往将文件改名为filename即可，接收fileMd5 和 fileName
        String tempFilePath = saveDir + File.separator + fileMd5 + ".tmp";
        String finalFilePath = saveDir + File.separator + fileName;

        File tempFile = new File(tempFilePath);
        File finallyFile = new File(finalFilePath);

        long fileSize = tempFile.length();
        System.out.println(fileSize+" Bytes");

        String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
        System.out.println(fileType);

        tempFile.renameTo(finallyFile);
        tempFile.delete();
        System.out.println(finallyFile + "  完成上传!");

        //将文件名添加到file的表中
        cloudFileService.addFile(fileName, fileMd5, fileType, fileSize, finalFilePath);

        //删除数据库中的分片记录
        chunkService.deleteChunksByFileMd5(fileMd5);
        return Result.success();
    }

    //断点续传，先获取已经上传文件的下标
    @GetMapping("/getUploadedChunksIdx")
    public Result<List<Integer>> getUploadedChunksIdx(String fileMd5){
        System.out.println("---------------------------------------ChunkController.getUploadedChunksIdx");
        System.out.println("查找："+fileMd5);
        List<Integer> idxList = chunkService.getUploadedChunksIdx(fileMd5);
        System.out.println(idxList);
        return Result.success(idxList);
    }

    //未完成文件的清理，包括以及上传的和数据库中的分片
    @DeleteMapping("/clearUncompletedFile")
    public Result clearUncompletedFile(String fileMd5){
        String tempFilePath = saveDir + File.separator + fileMd5 + ".tmp";
        long recoverSpace = 0;

        File tempFile = new File(tempFilePath);
        if(tempFile.exists()){
            recoverSpace = -tempFile.length();
            tempFile.delete();
            System.out.println("删除成功");

            //恢复空间
            userService.updateUsedSpace(recoverSpace);
        }


        System.out.println("recoverSpace =" + recoverSpace);
        chunkService.deleteChunksByFileMd5(fileMd5);
        return Result.success();
    }
}
