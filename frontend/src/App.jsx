import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import { AuthProvider } from './context/AuthContext'
import { ThemeProvider } from './context/ThemeContext'
import ProtectedRoute from './components/layout/ProtectedRoute'
import AdminRoute from './components/layout/AdminRoute'
import AppLayout from './components/layout/AppLayout'

// Public pages
import LandingPage from './pages/LandingPage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import ForgotPassword from './pages/ForgotPassword'
import ResetPassword from './pages/ResetPassword'

// User pages
import Dashboard from './pages/Dashboard'
import ResumeUpload from './pages/ResumeUpload'
import AnalysisPage from './pages/AnalysisPage'
import ScoreDetail from './pages/ScoreDetail'
import ScoreHistory from './pages/ScoreHistory'
import ReportsPage from './pages/ReportsPage'
import JobRoles from './pages/JobRoles'
import SubscriptionPage from './pages/SubscriptionPage'
import ProfilePage from './pages/ProfilePage'

// Admin pages
import AdminDashboard from './pages/admin/AdminDashboard'
import AdminUsers from './pages/admin/AdminUsers'
import AdminRoles from './pages/admin/AdminRoles'
import AdminScans from './pages/admin/AdminScans'

export default function App() {
  return (
    <ThemeProvider>
      <AuthProvider>
        <BrowserRouter>
          <Toaster
            position="top-right"
            toastOptions={{
              style: {
                background: '#1a1a2e',
                color: '#f1f0ff',
                border: '1px solid rgba(255,255,255,0.1)',
                borderRadius: '12px',
                fontSize: '0.875rem',
              },
              success: { iconTheme: { primary: '#10b981', secondary: '#fff' } },
              error:   { iconTheme: { primary: '#ef4444', secondary: '#fff' } },
            }}
          />
          <Routes>
            {/* Public routes */}
            <Route path="/" element={<LandingPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
            <Route path="/reset-password" element={<ResetPassword />} />

            {/* Protected user routes */}
            <Route element={<ProtectedRoute />}>
              <Route element={<AppLayout />}>
                <Route path="/dashboard"     element={<Dashboard />} />
                <Route path="/upload"        element={<ResumeUpload />} />
                <Route path="/analyze/:id"   element={<AnalysisPage />} />
                <Route path="/scores/:id"    element={<ScoreDetail />} />
                <Route path="/history"       element={<ScoreHistory />} />
                <Route path="/reports"       element={<ReportsPage />} />
                <Route path="/roles"         element={<JobRoles />} />
                <Route path="/subscription"  element={<SubscriptionPage />} />
                <Route path="/profile"       element={<ProfilePage />} />

                {/* Admin routes */}
                <Route element={<AdminRoute />}>
                  <Route path="/admin"           element={<AdminDashboard />} />
                  <Route path="/admin/users"     element={<AdminUsers />} />
                  <Route path="/admin/roles"     element={<AdminRoles />} />
                  <Route path="/admin/scans"     element={<AdminScans />} />
                </Route>
              </Route>
            </Route>

            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </ThemeProvider>
  )
}
