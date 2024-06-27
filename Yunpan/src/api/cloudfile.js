import request from '@/utils/request.js'

//获取用户所有文件
export const cloudFileListService = () => {
    return request.get('/file/getAllFiles');
}

//根据类型获取文件信息,这里fileType是一个模糊的string,Video/Music/Photo/Documents/Else，后端需要处理成数组去获取
export const cloudFileListByTypeService = (fileType) => {
    return request.get('/file/getFilesByType', { params: { fileType } });
}


//查询文件是否已经存在
export const existCloudFileService = (fileMd5) => {
    return request.get('/file/existCloudFileByFileMd5', { params: { fileMd5 } });
}

//秒传
export const secondsUploadService = (fileMd5, fileName) => {
    const params = new URLSearchParams();
    params.append('fileMd5', fileMd5);
    params.append('fileName', fileName);

    return request.post('/file/secondsUpload', params);
}


//根据fileIDList进行批量删除文件
export const deleteCloudFileByFileIdListService = (fileIdList) => {
    console.log(fileIdList)
    // let id = fileIdList[0]
    return request.post('/file/deleteCloudFileByFileIdList', fileIdList);
}

