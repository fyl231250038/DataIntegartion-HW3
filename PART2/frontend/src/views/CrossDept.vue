<template>
  <el-card>
    <h2>Cross Department</h2>
    <el-form :inline="true" :model="filters" style="margin-bottom: 16px;">
      <el-form-item label="Target">
        <el-select v-model="filters.target" placeholder="All">
          <el-option label="All" value="" />
          <el-option label="A" value="A" />
          <el-option label="B" value="B" />
          <el-option label="C" value="C" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="load">Search</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="classes">
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
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { listSharedClasses, selectCrossCourse } from '../api/integration';
import { useAuthStore } from '../store';

const store = useAuthStore();
const classes = ref([]);
const filters = reactive({ target: '' });

const load = async () => {
  const { data } = await listSharedClasses(filters.target || undefined);
  classes.value = data;
};

const select = async (row) => {
  if (!store.loggedIn || !store.user?.studentId) {
    ElMessage.warning('please login first');
    return;
  }
  if (!filters.target) {
    ElMessage.warning('choose target department');
    return;
  }
  await selectCrossCourse({
    courseId: row.id,
    targetSystem: filters.target
  });
  ElMessage.success('cross select sent');
};

load();
</script>
