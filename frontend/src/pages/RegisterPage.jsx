import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { motion } from 'framer-motion'
import { User, Mail, Lock, Zap, ArrowRight, Loader2, CheckCircle2 } from 'lucide-react'
import toast from 'react-hot-toast'

export default function RegisterPage() {
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
    confirmPassword: ''
  })
  const [loading, setLoading] = useState(false)
  const { register } = useAuth()
  const navigate = useNavigate()

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    const { fullName, email, password, confirmPassword } = formData

    if (!fullName || !email || !password) return toast.error('Please fill in all fields')
    if (password !== confirmPassword) return toast.error('Passwords do not match')
    if (password.length < 6) return toast.error('Password must be at least 6 characters')

    setLoading(true)
    try {
      await register(fullName, email, password)
      toast.success('Account created! Welcome to ResumeIQ.')
      navigate('/dashboard')
    } catch (err) {
      // Error handled by interceptor
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-page flex items-center justify-center min-h-screen p-md">
      <motion.div 
        className="auth-card card p-2xl w-full"
        style={{ maxWidth: '500px' }}
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
          <h2 className="mb-xs">Create Free Account</h2>
          <p className="text-muted">Start optimizing your resume with AI today</p>
        </div>

        <form onSubmit={handleSubmit} className="flex flex-col gap-md">
          <div className="form-group">
            <label className="form-label">Full Name</label>
            <div style={{ position: 'relative' }}>
              <User 
                size={18} 
                style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }} 
                className="text-muted" 
              />
              <input 
                name="fullName"
                type="text" 
                className="form-input" 
                placeholder="John Doe"
                style={{ paddingLeft: '40px' }}
                value={formData.fullName}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Email Address</label>
            <div style={{ position: 'relative' }}>
              <Mail 
                size={18} 
                style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }} 
                className="text-muted" 
              />
              <input 
                name="email"
                type="email" 
                className="form-input" 
                placeholder="john@example.com"
                style={{ paddingLeft: '40px' }}
                value={formData.email}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          <div className="grid-2">
            <div className="form-group">
              <label className="form-label">Password</label>
              <div style={{ position: 'relative' }}>
                <Lock 
                  size={18} 
                  style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }} 
                  className="text-muted" 
                />
                <input 
                  name="password"
                  type="password" 
                  className="form-input" 
                  placeholder="••••••••"
                  style={{ paddingLeft: '40px' }}
                  value={formData.password}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>
            <div className="form-group">
              <label className="form-label">Confirm</label>
              <div style={{ position: 'relative' }}>
                <Lock 
                  size={18} 
                  style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }} 
                  className="text-muted" 
                />
                <input 
                  name="confirmPassword"
                  type="password" 
                  className="form-input" 
                  placeholder="••••••••"
                  style={{ paddingLeft: '40px' }}
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>
          </div>

          <div className="mt-md">
            <div className="flex items-center gap-sm mb-xs">
              <CheckCircle2 size={14} className="text-success" />
              <span className="text-xs text-muted">ATS-compliant formatting analysis</span>
            </div>
            <div className="flex items-center gap-sm mb-xs">
              <CheckCircle2 size={14} className="text-success" />
              <span className="text-xs text-muted">2 scans/day on Free Plan</span>
            </div>
            <div className="flex items-center gap-sm">
              <CheckCircle2 size={14} className="text-success" />
              <span className="text-xs text-muted">Detailed improvement suggestions</span>
            </div>
          </div>

          <button 
            type="submit" 
            className="btn btn-primary btn-lg btn-full mt-md"
            disabled={loading}
          >
            {loading ? <Loader2 className="spinner-sm" /> : 'Create My Account'}
            {!loading && <ArrowRight size={18} className="ml-xs" />}
          </button>
        </form>

        <div className="text-center mt-xl">
          <p className="text-sm">
            Already have an account? <Link to="/login" className="font-bold">Login here</Link>
          </p>
        </div>
      </motion.div>
    </div>
  )
}
