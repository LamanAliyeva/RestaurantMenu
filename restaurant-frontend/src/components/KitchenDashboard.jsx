"use client"

import { useState, useEffect } from "react"
import { useOrders } from "../contexts/OrderContext"
import OrderCard from "./OrderCard"
import "../styles/KitchenDashboard.css"

const KitchenDashboard = () => {
  const { orders, updateOrderStatus } = useOrders()
  const [pendingOrders, setPendingOrders] = useState([])

  useEffect(() => {
    // Filter orders that are pending (not ready yet)
    console.log("🗂️ all orders in context:", orders)
    const filtered = orders.filter(
      o => o.status?.toString().toLowerCase() === "pending"
    )
    // Sort by creation time (oldest first)
    filtered.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
    setPendingOrders(filtered)
    console.log("Pending orders:", filtered)
  }, [orders])

  const handleMarkAsReady = (orderId) => {
    updateOrderStatus(id, "in_prep")
  }

  return (
    <div className="kitchen-dashboard">
      <h2>Kitchen Dashboard</h2>

      {pendingOrders.length === 0 ? (
        <div className="no-orders">
          <p>No pending orders</p>
        </div>
      ) : (
        <div className="orders-grid">
          {pendingOrders.map((order) => (
            <OrderCard
              key={order.id}
              order={order}
              actionLabel="Mark as In Prep"
              onAction={() => handleMarkAsReady(order.id)}
              showComments={true}
              showTimeSince={true}
            />
          ))}
        </div>
      )}
    </div>
  )
}

export default KitchenDashboard
