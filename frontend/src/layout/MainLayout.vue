<template>
  <el-container class="layout">
    <el-aside width="224px" class="sidebar">
      <div class="logo">
        <div class="logo-icon">
          <el-icon :size="22"><School /></el-icon>
        </div>
        <div class="logo-text">
          <div class="logo-title">学生管理系统</div>
          <div class="logo-sub">SMS Admin</div>
        </div>
      </div>

      <el-menu
        :default-active="activeMenu"
        router
        class="menu"
        background-color="transparent"
        text-color="#aab3c8"
        active-text-color="#ffffff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item v-if="isAdmin" index="/students">
          <el-icon><User /></el-icon>
          <span>学生管理</span>
        </el-menu-item>
        <el-menu-item v-if="isAdmin" index="/grades">
          <el-icon><DataAnalysis /></el-icon>
          <span>成绩管理</span>
        </el-menu-item>
        <el-menu-item v-if="!isAdmin" index="/my-grades">
          <el-icon><DataLine /></el-icon>
          <span>我的成绩</span>
        </el-menu-item>
        <el-menu-item index="/notifications">
          <el-icon><Bell /></el-icon>
          <span>通知中心</span>
        </el-menu-item>
        <el-menu-item index="/profile">
          <el-icon><Setting /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer">
        <el-icon><Cpu /></el-icon>
        <span>Student Management v1.0</span>
      </div>
    </el-aside>

    <el-container class="body">
      <el-header class="header" height="64px">
        <div class="header-left">
          <div class="header-title">{{ title }}</div>
          <div class="header-crumb">首页 / {{ title }}</div>
        </div>

        <el-dropdown trigger="click" @command="onCommand">
          <div class="user-chip">
            <el-avatar :size="34" class="avatar">{{ avatarText }}</el-avatar>
            <div class="user-meta">
              <div class="user-name">{{ user?.username }}</div>
              <el-tag :type="isAdmin ? 'danger' : 'success'" size="small" effect="light">
                {{ isAdmin ? '管理员' : '学生' }}
              </el-tag>
            </div>
            <el-icon class="caret"><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>个人中心
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided>
                <el-icon><SwitchButton /></el-icon>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main class="main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { user, isAdmin, logout } from '../store/auth'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => route.path)
const title = computed(() => route.meta.title || '首页')
const avatarText = computed(() => (user.value?.username || '?').charAt(0).toUpperCase())

function onCommand(cmd) {
  if (cmd === 'logout') {
    logout()
    router.push('/login')
  } else if (cmd === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.layout {
  height: 100vh;
}

.sidebar {
  background: var(--sidebar-bg);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 20px;
}

.logo-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #5b6cff, #8b5cf6);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 16px rgba(91, 108, 255, 0.4);
}

.logo-title {
  color: #fff;
  font-weight: 700;
  font-size: 16px;
  letter-spacing: 0.5px;
}

.logo-sub {
  color: #6b7590;
  font-size: 11px;
  letter-spacing: 1px;
}

.menu {
  flex: 1;
  border-right: none;
  padding: 8px 12px;
}

.menu :deep(.el-menu-item) {
  height: 48px;
  border-radius: 10px;
  margin-bottom: 4px;
}

.menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.06);
}

.menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #5b6cff, #8b5cf6);
  box-shadow: 0 6px 16px rgba(91, 108, 255, 0.35);
}

.sidebar-footer {
  padding: 16px 20px;
  color: #6b7590;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.body {
  background: var(--bg);
}

.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.04);
}

.header-title {
  font-size: 18px;
  font-weight: 700;
}

.header-crumb {
  font-size: 12px;
  color: #9aa1b0;
  margin-top: 2px;
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 10px;
  transition: background 0.2s;
}

.user-chip:hover {
  background: #f4f6fb;
}

.avatar {
  background: linear-gradient(135deg, #5b6cff, #8b5cf6);
  color: #fff;
  font-weight: 700;
}

.user-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  line-height: 1;
}

.caret {
  color: #9aa1b0;
  font-size: 12px;
}

.main {
  padding: 24px;
  overflow: auto;
}
</style>
