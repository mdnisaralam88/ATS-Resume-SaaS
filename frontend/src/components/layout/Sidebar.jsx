import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import {
  LayoutDashboard, Upload, FileText, History, Download,
  Briefcase, CreditCard, User, Settings, LogOut, Shield,
  Users, BarChart2, Scan, Zap
} from 'lucide-react'
import './Sidebar.css'

const userNav = [
  { to: '/dashboard',   icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/upload',      icon: Upload,          label: 'Upload Resume' },
  { to: '/history',     icon: History,         label: 'Score History' },
  { to: '/reports',     icon: Download,        label: 'Reports' },
  { to: '/roles',       icon: Briefcase,       label: 'Job Roles' },
  { to: '/subscription',icon: CreditCard,      label: 'Subscription' },
  { to: '/profile',     icon: User,            label: 'Profile' },
]

const adminNav = [
  { to: '/admin',         icon: Shield,      label: 'Admin Dashboard' },
  { to: '/admin/users',   icon: Users,       label: 'Users' },
  { to: '/admin/roles',   icon: Briefcase,   label: 'Job Roles' },
  { to: '/admin/scans',   icon: Scan,        label: 'Scan Logs' },
]

export default function Sidebar() {
  const { user, logout, isAdmin } = useAuth()
  const navigate = useNavigate()

  const handleLogout = async () => {
    await logout()
    navigate('/login')
  }

  const planColors = { FREE: '#6b688a', PRO: '#7c3aed', PREMIUM: '#f59e0b' }
  const planColor = planColors[user?.subscriptionPlan] || '#6b688a'

  return (
    <aside className="sidebar">
      {/* Logo */}
      <div className="sidebar-logo">
        <div className="sidebar-logo-icon">
          <Zap size={20} color="#fff" />
        </div>
        <span className="sidebar-logo-text">ResumeIQ</span>
      </div>

      {/* User Info */}
      <div className="sidebar-user">
        <div className="sidebar-avatar">
          {user?.profileImage
            ? <img src={user.profileImage} alt="avatar" />
            : <span>{user?.fullName?.[0]?.toUpperCase()}</span>}
        </div>
        <div className="sidebar-user-info">
          <p className="sidebar-user-name">{user?.fullName}</p>
          <span className="sidebar-user-plan" style={{ color: planColor }}>
            {user?.subscriptionPlan}
          </span>
        </div>
      </div>

      {/* Navigation */}
      <nav className="sidebar-nav">
        <p className="sidebar-section-label">MAIN MENU</p>
        {userNav.map(({ to, icon: Icon, label }) => (
          <NavLink key={to} to={to} className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}>
            <Icon size={18} />
            <span>{label}</span>
          </NavLink>
        ))}

        {isAdmin && (
          <>
            <p className="sidebar-section-label" style={{ marginTop: '1.5rem' }}>ADMIN</p>
            {adminNav.map(({ to, icon: Icon, label }) => (
              <NavLink key={to} to={to} className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}>
                <Icon size={18} />
                <span>{label}</span>
              </NavLink>
            ))}
          </>
        )}
      </nav>

      {/* Logout */}
      <div className="sidebar-footer">
        <button className="sidebar-logout" onClick={handleLogout}>
          <LogOut size={18} />
          <span>Logout</span>
        </button>
      </div>
    </aside>
  )
}
