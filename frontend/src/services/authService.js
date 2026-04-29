import api from './api'

export const authService = {
  register: async (data) => {
    const res = await api.post('/auth/register', data)
    return res.data.data
  },
  login: async (data) => {
    const res = await api.post('/auth/login', data)
    return res.data.data
  },
  logout: async () => {
    await api.post('/auth/logout')
  },
  refreshToken: async (token) => {
    const res = await api.post('/auth/refresh-token', null, { params: { token } })
    return res.data.data
  },
  forgotPassword: async (email) => {
    const res = await api.post('/auth/forgot-password', { email })
    return res.data
  },
  resetPassword: async (token, newPassword) => {
    const res = await api.post('/auth/reset-password', { token, newPassword })
    return res.data
  },
  changePassword: async (currentPassword, newPassword) => {
    const res = await api.put('/auth/change-password', { currentPassword, newPassword })
    return res.data
  },
  verifyEmail: async (token) => {
    const res = await api.get('/auth/verify-email', { params: { token } })
    return res.data
  },
}
