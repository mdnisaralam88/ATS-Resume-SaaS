import { useState, useRef } from 'react'
import { useAuth } from '../context/AuthContext'
import { userService, authService } from '../services'
import { 
  User, Mail, Phone, MapPin, 
  Linkedin, Github, Camera, Save, 
  Lock, ShieldAlert, Loader2, Globe,
  Building, Briefcase, Info
} from 'lucide-react'
import toast from 'react-hot-toast'
import { motion } from 'framer-motion'

export default function ProfilePage() {
  const { user, updateUser } = useAuth()
  const [formData, setFormData] = useState({
    fullName: user?.fullName || '',
    phone: user?.phone || '',
    location: user?.location || '',
    bio: user?.bio || '',
    jobTitle: user?.jobTitle || '',
    company: user?.company || '',
    linkedinUrl: user?.linkedinUrl || '',
    githubUrl: user?.githubUrl || ''
  })
  const [passwords, setPasswords] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
  const [loading, setLoading] = useState(false)
  const [passLoading, setPassLoading] = useState(false)
  const [uploading, setUploading] = useState(false)
  const fileInputRef = useRef()

  const handleProfileChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value })
  const handlePasswordChange = (e) => setPasswords({ ...passwords, [e.target.name]: e.target.value })

  const handleUpdateProfile = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      const updatedUser = await userService.updateProfile(formData)
      updateUser(updatedUser)
      toast.success('Profile updated successfully')
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handleChangePassword = async (e) => {
    e.preventDefault()
    if (passwords.newPassword !== passwords.confirmPassword) {
      return toast.error('New passwords do not match')
    }
    setPassLoading(true)
    try {
      await authService.changePassword(passwords.currentPassword, passwords.newPassword)
      toast.success('Password changed successfully')
      setPasswords({ currentPassword: '', newPassword: '', confirmPassword: '' })
    } catch (err) {
      console.error(err)
    } finally {
      setPassLoading(false)
    }
  }

  const handleImageClick = () => fileInputRef.current.click()

  const handleImageUpload = async (e) => {
    const file = e.target.files[0]
    if (!file) return
    
    setUploading(true)
    try {
      const updatedUser = await userService.uploadProfileImage(file)
      updateUser(updatedUser)
      toast.success('Profile image updated')
    } catch (err) {
      console.error(err)
    } finally {
      setUploading(false)
    }
  }

  return (
    <div className="page-container animate-fade-in" style={{ maxWidth: '1000px' }}>
      <header className="page-header">
        <h1 className="page-title">Profile Settings</h1>
        <p className="page-subtitle">Manage your personal information and account security.</p>
      </header>

      <div className="grid-2" style={{ gridTemplateColumns: '1fr 1.5fr' }}>
        {/* Sidebar: Avatar and Security */}
        <div className="flex flex-col gap-lg">
          <div className="card text-center p-2xl">
            <div className="relative inline-block mx-auto mb-lg group">
              <div 
                className="sidebar-avatar cursor-pointer" 
                style={{ width: '120px', height: '120px', fontSize: '2.5rem' }}
                onClick={handleImageClick}
              >
                {uploading ? (
                  <Loader2 className="spinner" />
                ) : user?.profileImage ? (
                  <img src={user.profileImage} alt="profile" />
                ) : (
                  <span>{user?.fullName?.[0]?.toUpperCase()}</span>
                )}
                <div className="absolute inset-0 bg-black/40 rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition">
                   <Camera size={24} color="#fff" />
                </div>
              </div>
              <input 
                type="file" 
                ref={fileInputRef} 
                onChange={handleImageUpload} 
                hidden 
                accept="image/*" 
              />
            </div>
            <h3 className="mb-xs">{user?.fullName}</h3>
            <p className="text-xs text-muted mb-lg">{user?.email}</p>
            <div className="badge badge-primary">{user?.subscriptionPlan} PLAN</div>
          </div>

          <div className="card">
            <h3 className="text-sm mb-lg flex items-center gap-md">
              <Lock size={18} className="text-primary" /> Security
            </h3>
            <form onSubmit={handleChangePassword} className="flex flex-col gap-md">
              <div className="form-group">
                <label className="form-label">Current Password</label>
                <input 
                  type="password" 
                  name="currentPassword"
                  className="form-input" 
                  placeholder="••••••••"
                  value={passwords.currentPassword}
                  onChange={handlePasswordChange}
                  required
                />
              </div>
              <div className="form-group">
                <label className="form-label">New Password</label>
                <input 
                  type="password" 
                  name="newPassword"
                  className="form-input" 
                  placeholder="••••••••"
                  value={passwords.newPassword}
                  onChange={handlePasswordChange}
                  required
                />
              </div>
              <div className="form-group">
                <label className="form-label">Confirm New Password</label>
                <input 
                  type="password" 
                  name="confirmPassword"
                  className="form-input" 
                  placeholder="••••••••"
                  value={passwords.confirmPassword}
                  onChange={handlePasswordChange}
                  required
                />
              </div>
              <button className="btn btn-secondary btn-full" disabled={passLoading}>
                {passLoading ? <Loader2 className="spinner-sm" /> : 'Update Password'}
              </button>
            </form>
          </div>

          <div className="card bg-danger/10 border-danger/20">
             <h3 className="text-sm text-danger mb-md flex items-center gap-md">
                <ShieldAlert size={18} /> Danger Zone
             </h3>
             <p className="text-xs text-secondary mb-lg leading-relaxed">
               Deactivating your account will disable your profile and remove your access to ResumeIQ. 
               This action is permanent.
             </p>
             <button className="btn btn-danger btn-sm btn-full">Deactivate Account</button>
          </div>
        </div>

        {/* Main: Profile Info */}
        <div className="card">
          <h3 className="mb-xl flex items-center gap-md">
            <User size={20} className="text-primary" /> Personal Information
          </h3>
          
          <form onSubmit={handleUpdateProfile} className="flex flex-col gap-lg">
            <div className="grid-2">
              <div className="form-group">
                <label className="form-label">Full Name</label>
                <div className="flex items-center gap-md bg-elevated px-md rounded-md border border-border focus-within:border-primary">
                  <User size={16} className="text-muted" />
                  <input 
                    name="fullName"
                    type="text" 
                    className="form-input" 
                    style={{ border: 'none', background: 'transparent' }}
                    value={formData.fullName}
                    onChange={handleProfileChange}
                  />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Email (Locked)</label>
                <div className="flex items-center gap-md bg-elevated px-md rounded-md border border-border opacity-50 cursor-not-allowed">
                  <Mail size={16} className="text-muted" />
                  <input 
                    type="email" 
                    className="form-input" 
                    style={{ border: 'none', background: 'transparent' }}
                    value={user?.email}
                    disabled
                  />
                </div>
              </div>
            </div>

            <div className="grid-2">
              <div className="form-group">
                <label className="form-label">Phone Number</label>
                <div className="flex items-center gap-md bg-elevated px-md rounded-md border border-border focus-within:border-primary">
                  <Phone size={16} className="text-muted" />
                  <input 
                    name="phone"
                    type="text" 
                    className="form-input" 
                    placeholder="+1 (555) 000-0000"
                    style={{ border: 'none', background: 'transparent' }}
                    value={formData.phone}
                    onChange={handleProfileChange}
                  />
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Location</label>
                <div className="flex items-center gap-md bg-elevated px-md rounded-md border border-border focus-within:border-primary">
                  <MapPin size={16} className="text-muted" />
                  <input 
                    name="location"
                    type="text" 
                    className="form-input" 
                    placeholder="New York, USA"
                    style={{ border: 'none', background: 'transparent' }}
                    value={formData.location}
                    onChange={handleProfileChange}
                  />
                </div>
              </div>
            </div>

            <div className="grid-2">
               <div className="form-group">
                  <label className="form-label">Current Job Title</label>
                  <div className="flex items-center gap-md bg-elevated px-md rounded-md border border-border focus-within:border-primary">
                    <Briefcase size={16} className="text-muted" />
                    <input 
                      name="jobTitle"
                      type="text" 
                      className="form-input" 
                      placeholder="Software Engineer"
                      style={{ border: 'none', background: 'transparent' }}
                      value={formData.jobTitle}
                      onChange={handleProfileChange}
                    />
                  </div>
               </div>
               <div className="form-group">
                  <label className="form-label">Company</label>
                  <div className="flex items-center gap-md bg-elevated px-md rounded-md border border-border focus-within:border-primary">
                    <Building size={16} className="text-muted" />
                    <input 
                      name="company"
                      type="text" 
                      className="form-input" 
                      placeholder="Tech Inc."
                      style={{ border: 'none', background: 'transparent' }}
                      value={formData.company}
                      onChange={handleProfileChange}
                    />
                  </div>
               </div>
            </div>

            <div className="form-group">
              <label className="form-label">Professional Bio</label>
              <textarea 
                name="bio"
                className="form-textarea" 
                rows="4" 
                placeholder="Brief description for your profile..."
                value={formData.bio}
                onChange={handleProfileChange}
              />
            </div>

            <div className="grid-2">
               <div className="form-group">
                  <label className="form-label">LinkedIn URL</label>
                  <div className="flex items-center gap-md bg-elevated px-md rounded-md border border-border focus-within:border-primary">
                    <Linkedin size={16} className="text-muted" />
                    <input 
                      name="linkedinUrl"
                      type="url" 
                      className="form-input" 
                      placeholder="https://linkedin.com/in/username"
                      style={{ border: 'none', background: 'transparent' }}
                      value={formData.linkedinUrl}
                      onChange={handleProfileChange}
                    />
                  </div>
               </div>
               <div className="form-group">
                  <label className="form-label">GitHub URL</label>
                  <div className="flex items-center gap-md bg-elevated px-md rounded-md border border-border focus-within:border-primary">
                    <Github size={16} className="text-muted" />
                    <input 
                      name="githubUrl"
                      type="url" 
                      className="form-input" 
                      placeholder="https://github.com/username"
                      style={{ border: 'none', background: 'transparent' }}
                      value={formData.githubUrl}
                      onChange={handleProfileChange}
                    />
                  </div>
               </div>
            </div>

            <div className="flex justify-end mt-xl pt-lg border-top border-border">
              <button 
                type="submit" 
                className="btn btn-primary btn-lg"
                disabled={loading}
              >
                {loading ? <Loader2 className="spinner-sm" /> : <Save size={18} />}
                {loading ? 'Saving...' : 'Save Changes'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}
