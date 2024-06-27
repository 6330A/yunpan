//定义store
import { defineStore } from "pinia";
import {ref} from 'vue';

const useUserInfoStore = defineStore('userInfo', ()=>{
    //定义状态相关的内容

    const info = ref({})

    const setInfo = (newInfo) =>{
        info.value = newInfo
    }

    const removeInfo = ()=>{
        info.value = {}
    }

    //额外添加一个属性，showall中测试是否可以共享数据来实时渲染父路由和子路由
    const updateSpace = () =>{
        info.value.usedSpace += 1073741824
    }

    return {info, setInfo, removeInfo, updateSpace}
}, {persist:true})

export default useUserInfoStore;