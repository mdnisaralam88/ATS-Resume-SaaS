import { useState, useEffect } from 'react'
import { adminService } from '../../services'
import { 
  Shield, Users, Scan, FileText, 
  TrendingUp, Award, Clock, ArrowUpRight,
  UserCheck, UserX, BarChart2
} from 'lucide-react'
import { 
  BarChart, Bar, XAxis, YAxis, CartesianGrid, 
  Tooltip, ResponsiveContainer, PieChart, Pie, Cell 
} from 'recharts'
import { motion } from 'framer-motion'

export default function AdminDashboard() {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    adminService.getDashboard()
      .then(setData)
      .finally(() => setLoading(false))
  }, [])

  if (loading) return (
    <div className="loading-overlay">
      <div className="spinner" />
      <p>Loading administration panel...</p>
    </div>
  )

  const COLORS = ['#7c3aed', '#06b6d4', '#f59e0b', '#10b981', '#ef4444', '#6366f1']
  const planData = [
    { name: 'Free', value: data.freeUsers },
    { name: 'Pro', value: data.proUsers },
    { name: 'Premium', value: data.premiumUsers }
  ]

  return (
    <div className="page-container animate-fade-in">
      <header className="page-header">
        <div className="flex items-center gap-md mb-xs">
          <Shield size={24} className="text-primary" />
          <h1 className="page-title">Admin Dashboard</h1>
        </div>
        <p className="page-subtitle">Global platform overview and system analytics.</p>
      </header>

      <div className="stats-grid">
        <StatCard icon={Users} label="Total Users" value={data.totalUsers} color="#7c3aed" />
        <StatCard icon={Scan} label="Total Scans" value={data.totalScans} color="#06b6d4" />
        <StatCard icon={FileText} label="Resumes" value={data.totalResumes} color="#10b981" />
        <StatCard icon={Award} label="Avg Score" value={`${data.averageAtsScore}%`} color="#f59e0b" />
      </div>

      <div className="grid-2">
        {/* Daily Scan Trend */}
        <div className="card">
          <h3 className="mb-lg">Scan Activity (Last 7 Days)</h3>
          <div style={{ width: '100%', height: 300 }}>
            <ResponsiveContainer>
              <BarChart data={data.dailyScans}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" vertical={false} />
                <XAxis dataKey="date" stroke="var(--text-muted)" fontSize={12} tickLine={false} axisLine={false} />
                <YAxis stroke="var(--text-muted)" fontSize={12} tickLine={false} axisLine={false} />
                <Tooltip 
                  contentStyle={{ background: 'var(--bg-card)', borderColor: 'var(--border)', borderRadius: '8px' }}
                />
                <Bar dataKey="scans" fill="var(--primary)" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* User Distribution */}
        <div className="card">
          <h3 className="mb-lg">Subscription Distribution</h3>
          <div style={{ width: '100%', height: 300, display: 'flex', alignItems: 'center' }}>
            <ResponsiveContainer>
              <PieChart>
                <Pie
                  data={planData}
                  innerRadius={60}
                  outerRadius={100}
                  paddingAngle={5}
                  dataKey="value"
                >
                  {planData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
            <div className="flex flex-col gap-md pr-xl">
               {planData.map((p, i) => (
                 <div key={p.name} className="flex items-center gap-md">
                    <div style={{ width: '12px', height: '12px', borderRadius: '50%', background: COLORS[i] }} />
                    <span className="text-xs font-bold text-secondary uppercase">{p.name}</span>
                    <span className="text-xs text-muted">{p.value}</span>
                 </div>
               ))}
            </div>
          </div>
        </div>
      </div>

      <div className="grid-2 mt-lg">
        {/* Top Job Roles */}
        <div className="card">
          <h3 className="mb-lg">Popular Job Roles</h3>
          <div className="flex flex-col gap-md">
            {data.topJobRoles.map((item, i) => (
              <div key={i} className="flex items-center justify-between p-md bg-elevated rounded-md">
                <div className="flex items-center gap-md">
                   <div className="badge badge-ghost">{i + 1}</div>
                   <span className="font-bold text-sm">{item.role}</span>
                </div>
                <div className="flex items-center gap-md">
                   <span className="text-xs text-muted">{item.count} scans</span>
                   <div className="progress-bar" style={{ width: '100px' }}>
                      <div className="progress-fill" style={{ width: `${(item.count / data.totalScans) * 100}%` }} />
                   </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* System Health / Quick Info */}
        <div className="card bg-surface border-primary">
           <h3 className="mb-lg flex items-center gap-md">
             <BarChart2 size={20} className="text-primary" /> System Metrics
           </h3>
           <div className="flex flex-col gap-lg">
              <div className="flex justify-between">
                 <span className="text-xs text-muted">Active Users Today</span>
                 <span className="text-sm font-bold text-success">{data.activeUsers}</span>
              </div>
              <div className="flex justify-between">
                 <span className="text-xs text-muted">Conversion Rate</span>
                 <span className="text-sm font-bold text-primary-light">
                   {Math.round((data.proUsers + data.premiumUsers) / data.totalUsers * 100)}%
                 </span>
              </div>
              <div className="flex justify-between">
                 <span className="text-xs text-muted">Storage Used</span>
                 <span className="text-sm font-bold">1.2 GB / 10 GB</span>
              </div>
              <div className="flex justify-between">
                 <span className="text-xs text-muted">API Health</span>
                 <span className="badge badge-success text-[10px]">OPERATIONAL</span>
              </div>
           </div>
        </div>
      </div>
    </div>
  )
}

function StatCard({ icon: Icon, label, value, color }) {
  return (
    <div className="card flex items-center gap-lg">
      <div className="feature-icon" style={{ background: `${color}15` }}>
        <Icon size={24} style={{ color }} />
      </div>
      <div>
        <p className="text-muted text-xs font-bold uppercase tracking-wider">{label}</p>
        <h3 className="text-2xl">{value}</h3>
      </div>
    </div>
  )
}
