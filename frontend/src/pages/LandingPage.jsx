import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { 
  Zap, Shield, Target, BarChart, 
  ArrowRight, CheckCircle, FileText, 
  Search, Award, Star
} from 'lucide-react'
import './LandingPage.css'

export default function LandingPage() {
  const fadeIn = {
    initial: { opacity: 0, y: 20 },
    animate: { opacity: 1, y: 0 },
    transition: { duration: 0.6 }
  }

  const stagger = {
    animate: {
      transition: {
        staggerChildren: 0.1
      }
    }
  }

  return (
    <div className="landing-page">
      {/* Navigation */}
      <nav className="landing-nav">
        <div className="landing-container flex justify-between items-center">
          <div className="landing-logo">
            <div className="sidebar-logo-icon">
              <Zap size={20} color="#fff" />
            </div>
            <span className="sidebar-logo-text">ResumeIQ</span>
          </div>
          <div className="landing-nav-links flex items-center gap-lg">
            <Link to="/login" className="btn btn-ghost">Login</Link>
            <Link to="/register" className="btn btn-primary">Get Started</Link>
          </div>
        </div>
      </nav>

      {/* Hero Section */}
      <section className="hero-section">
        <div className="landing-container">
          <motion.div 
            className="hero-content text-center"
            initial="initial"
            animate="animate"
            variants={stagger}
          >
            <motion.div variants={fadeIn} className="badge badge-primary mb-md">
              <Star size={12} className="mr-xs" />
              AI-Powered ATS Analysis
            </motion.div>
            <motion.h1 variants={fadeIn} className="hero-title mb-md">
              Beat the Bots. <br />
              <span className="text-gradient">Get Hired Faster.</span>
            </motion.h1>
            <motion.p variants={fadeIn} className="hero-description mb-lg">
              ResumeIQ uses advanced AI to analyze your resume against job descriptions, 
              giving you the exact keywords and formatting tips you need to pass ATS 
              filters and land interviews.
            </motion.p>
            <motion.div variants={fadeIn} className="hero-actions flex items-center justify-center gap-md">
              <Link to="/register" className="btn btn-primary btn-lg">
                Analyze My Resume <ArrowRight size={18} className="ml-xs" />
              </Link>
              <a href="#how-it-works" className="btn btn-secondary btn-lg">
                See How It Works
              </a>
            </motion.div>
            
            <motion.div variants={fadeIn} className="hero-preview mt-2xl">
              <div className="preview-card">
                <div className="preview-header">
                  <div className="flex gap-xs">
                    <div className="dot red"></div>
                    <div className="dot yellow"></div>
                    <div className="dot green"></div>
                  </div>
                </div>
                <div className="preview-body">
                   {/* Abstract representation of dashboard */}
                   <div className="flex gap-lg">
                      <div className="preview-sidebar"></div>
                      <div className="preview-main">
                        <div className="flex justify-between mb-lg">
                          <div className="preview-box-lg"></div>
                          <div className="preview-box-sm"></div>
                        </div>
                        <div className="preview-chart"></div>
                      </div>
                   </div>
                </div>
              </div>
            </motion.div>
          </motion.div>
        </div>
      </section>

      {/* Features Section */}
      <section id="features" className="features-section">
        <div className="landing-container">
          <div className="section-header text-center mb-2xl">
            <h2 className="section-title mb-sm">Why Choose ResumeIQ?</h2>
            <p className="section-subtitle">The most advanced resume analysis engine on the market.</p>
          </div>
          
          <div className="grid-3">
            {[
              { 
                icon: Target, 
                title: 'Keyword Matching', 
                desc: 'Identify critical missing keywords that recruiters are looking for.' 
              },
              { 
                icon: BarChart, 
                title: 'ATS Scoring', 
                desc: 'Get an instant score out of 100 based on modern ATS algorithms.' 
              },
              { 
                icon: Shield, 
                title: 'Format Verification', 
                desc: 'Ensure your resume is readable by automated systems without errors.' 
              },
              { 
                icon: FileText, 
                title: 'PDF Reports', 
                desc: 'Download detailed professional reports to guide your improvements.' 
              },
              { 
                icon: Search, 
                title: 'Role Comparison', 
                desc: 'Compare your resume against specific roles like Java Dev or DevOps.' 
              },
              { 
                icon: Award, 
                title: 'Expert Suggestions', 
                desc: 'Receive actionable tips to improve every section of your resume.' 
              }
            ].map((f, i) => (
              <div key={i} className="card card-glow p-lg">
                <div className="feature-icon mb-md">
                  <f.icon size={24} className="text-primary" />
                </div>
                <h3 className="mb-sm">{f.title}</h3>
                <p>{f.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="stats-section">
        <div className="landing-container">
          <div className="grid-4 text-center">
            {[
              { val: '95%', label: 'Success Rate' },
              { val: '10k+', label: 'Resumes Scanned' },
              { val: '24/7', label: 'AI Analysis' },
              { val: '4.9/5', label: 'User Rating' }
            ].map((s, i) => (
              <div key={i} className="stat-item">
                <h2 className="stat-value text-gradient">{s.val}</h2>
                <p className="stat-label">{s.label}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="landing-footer">
        <div className="landing-container">
          <div className="footer-content grid-4 mb-xl">
            <div className="footer-brand">
              <div className="landing-logo mb-md">
                <div className="sidebar-logo-icon">
                  <Zap size={20} color="#fff" />
                </div>
                <span className="sidebar-logo-text">ResumeIQ</span>
              </div>
              <p>Empowering job seekers with AI-driven insights to land their dream roles.</p>
            </div>
            <div>
              <h4 className="mb-md">Product</h4>
              <ul className="footer-links">
                <li><a href="#">Features</a></li>
                <li><a href="#">Pricing</a></li>
                <li><a href="#">Job Roles</a></li>
              </ul>
            </div>
            <div>
              <h4 className="mb-md">Company</h4>
              <ul className="footer-links">
                <li><a href="#">About Us</a></li>
                <li><a href="#">Contact</a></li>
                <li><a href="#">Privacy Policy</a></li>
              </ul>
            </div>
            <div>
              <h4 className="mb-md">Newsletter</h4>
              <p className="mb-sm">Get the latest career tips.</p>
              <div className="flex gap-xs">
                <input type="email" className="form-input" placeholder="Email address" />
                <button className="btn btn-primary btn-sm">Join</button>
              </div>
            </div>
          </div>
          <div className="footer-bottom text-center">
            <p>&copy; {new Date().getFullYear()} ResumeIQ. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </div>
  )
}
