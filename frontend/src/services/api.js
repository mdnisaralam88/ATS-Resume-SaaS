import axios from 'axios'
import toast from 'react-hot-toast'

const api = axios.create({
  baseURL: '/api/v1',
  headers: { 'Content-Type': 'application/json' },
  timeout: 30000,
})

// Request interceptor — attach JWT
api.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
}, err => Promise.reject(err))

// Response interceptor — handle 401 and refresh
api.interceptors.response.use(
  res => res,
  async err => {
    const original = err.config
    if (err.response?.status === 401 && !original._retry) {
      original._retry = true
      const refreshToken = localStorage.getItem('refreshToken')
      if (refreshToken) {
        try {
          const { data } = await axios.post('/api/v1/auth/refresh-token', null, {
            params: { token: refreshToken }
          })
          if (data.data?.accessToken) {
            localStorage.setItem('accessToken', data.data.accessToken)
            original.headers.Authorization = `Bearer ${data.data.accessToken}`
            return api(original)
          }
        } catch {
          localStorage.clear()
          window.location.href = '/login'
        }
      } else {
        localStorage.clear()
        window.location.href = '/login'
      }
    }
    const message = err.response?.data?.message || err.message || 'Something went wrong'
    if (err.response?.status !== 401) toast.error(message)
    return Promise.reject(err)
  }
)

export default api
