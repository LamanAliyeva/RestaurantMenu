import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom"
import TableMenu from "./pages/TableMenu"
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
            <Route path="/admin/login" element={null} />
            <Route
              path="/admin/:role"
              element={null}
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
