<script setup lang="ts">
import SparkMD5 from 'spark-md5';
import { ref } from 'vue'
import { Upload } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus';
import { uploadChunksService, mergeChunksService, getUploadedChunksIdxService } from '@/api/chunk.js';
import { existCloudFileService, secondsUploadService } from '@/api/cloudfile.js'
import { clearUncompletedFileService } from '@/api/chunk.js';

import { userInfoService } from '@/api/user.js';

//pinia中存放者上传列表以及用户信息（主要是用户空间大小用于判断是否继续上传）
import useUserInfoStore from '@/stores/userInfo.js';
import useUploadListStore from '@/stores/uploadList.js';

//1MB = 1024KB = 1024B，这里上传分片大小为1MB，再大表示超出最大上传大小了
const CHUNK_SIZE = 1 * 1024 * 1024

const userInfoStore = useUserInfoStore();
const UploadListStore = useUploadListStore()

//文件上传按钮
const fileInput = ref(null);

//通过点击上传按钮，触发背后面的input文件输入按钮的click()事件
//简单来说就是一个按钮触发另一个按钮的
const openFileInput = () => {
  fileInput.value.click();
};

const emit = defineEmits(['refreshList'])

const handleFileInputChange = async (e: Event) => {
  const files = (e.target as HTMLInputElement).files;
  if (!files) return

  //读取文件
  Array.from(files).forEach(file => {
    console.log(file.name)
    console.log(file.size + " Byte")
  });

  //添加到模型当中，哈希值后面每个文件单独计算
  Array.from(files).forEach(file => {
    let chunks = createChunks(file)
    //pinia中定义的addFile函数，用户长期存储添加的文件信息及其状态
    UploadListStore.addFile({
      //这里多加一层，作为弹出框的第一列
      uploadInfo: {
        fileMd5: '',
        fileName: file.name,
        fileSize: file.size,
        chunks: chunks,
        chunkCount: chunks.length,
        uploadedCount: 0,
        progress: 0,
        isPause: false,
        status: '正在上传',
        completed: false
      }
    });
  })

  //将上传列表显示模型存放到pinia中
  UploadListStore.showUploader = true    //打开pinia中存放的上传窗口

  UploadListStore.uploadList.forEach(async (row) => {
    let singleFile = row.uploadInfo
    if (!singleFile.completed) {
      singleFile.fileMd5 = await calculateHash(singleFile.chunks)

      //打印一下当前文件信息
      console.log('文件名，文件大小，分片总数，哈希值')
      console.log(singleFile.fileName + '    ' + singleFile.fileSize + '    ' + singleFile.chunkCount)
      console.log(singleFile.fileMd5)
      console.log('----------------------------------------')
      judgeExistFile(singleFile)
    }
  })
}

//创建分片
const createChunks = (file: File) => {
  let cur = 0
  let chunks = []

  while (cur < file.size) {
    const blob = file.slice(cur, cur + CHUNK_SIZE)
    chunks.push(blob)
    cur += CHUNK_SIZE
  }
  return chunks
}

//计算hash值
const calculateHash = (chunks: Blob[]) => {
  return new Promise(
    resolve => {
      //第一个和最后一个切片全部参与计算,中间的切片只计算前面两个字节，中间两个字节，最后两个字节
      const targets: Blob[] = []
      const spark = new SparkMD5.ArrayBuffer()
      const fileReader = new FileReader()

      chunks.forEach((chunk, index) => {
        if (index === 0 || index === chunks.length - 1) {
          targets.push(chunk)
        } else {
          targets.push(chunk.slice(0, 2))
          targets.push(chunk.slice(CHUNK_SIZE / 2, CHUNK_SIZE / 2 + 2))
          targets.push(chunk.slice(CHUNK_SIZE - 2, CHUNK_SIZE))
        }
      })

      fileReader.readAsArrayBuffer(new Blob(targets))
      fileReader.onload = (e) => {
        spark.append(((e.target as FileReader).result) as ArrayBuffer)
        //注意返回函数值，后面加上括号
        resolve(spark.end())
      }
    }
  )
}

//判断文件是否存在，存在显示秒传动画，不存在则上传
const judgeExistFile = async (singleFile: any) => {
  let result = await existCloudFileService(singleFile.fileMd5);
  if (result.data) {
    singleFile.status = "秒传"   //先更改状态
    await secondsUploadService(singleFile.fileMd5, singleFile.fileName)
    console.log("秒传......")
    singleFile.progress = 0
    let interval = setInterval(() => {
      singleFile.progress += 10
      if (singleFile.progress === 100) {
        singleFile.completed = true
        clearInterval(interval)
        //通知父组件刷新页面
        emit('refreshList')
      }
    }, 50)
  } else {
    uploadChunks(singleFile)
  }
}

