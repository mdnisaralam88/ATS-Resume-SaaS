import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { motion } from 'framer-motion'
import { LogIn, Mail, Lock, Zap, ArrowRight, Loader2 } from 'lucide-react'
import toast from 'react-hot-toast'

export default function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!email || !password) return toast.error('Please fill in all fields')

    setLoading(true)
    try {
      await login(email, password)
      toast.success('Welcome back!')
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
          <h2 className="mb-xs">Welcome Back</h2>
          <p className="text-muted">Enter your credentials to access your dashboard</p>
        </div>

        <form onSubmit={handleSubmit} className="flex flex-col gap-lg">
          <div className="form-group">
            <label className="form-label">Email Address</label>
            <div style={{ position: 'relative' }}>
              <Mail 
                size={18} 
                style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }} 
                className="text-muted" 
              />
              <input 
                type="email" 
                className="form-input" 
                placeholder="name@company.com"
                style={{ paddingLeft: '40px' }}
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <div className="flex justify-between items-center">
              <label className="form-label">Password</label>
              <Link to="/forgot-password" style={{ fontSize: '0.8rem' }}>Forgot password?</Link>
            </div>
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
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
          </div>

          <button 
            type="submit" 
            className="btn btn-primary btn-lg btn-full"
            disabled={loading}
          >
            {loading ? <Loader2 className="spinner-sm" /> : 'Login to ResumeIQ'}
            {!loading && <ArrowRight size={18} className="ml-xs" />}
          </button>
        </form>

        <div className="text-center mt-xl">
          <p className="text-sm">
            Don't have an account? <Link to="/register" className="font-bold">Sign up free</Link>
          </p>
        </div>
      </motion.div>
    </div>
  )
}
