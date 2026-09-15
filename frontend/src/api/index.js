import axios from 'axios'

const http = axios.create({
  baseURL: '/api',
  timeout: 10000
})

http.interceptors.response.use(
  (res) => {
    const body = res.data
    if (body && body.code !== 0) {
      return Promise.reject(new Error(body.message || '请求失败'))
    }
    return body.data
  },
  (err) => Promise.reject(err)
)

export const authApi = {
  login: (data) => http.post('/auth/login', data),
  register: (data) => http.post('/auth/register', data)
}

export const studentApi = {
  list: () => http.get('/students'),
  search: (keyword) => http.get('/students/search', { params: { keyword } }),
  add: (data) => http.post('/students', data),
  update: (id, data) => http.put(`/students/${id}`, data),
  remove: (id) => http.delete(`/students/${id}`)
}

export const gradeApi = {
  list: () => http.get('/grades'),
  search: (params) => http.get('/grades/search', { params }),
  add: (data) => http.post('/grades', data),
  update: (id, data) => http.put(`/grades/${id}`, data),
  remove: (id) => http.delete(`/grades/${id}`)
}

export const notificationApi = {
  list: (username) => http.get('/notifications', { params: { username } }),
  markRead: (id) => http.post(`/notifications/${id}/read`),
  markAllRead: (username) => http.post('/notifications/read-all', null, { params: { username } })
}

export const statsApi = {
  get: () => http.get('/stats')
}
