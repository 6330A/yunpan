<template>
  <div>
    <input type="file" @change="handleFileChange" />
    <button @click="clearFile">Clear File</button>
    <div v-if="file">
      <h2>Selected File:</h2>
      <p>{{ file.name }}</p>
      <p>{{ file.size }} bytes</p>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';

export default {
  setup() {
    const file = ref(null);

    // 在页面加载时尝试从localStorage中获取文件对象
    onMounted(() => {
      const storedFile = localStorage.getItem('selectedFile');
      if (storedFile) {
        file.value = JSON.parse(storedFile);
      }
    });

    // 处理文件选择
    const handleFileChange = (event) => {
      const selectedFile = event.target.files[0];
      file.value = selectedFile;
      // 将文件对象存储在localStorage中
      localStorage.setItem('selectedFile', JSON.stringify(selectedFile));
    };

    // 清除文件
    const clearFile = () => {
      file.value = null;
      localStorage.removeItem('selectedFile');
    };

    return {
      file,
      handleFileChange,
      clearFile
    };
  }
};
</script>