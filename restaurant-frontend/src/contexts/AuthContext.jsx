"use client"

import { createContext, useContext, useState, useEffect } from "react"

const AuthContext = createContext()

export const useAuth = () => useContext(AuthContext)

export const AuthProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(() => {
    const savedUser = localStorage.getItem("restaurantUser")
    return savedUser ? JSON.parse(savedUser) : null
  })

  useEffect(() => {
    if (currentUser) {
      localStorage.setItem("restaurantUser", JSON.stringify(currentUser))
    } else {
      localStorage.removeItem("restaurantUser")
    }
  }, [currentUser])

  // Mock login function
  const login = (username, password, role) => {
    // In a real app, you would validate credentials against a backend
    // For this demo, we'll accept any non-empty username/password
    if (username && password) {
      const user = { username, role }
      setCurrentUser(user)
      return true
    }
    return false
  }

  const logout = () => {
    setCurrentUser(null)
  }

  const value = {
    currentUser,
    login,
    logout,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
