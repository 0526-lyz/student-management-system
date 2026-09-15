<template>
  <div class="page-card">
    <div class="page-toolbar">
      <span class="notif-count">共 {{ list.length }} 条通知</span>
      <div class="spacer" />
      <el-button type="primary" plain :icon="Check" @click="onReadAll">全部已读</el-button>
    </div>

    <el-empty v-if="!loading && !list.length" description="暂无通知" />
    <el-scrollbar v-else max-height="620px">
      <div
        v-for="n in list"
        :key="n.notificationId"
        class="notif-item"
        :class="{ unread: !n.read }"
      >
        <div class="notif-dot">
          <el-icon v-if="!n.read"><CircleCheckFilled /></el-icon>
          <el-icon v-else><CircleCheck /></el-icon>
        </div>
        <div class="notif-body">
          <div class="notif-content">{{ n.content }}</div>
          <div class="notif-meta">
            <span>收件人：{{ n.username }}</span>
            <span class="dot-sep">·</span>
            <span>{{ n.createTime }}</span>
          </div>
        </div>
        <el-button v-if="!n.read" link type="primary" @click="onRead(n)">标记已读</el-button>
      </div>
    </el-scrollbar>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Check } from '@element-plus/icons-vue'
import { notificationApi } from '../api'
import { user, isAdmin } from '../store/auth'

const list = ref([])
const loading = ref(false)

const username = computed(() => (isAdmin.value ? '' : user.value?.username))

async function load() {
  loading.value = true
  try {
    list.value = await notificationApi.list(username.value)
  } finally {
    loading.value = false
  }
}

async function onRead(n) {
  try {
    await notificationApi.markRead(n.notificationId)
    n.read = true
    ElMessage.success('已标记为已读')
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

async function onReadAll() {
  try {
    await notificationApi.markAllRead(username.value)
    list.value.forEach((n) => (n.read = true))
    ElMessage.success('已全部标记为已读')
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

onMounted(load)
</script>

<style scoped>
.notif-count {
  font-weight: 600;
  color: #4a4f5e;
}

.notif-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 12px;
  border-radius: 12px;
  transition: background 0.2s;
}

.notif-item:hover {
  background: #f7f8fc;
}

.notif-item.unread {
  background: #f5f7ff;
}

.notif-dot {
  color: #5b6cff;
  font-size: 20px;
}

.notif-item:not(.unread) .notif-dot {
  color: #c4c9d4;
}

.notif-body {
  flex: 1;
}

.notif-content {
  font-size: 14px;
  color: #2b2f3a;
}

.notif-item.unread .notif-content {
  font-weight: 600;
}

.notif-meta {
  font-size: 12px;
  color: #9aa1b0;
  margin-top: 6px;
}

.dot-sep {
  margin: 0 6px;
}
</style>
