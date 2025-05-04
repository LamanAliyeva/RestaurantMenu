"use client"

import { useState, useEffect } from "react"
import { useOrders } from "../contexts/OrderContext"
import OrderCard from "./OrderCard"
import "../styles/WaiterDashboard.css"

const WaiterDashboard = () => {
  const { orders, updateOrderStatus } = useOrders()
  const [readyOrders, setReadyOrders] = useState([])

  useEffect(() => {
    // Filter orders that are ready to be served
    const filtered = orders.filter((order) => order.status === "ready")
    // Sort by ready time (oldest first)
    filtered.sort((a, b) => new Date(a.readyAt) - new Date(b.readyAt))
    setReadyOrders(filtered)
  }, [orders])

  const handleMarkAsServed = (orderId) => {
    updateOrderStatus(orderId, "served")
  }

  return (
    <div className="waiter-dashboard">
      <h2>Waiter Dashboard</h2>

      {readyOrders.length === 0 ? (
        <div className="no-orders">
          <p>No orders ready to serve</p>
        </div>
      ) : (
        <div className="orders-grid">
          {readyOrders.map((order) => (
            <OrderCard
              key={order.id}
              order={order}
              actionLabel="Mark as Served"
              onAction={() => handleMarkAsServed(order.id)}
              showTable={true}
              showReadyTime={true}
            />
          ))}
        </div>
      )}
    </div>
  )
}

export default WaiterDashboard
