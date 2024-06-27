<script setup lang="ts">
import { ref } from 'vue'
import { Delete, Download, Search, Plus, SetUp } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus';
import { cloudFileListByTypeService, deleteCloudFileByFileIdListService } from '@/api/cloudfile.js'
import UploadButton from '@/components/UploadButton.vue';
import { formatFileSize } from '@/utils/formatFileSize.js'
import { onMounted } from 'vue';

import { userInfoService } from '@/api/user.js';
import useUserInfoStore from '@/stores/userInfo.js';
const userInfoStore = useUserInfoStore();

const fileType = ref('')
const searchFileName = ref('')  //字符串，.value获取值
const fileListTable = ref([
  {
    fileName: 'xxxxxx',
    fileUt: '2016-05-08 19:57:24',
    fileSize: 1245,
  }
])
const props = defineProps({
  fileType: String
})

const count = ref(0);

//--------------------------------------------------------------------------------------------------
//获取文件并绑定到模型fileListTable,根据传递的props的fileType显示类型
const cloudFileList = async () => {
  fileListTable.value = []   //先清空一下，其实不用也行，主要是避免切换时候残影
  let result = await cloudFileListByTypeService(fileType.value);
  fileListTable.value = result.data
}

onMounted(() => {
  fileType.value = props.fileType
  cloudFileList()
});

//--------------------------------------------------------------------------------------------------
const searchFileByName = () => {
  console.log(searchFileName.value)
}

const rowStyle = ({ row, rowIndex }) => {
  return { height: '45px' }
}


//多选进行删除
const fileIdList = ref([]);

const handleSelectionChange = (rows) => {
  // 清空 fileIdList
  fileIdList.value = rows.map(item => item.fileId)

  // 使用 map 方法从每一行中提取 fileId 并存入 fileIdList

};

//删除文件
const deleteFile = async () => {
  ElMessageBox.confirm(
    '确认删除选中的文件吗？',
    '温馨提示',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
      //调用删除接口
      await deleteCloudFileByFileIdListService(fileIdList.value)
      ElMessage.success('删除成功');
      //这里刷新列表
      cloudFileList()

      //更新pinia，用户空间信息刷新
      let result = await userInfoService();
      userInfoStore.setInfo(result.data);
    })
    .catch(() => {
      ElMessage.info('您已取消删除')
    })
}

const downloadFile = () => {

}

</script>

<template>
  <div>
    <el-input v-model="searchFileName" style="width: 356px; margin-right: 12px" placeholder="请输入您要查找的文件名称" clearable>
      <template #append>
        <el-button :icon="Search" @click="searchFileByName" />
      </template>
    </el-input>

    <el-button type="danger" plain :icon="Delete" :disabled="fileIdList.length === 0" @click="deleteFile">删除</el-button>
    <el-button type="success" plain :icon="Download" :disabled="fileIdList.length === 0"
      @click="downloadFile">下载</el-button>

    <!--  上传后通知页面刷新-->
    <uploadButton @refreshList="cloudFileList"></uploadButton>
  </div>


  <el-divider />
  <el-table :data="fileListTable" :row-style="rowStyle" height="760" @selection-change="handleSelectionChange">
    <el-table-column fixed type="selection" width="50" />
    <el-table-column prop="fileName" label="文件名称" />
    <el-table-column prop="fileUt" label="修改时间" width="200" />
    <!-- 文件大小需要根据字节数计算字符串，utils中定义了一个函数，为了复用到计算空间大小，做了判断是否存在属性fileSize -->
    <el-table-column prop="fileSize" label="文件大小" width="200" align="right" :formatter="formatFileSize" />
    <el-table-column width="30" />
  </el-table>
</template>
