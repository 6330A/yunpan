<script lang="ts" setup>
import { User, Crop, EditPen, SwitchButton, Menu as IconMenu, } from '@element-plus/icons-vue'
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ref } from 'vue'
//登录完成，调用函数，获取用户详细信息并存储到pinia中
import { userInfoService } from '@/api/user.js';
import useUserInfoStore from '@/stores/userInfo.js';
import useUploadListStore from '@/stores/uploadList.js';

import { formatFileSize, calculatePercent } from '@/utils/formatFileSize.js'

import { cloudFileListByTypeService } from '@/api/cloudfile.js'

import { Delete, Download, Search, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus';
import { useTokenStore } from '@/stores/token';

const UploadListStore = useUploadListStore()

const router = useRouter();
const userInfoStore = useUserInfoStore();
// const tokenStore = useTokenStore();


const getUserInfo = async () => {
    let result = await userInfoService();
    console.log(result.data)
    userInfoStore.setInfo(result.data);
}

getUserInfo();

const handleCommand = (command) => {
    //判断指令

    // 退出登录，需要用户确认，确认后清空pinia和token
    if(command === 'logout'){
        ElMessageBox.confirm(
            '您确认要退出吗？','温馨提示',
        {
            confirmButtonText:'确认',
            cancelButtonText:'取消',
            type:'warning',
        })
        .then(async()=>{
            // 退出登录
            // 清空pinia数据以及token个人信息
            // userInfoStore.removeInfo();
            // 跳转路由
            router.push('/login')
            ElMessage({
                type:'success',
                message:'退出登录成功'
            })
        })
        .catch(()=>{
            ElMessage({
                type:'info',
                message:'用户取消退出'
            })
        })

    }else{
        ElMessage.success("暂未完成部分")
    }
}

//菜单项绑定的默认index，不会自动跳转路由，这里添加一个钩子函数，上面导包
onMounted(() => {
    router.push('/Showall')
})




const searchFileName = ref('')  //字符串，.value获取值
const fileListTable = ref([
  {
    fileName: '2016-05-03',
    fileUt: '2016-05-08 19:57:24',
    fileSize: 1245,
  }
])

const searchFileByName = () => {
  console.log(searchFileName.value)
}

const rowStyle = ({ row, rowIndex }) => {
  return { height: '45px' }
}
const tableListSelect = async(fileType)=>{
    let result = await cloudFileListByTypeService(fileType)
    fileListTable.value = result.data
}
</script>


<template>
    <el-container class="layout-container">
        <el-header style="box-shadow: 0px 3px 10px rgba(0, 0, 0, 0.1);">
            <div style="display: flex;  align-items: center;">
                <!-- 一行图标一行文字，上面的div定义它们垂直居中 -->
                <el-icon size="40" color="#6990f2">
                    <PartlyCloudy />
                </el-icon>
                <el-text tag="b" size="large" style="margin: 10px;">云盘</el-text>
            </div>
            <div style="display: flex; align-items: center">
                <!-- <el-text tag="b" style="margin: 10px;">NICKNAME</el-text> -->
                <el-text tag="b" style="margin: 10px;">{{ userInfoStore.info.nickname }}</el-text>
                <!-- 右边是下拉菜单，command:条目被点击后触发，这里添加一个函数 -->
                <el-dropdown placement="bottom-end" @command="handleCommand">
                    <span class="el-dropdown__box">
                        <el-avatar />
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="info" :icon="User">基本资料</el-dropdown-item>
                            <el-dropdown-item command="avatar" :icon="Crop">更换头像</el-dropdown-item>
                            <el-dropdown-item command="resetPassword" :icon="EditPen">修改密码</el-dropdown-item>
                            <el-dropdown-item command="logout" :icon="SwitchButton">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>

                <el-icon style="font-size: 18px; margin: 10px;"
                    @click="UploadListStore.showUploader = !UploadListStore.showUploader">
                    <Sort />
                </el-icon>
            </div>
        </el-header>
        <el-container>
            <el-aside
                style="width:210px; box-shadow: 3px 3px 10px rgba(0, 0, 0, 0.1); display: flex; flex-direction: column;   justify-content: space-between;">
                <el-menu default-active="/Showall" router>
                    <el-sub-menu index="/Showall">
                        <template #title>
                            <el-icon><icon-menu /></el-icon>
                            <span>首页</span>
                        </template>
                        <el-menu-item index="/Showall">全部</el-menu-item>
                        <el-menu-item index="/Video">视频</el-menu-item>
                        <el-menu-item index="/Music">音频</el-menu-item>
                        <el-menu-item index="/Photo">图片</el-menu-item>
                        <el-menu-item index="/Document">文档</el-menu-item>
                        <el-menu-item index="/Else">其他</el-menu-item>
                    </el-sub-menu>

                    <el-menu-item index="/Share">
                        <template #title>
                            <el-icon>
                                <Share />
                            </el-icon> <span>分享</span>
                        </template>
                    </el-menu-item>

                    <el-menu-item index="/Recycle">
                        <template #title>
                            <el-icon>
                                <Delete />
                            </el-icon> <span>回收站</span>
                        </template>
                    </el-menu-item>
                </el-menu>
                <div style="margin: 20px;">
                    <p style="font-size:12px ;margin-bottom: -2px;">{{ formatFileSize(userInfoStore.info.usedSpace) }} /
                        {{
                            formatFileSize(userInfoStore.info.diskSpace) }}</p>
                    <el-progress stroke-width:2
                        :percentage="calculatePercent(userInfoStore.info.usedSpace, userInfoStore.info.diskSpace)" />
                </div>
            </el-aside>
            <el-main class="main" style="height: 100%">
                <router-view></router-view>
            </el-main>
        </el-container>
    </el-container>
</template>


<style lang="scss" scoped>
.layout-container {
    height: 100vh;

    .el-header {
        height: 50px;
        background-color: #fff;
        display: flex;
        align-items: center;
        justify-content: space-between;

        .el-dropdown__box {
            display: flex;
            align-items: center;

            .el-icon {
                color: #ddd;
                margin-left: 10px;
            }

            &:focus {
                outline: none;
            }
        }
    }

    .el-menu-item.is-active {
        background-color: #ecf5ff;
    }
}
</style>