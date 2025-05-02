import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom"
import { OrderProvider } from "./contexts/OrderContext"
import { AuthProvider } from "./contexts/AuthContext"
import TableMenu from "./pages/TableMenu"
import AdminLogin from "./pages/AdminLogin"
import AdminDashboard from "./pages/AdminDashboard"
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

            {/* Admin Routes */}
            <Route path="/admin/login" element={<AdminLogin />} />
            <Route path="/admin/:role" element={<AdminDashboard />} />
            <Route path="/admin" element={<Navigate to="/admin/login" replace />} />

            {/* Default Routes */}
            <Route path="/" element={<Navigate to="/1" replace />} />
            <Route path="*" element={<NotFound />} />
          </Routes>
        </OrderProvider>
      </AuthProvider>
    </Router>
  )
}

export default App
