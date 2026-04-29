import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import { scoreService, reportService } from '../services'
import { 
  Download, FileText, CheckCircle, AlertCircle, 
  ArrowRight, Award, Layers, Target, 
  Zap, Info, Printer, Share2, ChevronLeft,
  Loader2
} from 'lucide-react'
import { 
  PieChart, Pie, Cell, ResponsiveContainer, 
  BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid 
} from 'recharts'
import toast from 'react-hot-toast'
import { motion } from 'framer-motion'

export default function ScoreDetail() {
  const { id } = useParams()
  const [score, setScore] = useState(null)
  const [loading, setLoading] = useState(true)
  const [generating, setGenerating] = useState(false)

  useEffect(() => {
    async function fetchData() {
      try {
        const data = await scoreService.getById(id)
        setScore(data)
      } catch (err) {
        console.error(err)
      } finally {
        setLoading(false)
      }
    }
    fetchData()
  }, [id])

  const handleDownload = async () => {
    setGenerating(true)
    try {
      let reportId = score.reportId
      if (!reportId) {
        const report = await reportService.generate(score.id)
        reportId = report.id
      }
      await reportService.download(reportId)
      toast.success('Report downloaded successfully')
    } catch (err) {
      console.error(err)
    } finally {
      setGenerating(false)
    }
  }

  if (loading) return (
    <div className="loading-overlay">
      <div className="spinner" />
      <p>Retrieving analysis results...</p>
    </div>
  )

  const gradeColor = {
    'A+': '#10b981', 'A': '#10b981',
    'B': '#06b6d4', 'C': '#f59e0b',
    'D': '#f97316', 'F': '#ef4444'
  }[score.grade] || 'var(--primary)'

  const pieData = [
    { name: 'Score', value: score.overallScore },
    { name: 'Remaining', value: 100 - score.overallScore }
  ]

  return (
    <div className="page-container animate-fade-in">
      <header className="page-header flex justify-between items-start">
        <div>
          <Link to="/history" className="flex items-center gap-xs text-muted mb-sm hover:text-primary transition">
            <ChevronLeft size={16} /> History
          </Link>
          <h1 className="page-title">Analysis Result</h1>
          <p className="page-subtitle">
            Role: <span className="font-bold text-primary-light">{score.jobRoleName}</span> • 
            File: <span className="text-muted">{score.resumeFileName}</span>
          </p>
        </div>
        
        <div className="flex gap-md">
          <button className="btn btn-secondary" onClick={() => window.print()}>
            <Printer size={18} /> Print
          </button>
          <button 
            className="btn btn-primary" 
            onClick={handleDownload}
            disabled={generating}
          >
            {generating ? <Loader2 className="spinner-sm" /> : <Download size={18} />}
            {generating ? 'Generating PDF...' : 'Download PDF Report'}
          </button>
        </div>
      </header>

      <div className="grid-3" style={{ gridTemplateColumns: '350px 1fr' }}>
        {/* Left Column: Score Gauge & Key Metrics */}
        <div className="flex flex-col gap-lg">
          <div className="card text-center p-2xl">
            <h3 className="mb-lg uppercase tracking-widest text-xs text-muted">Overall ATS Score</h3>
            <div style={{ width: '100%', height: 200, position: 'relative' }}>
              <ResponsiveContainer>
                <PieChart>
                  <Pie
                    data={pieData}
                    innerRadius={60}
                    outerRadius={80}
                    startAngle={90}
                    endAngle={450}
                    dataKey="value"
                  >
                    <Cell fill={gradeColor} />
                    <Cell fill="var(--bg-elevated)" />
                  </Pie>
                </PieChart>
              </ResponsiveContainer>
              <div className="absolute inset-0 flex flex-col items-center justify-center">
                <span className="text-4xl font-black" style={{ color: gradeColor }}>{score.overallScore}%</span>
                <span className="badge badge-ghost mt-xs">Grade: {score.grade}</span>
              </div>
            </div>
            <p className="mt-md text-sm text-muted">
              {score.overallScore >= 80 ? 'Excellent match! Your resume is highly optimized.' :
               score.overallScore >= 60 ? 'Good start, but there is room for improvement.' :
               'Needs significant work to pass ATS filters.'}
            </p>
          </div>

          <div className="card">
            <h3 className="text-sm mb-lg flex items-center gap-sm">
              <Target size={16} className="text-primary" /> Key Metrics
            </h3>
            <div className="flex flex-col gap-md">
              <MetricRow label="Role Match" value={`${score.roleMatchPercentage}%`} />
              <MetricRow label="Keywords Found" value={`${score.matchedKeywords.length}`} />
              <MetricRow label="Missing Terms" value={`${score.missingKeywords.length}`} color="#ef4444" />
              <MetricRow label="Readability" value={`${score.readabilityScore}%`} />
            </div>
          </div>
          
          <div className="card bg-primary-glow border-primary">
             <h3 className="text-sm mb-md flex items-center gap-sm">
               <Award size={16} className="text-primary" /> Success Insight
             </h3>
             <p className="text-xs leading-relaxed">
               Candidates with an ATS score above <b>80%</b> are <b>4x</b> more likely to get 
               an interview for <b>{score.jobRoleName}</b> positions.
             </p>
          </div>
        </div>

        {/* Right Column: Detailed Breakdowns */}
        <div className="flex flex-col gap-lg">
          {/* Breakdown Tabs / Sections */}
          <div className="card">
            <h3 className="mb-xl">Analysis Breakdown</h3>
            <div className="flex flex-col gap-xl">
              {score.breakdowns.map(item => (
                <div key={item.id}>
                  <div className="flex justify-between items-end mb-sm">
                    <div>
                      <span className="text-sm font-bold">{item.category}</span>
                      <p className="text-xs text-muted">{item.details}</p>
                    </div>
                    <div className="text-right">
                      <span className="text-sm font-bold" style={{ color: item.percentage >= 70 ? '#10b981' : '#f59e0b' }}>
                        {item.score} / {item.maxScore}
                      </span>
                    </div>
                  </div>
                  <div className="progress-bar">
                    <div 
                      className="progress-fill" 
                      style={{ 
                        width: `${item.percentage}%`,
                        background: item.percentage >= 70 ? 'var(--success)' : 'var(--primary)'
                      }} 
                    />
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Suggestions */}
          <div className="card border-primary">
            <h3 className="mb-lg flex items-center gap-md">
              <Zap size={20} className="text-primary" /> Actionable Suggestions
            </h3>
            <div className="flex flex-col gap-md">
              {score.suggestions.map((s, i) => (
                <motion.div 
                  key={s.id}
                  initial={{ opacity: 0, x: 20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: i * 0.1 }}
                  className={`flex gap-md p-md rounded-lg ${s.category === 'CRITICAL' ? 'bg-danger/10 border border-danger/20' : 'bg-elevated'}`}
                >
                  <div className="flex-shrink-0 mt-xs">
                    {s.category === 'CRITICAL' ? <AlertCircle size={18} className="text-danger" /> : <CheckCircle size={18} className="text-primary-light" />}
                  </div>
                  <div>
                    <div className="flex items-center gap-md mb-xs">
                      <span className={`badge ${s.category === 'CRITICAL' ? 'badge-danger' : 'badge-primary'} text-[10px]`}>{s.category}</span>
                      {s.section && <span className="text-[10px] text-muted font-bold uppercase">{s.section}</span>}
                    </div>
                    <p className="text-sm text-secondary">{s.text}</p>
                  </div>
                </motion.div>
              ))}
            </div>
          </div>

          {/* Keywords Lists */}
          <div className="grid-2">
            <div className="card">
              <h3 className="text-sm mb-lg text-success">Keywords Found ({score.matchedKeywords.length})</h3>
              <div className="flex flex-wrap gap-xs">
                {score.matchedKeywords.map((kw, i) => (
                  <span key={i} className="badge badge-success text-[10px]">{kw}</span>
                ))}
                {score.matchedKeywords.length === 0 && <p className="text-xs text-muted">No keywords found.</p>}
              </div>
            </div>
            <div className="card">
              <h3 className="text-sm mb-lg text-danger">Missing Keywords ({score.missingKeywords.length})</h3>
              <div className="flex flex-wrap gap-xs">
                {score.missingKeywords.map((kw, i) => (
                  <span key={i} className="badge badge-danger text-[10px]">{kw}</span>
                ))}
                {score.missingKeywords.length === 0 && <p className="text-xs text-muted">Perfect keyword coverage!</p>}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

function MetricRow({ label, value, color }) {
  return (
    <div className="flex justify-between items-center py-sm border-bottom border-border last-border-none">
      <span className="text-xs text-muted font-medium">{label}</span>
      <span className="text-sm font-bold" style={{ color: color || 'var(--text-primary)' }}>{value}</span>
    </div>
  )
}
