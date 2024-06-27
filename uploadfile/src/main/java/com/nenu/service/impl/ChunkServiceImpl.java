package com.nenu.service.impl;
import com.nenu.mapper.ChunkMaper;
import com.nenu.service.ChunkService;
import com.nenu.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChunkServiceImpl implements ChunkService {
    @Autowired
    private ChunkMaper chunkMaper;

    @Override
    public void uploadChunks(Integer currentIndex, Integer chunkCount, String fileMd5) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = Integer.valueOf(claims.get("userId").toString());

        chunkMaper.addChunk(userId, currentIndex, chunkCount, fileMd5);
    }

    //上传完成之后从数据库中删除每一个分片的记录
    @Override
    public void deleteChunksByFileMd5(String fileMd5) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = Integer.valueOf(claims.get("userId").toString());

        chunkMaper.deleteChunksByFileMd5(fileMd5, userId);
    }


    //断点续传
    @Override
    public List<Integer> getUploadedChunksIdx(String fileMd5) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = Integer.valueOf(claims.get("userId").toString());

        return chunkMaper.getUploadedChunksIdx(fileMd5, userId);
    }
}
