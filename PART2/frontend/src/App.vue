<template>
  <div class="app">
    <header class="app-header">
      <h1>Department System</h1>
      <nav>
        <RouterLink to="/login">Login</RouterLink>
        <RouterLink to="/local">Local</RouterLink>
        <RouterLink to="/cross">Cross</RouterLink>
        <RouterLink to="/drop">Drop</RouterLink>
      </nav>
      <div class="auth-info" v-if="store.loggedIn">
        <span>Student: {{ store.user?.studentId }}</span>
        <el-button size="small" @click="logout">Logout</el-button>
      </div>
    </header>
    <main class="app-main">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { useAuthStore } from './store';
import { useRouter } from 'vue-router';

const store = useAuthStore();
const router = useRouter();

const logout = () => {
  localStorage.removeItem('auth_token');
  localStorage.removeItem('auth_student_id');
  localStorage.removeItem('auth_username');
  store.logout();
  router.push('/login');
};
</script>

<style scoped>
.app {
  min-height: 100vh;
  background: #f3f4f6;
  color: #111827;
}

.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
}

.app-header nav {
  display: flex;
  gap: 16px;
}

.auth-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.app-main {
  padding: 24px;
}
</style>
