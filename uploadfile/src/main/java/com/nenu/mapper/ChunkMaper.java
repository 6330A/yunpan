package com.nenu.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChunkMaper {
    @Insert("insert into chunk (user_id, chunk_index, chunk_count, file_md5) values (#{userId}, #{currentIndex}, #{chunkCount}, #{fileMd5})")
    void addChunk(Integer userId, Integer currentIndex, Integer chunkCount, String fileMd5);

    //删除分片记录
    @Delete("delete from chunk where file_md5=#{fileMd5} and user_id=#{userId}")
    void deleteChunksByFileMd5(String fileMd5, Integer userId);

    //断点续传，获取已经上传的分片下标
    @Select("select chunk_index from chunk where file_md5 = #{fileMd5} and user_id=#{userId}")
    List<Integer> getUploadedChunksIdx(String fileMd5, Integer userId);
}
