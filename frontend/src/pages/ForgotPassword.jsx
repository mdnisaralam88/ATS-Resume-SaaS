import { useState } from 'react'
import { Link } from 'react-router-dom'
import { authService } from '../services/authService'
import { motion } from 'framer-motion'
import { Mail, Zap, ArrowRight, ChevronLeft, Loader2, CheckCircle } from 'lucide-react'
import toast from 'react-hot-toast'

export default function ForgotPassword() {
  const [email, setEmail] = useState('')
  const [loading, setLoading] = useState(false)
  const [submitted, setSubmitted] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      await authService.forgotPassword(email)
      setSubmitted(true)
      toast.success('Reset link sent to your email')
    } catch (err) {
      console.error(err)
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
        <div className="mb-lg">
          <Link to="/login" className="flex items-center gap-xs text-xs text-muted hover:text-primary transition mb-lg">
            <ChevronLeft size={14} /> Back to login
          </Link>
          <div className="sidebar-logo mb-md">
            <div className="sidebar-logo-icon">
              <Zap size={20} color="#fff" />
            </div>
            <span className="sidebar-logo-text">ResumeIQ</span>
          </div>
          <h2 className="mb-xs">Forgot Password?</h2>
          <p className="text-muted">Enter your email and we'll send you a link to reset your password.</p>
        </div>

        {submitted ? (
          <div className="text-center py-xl">
             <div className="feature-icon bg-success mx-auto mb-lg" style={{ width: '64px', height: '64px' }}>
                <CheckCircle size={32} color="#fff" />
             </div>
             <h3 className="mb-md">Check your inbox</h3>
             <p className="text-sm text-muted mb-xl">
               If an account exists for {email}, you will receive a password reset link shortly.
             </p>
             <button className="btn btn-secondary btn-full" onClick={() => setSubmitted(false)}>
               Try another email
             </button>
          </div>
        ) : (
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

            <button 
              type="submit" 
              className="btn btn-primary btn-lg btn-full"
              disabled={loading}
            >
              {loading ? <Loader2 className="spinner-sm" /> : 'Send Reset Link'}
              {!loading && <ArrowRight size={18} className="ml-xs" />}
            </button>
          </form>
        )}
      </motion.div>
    </div>
  )
}
