import { Navigate, useParams } from "react-router-dom"
import { useAuth } from "../contexts/AuthContext"

export default function ProtectedRoute({ children }) {
    const { currentUser } = useAuth()
    const { role } = useParams()

    if (!currentUser) {
        // not logged in
        return <Navigate to="/admin/login" replace />
    }
    if (role && currentUser.role !== role) {
        // wrong role for this dashboard
        return <Navigate to={`/admin/${currentUser.role}`} replace />
    }
    return children
}
