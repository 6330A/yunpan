import { defineStore } from "pinia";
import { ref } from 'vue'

const useUploadListStore = defineStore('uploadList', () => {
    const uploadList = ref([])
    const showUploader = ref(false)

    //添加文件信息
    const addFile = (fileInfo) => {
        uploadList.value.push(fileInfo)
    }

    const removeFile = (row) => {
        uploadList.value.filter(item => item !== row)
    }

    return {
        uploadList, showUploader, addFile, removeFile
    }
}, { persist: true });

export default useUploadListStore;