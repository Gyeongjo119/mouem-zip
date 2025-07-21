import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '@/pages/HomePage.vue'
import TermView from "@/pages/TermView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [{ path: '/', name: 'home', component: HomePage },
    {path: '/terms', component: TermView}
  ],
});
export default router;
