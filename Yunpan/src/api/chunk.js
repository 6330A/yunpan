import request from '@/utils/request.js'

//上传分片
export const uploadChunksService = (chunkData) =>{
    return request.post('/chunk/uploadChunks', chunkData)
} 


//合并分片
export const mergeChunksService = (fileMd5, fileName) => {
    const params = new URLSearchParams();
    params.append('fileMd5', fileMd5);
    params.append('fileName', fileName);
    
    return request.post('/chunk/mergeChunks', params);
}


//获取已上传分片下标
export const getUploadedChunksIdxService = (fileMd5) =>{
    return request.get('/chunk/getUploadedChunksIdx', { params: { fileMd5 } })
}

//删除数据库中的分片信息以及清除文件，对于的下载未完成，用户点击删除按钮
export const clearUncompletedFileService = (fileMd5) =>{
    return request.delete('/chunk/clearUncompletedFile',  { params: { fileMd5 } });
}