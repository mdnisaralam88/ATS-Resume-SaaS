import { useState, useEffect } from 'react'
import { jobRoleService } from '../services'
import { 
  Briefcase, Search, Filter, Tag, 
  Info, ChevronRight, ArrowRight,
  Database, Code, Terminal, Cpu, Globe, Cloud
} from 'lucide-react'
import { motion, AnimatePresence } from 'framer-motion'

const iconMap = {
  'Coffee': Database,
  'Layers': Globe,
  'Server': Terminal,
  'Monitor': Code,
  'Code': Cpu,
  'GitBranch': Cloud
}

export default function JobRoles() {
  const [roles, setRoles] = useState([])
  const [loading, setLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState('')
  const [selectedRole, setSelectedRole] = useState(null)

  useEffect(() => {
    jobRoleService.getAll()
      .then(setRoles)
      .finally(() => setLoading(false))
  }, [])

  const filteredRoles = roles.filter(role => 
    role.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    role.category.toLowerCase().includes(searchTerm.toLowerCase())
  )

  if (loading) return (
    <div className="loading-overlay">
      <div className="spinner" />
      <p>Loading role library...</p>
    </div>
  )

  return (
    <div className="page-container animate-fade-in">
      <header className="page-header flex justify-between items-end">
        <div>
          <h1 className="page-title">Role Library</h1>
          <p className="page-subtitle">Browse all supported job roles and their target keyword libraries.</p>
        </div>
        <div className="flex items-center gap-md bg-elevated px-md py-xs rounded-md border border-border">
          <Search size={18} className="text-muted" />
          <input 
            type="text" 
            placeholder="Search roles..." 
            className="form-input" 
            style={{ border: 'none', background: 'transparent', width: '250px' }}
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </header>

      <div className="grid-3">
        {filteredRoles.map(role => {
          const Icon = iconMap[role.iconName] || Briefcase
          return (
            <motion.div 
              key={role.id}
              className="card card-glow hover:border-primary cursor-pointer transition"
              onClick={() => setSelectedRole(role)}
              whileHover={{ translateY: -5 }}
            >
              <div className="flex justify-between items-start mb-lg">
                <div className="feature-icon bg-primary">
                  <Icon size={24} color="#fff" />
                </div>
                <div className="badge badge-primary">{role.category}</div>
              </div>
              
              <h3 className="mb-sm">{role.name}</h3>
              <p className="text-xs text-muted mb-lg line-clamp-3">{role.description}</p>
              
              <div className="flex items-center justify-between mt-auto pt-md border-top border-border">
                <span className="text-xs font-bold text-primary-light">
                  {role.keywords?.length || 0} Target Keywords
                </span>
                <ChevronRight size={18} className="text-muted" />
              </div>
            </motion.div>
          )
        })}
      </div>

      {/* Role Details Modal (Overlay) */}
      <AnimatePresence>
        {selectedRole && (
          <div className="modal-overlay" onClick={() => setSelectedRole(null)}>
            <motion.div 
              className="modal-content card p-2xl w-full"
              style={{ maxWidth: '800px' }}
              initial={{ opacity: 0, scale: 0.9, y: 20 }}
              animate={{ opacity: 1, scale: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.9, y: 20 }}
              onClick={(e) => e.stopPropagation()}
            >
              <div className="flex justify-between items-start mb-xl">
                <div className="flex items-center gap-lg">
                  <div className="feature-icon bg-primary" style={{ width: '60px', height: '60px' }}>
                    {(iconMap[selectedRole.iconName] ? React.createElement(iconMap[selectedRole.iconName], { size: 30, color: '#fff' }) : <Briefcase size={30} color="#fff" />)}
                  </div>
                  <div>
                    <h2 className="text-2xl mb-xs">{selectedRole.name}</h2>
                    <div className="flex gap-md">
                      <span className="badge badge-info">{selectedRole.category}</span>
                      <span className="badge badge-ghost">Level: {selectedRole.experienceLevel}</span>
                      <span className="badge badge-ghost">Exp: {selectedRole.minExperienceYears}-{selectedRole.maxExperienceYears} yrs</span>
                    </div>
                  </div>
                </div>
                <button className="btn btn-ghost btn-sm" onClick={() => setSelectedRole(null)}>Close</button>
              </div>

              <div className="grid-2 mt-lg">
                <div>
                  <h4 className="mb-md text-primary-light uppercase tracking-widest text-xs">Job Description</h4>
                  <p className="text-sm leading-relaxed text-secondary">{selectedRole.description}</p>
                  
                  <div className="mt-xl p-md bg-primary-glow border-primary rounded-md">
                     <div className="flex items-center gap-sm mb-sm text-primary-light">
                        <Info size={16} />
                        <span className="text-xs font-bold uppercase">ATS Tip</span>
                     </div>
                     <p className="text-xs text-secondary">
                       For this role, ATS systems prioritize technical skills and specific framework mentions. 
                       Ensure your 'Skills' section is clearly labeled and contains at least 70% of the keywords listed here.
                     </p>
                  </div>
                </div>

                <div>
                  <h4 className="mb-md text-primary-light uppercase tracking-widest text-xs">Keyword Library ({selectedRole.keywords?.length || 0})</h4>
                  <div className="flex flex-col gap-sm max-h-[300px] overflow-y-auto pr-sm">
                    {selectedRole.keywords?.map(kw => (
                      <div key={kw.id} className="flex justify-between items-center p-sm bg-elevated rounded border border-border">
                        <span className="text-sm font-medium">{kw.keyword}</span>
                        <div className="flex items-center gap-md">
                          <span className={`text-[10px] font-bold ${kw.type === 'REQUIRED' ? 'text-danger' : 'text-success'}`}>
                            {kw.type}
                          </span>
                          <div className="badge badge-ghost text-[10px]">W: {kw.weight}</div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              <div className="mt-2xl flex justify-end">
                <button 
                  className="btn btn-primary"
                  onClick={() => {
                    // Logic to start analysis with this role
                    // We'd need a resume first, so maybe redirect to upload with role preset?
                    // For now just close
                    setSelectedRole(null)
                  }}
                >
                  Analyze Resume for this Role <ArrowRight size={18} className="ml-xs" />
                </button>
              </div>
            </motion.div>
          </div>
        )}
      </AnimatePresence>

      <style dangerouslySetInnerHTML={{ __html: `
        .modal-overlay {
          position: fixed; top: 0; left: 0; right: 0; bottom: 0;
          background: rgba(0,0,0,0.8); backdrop-filter: blur(5px);
          display: flex; align-items: center; justify-content: center;
          z-index: 1000; padding: var(--spacing-xl);
        }
        .line-clamp-3 {
          display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden;
        }
      `}} />
    </div>
  )
}
