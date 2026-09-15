<template>
  <div>
    <div class="hero">
      <div class="hero-text">
        <h2>{{ greeting }}，{{ user?.username }}</h2>
        <p>{{ isAdmin ? '欢迎进入学生信息管理系统管理后台' : '欢迎回来，看看你的最新成绩与通知' }}</p>
      </div>
      <div class="hero-icon">
        <el-icon :size="34"><component :is="isAdmin ? 'Management' : 'Reading'" /></el-icon>
      </div>
    </div>

    <el-row :gutter="20" class="stats">
      <el-col v-for="c in cards" :key="c.label" :xs="12" :sm="12" :md="6">
        <div class="stat-card" :style="{ '--c': c.color }">
          <div class="stat-icon">
            <el-icon :size="24"><component :is="c.icon" /></el-icon>
          </div>
          <div class="stat-value">{{ c.value }}</div>
          <div class="stat-label">{{ c.label }}</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="24">
        <div class="page-card">
          <div class="quick-title">
            <span>快捷入口</span>
          </div>
          <div class="quick-grid">
            <div
              v-for="q in quicks"
              :key="q.path"
              class="quick-item"
              @click="$router.push(q.path)"
            >
              <el-icon :size="22"><component :is="q.icon" /></el-icon>
              <span>{{ q.label }}</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { user, isAdmin } from '../store/auth'
import { statsApi } from '../api'

const stats = ref({})

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '凌晨好'
  if (h < 12) return '上午好'
  if (h < 14) return '中午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

const cards = computed(() => {
  if (isAdmin.value) {
    return [
      { label: '学生总数', value: stats.value.studentCount ?? 0, icon: 'User', color: '#5b6cff' },
      { label: '成绩记录', value: stats.value.gradeCount ?? 0, icon: 'DataAnalysis', color: '#8b5cf6' },
      { label: '站内通知', value: stats.value.notificationCount ?? 0, icon: 'Bell', color: '#f59e0b' },
      { label: '系统用户', value: stats.value.userCount ?? 0, icon: 'Avatar', color: '#10b981' }
    ]
  }
  return [
    { label: '成绩记录', value: stats.value.gradeCount ?? 0, icon: 'DataAnalysis', color: '#8b5cf6' },
    { label: '我的通知', value: stats.value.notificationCount ?? 0, icon: 'Bell', color: '#f59e0b' }
  ]
})

const quicks = computed(() => {
  const base = [
    { label: '通知中心', path: '/notifications', icon: 'Bell' },
    { label: '个人中心', path: '/profile', icon: 'Setting' }
  ]
  if (isAdmin.value) {
    return [
      { label: '学生管理', path: '/students', icon: 'User' },
      { label: '成绩管理', path: '/grades', icon: 'DataAnalysis' },
      ...base
    ]
  }
  return [{ label: '我的成绩', path: '/my-grades', icon: 'DataLine' }, ...base]
})

onMounted(async () => {
  try {
    stats.value = await statsApi.get()
  } catch {
    /* ignore */
  }
})
</script>

<style scoped>
.hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 30px 32px;
  border-radius: 16px;
  background: linear-gradient(135deg, #5b6cff, #8b5cf6);
  color: #fff;
  box-shadow: 0 14px 40px rgba(91, 108, 255, 0.3);
  margin-bottom: 20px;
}

.hero h2 {
  margin: 0 0 8px;
  font-size: 24px;
}

.hero p {
  margin: 0;
  opacity: 0.85;
}

.hero-icon {
  width: 68px;
  height: 68px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.16);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(4px);
}

.stats {
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 14px;
  padding: 20px 22px;
  box-shadow: 0 8px 30px rgba(31, 36, 56, 0.06);
  margin-bottom: 20px;
  position: relative;
  overflow: hidden;
}

.stat-card::after {
  content: '';
  position: absolute;
  right: -20px;
  top: -20px;
  width: 90px;
  height: 90px;
  border-radius: 50%;
  background: var(--c);
  opacity: 0.1;
}

.stat-icon {
  width: 46px;
  height: 46px;
  border-radius: 12px;
  background: var(--c);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 14px;
}

.stat-value {
  font-size: 30px;
  font-weight: 800;
  color: #2b2f3a;
}

.stat-label {
  color: #9aa1b0;
  font-size: 13px;
  margin-top: 4px;
}

.quick-title {
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 18px;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 14px;
}

.quick-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px;
  border-radius: 12px;
  background: #f7f8fc;
  color: #4a4f5e;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
}

.quick-item:hover {
  background: #eef0ff;
  color: #5b6cff;
  transform: translateY(-2px);
}
</style>
