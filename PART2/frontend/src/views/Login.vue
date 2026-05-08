<template>
  <el-card class="panel">
    <h2>Login</h2>
    <el-form :model="form" label-width="90px">
      <el-form-item label="Username">
        <el-input v-model="form.username" />
      </el-form-item>
      <el-form-item label="Password">
        <el-input v-model="form.password" type="password" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleLogin">Login</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { login } from '../api/integration';
import { useAuthStore } from '../store';
import { useRouter } from 'vue-router';

const store = useAuthStore();
const router = useRouter();
const form = reactive({ username: '', password: '' });

const handleLogin = async () => {
  try {
    const { data } = await login(form);
    if (data.success) {
      localStorage.setItem('auth_token', data.token);
      localStorage.setItem('auth_student_id', data.studentId || form.username);
      localStorage.setItem('auth_username', form.username);
      store.setLogin({
        username: form.username,
        studentId: data.studentId || form.username,
        token: data.token
      });
      ElMessage.success('login ok');
      router.push('/local');
    } else {
      ElMessage.error(data.message || 'login failed');
    }
  } catch (error) {
    ElMessage.error('login failed');
  }
};
</script>

<style scoped>
.panel {
  max-width: 420px;
  margin: 80px auto;
}
</style>
