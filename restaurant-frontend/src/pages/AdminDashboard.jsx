import { useEffect } from "react"
import { useParams, useNavigate } from "react-router-dom"
import { useAuth } from "../contexts/AuthContext"
import KitchenDashboard from "../components/KitchenDashboard"
import WaiterDashboard from "../components/WaiterDashboard"
import ManagerDashboard from "../components/ManagerDashboard"
import AdminDashboard from "../components/AdminDashboard"
import "../styles/AdminDashboard.css"

const AdminsDashboard = () => {
  const { role } = useParams()
  const { currentUser, logout } = useAuth()
  const navigate = useNavigate()

  // Redirect if not logged in
  useEffect(() => {
    if (!currentUser) {
      navigate("/admin/login")
    } else if (currentUser.role !== role) {
      // If user tries to access a different role's dashboard
      navigate(`/admin/${currentUser.role}`)
    }
  }, [currentUser, role, navigate])

  // Validate role
  useEffect(() => {
    const validRoles = ["chef", "waiter", "admin", "manager"]
    if (!validRoles.includes(role)) {
      navigate("/admin/login")
    }
  }, [role, navigate])

  const handleLogout = () => {
    logout()
    navigate("/logout")
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
        {role === "chef" && <KitchenDashboard />}
        {role === "waiter" && <WaiterDashboard />}
        {role === "manager" && <ManagerDashboard />}
        {role === "admin" && <AdminDashboard />}
      </main>
    </div>
  )
}

export default AdminsDashboard