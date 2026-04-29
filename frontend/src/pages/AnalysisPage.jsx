import { useState, useEffect } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { 
  resumeService, jobRoleService, scoreService, 
  subscriptionService 
} from '../services'
import { 
  Briefcase, Search, Filter, Loader2, 
  ArrowRight, ChevronLeft, Zap, Info
} from 'lucide-react'
import toast from 'react-hot-toast'
import { motion } from 'framer-motion'

export default function AnalysisPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [resume, setResume] = useState(null)
  const [roles, setRoles] = useState([])
  const [selectedRole, setSelectedRole] = useState(null)
  const [loading, setLoading] = useState(true)
  const [analyzing, setAnalyzing] = useState(false)
  const [searchTerm, setSearchTerm] = useState('')
  const [scanStatus, setScanStatus] = useState(null)

  useEffect(() => {
    async function init() {
      try {
        const [resumeData, rolesData, subData] = await Promise.all([
          resumeService.getById(id),
          jobRoleService.getAll(),
          subscriptionService.getMyPlan()
        ])
        setResume(resumeData)
        setRoles(rolesData)
        setScanStatus(subData)
      } catch (err) {
        console.error(err)
      } finally {
        setLoading(false)
      }
    }
    init()
  }, [id])

  const handleAnalyze = async () => {
    if (!selectedRole) return toast.error('Please select a target job role')
    
    setAnalyzing(true)
    try {
      const result = await scoreService.analyze(id, selectedRole.id)
      toast.success('Analysis complete!')
      navigate(`/scores/${result.id}`)
    } catch (err) {
      if (err.response?.status === 403 || err.response?.data?.error === 'ScanLimitExceeded') {
        toast.error('Daily scan limit reached. Please upgrade your plan.')
      }
    } finally {
      setAnalyzing(false)
    }
  }

  const filteredRoles = roles.filter(role => 
    role.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    role.category.toLowerCase().includes(searchTerm.toLowerCase())
  )

  if (loading) return (
    <div className="loading-overlay">
      <div className="spinner" />
      <p>Preparing analyzer...</p>
    </div>
  )

  return (
    <div className="page-container animate-fade-in">
      <header className="page-header flex justify-between items-center">
        <div>
          <Link to="/upload" className="flex items-center gap-xs text-muted mb-sm hover:text-primary transition">
            <ChevronLeft size={16} /> Back to upload
          </Link>
          <h1 className="page-title">Step 2: Select Target Role</h1>
          <p className="page-subtitle">Comparing: <span className="font-bold text-primary-light">{resume.originalFileName}</span></p>
        </div>
        
        {scanStatus && (
          <div className="badge badge-info p-md flex items-center gap-md">
             <Zap size={16} />
             <span>{scanStatus.remainingScansToday} scans remaining today</span>
          </div>
        )}
      </header>

      <div className="grid-2" style={{ gridTemplateColumns: '1fr 350px' }}>
        <div className="flex flex-col gap-lg">
          {/* Role Search */}
          <div className="card p-md">
            <div className="flex items-center gap-md bg-elevated rounded-md px-md border border-border focus-within:border-primary transition">
              <Search size={18} className="text-muted" />
              <input 
                type="text" 
                className="form-input" 
                placeholder="Search job roles (e.g. Java, Frontend, DevOps)..."
                style={{ border: 'none', background: 'transparent', boxShadow: 'none' }}
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
          </div>

          {/* Role Grid */}
          <div className="grid-2">
            {filteredRoles.map(role => (
              <motion.div 
                key={role.id}
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
                className={`card cursor-pointer transition-all ${selectedRole?.id === role.id ? 'border-primary bg-elevated shadow-glow' : 'hover:border-border-strong'}`}
                onClick={() => setSelectedRole(role)}
              >
                <div className="flex justify-between items-start mb-md">
                  <div className="feature-icon" style={{ background: 'rgba(124, 58, 237, 0.1)' }}>
                    <Briefcase size={20} className="text-primary" />
                  </div>
                  <div className="badge badge-primary">{role.category}</div>
                </div>
                <h3 className="text-lg mb-xs">{role.name}</h3>
                <p className="text-xs text-muted line-clamp-2">{role.description}</p>
                <div className="mt-md flex items-center gap-xs text-xs font-bold text-primary-light">
                   {role.keywords?.length || 0} Keywords library <ArrowRight size={12} />
                </div>
              </motion.div>
            ))}
          </div>
        </div>

        {/* Sidebar / Selection Summary */}
        <div className="sticky-sidebar">
          <div className="card sticky" style={{ top: 'calc(var(--topbar-height) + 20px)' }}>
            <h3 className="mb-lg">Analysis Summary</h3>
            
            <div className="flex flex-col gap-md">
              <div className="p-md bg-surface rounded-md border border-border">
                <p className="text-xs text-muted uppercase font-bold mb-xs">Selected Resume</p>
                <p className="text-sm font-bold truncate">{resume.originalFileName}</p>
              </div>

              {selectedRole ? (
                <motion.div 
                  initial={{ opacity: 0, x: 20 }}
                  animate={{ opacity: 1, x: 0 }}
                  className="p-md bg-surface rounded-md border border-primary"
                >
                  <p className="text-xs text-muted uppercase font-bold mb-xs">Target Role</p>
                  <p className="text-sm font-bold text-primary-light">{selectedRole.name}</p>
                  <div className="mt-md flex flex-wrap gap-xs">
                    {selectedRole.keywords?.slice(0, 5).map(kw => (
                      <span key={kw.id} className="badge badge-ghost text-[10px]">{kw.keyword}</span>
                    ))}
                    {(selectedRole.keywords?.length > 5) && (
                      <span className="text-[10px] text-muted">+{selectedRole.keywords.length - 5} more</span>
                    )}
                  </div>
                </motion.div>
              ) : (
                <div className="p-md border border-dashed border-border rounded-md text-center py-xl">
                  <p className="text-sm text-muted">Select a job role from the list to continue</p>
                </div>
              )}

              <div className="flex items-start gap-sm mt-md p-sm rounded bg-primary-glow">
                <Info size={16} className="text-primary-light flex-shrink-0 mt-xs" />
                <p className="text-[10px] leading-relaxed">
                  Our AI will scan your resume for {selectedRole?.keywords?.length || 'role'} specific keywords, 
                  formatting issues, and section completeness relative to industry standards for this position.
                </p>
              </div>

              <button 
                className="btn btn-primary btn-lg btn-full mt-lg"
                disabled={!selectedRole || analyzing}
                onClick={handleAnalyze}
              >
                {analyzing ? <Loader2 className="spinner-sm" /> : 'Start ATS Analysis'}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
