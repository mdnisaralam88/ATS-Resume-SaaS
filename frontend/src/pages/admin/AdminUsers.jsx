import { useState, useEffect } from 'react'
import { adminService } from '../../services'
import { 
  Users, Search, Filter, Mail, 
  Calendar, CreditCard, ShieldCheck,
  UserCheck, UserX, MoreVertical,
  ChevronLeft, ChevronRight, Loader2
} from 'lucide-react'
import toast from 'react-hot-toast'

export default function AdminUsers() {
  const [data, setData] = useState({ content: [], totalPages: 0, totalElements: 0 })
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const [toggling, setToggling] = useState(null)

  useEffect(() => {
    async function fetchUsers() {
      setLoading(true)
      try {
        const res = await adminService.getUsers(page, 15, search)
        setData(res)
      } catch (err) {
        console.error(err)
      } finally {
        setLoading(false)
      }
    }
    const timer = setTimeout(fetchUsers, 300)
    return () => clearTimeout(timer)
  }, [page, search])

  const handleToggleStatus = async (userId) => {
    setToggling(userId)
    try {
      await adminService.toggleUserStatus(userId)
      setData(prev => ({
        ...prev,
        content: prev.content.map(u => u.id === userId ? { ...u, active: !u.active } : u)
      }))
      toast.success('User status updated')
    } catch (err) {
      console.error(err)
    } finally {
      setToggling(null)
    }
  }

  if (loading && page === 0 && !search) return (
    <div className="loading-overlay">
      <div className="spinner" />
      <p>Loading user management...</p>
    </div>
  )

  return (
    <div className="page-container animate-fade-in">
      <header className="page-header flex justify-between items-center">
        <div>
          <h1 className="page-title">User Management</h1>
          <p className="page-subtitle">Managing {data.totalElements} total registered users.</p>
        </div>
      </header>

      <div className="card p-0 overflow-hidden">
        <div className="p-md border-bottom border-border flex justify-between items-center bg-surface">
          <div className="flex items-center gap-md bg-elevated px-md py-xs rounded-md border border-border focus-within:border-primary transition">
            <Search size={18} className="text-muted" />
            <input 
              type="text" 
              className="form-input" 
              placeholder="Search by name or email..."
              style={{ border: 'none', background: 'transparent', width: '300px' }}
              value={search}
              onChange={(e) => { setSearch(e.target.value); setPage(0); }}
            />
          </div>
          <div className="badge badge-primary">{data.totalElements} Users found</div>
        </div>

        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>User</th>
                <th>Status</th>
                <th>Role</th>
                <th>Plan</th>
                <th>Joined</th>
                <th className="text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {data.content.length > 0 ? (
                data.content.map(user => (
                  <tr key={user.id}>
                    <td>
                      <div className="flex items-center gap-md">
                        <div className="topbar-avatar" style={{ width: '32px', height: '32px' }}>
                           {user.profileImage ? <img src={user.profileImage} /> : <span>{user.fullName[0]}</span>}
                        </div>
                        <div>
                          <p className="text-sm font-bold">{user.fullName}</p>
                          <p className="text-xs text-muted">{user.email}</p>
                        </div>
                      </div>
                    </td>
                    <td>
                      <div className={`badge ${user.active ? 'badge-success' : 'badge-danger'}`}>
                        {user.active ? <UserCheck size={12} className="mr-xs" /> : <UserX size={12} className="mr-xs" />}
                        {user.active ? 'Active' : 'Inactive'}
                      </div>
                    </td>
                    <td>
                       <span className="text-xs font-bold uppercase tracking-wider">{user.role}</span>
                    </td>
                    <td>
                      <div className="flex items-center gap-sm">
                        <div 
                          className="w-2 h-2 rounded-full" 
                          style={{ background: user.subscriptionPlan === 'FREE' ? '#6b688a' : user.subscriptionPlan === 'PRO' ? '#7c3aed' : '#f59e0b' }} 
                        />
                        <span className="text-xs font-bold">{user.subscriptionPlan}</span>
                      </div>
                    </td>
                    <td>
                      <span className="text-xs text-muted">{new Date(user.createdAt).toLocaleDateString()}</span>
                    </td>
                    <td className="text-right">
                       <button 
                         className={`btn btn-sm ${user.active ? 'btn-ghost' : 'btn-primary'}`}
                         disabled={toggling === user.id}
                         onClick={() => handleToggleStatus(user.id)}
                       >
                         {toggling === user.id ? <Loader2 className="spinner-sm" /> : user.active ? 'Deactivate' : 'Activate'}
                       </button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="6" className="text-center py-2xl">
                    <Users size={48} className="text-muted mb-md mx-auto" />
                    <p className="text-muted">No users found matching your search.</p>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {data.totalPages > 1 && (
          <div className="p-md flex justify-between items-center bg-surface border-top border-border">
            <p className="text-xs text-muted">Page {page + 1} of {data.totalPages}</p>
            <div className="flex gap-xs">
              <button 
                className="btn btn-ghost btn-sm"
                disabled={page === 0}
                onClick={() => setPage(p => p - 1)}
              >
                <ChevronLeft size={16} /> Previous
              </button>
              <button 
                className="btn btn-ghost btn-sm"
                disabled={page >= data.totalPages - 1}
                onClick={() => setPage(p => p + 1)}
              >
                Next <ChevronRight size={16} />
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
