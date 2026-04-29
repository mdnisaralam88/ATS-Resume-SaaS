import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { scoreService } from '../services'
import { 
  History, Search, Filter, ArrowRight, 
  Trash2, FileText, Calendar, Target,
  ChevronLeft, ChevronRight, MoreVertical
} from 'lucide-react'
import toast from 'react-hot-toast'

export default function ScoreHistory() {
  const [history, setHistory] = useState({ content: [], totalPages: 0, totalElements: 0 })
  const [page, setPage] = useState(0)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    async function fetchHistory() {
      setLoading(true)
      try {
        const data = await scoreService.getHistory(page, 10)
        setHistory(data)
      } catch (err) {
        console.error(err)
      } finally {
        setLoading(false)
      }
    }
    fetchHistory()
  }, [page])

  if (loading && page === 0) return (
    <div className="loading-overlay">
      <div className="spinner" />
      <p>Loading analysis history...</p>
    </div>
  )

  return (
    <div className="page-container animate-fade-in">
      <header className="page-header flex justify-between items-center">
        <div>
          <h1 className="page-title">Analysis History</h1>
          <p className="page-subtitle">You have performed {history.totalElements} scans in total.</p>
        </div>
        <Link to="/upload" className="btn btn-primary">New Analysis</Link>
      </header>

      <div className="card p-0 overflow-hidden">
        <div className="p-md border-bottom border-border flex justify-between items-center bg-surface">
          <div className="flex items-center gap-md">
            <div className="badge badge-info">{history.totalElements} Results</div>
          </div>
          <div className="flex items-center gap-sm">
             <button className="btn btn-ghost btn-sm">
               <Filter size={14} className="mr-xs" /> Filter
             </button>
          </div>
        </div>

        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Date</th>
                <th>Resume</th>
                <th>Target Role</th>
                <th>ATS Score</th>
                <th>Grade</th>
                <th className="text-right">Action</th>
              </tr>
            </thead>
            <tbody>
              {history.content.length > 0 ? (
                history.content.map(score => (
                  <tr key={score.id}>
                    <td>
                      <div className="flex items-center gap-sm">
                        <Calendar size={14} className="text-muted" />
                        <span className="text-xs">{new Date(score.createdAt).toLocaleDateString()}</span>
                      </div>
                    </td>
                    <td>
                      <div className="flex items-center gap-sm">
                        <FileText size={16} className="text-primary-light" />
                        <span className="font-medium">{score.resumeFileName}</span>
                      </div>
                    </td>
                    <td>
                      <div className="flex items-center gap-sm">
                        <Target size={14} className="text-muted" />
                        <span className="text-sm">{score.jobRoleName}</span>
                      </div>
                    </td>
                    <td>
                      <div className="flex items-center gap-sm">
                        <div className="progress-bar" style={{ width: '60px', height: '6px' }}>
                          <div className="progress-fill" style={{ width: `${score.overallScore}%` }} />
                        </div>
                        <span className="font-bold">{score.overallScore}%</span>
                      </div>
                    </td>
                    <td>
                      <span className={`badge ${score.overallScore >= 70 ? 'badge-success' : 'badge-warning'}`}>
                        {score.grade}
                      </span>
                    </td>
                    <td className="text-right">
                      <Link to={`/scores/${score.id}`} className="btn btn-ghost btn-sm">
                        View Result <ArrowRight size={14} className="ml-xs" />
                      </Link>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="6" className="text-center py-2xl">
                    <History size={48} className="text-muted mb-md mx-auto" />
                    <p className="text-muted">No analysis history found.</p>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {history.totalPages > 1 && (
          <div className="p-md flex justify-between items-center bg-surface border-top border-border">
            <p className="text-xs text-muted">
              Page {page + 1} of {history.totalPages}
            </p>
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
                disabled={page >= history.totalPages - 1}
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
