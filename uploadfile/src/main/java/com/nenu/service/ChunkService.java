package com.nenu.service;

import com.nenu.pojo.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChunkService {
    void uploadChunks(Integer currentIndex, Integer chunkCount, String fileMd5);

    void deleteChunksByFileMd5(String fileMd5);

    List<Integer> getUploadedChunksIdx(String fileMd5);
}
