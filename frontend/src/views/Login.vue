<template>
  <div class="login-page">
    <div class="brand">
      <div class="brand-inner">
        <div class="brand-badge">
          <el-icon :size="34"><School /></el-icon>
        </div>
        <h1>学生信息管理系统</h1>
        <p class="brand-sub">Student Management System</p>
        <ul class="features">
          <li><el-icon><CircleCheck /></el-icon> 学生信息一站式管理</li>
          <li><el-icon><CircleCheck /></el-icon> 成绩录入与自动站内通知</li>
          <li><el-icon><CircleCheck /></el-icon> 管理员 / 学生角色隔离</li>
        </ul>
      </div>
    </div>

    <div class="form-side">
      <div class="form-card">
        <div class="form-head">
          <h2>{{ mode === 'login' ? '欢迎回来' : '创建账号' }}</h2>
          <p>{{ mode === 'login' ? '请登录以继续使用系统' : '注册一个学生账号' }}</p>
        </div>

        <el-tabs v-model="mode" stretch class="tabs">
          <el-tab-pane label="登录" name="login" />
          <el-tab-pane label="注册" name="register" />
        </el-tabs>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          @submit.prevent="onSubmit"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="form.username"
              size="large"
              placeholder="请输入用户名 / 学号"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              size="large"
              type="password"
              show-password
              placeholder="请输入密码"
              :prefix-icon="Lock"
            />
          </el-form-item>

          <el-form-item v-if="mode === 'register'" label="确认密码" prop="confirm">
            <el-input
              v-model="form.confirm"
              size="large"
              type="password"
              show-password
              placeholder="请再次输入密码"
              :prefix-icon="Lock"
            />
          </el-form-item>

          <el-form-item v-if="mode === 'register'" label="邮箱" prop="email">
            <el-input
              v-model="form.email"
              size="large"
              placeholder="请输入邮箱（选填）"
              :prefix-icon="Message"
            />
          </el-form-item>

          <el-button
            type="primary"
            size="large"
            class="submit"
            :loading="loading"
            native-type="submit"
          >
            {{ mode === 'login' ? '登 录' : '注 册' }}
          </el-button>
        </el-form>

        <div class="tip">
          默认管理员：admin / admin123；学生账号密码默认 123456
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message } from '@element-plus/icons-vue'
import { authApi } from '../api'
import { setUser } from '../store/auth'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const loading = ref(false)
const mode = ref('login')

const form = reactive({
  username: '',
  password: '',
  confirm: '',
  email: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirm: [
    {
      validator: (rule, value, cb) => {
        if (mode.value === 'register' && !value) cb(new Error('请再次输入密码'))
        else if (value !== form.password) cb(new Error('两次密码不一致'))
        else cb()
      },
      trigger: 'blur'
    }
  ],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]
}

async function onSubmit() {
  await formRef.value.validate()
  loading.value = true
  try {
    if (mode.value === 'login') {
      const user = await authApi.login({ username: form.username, password: form.password })
      setUser(user)
      ElMessage.success('登录成功')
      router.push(route.query.redirect || '/dashboard')
    } else {
      await authApi.register({
        username: form.username,
        password: form.password,
        role: 'student',
        email: form.email
      })
      ElMessage.success('注册成功，请登录')
      mode.value = 'login'
      form.password = ''
      form.confirm = ''
    }
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  background: #fff;
}

.brand {
  flex: 1.1;
  background: linear-gradient(135deg, #2a2f4a 0%, #4a55e0 55%, #8b5cf6 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.brand::after {
  content: '';
  position: absolute;
  width: 480px;
  height: 480px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.06);
  top: -120px;
  right: -120px;
}

.brand::before {
  content: '';
  position: absolute;
  width: 360px;
  height: 360px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
  bottom: -100px;
  left: -80px;
}

.brand-inner {
  position: relative;
  z-index: 1;
  max-width: 440px;
  padding: 40px;
}

.brand-badge {
  width: 68px;
  height: 68px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 28px;
  backdrop-filter: blur(4px);
}

.brand h1 {
  font-size: 34px;
  margin: 0 0 10px;
  letter-spacing: 1px;
}

.brand-sub {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.7);
  margin: 0 0 36px;
  letter-spacing: 2px;
}

.features {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.features li {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
  color: rgba(255, 255, 255, 0.92);
}

.form-side {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f7f8fc;
  padding: 32px;
}

.form-card {
  width: 100%;
  max-width: 400px;
  background: #fff;
  border-radius: 18px;
  padding: 40px 36px;
  box-shadow: 0 20px 60px rgba(31, 36, 56, 0.1);
}

.form-head h2 {
  margin: 0 0 6px;
  font-size: 24px;
}

.form-head p {
  margin: 0 0 8px;
  color: #9aa1b0;
  font-size: 14px;
}

.tabs {
  margin-bottom: 8px;
}

.submit {
  width: 100%;
  margin-top: 8px;
  letter-spacing: 4px;
  font-weight: 600;
}

.tip {
  margin-top: 20px;
  text-align: center;
  font-size: 12px;
  color: #b3b9c6;
  line-height: 1.6;
}

@media (max-width: 860px) {
  .brand {
    display: none;
  }
}
</style>
