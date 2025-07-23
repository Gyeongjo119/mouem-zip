// router/index.js

import { createRouter, createWebHistory } from 'vue-router';

import CategoryAll from '@/views/category/CategoryAll.vue';
import CategoryManager from '@/views/category/CategoryManager.vue';
import CategoryAdd from '@/views/category/CategoryAdd.vue';
import CategoryEdit from '@/views/category/CategoryEdit.vue';

import TermManager from '@/views/term/TermManager.vue';
import TermAdd from '@/views/term/TermAdd.vue';
import TermEdit from '@/views/term/TermEdit.vue';

const routes = [
  {
    path: '/',
    redirect: '/category',
  },
  {
    path: '/category',
    component: CategoryAll,
    children: [
      {
        path: '',
        name: 'CategoryManager',
        component: CategoryManager,
      },
      {
        path: 'add',
        name: 'CategoryAdd',
        component: CategoryAdd,
      },
      {
        path: 'edit/:id',
        name: 'CategoryEdit',
        component: CategoryEdit,
        props: true,
      },
      {
        path: 'term', // 용어 관리 페이지 경로
        name: 'TermManager',
        component: TermManager,
      },
      {
        path: 'term/add',
        name: 'TermAdd',
        component: TermAdd,
      },
      {
        path: 'term/edit/:id',
        name: 'TermEdit',
        component: TermEdit,
        props: true,
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
