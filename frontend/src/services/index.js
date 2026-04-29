import api from './api'

export const resumeService = {
  upload: async (file, title) => {
    const formData = new FormData()
    formData.append('file', file)
    if (title) formData.append('title', title)
    const res = await api.post('/resumes/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    return res.data.data
  },
  getAll: async (page = 0, size = 10) => {
    const res = await api.get('/resumes', { params: { page, size } })
    return res.data.data
  },
  getById: async (id) => {
    const res = await api.get(`/resumes/${id}`)
    return res.data.data
  },
  delete: async (id) => {
    const res = await api.delete(`/resumes/${id}`)
    return res.data
  },
}

export const scoreService = {
  analyze: async (resumeId, jobRoleId) => {
    const res = await api.post('/scores/analyze', { resumeId, jobRoleId })
    return res.data.data
  },
  getHistory: async (page = 0, size = 10) => {
    const res = await api.get('/scores', { params: { page, size } })
    return res.data.data
  },
  getById: async (id) => {
    const res = await api.get(`/scores/${id}`)
    return res.data.data
  },
  getLatest: async () => {
    const res = await api.get('/scores/latest')
    return res.data.data
  },
}

export const jobRoleService = {
  getAll: async () => {
    const res = await api.get('/roles')
    return res.data.data
  },
  getById: async (id) => {
    const res = await api.get(`/roles/${id}`)
    return res.data.data
  },
  create: async (data) => {
    const res = await api.post('/roles', data)
    return res.data.data
  },
  update: async (id, data) => {
    const res = await api.put(`/roles/${id}`, data)
    return res.data.data
  },
  delete: async (id) => {
    const res = await api.delete(`/roles/${id}`)
    return res.data
  },
}

export const reportService = {
  generate: async (atsScoreId) => {
    const res = await api.post(`/reports/generate/${atsScoreId}`)
    return res.data.data
  },
  getAll: async () => {
    const res = await api.get('/reports')
    return res.data.data
  },
  download: async (reportId) => {
    const res = await api.get(`/reports/download/${reportId}`, { responseType: 'blob' })
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `ats-report-${reportId}.pdf`)
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)
  },
}

export const subscriptionService = {
  getMyPlan: async () => {
    const res = await api.get('/subscriptions/me')
    return res.data.data
  },
  getPlans: async () => {
    const res = await api.get('/subscriptions/plans')
    return res.data.data
  },
  upgrade: async (plan) => {
    const res = await api.post('/subscriptions/upgrade', null, { params: { plan } })
    return res.data.data
  },
}

export const userService = {
  getProfile: async () => {
    const res = await api.get('/users/me')
    return res.data.data
  },
  updateProfile: async (data) => {
    const res = await api.put('/users/me', data)
    return res.data.data
  },
  uploadProfileImage: async (file) => {
    const formData = new FormData()
    formData.append('file', file)
    const res = await api.post('/users/me/profile-image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    return res.data.data
  },
}

export const notificationService = {
  getAll: async (page = 0, size = 20) => {
    const res = await api.get('/notifications', { params: { page, size } })
    return res.data.data
  },
  getUnreadCount: async () => {
    const res = await api.get('/notifications/unread-count')
    return res.data.data.count
  },
  markAllRead: async () => {
    await api.put('/notifications/mark-all-read')
  },
  markRead: async (id) => {
    await api.put(`/notifications/${id}/read`)
  },
}

export const adminService = {
  getDashboard: async () => {
    const res = await api.get('/admin/dashboard')
    return res.data.data
  },
  getUsers: async (page = 0, size = 20, search = '') => {
    const res = await api.get('/admin/users', { params: { page, size, search } })
    return res.data.data
  },
  toggleUserStatus: async (id) => {
    const res = await api.put(`/admin/users/${id}/toggle-status`)
    return res.data.data
  },
}
