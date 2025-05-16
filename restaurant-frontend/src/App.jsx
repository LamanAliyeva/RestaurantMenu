import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom"
import { OrderProvider } from "./contexts/OrderContext"
import { AuthProvider } from "./contexts/AuthContext"
import ProtectedRoute from "./components/ProtectedRoute"
import TableMenu from "./pages/TableMenu"
import AdminLogin from "./pages/AdminLogin"
import AdminDashboard from "./pages/AdminsDashboard"
import TrackOrder from "./pages/TrackOrder"
import NotFound from "./pages/NotFound"
import "./App.css"

function App() {
  return (
    <Router>
      <AuthProvider>
        <OrderProvider>
          <Routes>
            {/* Table Routes */}
            <Route path="/:tableId" element={<TableMenu />} />
            <Route path="/track/:code" element={<TrackOrder />} />


            {/* Admin Routes */}
            <Route path="/admin/login" element={<AdminLogin />} />
            <Route
              path="/admin/:role"
              element={
                <ProtectedRoute>
                  <AdminDashboard />
                </ProtectedRoute>
              }
            />
            <Route path="/admin" element={<Navigate to="/admin/login" replace />} />
            <Route path="/logout" element={<Navigate to="/admin/login" replace />} />

            {/* Default Routes */}
            <Route path="/" element={<Navigate to="/1" replace />} />

            {/* 404 Not Found */}
            <Route path="*" element={<NotFound />} />
          </Routes>
        </OrderProvider>
      </AuthProvider>
    </Router>
  )
}

export default App
