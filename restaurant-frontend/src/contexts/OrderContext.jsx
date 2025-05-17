"use client"

import api from "../api"
import { createContext, useContext, useState, useEffect } from "react"

const OrderContext = createContext()
export const useOrders = () => useContext(OrderContext)

export const OrderProvider = ({ children }) => {
  // 1) Orders come from the backend (no localStorage for orders)
  const [orders, setOrders] = useState([])

  // 2) Cart can still be persisted locally if you like
  const [cart, setCart] = useState(() => {
    const saved = localStorage.getItem("restaurantCart")
    return saved ? JSON.parse(saved) : {}
  })

  // 3) Load existing orders once on mount


  // 4) Persist cart to localStorage (optional)
  useEffect(() => {
    localStorage.setItem("restaurantCart", JSON.stringify(cart))
  }, [cart])

  // 5) Cart helpers
  const addToCart = (tableId, item) => {
    setCart(prev => {
      const tbl = prev[tableId] || []
      const idx = tbl.findIndex(i => i.id === item.id)
      if (idx >= 0) {
        const updated = [...tbl]
        updated[idx] = { ...updated[idx], quantity: updated[idx].quantity + 1 }
        return { ...prev, [tableId]: updated }
      }
      return { ...prev, [tableId]: [...tbl, { ...item, quantity: 1, comment: "" }] }
    })
  }

  const removeFromCart = (tableId, itemId) => {
    setCart(prev => ({
      ...prev,
      [tableId]: (prev[tableId] || []).filter(i => i.id !== itemId)
    }))
  }

  const updateQuantity = (tableId, itemId, qty) => {
    if (qty < 1) return
    setCart(prev => ({
      ...prev,
      [tableId]: prev[tableId].map(i => i.id === itemId ? { ...i, quantity: qty } : i)
    }))
  }

  const updateComment = (tableId, itemId, comment) => {
    setCart(prev => ({
      ...prev,
      [tableId]: prev[tableId].map(i => i.id === itemId ? { ...i, comment } : i)
    }))
  }

  // 6) placeOrder → POST /api/orders
  const placeOrder = async (tableId, items) => {
    const { data } = await api.post("/orders", { tableId, items });
    setOrders(prev => [...prev, data.order]);
    setCart(prev => ({ ...prev, [tableId]: [] }));
    return data.trackCode;   // <-- now returns just the code
  };

  // 7) updateOrderStatus → PUT /api/orders/{id}/status
  const updateOrderStatus = async (orderId, status) => {
    const { data } = await api.put(`/orders/${orderId}/status`, { status })
    setOrders(prev => prev.map(o => o.id === data.id ? data : o))
    return data
  }

  // 8) Helpers for components
  const getTableCart = tableId => cart[tableId] || []
  const getFilteredOrders = status => status ? orders.filter(o => o.status === status) : orders

  const value = {
    orders,
    addToCart,
    removeFromCart,
    updateQuantity,
    updateComment,
    placeOrder,
    updateOrderStatus,
    getTableCart,
    getFilteredOrders,
  }

  return <OrderContext.Provider value={value}>{children}</OrderContext.Provider>
}
