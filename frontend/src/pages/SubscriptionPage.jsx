import { useState, useEffect } from 'react'
import { subscriptionService } from '../services'
import { useAuth } from '../context/AuthContext'
import { 
  Check, Zap, CreditCard, Shield, 
  ArrowRight, Crown, Star, Loader2 
} from 'lucide-react'
import toast from 'react-hot-toast'
import { motion } from 'framer-motion'

export default function SubscriptionPage() {
  const { user, updateUser } = useAuth()
  const [plans, setPlans] = useState([])
  const [currentSub, setCurrentSub] = useState(null)
  const [loading, setLoading] = useState(true)
  const [upgrading, setUpgrading] = useState(null)

  useEffect(() => {
    async function fetchData() {
      try {
        const [plansData, subData] = await Promise.all([
          subscriptionService.getPlans(),
          subscriptionService.getMyPlan()
        ])
        setPlans(plansData)
        setCurrentSub(subData)
      } catch (err) {
        console.error(err)
      } finally {
        setLoading(false)
      }
    }
    fetchData()
  }, [])

  const handleUpgrade = async (planName) => {
    if (planName === currentSub.plan) return
    
    setUpgrading(planName)
    try {
      const updatedSub = await subscriptionService.upgrade(planName)
      setCurrentSub(updatedSub)
      // Update the user object in context as well
      updateUser({ ...user, subscriptionPlan: planName })
      toast.success(`Successfully upgraded to ${planName}!`)
    } catch (err) {
      console.error(err)
    } finally {
      setUpgrading(null)
    }
  }

  if (loading) return (
    <div className="loading-overlay">
      <div className="spinner" />
      <p>Fetching subscription details...</p>
    </div>
  )

  return (
    <div className="page-container animate-fade-in">
      <header className="page-header text-center">
        <h1 className="page-title">Choose Your Plan</h1>
        <p className="page-subtitle">Unlock unlimited scans and advanced AI analysis with our premium plans.</p>
      </header>

      {/* Current Plan Banner */}
      {currentSub && (
        <div className="card bg-primary-glow border-primary mb-2xl flex justify-between items-center p-xl">
           <div className="flex items-center gap-xl">
              <div className="feature-icon bg-primary" style={{ width: '60px', height: '60px' }}>
                 <Crown size={30} color="#fff" />
              </div>
              <div>
                 <p className="text-xs font-bold uppercase tracking-widest text-primary-light">Current Plan</p>
                 <h2 className="text-2xl">{currentSub.plan}</h2>
                 <p className="text-sm text-muted">
                    {currentSub.scansUsedToday} / {currentSub.scansLimit === -1 ? '∞' : currentSub.scansLimit} scans used today. 
                    Renews on {new Date(currentSub.renewalDate).toLocaleDateString()}.
                 </p>
              </div>
           </div>
           <div className="flex items-center gap-md">
              <div className="badge badge-success px-md py-sm">ACTIVE</div>
           </div>
        </div>
      )}

      {/* Pricing Grid */}
      <div className="grid-3">
        {plans.map((plan, i) => {
          const isCurrent = currentSub?.plan === plan.name
          return (
            <motion.div 
              key={plan.name}
              className={`card flex flex-col ${plan.name === 'PRO' ? 'border-primary shadow-glow relative scale-105 z-10' : ''}`}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.1 }}
            >
              {plan.name === 'PRO' && (
                <div className="absolute top-0 left-1/2 -translate-x-1/2 -translate-y-1/2 badge badge-primary px-lg font-bold">
                  MOST POPULAR
                </div>
              )}

              <div className="mb-xl">
                <h3 className="text-xl mb-sm">{plan.name}</h3>
                <div className="flex items-end gap-xs mb-md">
                  <span className="text-3xl font-black">${plan.price}</span>
                  <span className="text-muted text-sm pb-1">/ month</span>
                </div>
                <p className="text-xs text-muted">{plan.name === 'FREE' ? 'Basic tools for job seekers' : plan.name === 'PRO' ? 'The standard for professionals' : 'Full power for power users'}</p>
              </div>

              <div className="flex flex-col gap-md mb-2xl flex-1">
                {plan.features.map((feature, idx) => (
                  <div key={idx} className="flex items-start gap-md">
                    <div className="bg-success/20 rounded-full p-xs mt-xs">
                      <Check size={12} className="text-success" />
                    </div>
                    <span className="text-sm text-secondary">{feature}</span>
                  </div>
                ))}
              </div>

              <button 
                className={`btn btn-lg btn-full ${isCurrent ? 'btn-ghost' : 'btn-primary'}`}
                disabled={isCurrent || upgrading === plan.name}
                onClick={() => handleUpgrade(plan.name)}
              >
                {upgrading === plan.name ? <Loader2 className="spinner-sm" /> : isCurrent ? 'Current Plan' : `Upgrade to ${plan.name}`}
                {!upgrading && !isCurrent && <ArrowRight size={18} className="ml-xs" />}
              </button>
            </motion.div>
          )
        })}
      </div>

      {/* Trust Badges */}
      <div className="mt-2xl flex flex-wrap justify-center gap-2xl opacity-50 grayscale">
         <div className="flex items-center gap-md">
            <Shield size={24} />
            <span className="text-xs font-bold uppercase tracking-widest">Secure Payments</span>
         </div>
         <div className="flex items-center gap-md">
            <Zap size={24} />
            <span className="text-xs font-bold uppercase tracking-widest">Instant Activation</span>
         </div>
         <div className="flex items-center gap-md">
            <Star size={24} />
            <span className="text-xs font-bold uppercase tracking-widest">Cancel Anytime</span>
         </div>
      </div>
    </div>
  )
}
