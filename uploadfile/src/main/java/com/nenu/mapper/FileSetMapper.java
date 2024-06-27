package com.nenu.mapper;

import com.nenu.pojo.FileSet;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FileSetMapper {
    //查询未被引用的文件信息
    @Select("select * from fileset where user_id=#{userId} and file_count = 0")
    List<FileSet> getZeroReferenced(Integer userId);

    //上面查询完成后，删除未被引用的文件信息
    @Delete("delete from fileset where user_id=#{userId} and file_count = 0")
    void deleteZeroReferenced(Integer userId);
}
