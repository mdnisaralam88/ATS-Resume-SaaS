import { useState } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { authService } from '../services/authService'
import { motion } from 'framer-motion'
import { Lock, Zap, ArrowRight, Loader2, CheckCircle2 } from 'lucide-react'
import toast from 'react-hot-toast'

export default function ResetPassword() {
  const [searchParams] = useSearchParams()
  const token = searchParams.get('token')
  const navigate = useNavigate()
  
  const [passwords, setPasswords] = useState({
    password: '',
    confirmPassword: ''
  })
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!token) return toast.error('Invalid or missing reset token')
    if (passwords.password !== passwords.confirmPassword) return toast.error('Passwords do not match')
    if (passwords.password.length < 6) return toast.error('Password must be at least 6 characters')

    setLoading(true)
    try {
      await authService.resetPassword(token, passwords.password)
      toast.success('Password reset successfully. You can now login.')
      navigate('/login')
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  if (!token) return (
    <div className="auth-page flex items-center justify-center min-h-screen">
       <div className="card text-center p-2xl">
          <h3 className="text-danger mb-md">Invalid Reset Link</h3>
          <p className="text-muted mb-lg">This link is either broken or has expired.</p>
          <Link to="/forgot-password" title="Request new link" className="btn btn-primary">Request New Link</Link>
       </div>
    </div>
  )

  return (
    <div className="auth-page flex items-center justify-center min-h-screen p-md">
      <motion.div 
        className="auth-card card p-2xl w-full"
        style={{ maxWidth: '450px' }}
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
      >
        <div className="text-center mb-xl">
          <div className="sidebar-logo flex justify-center mb-md">
            <div className="sidebar-logo-icon">
              <Zap size={20} color="#fff" />
            </div>
            <span className="sidebar-logo-text">ResumeIQ</span>
          </div>
          <h2 className="mb-xs">Reset Your Password</h2>
          <p className="text-muted">Enter a new secure password for your account</p>
        </div>

        <form onSubmit={handleSubmit} className="flex flex-col gap-lg">
          <div className="form-group">
            <label className="form-label">New Password</label>
            <div style={{ position: 'relative' }}>
              <Lock 
                size={18} 
                style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }} 
                className="text-muted" 
              />
              <input 
                type="password" 
                className="form-input" 
                placeholder="••••••••"
                style={{ paddingLeft: '40px' }}
                value={passwords.password}
                onChange={(e) => setPasswords({ ...passwords, password: e.target.value })}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Confirm New Password</label>
            <div style={{ position: 'relative' }}>
              <Lock 
                size={18} 
                style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }} 
                className="text-muted" 
              />
              <input 
                type="password" 
                className="form-input" 
                placeholder="••••••••"
                style={{ paddingLeft: '40px' }}
                value={passwords.confirmPassword}
                onChange={(e) => setPasswords({ ...passwords, confirmPassword: e.target.value })}
                required
              />
            </div>
          </div>

          <button 
            type="submit" 
            className="btn btn-primary btn-lg btn-full"
            disabled={loading}
          >
            {loading ? <Loader2 className="spinner-sm" /> : 'Reset Password'}
            {!loading && <ArrowRight size={18} className="ml-xs" />}
          </button>
        </form>
      </motion.div>
    </div>
  )
}
