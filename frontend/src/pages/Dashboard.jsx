import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { 
  LayoutDashboard, FileText, History, Zap, 
  ArrowUpRight, Clock, CheckCircle, AlertCircle,
  TrendingUp, Award, Target, Plus
} from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { scoreService, resumeService, subscriptionService } from '../services'
import { motion } from 'framer-motion'
import { 
  AreaChart, Area, XAxis, YAxis, CartesianGrid, 
  Tooltip, ResponsiveContainer, BarChart, Bar, Cell 
} from 'recharts'

export default function Dashboard() {
  const { user } = useAuth()
  const [stats, setStats] = useState({
    totalScans: 0,
    avgScore: 0,
    latestScore: null,
    scansUsedToday: 0,
    scansLimit: 2
  })
  const [recentScores, setRecentScores] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    async function fetchData() {
      try {
        const [history, sub] = await Promise.all([
          scoreService.getHistory(0, 5),
          subscriptionService.getMyPlan()
        ])
        
        setRecentScores(history.content)
        
        // Calculate basic stats
        if (history.content.length > 0) {
          const total = history.totalElements
          const sum = history.content.reduce((acc, curr) => acc + curr.overallScore, 0)
          setStats({
            totalScans: total,
            avgScore: total > 0 ? Math.round(sum / history.content.length) : 0,
            latestScore: history.content[0],
            scansUsedToday: sub.scansUsedToday,
            scansLimit: sub.scansLimit
          })
        } else {
          setStats(prev => ({ ...prev, scansUsedToday: sub.scansUsedToday, scansLimit: sub.scansLimit }))
        }
      } catch (err) {
        console.error(err)
      } finally {
        setLoading(false)
      }
    }
    fetchData()
  }, [])

  if (loading) return (
    <div className="loading-overlay">
      <div className="spinner" />
      <p>Loading your dashboard...</p>
    </div>
  )

  const chartData = recentScores.slice().reverse().map(s => ({
    name: new Date(s.createdAt).toLocaleDateString('en-US', { month: 'short', day: 'numeric' }),
    score: s.overallScore
  }))

  return (
    <div className="page-container animate-fade-in">
      <header className="page-header flex justify-between items-end">
        <div>
          <h1 className="page-title">Welcome back, {user?.fullName?.split(' ')[0]}!</h1>
          <p className="page-subtitle">Here's what's happening with your resume scans.</p>
        </div>
        <Link to="/upload" className="btn btn-primary">
          <Plus size={18} /> New Analysis
        </Link>
      </header>

      {/* Stats Cards */}
      <div className="stats-grid">
        <div className="card flex items-center gap-lg">
          <div className="feature-icon" style={{ background: 'rgba(124, 58, 237, 0.1)' }}>
            <Zap size={24} className="text-primary" />
          </div>
          <div>
            <p className="text-muted text-xs font-bold uppercase tracking-wider">Total Scans</p>
            <h3 className="text-2xl">{stats.totalScans}</h3>
          </div>
        </div>
        
        <div className="card flex items-center gap-lg">
          <div className="feature-icon" style={{ background: 'rgba(16, 185, 129, 0.1)' }}>
            <Award size={24} style={{ color: '#10b981' }} />
          </div>
          <div>
            <p className="text-muted text-xs font-bold uppercase tracking-wider">Avg. Score</p>
            <h3 className="text-2xl">{stats.avgScore}%</h3>
          </div>
        </div>

        <div className="card flex items-center gap-lg">
          <div className="feature-icon" style={{ background: 'rgba(6, 182, 212, 0.1)' }}>
            <Target size={24} style={{ color: '#06b6d4' }} />
          </div>
          <div>
            <p className="text-muted text-xs font-bold uppercase tracking-wider">Scans Today</p>
            <h3 className="text-2xl">
              {stats.scansUsedToday} / {stats.scansLimit === -1 ? '∞' : stats.scansLimit}
            </h3>
          </div>
        </div>

        <div className="card flex items-center gap-lg">
          <div className="feature-icon" style={{ background: 'rgba(245, 158, 11, 0.1)' }}>
            <TrendingUp size={24} style={{ color: '#f59e0b' }} />
          </div>
          <div>
            <p className="text-muted text-xs font-bold uppercase tracking-wider">Plan</p>
            <h3 className="text-2xl">{user?.subscriptionPlan}</h3>
          </div>
        </div>
      </div>

      <div className="grid-2 mt-lg">
        {/* Score Trend Chart */}
        <div className="card">
          <div className="flex justify-between items-center mb-lg">
            <h3>Score Trend</h3>
            <Link to="/history" className="text-xs flex items-center gap-xs">
              View All <ArrowUpRight size={14} />
            </Link>
          </div>
          <div style={{ width: '100%', height: 250 }}>
            <ResponsiveContainer>
              <AreaChart data={chartData}>
                <defs>
                  <linearGradient id="colorScore" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="var(--primary)" stopOpacity={0.3}/>
                    <stop offset="95%" stopColor="var(--primary)" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" vertical={false} />
                <XAxis dataKey="name" stroke="var(--text-muted)" fontSize={12} tickLine={false} axisLine={false} />
                <YAxis stroke="var(--text-muted)" fontSize={12} tickLine={false} axisLine={false} domain={[0, 100]} />
                <Tooltip 
                  contentStyle={{ background: 'var(--bg-card)', borderColor: 'var(--border)', borderRadius: '8px' }}
                  itemStyle={{ color: 'var(--primary-light)' }}
                />
                <Area type="monotone" dataKey="score" stroke="var(--primary)" strokeWidth={3} fillOpacity={1} fill="url(#colorScore)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Recent Activity */}
        <div className="card">
          <div className="flex justify-between items-center mb-lg">
            <h3>Recent Scans</h3>
            <Link to="/history" className="text-xs flex items-center gap-xs">
              History <ArrowUpRight size={14} />
            </Link>
          </div>
          
          <div className="flex flex-col gap-md">
            {recentScores.length > 0 ? (
              recentScores.map(score => (
                <Link key={score.id} to={`/scores/${score.id}`} className="flex items-center justify-between p-md bg-elevated rounded-md hover-bg-hover transition border border-transparent hover-border-primary">
                  <div className="flex items-center gap-md">
                    <div className={`badge ${score.overallScore >= 70 ? 'badge-success' : 'badge-warning'}`}>
                      {score.grade}
                    </div>
                    <div>
                      <p className="text-sm font-bold text-primary-light">{score.jobRoleName}</p>
                      <p className="text-xs text-muted flex items-center gap-xs">
                        <Clock size={12} /> {new Date(score.createdAt).toLocaleDateString()}
                      </p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="font-bold">{score.overallScore}%</p>
                    <p className="text-xs text-muted">ATS Score</p>
                  </div>
                </Link>
              ))
            ) : (
              <div className="text-center py-xl">
                <AlertCircle size={32} className="text-muted mb-sm mx-auto" />
                <p className="text-muted">No scans yet. Start by uploading your resume!</p>
                <Link to="/upload" className="btn btn-primary btn-sm mt-md">Upload Now</Link>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Suggested Next Steps */}
      <div className="mt-2xl">
        <h3 className="mb-lg">Quick Tips</h3>
        <div className="grid-3">
          <div className="card bg-surface border-primary">
            <div className="flex items-center gap-md mb-sm">
              <CheckCircle size={20} className="text-success" />
              <h4 className="text-sm">Optimize for Keywords</h4>
            </div>
            <p className="text-xs">Ensure your resume contains the top 5 mandatory keywords for the role you're targeting.</p>
          </div>
          <div className="card bg-surface border-primary">
            <div className="flex items-center gap-md mb-sm">
              <CheckCircle size={20} className="text-success" />
              <h4 className="text-sm">Quantify Results</h4>
            </div>
            <p className="text-xs">ATS systems look for numbers, percentages, and metrics to rank your achievements higher.</p>
          </div>
          <div className="card bg-surface border-primary">
            <div className="flex items-center gap-md mb-sm">
              <CheckCircle size={20} className="text-success" />
              <h4 className="text-sm">Use PDF Format</h4>
            </div>
            <p className="text-xs">While we support DOCX, standard PDF format is the most reliable for consistent ATS parsing.</p>
          </div>
        </div>
      </div>
    </div>
  )
}
