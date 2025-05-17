"use client"

import { useState, useEffect } from "react"
import api from "../api"
import OrderCard from "./OrderCard"
import "../styles/ManagerDashboard.css"

export default function AdminDashboard() {
  const [orders, setOrders] = useState([])
  const [error, setError] = useState("")

  // Load all orders for admin (excluding completed if desired)
  useEffect(() => {
    api.get("/orders")
      .then(({ data }) => setOrders(data))
      .catch(err => {
        console.error("Failed to load orders:", err)
        setError("Could not load orders.")
      })
  }, [])

  const handleComplete = async (orderId) => {
    try {
      await api.put(`/orders/${orderId}/status`, { status: "completed" })
      setOrders(prev => prev.filter(o => o.id !== orderId))
    } catch (err) {
      console.error("Failed to complete order:", err)
      setError("Could not mark order as completed.")
    }
  }

  const handleDelete = async (orderId) => {
    try {
      await api.delete(`/orders/${orderId}`)
      setOrders(prev => prev.filter(o => o.id !== orderId))
    } catch (err) {
      console.error("Failed to delete order:", err)
      setError("Could not delete order.")
    }
  }

  return (
    <div className="admin-dashboard">
      <h2>Admin Dashboard</h2>
      {error && <div className="error">{error}</div>}

      {orders.length === 0 ? (
        <p>No orders available</p>
      ) : (
        <div className="orders-grid">
          {orders.map(order => (
            <div key={order.id} className="order-wrapper">
              <OrderCard
                order={order}
                showStatus={true}
                showAllTimes={true}
                showComments={true}
              />
              <div className="actions">
                {order.status === "served" && (
                  <button className="action-button" onClick={() => handleComplete(order.id)}>
                    Mark as Completed
                  </button>
                )}
                <button className="action-button" onClick={() => handleDelete(order.id)}>
                  Delete Order
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
