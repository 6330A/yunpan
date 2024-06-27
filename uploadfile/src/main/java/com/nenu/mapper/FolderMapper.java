package com.nenu.mapper;

import com.nenu.pojo.Folder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FolderMapper {
    @Select("select * from folder")
    List<Folder> getAllFolder();
}
