import { useState, useEffect } from 'react'
import { useLocation, Link } from 'react-router-dom'
import { Bell, Search, ChevronRight } from 'lucide-react'
import { useAuth } from '../../context/AuthContext'
import { notificationService } from '../../services'
import './Topbar.css'

const routeLabels = {
  '/dashboard': 'Dashboard',
  '/upload': 'Upload Resume',
  '/history': 'Score History',
  '/reports': 'Reports',
  '/roles': 'Job Roles',
  '/subscription': 'Subscription',
  '/profile': 'Profile Settings',
  '/admin': 'Admin Dashboard',
  '/admin/users': 'User Management',
  '/admin/roles': 'Role Templates',
  '/admin/scans': 'Scan Logs',
}

export default function Topbar() {
  const { user } = useAuth()
  const location = useLocation()
  const [unread, setUnread] = useState(0)
  const [showNotif, setShowNotif] = useState(false)

  const pageTitle = routeLabels[location.pathname] || 'ResumeIQ'

  useEffect(() => {
    notificationService.getUnreadCount().then(setUnread).catch(() => {})
  }, [location.pathname])

  return (
    <header className="topbar">
      <div className="topbar-left">
        <div className="topbar-breadcrumb">
          <Link to="/dashboard" className="topbar-home">Home</Link>
          <ChevronRight size={14} color="var(--text-muted)" />
          <span className="topbar-current">{pageTitle}</span>
        </div>
      </div>

      <div className="topbar-right">
        <button
          className="topbar-icon-btn"
          onClick={() => setShowNotif(!showNotif)}
          style={{ position: 'relative' }}
        >
          <Bell size={20} />
          {unread > 0 && (
            <span className="topbar-badge">{unread > 9 ? '9+' : unread}</span>
          )}
        </button>

        <Link to="/profile" className="topbar-user">
          <div className="topbar-avatar">
            {user?.profileImage
              ? <img src={user.profileImage} alt="avatar" />
              : <span>{user?.fullName?.[0]?.toUpperCase()}</span>}
          </div>
          <span className="topbar-username">{user?.fullName?.split(' ')[0]}</span>
        </Link>
      </div>
    </header>
  )
}
