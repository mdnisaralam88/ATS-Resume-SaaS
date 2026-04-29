import { useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { useDropzone } from 'react-dropzone'
import { 
  Upload, FileText, X, CheckCircle, 
  AlertCircle, Loader2, ArrowRight, ShieldCheck
} from 'lucide-react'
import { resumeService } from '../services'
import toast from 'react-hot-toast'
import { motion, AnimatePresence } from 'framer-motion'

export default function ResumeUpload() {
  const [file, setFile] = useState(null)
  const [title, setTitle] = useState('')
  const [uploading, setUploading] = useState(false)
  const navigate = useNavigate()

  const onDrop = useCallback((acceptedFiles) => {
    const selectedFile = acceptedFiles[0]
    if (selectedFile) {
      setFile(selectedFile)
      if (!title) {
        setTitle(selectedFile.name.replace(/\.[^/.]+$/, ""))
      }
    }
  }, [title])

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      'application/pdf': ['.pdf'],
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document': ['.docx']
    },
    maxFiles: 1,
    multiple: false
  })

  const handleUpload = async () => {
    if (!file) return toast.error('Please select a file first')
    
    setUploading(true)
    try {
      const resume = await resumeService.upload(file, title)
      toast.success('Resume uploaded and parsed!')
      navigate(`/analyze/${resume.id}`)
    } catch (err) {
      console.error(err)
    } finally {
      setUploading(false)
    }
  }

  return (
    <div className="page-container animate-fade-in" style={{ maxWidth: '800px' }}>
      <header className="page-header text-center">
        <h1 className="page-title">Upload Your Resume</h1>
        <p className="page-subtitle">We support PDF and DOCX formats (Max 10MB)</p>
      </header>

      <div className="card p-2xl">
        <div 
          {...getRootProps()} 
          className={`dropzone ${isDragActive ? 'active' : ''} ${file ? 'has-file' : ''}`}
          style={{
            border: '2px dashed var(--border-strong)',
            borderRadius: 'var(--radius-xl)',
            padding: '4rem 2rem',
            textAlign: 'center',
            cursor: 'pointer',
            transition: 'var(--transition)',
            background: isDragActive ? 'rgba(124, 58, 237, 0.05)' : 'transparent',
            borderColor: isDragActive ? 'var(--primary)' : 'var(--border-strong)'
          }}
        >
          <input {...getInputProps()} />
          
          <AnimatePresence mode="wait">
            {!file ? (
              <motion.div 
                key="empty"
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
              >
                <div className="flex justify-center mb-md">
                  <div className="sidebar-logo-icon" style={{ width: '64px', height: '64px' }}>
                    <Upload size={32} color="#fff" />
                  </div>
                </div>
                <h3>Drag & Drop your resume</h3>
                <p className="text-muted mt-xs">or click to browse your files</p>
              </motion.div>
            ) : (
              <motion.div 
                key="file"
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                className="flex flex-col items-center"
              >
                <div className="flex items-center gap-md p-lg bg-elevated rounded-lg border border-primary w-full max-w-md">
                  <div className="feature-icon bg-primary">
                    <FileText size={20} color="#fff" />
                  </div>
                  <div className="flex-1 text-left truncate">
                    <p className="font-bold text-sm truncate">{file.name}</p>
                    <p className="text-xs text-muted">{(file.size / 1024 / 1024).toFixed(2)} MB</p>
                  </div>
                  <button 
                    onClick={(e) => { e.stopPropagation(); setFile(null); }}
                    className="p-xs hover:bg-hover rounded-full transition"
                  >
                    <X size={18} />
                  </button>
                </div>
                <div className="flex items-center gap-xs mt-md text-success">
                  <CheckCircle size={16} />
                  <span className="text-xs font-bold">Ready to analyze</span>
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>

        <div className="mt-2xl flex flex-col gap-lg">
          <div className="form-group">
            <label className="form-label">Analysis Title (Optional)</label>
            <input 
              type="text" 
              className="form-input" 
              placeholder="e.g., Software Engineer Application - Google"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
            />
          </div>

          <div className="flex items-start gap-md p-md bg-surface border border-dashed border-muted rounded-md">
            <ShieldCheck size={20} className="text-primary-light mt-xs" />
            <div>
              <p className="text-xs font-bold text-primary-light uppercase mb-xs">Privacy Guaranteed</p>
              <p className="text-xs text-muted">Your resume data is processed securely. We use local AI analysis and never share your data with third parties.</p>
            </div>
          </div>

          <button 
            className="btn btn-primary btn-lg btn-full"
            disabled={!file || uploading}
            onClick={handleUpload}
          >
            {uploading ? <Loader2 className="spinner-sm" /> : 'Process Resume'}
            {!uploading && <ArrowRight size={18} className="ml-xs" />}
          </button>
        </div>
      </div>

      <div className="mt-xl grid-2">
        <div className="flex items-center gap-md">
           <div className="badge badge-info">1</div>
           <p className="text-sm font-medium">Upload your PDF or DOCX</p>
        </div>
        <div className="flex items-center gap-md">
           <div className="badge badge-info">2</div>
           <p className="text-sm font-medium">AI parses and cleans the text</p>
        </div>
      </div>
    </div>
  )
}
