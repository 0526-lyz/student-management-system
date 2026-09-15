<template>
  <div class="page-card profile-card">
    <div class="profile-avatar">
      <el-avatar :size="72">{{ avatarText }}</el-avatar>
    </div>
    <h2 class="profile-name">{{ user?.username }}</h2>
    <el-tag :type="isAdmin ? 'danger' : 'success'" effect="light" size="large">
      {{ isAdmin ? '管理员' : '学生' }}
    </el-tag>

    <el-descriptions :column="1" border class="profile-desc">
      <el-descriptions-item label="用户 ID">{{ user?.userId }}</el-descriptions-item>
      <el-descriptions-item label="用户名">{{ user?.username }}</el-descriptions-item>
      <el-descriptions-item label="角色">{{ isAdmin ? '管理员' : '学生' }}</el-descriptions-item>
      <el-descriptions-item label="邮箱">{{ user?.email || '未填写' }}</el-descriptions-item>
    </el-descriptions>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { user, isAdmin } from '../store/auth'

const avatarText = computed(() => (user.value?.username || '?').charAt(0).toUpperCase())
</script>

<style scoped>
.profile-card {
  max-width: 560px;
  text-align: center;
  padding: 40px;
}

.profile-avatar {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}

.profile-avatar :deep(.el-avatar) {
  background: linear-gradient(135deg, #5b6cff, #8b5cf6);
  color: #fff;
  font-size: 30px;
  font-weight: 700;
}

.profile-name {
  margin: 0 0 10px;
}

.profile-desc {
  margin-top: 28px;
  text-align: left;
}
</style>
