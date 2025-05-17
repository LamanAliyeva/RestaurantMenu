"use client"

import { createContext, useContext, useState, useEffect } from "react"
import api from '../api'

const AuthContext = createContext()

export const useAuth = () => useContext(AuthContext)

export const AuthProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(() => {
    const saved = localStorage.getItem("restaurantUser")
    return saved ? JSON.parse(saved) : null
  })

  useEffect(() => {
    if (currentUser) {
      // Persist user + token
      localStorage.setItem("restaurantUser", JSON.stringify(currentUser))
      // Add Authorization header for all future API calls
      api.defaults.headers.common["Authorization"] = `Bearer ${currentUser.token}`
    } else {
      localStorage.removeItem("restaurantUser")
      // Remove header when logged out
      delete api.defaults.headers.common["Authorization"]
    }
  }, [currentUser])

  const login = async (username, password) => {
    try {
      const { data } = await api.post("/auth/login", { username, password })
      // data: { token, user: { username, role, … } }
      setCurrentUser({ ...data.user, token: data.token })
      return data.user.role;
    } catch (e) {
      console.error("Login failed", e)
      return null
    }
  }

  const logout = async () => {
    try {
      await api.post("/auth/logout")
    } catch (_) { /* ignore errors */ }
    setCurrentUser(null)
  }

  const value = {
    currentUser,
    login,
    logout,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
