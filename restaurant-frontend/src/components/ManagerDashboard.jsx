"use client"

import { useState, useEffect } from "react"
import { useOrders } from "../contexts/OrderContext"
import OrderCard from "./OrderCard"
import "../styles/ManagerDashboard.css"

const ManagerDashboard = () => {
  const { orders, updateOrderStatus } = useOrders()
  const [filteredOrders, setFilteredOrders] = useState([])
  const [statusFilter, setStatusFilter] = useState("all")

  useEffect(() => {
    let filtered = [...orders]

    // Apply status filter
    if (statusFilter !== "all") {
      filtered = filtered.filter((order) => order.status === statusFilter)
    }

    // Sort by creation time (newest first)
    filtered.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))

    setFilteredOrders(filtered)
  }, [orders, statusFilter])

  const handleCompleteOrder = (orderId) => {
    updateOrderStatus(orderId, "completed")
  }

  return (
    <div className="manager-dashboard">
      <h2>Manager Dashboard</h2>

      <div className="filter-controls">
        <label htmlFor="status-filter">Filter by status:</label>
        <select id="status-filter" value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
          <option value="all">All Orders</option>
          <option value="pending">Pending</option>
          <option value="ready">Ready</option>
          <option value="served">Served</option>
          <option value="completed">Completed</option>
        </select>
      </div>

      {filteredOrders.length === 0 ? (
        <div className="no-orders">
          <p>No orders found</p>
        </div>
      ) : (
        <div className="orders-grid">
          {filteredOrders.map((order) => (
            <OrderCard
              key={order.id}
              order={order}
              actionLabel={order.status !== "completed" ? "Mark as Completed" : null}
              onAction={order.status !== "completed" ? () => handleCompleteOrder(order.id) : null}
              showTable={true}
              showStatus={true}
              showAllTimes={true}
            />
          ))}
        </div>
      )}
    </div>
  )
}

export default ManagerDashboard
