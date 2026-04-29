import { useState, useEffect } from 'react'
import { jobRoleService } from '../../services'
import { 
  Briefcase, Plus, Search, Edit2, 
  Trash2, Filter, ArrowRight, X,
  Save, Loader2, Info
} from 'lucide-react'
import toast from 'react-hot-toast'

export default function AdminRoles() {
  const [roles, setRoles] = useState([])
  const [loading, setLoading] = useState(true)
  const [editingRole, setEditingRole] = useState(null)
  const [showModal, setShowModal] = useState(false)

  useEffect(() => {
    jobRoleService.getAll()
      .then(setRoles)
      .finally(() => setLoading(false))
  }, [])

  const handleEdit = (role) => {
    setEditingRole(role)
    setShowModal(true)
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to deactivate this job role?')) return
    try {
      await jobRoleService.delete(id)
      setRoles(roles.map(r => r.id === id ? { ...r, isActive: false } : r))
      toast.success('Role deactivated')
    } catch (err) {
      console.error(err)
    }
  }

  if (loading) return (
    <div className="loading-overlay">
      <div className="spinner" />
      <p>Loading job roles library...</p>
    </div>
  )

  return (
    <div className="page-container animate-fade-in">
      <header className="page-header flex justify-between items-center">
        <div>
          <h1 className="page-title">Job Role Templates</h1>
          <p className="page-subtitle">Manage the roles and keyword libraries used for ATS analysis.</p>
        </div>
        <button className="btn btn-primary" onClick={() => { setEditingRole(null); setShowModal(true); }}>
          <Plus size={18} /> Add New Role
        </button>
      </header>

      <div className="card p-0 overflow-hidden">
        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Role Name</th>
                <th>Category</th>
                <th>Level</th>
                <th>Keywords</th>
                <th>Status</th>
                <th className="text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {roles.map(role => (
                <tr key={role.id}>
                  <td>
                    <div className="flex items-center gap-md">
                      <div className="feature-icon bg-elevated" style={{ width: '32px', height: '32px' }}>
                        <Briefcase size={16} className="text-primary-light" />
                      </div>
                      <span className="font-bold text-sm">{role.name}</span>
                    </div>
                  </td>
                  <td><span className="badge badge-info">{role.category}</span></td>
                  <td><span className="text-xs text-muted">{role.experienceLevel}</span></td>
                  <td><span className="text-xs font-bold text-primary-light">{role.keywords?.length || 0} Terms</span></td>
                  <td>
                    <span className={`badge ${role.isActive ? 'badge-success' : 'badge-danger'}`}>
                      {role.isActive ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                  <td className="text-right">
                    <div className="flex justify-end gap-sm">
                      <button className="btn btn-ghost btn-sm" onClick={() => handleEdit(role)}>
                        <Edit2 size={14} />
                      </button>
                      <button className="btn btn-ghost btn-sm text-danger" onClick={() => handleDelete(role.id)}>
                        <Trash2 size={14} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {showModal && (
        <RoleModal 
          role={editingRole} 
          onClose={() => setShowModal(false)} 
          onSave={(newRole) => {
            if (editingRole) {
               setRoles(roles.map(r => r.id === newRole.id ? newRole : r))
            } else {
               setRoles([...roles, newRole])
            }
            setShowModal(false)
          }}
        />
      )}
    </div>
  )
}

function RoleModal({ role, onClose, onSave }) {
  const [formData, setFormData] = useState(role || {
    name: '',
    category: 'Backend',
    experienceLevel: 'Mid',
    description: '',
    minExperienceYears: 2,
    maxExperienceYears: 8,
    keywords: []
  })
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      let result
      if (role?.id) {
        result = await jobRoleService.update(role.id, formData)
        toast.success('Role updated successfully')
      } else {
        result = await jobRoleService.create(formData)
        toast.success('Role created successfully')
      }
      onSave(result)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="modal-overlay" style={{ position: 'fixed', inset: 0, zIndex: 1000, background: 'rgba(0,0,0,0.8)', display: 'flex', alignItems: 'center', justifyCenter: 'center', padding: '2rem' }}>
       <div className="card w-full max-w-2xl animate-fade-in overflow-y-auto max-h-[90vh]">
          <div className="flex justify-between items-center mb-xl border-bottom border-border pb-md">
             <h3>{role ? 'Edit Role Template' : 'Create New Role Template'}</h3>
             <button onClick={onClose} className="btn btn-ghost btn-sm"><X size={18} /></button>
          </div>

          <form onSubmit={handleSubmit} className="flex flex-col gap-lg">
             <div className="grid-2">
                <div className="form-group">
                   <label className="form-label">Role Name</label>
                   <input 
                     type="text" className="form-input" placeholder="e.g. Java Developer"
                     value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} required
                   />
                </div>
                <div className="form-group">
                   <label className="form-label">Category</label>
                   <select 
                     className="form-select"
                     value={formData.category} onChange={e => setFormData({...formData, category: e.target.value})}
                   >
                      <option>Backend</option>
                      <option>Frontend</option>
                      <option>Fullstack</option>
                      <option>DevOps</option>
                      <option>Mobile</option>
                      <option>Data Science</option>
                   </select>
                </div>
             </div>

             <div className="form-group">
                <label className="form-label">Description</label>
                <textarea 
                  className="form-textarea" rows="3"
                  value={formData.description} onChange={e => setFormData({...formData, description: e.target.value})}
                />
             </div>

             <div className="grid-2">
                <div className="form-group">
                   <label className="form-label">Min Exp (Yrs)</label>
                   <input 
                     type="number" className="form-input"
                     value={formData.minExperienceYears} onChange={e => setFormData({...formData, minExperienceYears: parseInt(e.target.value)})}
                   />
                </div>
                <div className="form-group">
                   <label className="form-label">Max Exp (Yrs)</label>
                   <input 
                     type="number" className="form-input"
                     value={formData.maxExperienceYears} onChange={e => setFormData({...formData, maxExperienceYears: parseInt(e.target.value)})}
                   />
                </div>
             </div>

             <div className="bg-primary-glow p-md rounded-md flex items-start gap-md">
                <Info size={18} className="text-primary-light mt-xs" />
                <p className="text-xs text-secondary">
                  Keywords and weights are currently managed via the database or API directly to ensure scoring precision. 
                  Role templates are used as the ground truth for ATS analysis.
                </p>
             </div>

             <div className="flex justify-end gap-md pt-lg border-top border-border">
                <button type="button" className="btn btn-ghost" onClick={onClose}>Cancel</button>
                <button type="submit" className="btn btn-primary" disabled={loading}>
                   {loading ? <Loader2 className="spinner-sm" /> : <Save size={18} />}
                   {loading ? 'Saving...' : 'Save Role'}
                </button>
             </div>
          </form>
       </div>
    </div>
  )
}
