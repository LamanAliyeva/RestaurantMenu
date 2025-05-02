
import { useEffect } from "react"
import { useParams, useNavigate } from "react-router-dom"
import ManagerDashboard from "../components/ManagerDashboard"
import "../styles/AdminDashboard.css"

const AdminDashboard = () => {
  const { role } = useParams()
  const { currentUser, logout } = useAuth()
  const navigate = useNavigate()

  useEffect(() => {
    if (!currentUser) {
      navigate("/admin/login")
    } else if (currentUser.role !== role) {
      navigate(`/admin/${currentUser.role}`)
    }
  }, [currentUser, role, navigate])

  const handleLogout = () => {
    logout()
    navigate("/admin/login")
  }

  if (!currentUser) return null

  return (
    <div className="admin-dashboard">
      <header className="dashboard-header">
        <h1>Restaurant Admin</h1>

        <div className="user-controls">
          

          <div className="user-info">
            <span>Logged in as: {currentUser.username}</span>
            <button onClick={handleLogout} className="logout-button">
              Logout
            </button>
          </div>
        </div>
      </header>

      <main className="dashboard-content">
        <ManagerDashboard />
      </main>
    </div>
  )
}

export default AdminDashboard
