"use client"

import { useState, useEffect } from "react"
import api from "../api"
import OrderCard from "./OrderCard"
import "../styles/ManagerDashboard.css"

export default function ManagerDashboard() {
  const [orders, setOrders] = useState([])
  const [error, setError] = useState("")

  // Load all orders for manager
  useEffect(() => {
    api.get("/orders")
      .then(({ data }) => {
        // Filter out completed orders
        const activeOrders = data.filter(order => order.status !== "completed")
        setOrders(activeOrders)
      })
      .catch(err => {
        console.error("Failed to load orders:", err)
        setError("Could not load orders.")
      })
  }, [])

  const handleComplete = async (orderId) => {
    try {
      await api.put(`/orders/${orderId}/status`, { status: "completed" })
      setOrders(orders.filter(o => o.id !== orderId))
    } catch (err) {
      console.error("Failed to complete order:", err)
      setError("Could not mark order as completed.")
    }
  }

  return (
    <div className="manager-dashboard">
      <h2>Manager Dashboard</h2>
      {error && <div className="error">{error}</div>}

      {orders.length === 0 ? (
        <p>No orders available</p>
      ) : (
        <div className="orders-grid">
          {orders.map(order => (
            <OrderCard
              key={order.id}
              order={order}
              // Only show complete button for served orders
              actionLabel={order.status === "served" ? "Mark as Completed" : null}
              onAction={order.status === "served" ? () => handleComplete(order.id) : null}
              showStatus={true}
              showAllTimes={true}
              showComments={true}
            />
          ))}
        </div>
      )}
    </div>
  )
}