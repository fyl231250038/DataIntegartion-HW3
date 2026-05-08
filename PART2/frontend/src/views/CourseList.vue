<template>
  <el-card>
    <h2>Local Classes</h2>
    <el-button type="primary" @click="load">Refresh</el-button>
    <el-table :data="classes" style="margin-top: 16px;">
      <el-table-column prop="id" label="ID" />
      <el-table-column prop="name" label="Name" />
      <el-table-column prop="teacher" label="Teacher" />
      <el-table-column prop="score" label="Score" />
      <el-table-column prop="location" label="Location" />
      <el-table-column label="Action">
        <template #default="scope">
          <el-button size="small" @click="select(scope.row)">Select</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { listLocalClasses, selectLocalCourse } from '../api/integration';
import { useAuthStore } from '../store';

const classes = ref([]);
const store = useAuthStore();

const load = async () => {
  const { data } = await listLocalClasses();
  classes.value = data;
};

const select = async (row) => {
  if (!store.loggedIn || !store.user?.studentId) {
    ElMessage.warning('please login first');
    return;
  }
  await selectLocalCourse({ courseId: row.id });
  ElMessage.success('selected');
};

load();
</script>