//上传分片，包含断点续传
const uploadChunks = async (singleFile: any) => {
  const formDatas = singleFile.chunks.map((chunk, index) => {
    const { fileMd5, chunkCount, fileName } = singleFile;
    const formData = createFormData(chunk, fileMd5, index, chunkCount, CHUNK_SIZE, fileName);
    return formData;
  });

  function createFormData(chunk, fileMd5, currentIndex, chunkCount, chunkSize, fileName) {
    const formData = new FormData();
    formData.append('chunk', chunk);
    formData.append('fileMd5', fileMd5);
    formData.append('StrcurrentIndex', String(currentIndex));
    formData.append('StrchunkSize', String(chunkSize));
    formData.append('StrchunkCount', String(chunkCount));
    formData.append('fileName', fileName);
    return formData;
  }

  //断点续传，先获取已上传分片信息
  let result = await getUploadedChunksIdxService(singleFile.fileMd5)
  let uploadedChunksIdxArray = result.data

  console.log(uploadedChunksIdxArray)

  singleFile.uploadedCount = uploadedChunksIdxArray.length;
  singleFile.progress = Math.floor(100 * singleFile.uploadedCount / singleFile.chunkCount)

  //标记已上传分片,文件分片数量为5，已上传下标0，1，2，构建这个上传的数组[1,1,1,0,0]表示最后两个分片未上传
  let uploadIdx = new Array(singleFile.chunkCount).fill(0);
  uploadedChunksIdxArray.forEach(i => { uploadIdx[i] = 1; });


  for (let index = 0; index < singleFile.chunkCount; index++) {
    if (uploadIdx[index] === 0) {
      //找到可以上传的分片下标，继续判断磁盘空间是否足够
      let freeSpace = userInfoStore.info.diskSpace - userInfoStore.info.usedSpace
      if (freeSpace < singleFile.fileSize - 1024 * 1024 * singleFile.uploadedCount) {
        singleFile.isPause = true
        singleFile.status = "网盘空间不足"
        break;
      }

      console.log("正在上传分片下标：" + index + " " + singleFile.fileName)
      if (singleFile.isPause) {
        // await new Promise(resolve => setTimeout(resolve, 1000)); // 暂停一秒钟
        break;
      }
      console.log(formDatas[index]);
      await uploadChunksService(formDatas[index]);
      singleFile.uploadedCount++
      singleFile.progress = Math.floor(100 * singleFile.uploadedCount / singleFile.chunkCount)
      // singleFile.status = singleFile.progress < 100 ? "正在上传" : "上传成功"

      //上传成功一个分片，更新网盘空间大小
      let result = await userInfoService();
      userInfoStore.setInfo(result.data);
    }
  }
  //合并分片
  if (singleFile.progress === 100) {
    await mergeChunks(singleFile)
    singleFile.completed = true
    singleFile.status = '上传成功'
    emit('refreshList')
  }
}

//合并请求，后端我只用了一个文件写入，所以只是改名
const mergeChunks = async (singleFile: any) => {
  await mergeChunksService(singleFile.fileMd5, singleFile.fileName)
}

//暂停上传
const pauseUpload = (row) => {
  row.uploadInfo.isPause = true;
  row.uploadInfo.status = '已暂停';
}
//恢复上传
const resumeUpload = (row) => {
  row.uploadInfo.isPause = false;
  row.uploadInfo.status = '正在上传'
  if(!(row.uploadInfo.chunks[0] instanceof Blob)){
    row.uploadInfo.status = '页面刷新导致文件丢失，请重新上传'
  }
  uploadChunks(row.uploadInfo)
}
//删除上传,如果用户已经上传成功了，那么只用删除前端的模型数据
//如果用户还未上完成所有的分片，那么除了先删除文件和数据库里的分片记录，然后从模型中剔除
const deleteUpload = async (row) => {
  pauseUpload(row)
  if (row.uploadInfo.completed === false) {
    //这里是对于上传未完成的文件进行删除，因此没有fileId，而且尚未重命名，可以根据fileMd5.tmp进行删除
    //同时需要删除数据库中存放的切片信息
    await clearUncompletedFileService(row.uploadInfo.fileMd5);
    let result = await userInfoService();
    userInfoStore.setInfo(result.data);
  }
  UploadListStore.uploadList = UploadListStore.uploadList.filter(item => item !== row);
}
</script>


<!-- 上传组件：一个按钮一个抽屉即可，抽屉显示上传列表，开关存放在pinia中，Frame.vue也可以控制抽屉显示 -->
<template>
  <el-button type="primary" :icon="Upload" @click="openFileInput">上传</el-button>
  <input type="file" ref="fileInput" multiple style="display :none" @change="handleFileInputChange" />

  <el-drawer v-model="UploadListStore.showUploader" :with-header="false" size=640>
    <el-table :data="UploadListStore.uploadList" empty-text="您的上传任务为空" style="margin-top: -11px;">
      <el-table-column width="500" prop="uploadInfo" label="上传任务" style="color: #409eff;">
        <template #default="{ row }">
          <p style="font-size:12px ;margin-top: -6px; margin-bottom: -7px;">{{
            row.uploadInfo.fileName }}</p>
          <el-progress stroke-width:6 :percentage=row.uploadInfo.progress />
          <p style="font-size:12px ;margin-top: -7px; margin-bottom: -6px;">{{
            row.uploadInfo.status }} </p>
        </template>
      </el-table-column>
      <el-table-column>
        <template #default="scope">
          <el-icon v-if="scope.row.uploadInfo.completed" style="color:#67C23A; margin-right: 4px;">
            <CircleCheck />
          </el-icon>
          <el-icon v-else-if="!scope.row.uploadInfo.isPause" style="color:#e6a23c; margin-right: 4px;"
            @click="pauseUpload(scope.row)">
            <VideoPause />
          </el-icon>
          <el-icon v-else style="color:#409eff; margin-right: 4px;" @click="resumeUpload(scope.row)">
            <Promotion />
          </el-icon>
          <el-icon style="color:#f56c6c; margin-left: 4px;" @click="deleteUpload(scope.row)">
            <CloseBold />
          </el-icon>
        </template>
      </el-table-column>
    </el-table>
  </el-drawer>
</template>