import { createRouter, createWebHistory } from 'vue-router'
import { user, isAdmin } from '../store/auth'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/Login.vue'),
    meta: { public: true, title: '登录' }
  },
  {
    path: '/',
    component: () => import('../layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'students',
        name: 'students',
        component: () => import('../views/Students.vue'),
        meta: { title: '学生管理', admin: true }
      },
      {
        path: 'grades',
        name: 'grades',
        component: () => import('../views/Grades.vue'),
        meta: { title: '成绩管理', admin: true }
      },
      {
        path: 'my-grades',
        name: 'my-grades',
        component: () => import('../views/MyGrades.vue'),
        meta: { title: '我的成绩' }
      },
      {
        path: 'notifications',
        name: 'notifications',
        component: () => import('../views/Notifications.vue'),
        meta: { title: '通知中心' }
      },
      {
        path: 'profile',
        name: 'profile',
        component: () => import('../views/Profile.vue'),
        meta: { title: '个人中心' }
      }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (!to.meta.public && !user.value) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.name === 'login' && user.value) {
    return { path: '/dashboard' }
  }
  if (to.meta.admin && !isAdmin.value) {
    return { path: '/dashboard' }
  }
  return true
})

export default router
