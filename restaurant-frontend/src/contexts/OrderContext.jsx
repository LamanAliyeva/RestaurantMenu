"use client"

import { createContext, useContext, useState, useEffect } from "react"

const OrderContext = createContext()

export const useOrders = () => useContext(OrderContext)

export const OrderProvider = ({ children }) => {
  const [orders, setOrders] = useState(() => {
    const savedOrders = localStorage.getItem("restaurantOrders")
    return savedOrders ? JSON.parse(savedOrders) : []
  })

  const [cart, setCart] = useState(() => {
    const savedCart = localStorage.getItem("restaurantCart")
    return savedCart ? JSON.parse(savedCart) : {}
  })

  useEffect(() => {
    localStorage.setItem("restaurantOrders", JSON.stringify(orders))
  }, [orders])

  useEffect(() => {
    localStorage.setItem("restaurantCart", JSON.stringify(cart))
  }, [cart])

  const addToCart = (tableId, item) => {
    setCart((prevCart) => {
      const tableCart = prevCart[tableId] || []

      const existingItemIndex = tableCart.findIndex((cartItem) => cartItem.id === item.id)

      if (existingItemIndex >= 0) {
        const updatedTableCart = [...tableCart]
        updatedTableCart[existingItemIndex] = {
          ...updatedTableCart[existingItemIndex],
          quantity: updatedTableCart[existingItemIndex].quantity + 1,
        }
        return { ...prevCart, [tableId]: updatedTableCart }
      } else {
        return {
          ...prevCart,
          [tableId]: [...tableCart, { ...item, quantity: 1, comment: "" }],
        }
      }
    })
  }

  useEffect(() => {
    api.get("/orders/pending")
      .then(({ data }) => setOrders(data))
      .catch(console.error)
  }, [])

  const removeFromCart = (tableId, itemId) => {
    setCart((prevCart) => {
      const tableCart = prevCart[tableId] || []
      const updatedTableCart = tableCart.filter((item) => item.id !== itemId)
      return { ...prevCart, [tableId]: updatedTableCart }
    })
  }

  const updateQuantity = (tableId, itemId, quantity) => {
    if (quantity < 1) return

    setCart((prevCart) => {
      const tableCart = prevCart[tableId] || []
      const updatedTableCart = tableCart.map((item) => (item.id === itemId ? { ...item, quantity } : item))
      return { ...prevCart, [tableId]: updatedTableCart }
    })
  }

  const updateComment = (tableId, itemId, comment) => {
    setCart((prevCart) => {
      const tableCart = prevCart[tableId] || []
      const updatedTableCart = tableCart.map((item) => (item.id === itemId ? { ...item, comment } : item))
      return { ...prevCart, [tableId]: updatedTableCart }
    })
  }

  const placeOrder = (tableId, cartItems) => {
    const newOrder = {
      id: `order-${Date.now()}`,
      tableId,
      items: cartItems,
      status: "pending", // pending, ready, served, completed
      createdAt: new Date().toISOString(),
      readyAt: null,
      servedAt: null,
      completedAt: null,
    }

    setOrders((prevOrders) => [...prevOrders, newOrder])

    setCart((prevCart) => ({ ...prevCart, [tableId]: [] }))

    return newOrder
  }

  const updateOrderStatus = (orderId, status) => {
    setOrders((prevOrders) =>
      prevOrders.map((order) => {
        if (order.id !== orderId) return order

        const updatedOrder = { ...order, status }

        if (status === "ready" && !order.readyAt) {
          updatedOrder.readyAt = new Date().toISOString()
        } else if (status === "served" && !order.servedAt) {
          updatedOrder.servedAt = new Date().toISOString()
        } else if (status === "completed" && !order.completedAt) {
          updatedOrder.completedAt = new Date().toISOString()
        }

        return updatedOrder
      }),
    )
  }

  const getTableCart = (tableId) => {
    return cart[tableId] || []
  }

  const getFilteredOrders = (status) => {
    if (!status) return orders
    return orders.filter((order) => order.status === status)
  }

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
