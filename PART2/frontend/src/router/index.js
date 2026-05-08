import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import CourseList from '../views/CourseList.vue';
import CrossDept from '../views/CrossDept.vue';
import DropCourse from '../views/DropCourse.vue';
import { useAuthStore } from '../store';

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/local', component: CourseList },
  { path: '/cross', component: CrossDept },
  { path: '/drop', component: DropCourse }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to) => {
  if (to.path === '/login') {
    return true;
  }
  const store = useAuthStore();
  if (!store.loggedIn) {
    return '/login';
  }
  return true;
});

export default router;
