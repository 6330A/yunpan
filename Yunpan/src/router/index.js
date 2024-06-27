import { createRouter, createWebHistory } from 'vue-router';

//导入组件
import LoginVue from '@/views/Login.vue'
import FrameVue from '@/views/Frame.vue';

import ShowallVue from '@/views/main/Showall.vue';

import ShareVue from '@/views/main/Share.vue';
import Recyclebin from '@/views/main/Recyclebin.vue';

import VideoVue from '@/views/main/Video.vue';
import MusicVue from '@/views/main/Music.vue';
import PhotoVue from '@/views/main/Photo.vue';
import DocumentVue from '@/views/main/Document.vue';
import ElseVue from '@/views/main/Else.vue';

const routes = [
    { path: '/login', component: LoginVue},
    { path: '/', component: FrameVue ,
        children:[
            { path: '/Showall', component: ShowallVue },

            { path: '/Video', component: VideoVue },
            { path: '/Music', component: MusicVue },
            { path: '/Photo', component: PhotoVue },
            { path: '/Document', component: DocumentVue },
            { path: '/Else', component: ElseVue },

            { path: '/Share', component: ShareVue },
            { path: '/Recycle', component: Recyclebin },
    ]}
];


//创建路由器
const router = createRouter({
    history: createWebHistory(),
    routes: routes
})

//导出路由
export default router