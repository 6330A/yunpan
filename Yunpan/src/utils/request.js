//定制请求实例
//导入axios npm install axios

import axios from 'axios';
import { ElMessage } from 'element-plus';
import { useTokenStore } from '@/stores/token.js';
import router from '@/router';

//定义一个变量，记录公共的前缀， baseURL，定义/api后面利用代理方式实现跨域，连接后端端口
const baseURL = '/api';
const instance = axios.create({baseURL})

//响应拦截器，对于后端响应回来的数据先进行判断是否正常
instance.interceptors.response.use(
    result=>{
        if(result.data.code === 0){
            return result.data;
        }
        ElMessage.error(result.data.msg ? result.data.msg : '服务异常');
        return Promise.reject(result.data);
    },
    err=>{
        if(err.response.status === 401){
            ElMessage.error("前先登录")
            router.push('/login')
        }else{
            ElMessage.error('服务异常.');
        }
        
        return Promise.reject(err)
    }
)

//请求拦截器
instance.interceptors.request.use(
    (config)=>{
        const tokenStore = useTokenStore();
        if(tokenStore.token){
            config.headers.Authorization = tokenStore.token
        }
        return config
    },
    (err)=>{
        Promise.reject(err)
    }
)


export default instance;