<template>
  <el-card>
    <h2>Drop Course</h2>
    <el-tabs v-model="activeTab" style="margin-top: 12px;">
      <el-tab-pane label="Local" name="local">
        <el-button type="primary" @click="load">Refresh</el-button>
        <el-table :data="selected" style="margin-top: 16px;">
          <el-table-column prop="id" label="ID" />
          <el-table-column prop="name" label="Name" />
          <el-table-column prop="teacher" label="Teacher" />
          <el-table-column prop="score" label="Score" />
          <el-table-column prop="location" label="Location" />
          <el-table-column label="Action">
            <template #default="scope">
              <el-button type="danger" size="small" @click="drop(scope.row)">Drop</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="Cross" name="cross">
        <el-form :model="form" label-width="90px">
          <el-form-item label="Target">
            <el-select v-model="form.targetSystem" placeholder="Select">
              <el-option label="A" value="A" />
              <el-option label="B" value="B" />
              <el-option label="C" value="C" />
            </el-select>
          </el-form-item>
          <el-form-item label="Course ID">
            <el-input v-model="form.courseId" />
          </el-form-item>
          <el-form-item>
            <el-button type="danger" @click="dropCross">Drop</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { dropLocalCourse, dropCrossCourse, listSelectedCourses } from '../api/integration';

const activeTab = ref('local');
const selected = ref([]);
const form = reactive({ courseId: '', targetSystem: '' });

const load = async () => {
  const { data } = await listSelectedCourses();
  selected.value = data;
};

const drop = async (row) => {
  await dropLocalCourse({ courseId: row.id });
  ElMessage.success('dropped');
  load();
};

const dropCross = async () => {
  if (!form.targetSystem) {
    ElMessage.warning('target required');
    return;
  }
  await dropCrossCourse({ courseId: form.courseId, targetSystem: form.targetSystem });
  ElMessage.success('cross drop sent');
};

load();
</script>
