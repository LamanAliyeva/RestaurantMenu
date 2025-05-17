"use client"

import { useState, useEffect } from "react"
import api from "../api"
import OrderCard from "./OrderCard"
import "../styles/WaiterDashboard.css"

export default function WaiterDashboard() {
  const [readyOrders, setReadyOrders] = useState([])
  const [error, setError] = useState("")

  // load only READY orders
  useEffect(() => {
    api.get("/orders?status=ready")
      .then(({ data }) => setReadyOrders(data))
      .catch(err => {
        console.error("Failed to load ready orders:", err)
        setError("Could not load ready orders.")
      })
  }, [])

  // mark as SERVED
  const handleServe = async (orderId) => {
    try {
      await api.put(`/orders/${orderId}/status`, { status: "served" })
      setReadyOrders(readyOrders.filter(o => o.id !== orderId))
    } catch (err) {
      console.error("Failed to serve order:", err)
      setError("Could not mark order as served.")
    }
  }

  return (
    <div className="waiter-dashboard">
      <h2>Waiter Dashboard</h2>
      {error && <div className="error">{error}</div>}

      {readyOrders.length === 0 ? (
        <p>No orders ready to serve</p>
      ) : (
        <div className="orders-grid">
          {readyOrders.map(order => (
            <OrderCard
              key={order.id}
              order={order}
              // only show for READY
              actionLabel={order.status === "ready" ? "Mark as Served" : null}
              onAction={order.status === "ready" ? () => handleServe(order.id) : null}
              showStatus={true}
              showTimeSince={true}
            />
          ))}
        </div>
      )}
    </div>
  )
}
