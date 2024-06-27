package com.nenu.controller;

import com.nenu.pojo.CloudFile;
import com.nenu.pojo.FileSet;
import com.nenu.pojo.Result;
import com.nenu.service.CloudFileService;
import com.nenu.service.FileSetService;
import com.nenu.service.UserService;
import com.nenu.utils.FileTypeList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;


@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    private CloudFileService cloudFileService;

    @Autowired
    private FileSetService fileSetService;

    @Autowired
    private UserService userService;

    @Value("${slice.saveDir}")
    private String saveDir;

    //获取用户所有的文件，id在service层通过threadLocal传递
    @GetMapping("/getAllFiles")
    public Result<List<CloudFile>> getAllFiles() {
        List<CloudFile> cloudFileList = cloudFileService.getAllFiles();
//        System.out.println(cloudFileList);
        return Result.success(cloudFileList);
    }

    @GetMapping("/getFilesByType")
    public Result<List<CloudFile>> getFilesByType(String fileType) {
        List<String> types= FileTypeList.get(fileType);

        if("Else".equals(fileType)){
            return Result.success(cloudFileService.getFilesByNotType(types));
        }else {
            return Result.success(cloudFileService.getFilesByType(types));
        }
    }

    //根据前端传递的文件fileMd5（以及userId）查询 cloudfile表中是否有该文件
    @GetMapping("/existCloudFileByFileMd5")
    public Result<Boolean> existCloudFileByFileMd5(String fileMd5) {
        CloudFile file = cloudFileService.existCloudFileByFileMd5(fileMd5);
//        System.out.println(file);

        return Result.success(file != null);
    }


    //如果前端查询fileMd5文件返回的true,实现秒传,直接添加记录
    //文件名还是需要的，可能同一份文件fileMd5相同但是文件名不一样，这一情况暂时不考虑吧，为了方便开发，假设每个文件名都一样且唯一
    @PostMapping("/secondsUpload")
    public Result secondsUpload(String fileMd5, String fileName) {
        CloudFile tempFile = cloudFileService.existCloudFileByFileMd5(fileMd5);
        //根据数据库中的文件复制一份添加到表中
        cloudFileService.addFile(fileName, fileMd5, tempFile.getFileType(), tempFile.getFileSize(), tempFile.getFilePath());
        return Result.success();
    }


    //用户查找文件，这里要求可以模糊查找，且需要校验一下参数是否合法
    @GetMapping("/findCloudFileByFileName")
    public Result<List<CloudFile>> findCloudFileByFileName(String fileName) {
        //模糊查询control层校验参数，mapper模糊查询
        List<CloudFile> file = cloudFileService.findCloudFileByFileName(fileName);
        return Result.success(file);
    }

    /**
     * 根据文件id进行删除,除了删除数据库中的记录，暂时先不对磁盘进行物理删除，真实情况需要查询文件的引用情况等
     * 通常情况下，为了确保数据的完整性和安全性，建议后端根据前端传递的文件主键ID进行查询，获取文件地址，然后再根据文件地址执行删除操作。这样做有以下几个优点：
     * 确保数据一致性： 通过根据主键ID查询文件地址，可以确保后端删除的是客户端实际请求删除的文件，而不是通过传递的地址删除了其他文件。
     * 减少潜在的安全风险： 直接使用客户端传递的文件地址进行删除操作存在安全风险，因为客户端可以伪造文件地址，试图删除系统中的其他文件，通过查询主键ID可以减少这种风险。
     * 提高代码的可维护性： 将删除操作的文件获取和删除逻辑分开，使代码更加清晰和易于维护。如果未来需要修改获取文件地址的逻辑，只需修改查询代码，而不需要修改删除代码。
     * 综上所述，建议后端根据文件的主键ID进行查询，获取文件地址后再执行删除操作，以确保数据的完整性和安全性。
     * @param fileId
     * @return
     */

    //这里批量删除有一些坑，就按这个写法接收数组吧
    @PostMapping("/deleteCloudFileByFileIdList")
    public Result deleteCloudFileByFileIdList(@RequestBody List<Integer> fileIdList) {
        cloudFileService.deleteCloudFileByFileIdList(fileIdList);

        //这里FileSet是数据库中触发器实现统计的，在插入删除cloudfile表时自动更新
        List<FileSet> fileZeroReferenced = fileSetService.getZeroReferenced();
        fileSetService.deleteZeroReferenced();
        for(FileSet fileSet : fileZeroReferenced){
            File tempFile = new File(fileSet.getFilePath());
            long recoverSpace = 0;
            if(tempFile.exists()){
                recoverSpace = -tempFile.length();
                tempFile.delete();
                System.out.println("删除成功");

                //恢复空间
                userService.updateUsedSpace(recoverSpace);
            }
        }
        return Result.success();
    }
}