package com.nenu.mapper;

import com.nenu.pojo.CloudFile;    //这个类要用自己的，千万不要用这个--->import java.io.File;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Indexed;

import java.util.List;

@Mapper
public interface CloudFileMapper {
//    @Select("select * from file where user_id=#{userId}")
//    List<File> getAllFiles(Integer userId);
    @Select("select * from cloudfile where user_id=#{userId}")
    List<CloudFile> getAllFiles(Integer userId);

    //秒传相关：根据fileMd5进行查询，这里不指定用户id，实现秒传只需要查找服务器中是否存在该文件
    @Select("select * from cloudfile where file_md5=#{fileMd5} and user_id=#{userId} limit 1")
    CloudFile existCloudFileByFileMd5(String fileMd5, Integer userId);

    //添加文件信息
    @Insert("insert into cloudfile (user_id, file_name, file_size, file_type, file_ct, file_ut, file_md5, file_path) values (#{userId}, #{fileName},#{fileSize},#{fileType},now(),now(),#{fileMd5},#{filePath})")
    void addFile(Integer userId, String fileName, String fileMd5, String fileType, long fileSize, String filePath);

    //根据用户输入进行模糊查询
    @Select("select * from cloudfile where user_id=#{userId} and file_name like concat('%', #{fileName}, '%')")
    List<CloudFile> findCloudFileByFileName(Integer userId, String fileName);

    List<CloudFile> getFilesByType(Integer userId, List<String> types);

    List<CloudFile> getFilesByNotType(Integer userId, List<String> types);

    //批量删除
    void deleteCloudFileByFileIdList(List<Integer> fileIdList);
}
