<script setup>

import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { ref } from 'vue'
const isRegister = ref(false)

//定义数据模型
const registerData = ref({
    username: '',
    password: '',
    rePassword: ''
})

//对应下面表单校验规则中的校验两次密码是否填写一致
const checkRePassword = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请再次确认密码'))
    } else if (value !== registerData.value.password) {
        callback(new Error('请确保两次输入的密码一样'))
    } else {
        callback()
    }
}

//定义表单校验规则，校验规则需要表单中的输入框含有prop属性进行绑定
const rules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 5, max: 16, message: '长度为5~16位非空字符', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 5, max: 16, message: '长度为5~16位非空字符', trigger: 'blur' }
    ],
    rePassword: [
        { validator: checkRePassword, trigger: 'blur' }
    ]
}

//由于登录注册判断是否操作成功部分相同，可以通过拦截器进行拦截
//调用后台接口，完成注册
import { userRegisterService, userLoginService } from '@/api/user.js'
const register = async () => {
    // registerData是一个响应式对象，使用.value获取值
    // 这里的result是后端传递过来的，看pojo里面的Result类
    // .code  .msg  .data
    let result = await userRegisterService(registerData.value);

    ElMessage.success('注册成功');
}

//绑定数据，复用注册表单的数据类型
//表单数据校验
//登录函数
import { useTokenStore } from '@/stores/token.js';

//登录要使用路由跳转，这里要注意是useRouter不是userRoute
import { useRouter } from 'vue-router';
const router = useRouter();
const tokenStore = useTokenStore();

const login = async () => {
    let result = await userLoginService(registerData.value);
    console.log(result)
    ElMessage.success('登录成功');
    //得到的token存储到pinia中
    tokenStore.setToken(result.data);
    //借助路由完成跳转
    router.push({ path: "/" })
}

const clearRegiserData = () => {
    registerData.value = {
        username: '',
        password: '',
        rePassword: ''
    }
}

</script>

<template>
    <el-row class="login-page">
        <el-col :span="6" :offset="6" class="bg"></el-col>
        <el-col :span="6" class="form">
            <!-- 注册表单 -->
            <el-card>
                <el-form ref="form" size="large" autocomplete="off" v-if="isRegister" :model="registerData"
                    :rules="rules">
                    <el-form-item>
                        <h1>注册</h1>
                    </el-form-item>
                    <el-form-item prop="username">
                        <el-input :prefix-icon="User" placeholder="请输入用户名" v-model="registerData.username"></el-input>
                    </el-form-item>

                    <el-form-item prop="password">
                        <el-input :prefix-icon="Lock" type="password" placeholder="请输入密码"
                            v-model="registerData.password" show-password></el-input>
                    </el-form-item>

                    <el-form-item prop="rePassword">
                        <el-input :prefix-icon="Lock" type="password" placeholder="请再次输入密码"
                            v-model="registerData.rePassword" show-password></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button class="button" type="primary" auto-insert-space @click="register">注册</el-button>
                    </el-form-item>
                    <el-form-item class="flex">
                        <el-link type="info" :underline="false" @click="isRegister = false; clearRegiserData()">
                            ← 返回
                        </el-link>
                    </el-form-item>
                </el-form>
                <!-- 登录表单 -->
                <el-form ref="form" size="large" autocomplete="off" v-else :model="registerData" :rules="rules">
                    <el-form-item>
                        <h1>登录</h1>
                    </el-form-item>
                    <el-form-item prop="username">
                        <el-input :prefix-icon="User" placeholder="请输入用户名" v-model="registerData.username"></el-input>
                    </el-form-item>
                    <el-form-item prop="password">
                        <el-input name="password" :prefix-icon="Lock" type="password" placeholder="请输入密码"
                            v-model="registerData.password" show-password></el-input>
                    </el-form-item>
                    <el-form-item class="flex">
                        <div class="flex">
                            <el-checkbox>记住我</el-checkbox>
                            <el-link type="primary" :underline="false">忘记密码？</el-link>
                        </div>
                    </el-form-item>
                    <el-form-item>
                        <el-button class="button" type="primary" auto-insert-space @click="login">登录</el-button>
                    </el-form-item>
                    <el-form-item class="flex">
                        <el-link type="info" :underline="false" @click="isRegister = true; clearRegiserData()">
                            注册 →
                        </el-link>
                    </el-form-item>
                </el-form>
            </el-card>
        </el-col>
    </el-row>
</template>


<style lang="scss" scoped>
.login-page {
    height: 100vh;
    background-color: #fff;

    .bg {
        background: url('@/assets/background.jpg') no-repeat 10% center / 500px auto;
        border-radius: 0 20px 20px 0;
    }

    .form {
        display: flex;
        flex-direction: column;
        justify-content: center;
        user-select: none;

        .title {
            margin: 0 auto;
        }

        .button {
            background-color: #5379f6;
            width: 100%;
        }

        .flex {
            width: 100%;
            display: flex;
            justify-content: space-between;
        }
    }
}
</style>
