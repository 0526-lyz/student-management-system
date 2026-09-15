import { ref, computed } from 'vue'

const KEY = 'sms_user'

function load() {
  try {
    const raw = localStorage.getItem(KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

const user = ref(load())

export const isLogin = computed(() => !!user.value)
export const isAdmin = computed(() => user.value?.role === 'admin')

export function setUser(u) {
  user.value = u
  localStorage.setItem(KEY, JSON.stringify(u))
}

export function logout() {
  user.value = null
  localStorage.removeItem(KEY)
}

export { user }
