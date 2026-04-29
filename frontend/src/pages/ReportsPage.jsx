import { useState, useEffect } from 'react'
import { reportService } from '../services'
import { 
  Download, FileText, Calendar, 
  Search, HardDrive, MoreVertical,
  CheckCircle, Loader2, FileDown
} from 'lucide-react'
import toast from 'react-hot-toast'

export default function ReportsPage() {
  const [reports, setReports] = useState([])
  const [loading, setLoading] = useState(true)
  const [downloading, setDownloading] = useState(null)

  useEffect(() => {
    async function fetchReports() {
      try {
        const data = await reportService.getAll()
        setReports(data)
      } catch (err) {
        console.error(err)
      } finally {
        setLoading(false)
      }
    }
    fetchReports()
  }, [])

  const handleDownload = async (reportId) => {
    setDownloading(reportId)
    try {
      await reportService.download(reportId)
      toast.success('Report downloaded')
    } catch (err) {
      console.error(err)
    } finally {
      setDownloading(null)
    }
  }

  if (loading) return (
    <div className="loading-overlay">
      <div className="spinner" />
      <p>Loading your reports...</p>
    </div>
  )

  return (
    <div className="page-container animate-fade-in">
      <header className="page-header">
        <h1 className="page-title">My Reports</h1>
        <p className="page-subtitle">Download your generated ATS analysis reports in PDF format.</p>
      </header>

      <div className="grid-3">
        {reports.length > 0 ? (
          reports.map(report => (
            <div key={report.id} className="card card-glow hover:border-primary transition">
              <div className="flex justify-between items-start mb-lg">
                <div className="feature-icon bg-primary">
                  <FileText size={24} color="#fff" />
                </div>
                <div className="badge badge-success">
                  <CheckCircle size={12} className="mr-xs" /> {report.status}
                </div>
              </div>
              
              <h3 className="text-md mb-xs truncate">{report.jobRoleName} Analysis</h3>
              <p className="text-xs text-muted mb-md truncate">Resume: {report.resumeFileName}</p>
              
              <div className="flex flex-col gap-sm mb-lg">
                <div className="flex items-center gap-sm text-xs text-muted">
                  <Calendar size={14} /> {new Date(report.generatedAt).toLocaleDateString()}
                </div>
                <div className="flex items-center gap-sm text-xs text-muted">
                  <HardDrive size={14} /> {(report.fileSize / 1024).toFixed(1)} KB
                </div>
              </div>

              <button 
                className="btn btn-secondary btn-full"
                disabled={downloading === report.id}
                onClick={() => handleDownload(report.id)}
              >
                {downloading === report.id ? <Loader2 className="spinner-sm" /> : <Download size={16} />}
                {downloading === report.id ? 'Downloading...' : 'Download PDF'}
              </button>
            </div>
          ))
        ) : (
          <div className="col-span-full card text-center py-2xl">
            <FileDown size={48} className="text-muted mb-md mx-auto" />
            <h3>No reports found</h3>
            <p className="text-muted mt-xs">Analyze a resume and click 'Download PDF Report' to see them here.</p>
          </div>
        )}
      </div>
    </div>
  )
}
