"use client"

import { useState, useEffect } from "react"
import api from "../api"
import OrderCard from "./OrderCard"
import "../styles/KitchenDashboard.css"

export default function KitchenDashboard() {
  const [pendingOrders, setPendingOrders] = useState([])
  const [error, setError] = useState("")

  // load only PENDING orders
  useEffect(() => {
    api.get("/orders?status=pending")
      .then(({ data }) => {
        console.log("Pending orders count:", data.length, data)
        const sorted = [...data].sort(
          (a, b) => new Date(a.createdAt) - new Date(b.createdAt)
        )
        setPendingOrders(sorted)
      })
      .catch(err => {
        console.error("Failed to load pending orders:", err)
        setError("Could not load pending orders.")
      })
  }, [])

  // mark as READY
  const handleMarkAsReady = async (orderId) => {
    try {
      await api.put(`/orders/${orderId}/status`, { status: "ready" })
      setPendingOrders(pendingOrders.filter(o => o.id !== orderId))
    } catch (err) {
      console.error("Failed to update order status:", err)
      setError("Could not update order status.")
    }
  }

  return (
    <div className="kitchen-dashboard">
      <h2>Kitchen Dashboard</h2>
      {error && <div className="error">{error}</div>}

      {pendingOrders.length === 0 ? (
        <p>No pending orders</p>
      ) : (
        <div className="orders-grid">
          {pendingOrders.map(order => (
            <OrderCard
              key={order.id}
              order={order}
              // only show for PENDING
              actionLabel={order.status === "pending" ? "Mark as Ready" : null}
              onAction={order.status === "pending" ? () => handleMarkAsReady(order.id) : null}
              showComments={true}
              showTimeSince={true}
            />
          ))}
        </div>
      )}
    </div>
  )
}
