"use client"

import { useState, useEffect } from "react"
import { useParams } from "react-router-dom"
import api from "../api"
import "../styles/TrackOrder.css"

export default function TrackOrder() {
  const { code } = useParams()

  // three states: loading, error, and data
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [info, setInfo] = useState(null)

  useEffect(() => {
    setLoading(true)
    api
      .get(`/orders/track/${code}`)
      .then(({ data }) => {
        setInfo(data) // got a valid payload
        setError("")
      })
      .catch(() => {
        setError("Invalid or expired tracking code. Please check your code and try again.")
        setInfo(null)
      })
      .finally(() => {
        setLoading(false) // either success or error, we're done loading
      })
  }, [code])

  // Get status class based on order status
  const getStatusClass = (status) => {
    switch (status?.toLowerCase()) {
      case "pending":
        return "status-pending"
      case "ready":
        return "status-ready"
      case "served":
        return "status-served"
      case "completed":
        return "status-completed"
      default:
        return ""
    }
  }

  // Get a friendly message based on status
  const getStatusMessage = (status) => {
    switch (status?.toLowerCase()) {
      case "pending":
        return "Your order is being prepared by our chefs. We'll notify you when it's ready!"
      case "ready":
        return "Great news! Your order is being served."
      case "served":
        return "Your order has been served. Enjoy your meal!"
      case "completed":
        return "Your order has been completed. Thank you for dining with us!"
      default:
        return "Thank you for your order!"
    }
  }

  // Format date nicely
  const formatDate = (dateString) => {
    const options = {
      weekday: "long",
      year: "numeric",
      month: "long",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    }
    return new Date(dateString).toLocaleDateString(undefined, options)
  }

  // 1) still loading? show spinner or message
  if (loading) {
    return (
      <div className="loading-message">
        <div className="loading-spinner"></div>
        Checking your order status…
      </div>
    )
  }

  // 2) not loading, but error? show it
  if (!loading && error) {
    return <div className="error">{error}</div>
  }

  // 3) not loading, no error, must have data
  return (
    <div className="track-page">
      <h1>Track Your Order</h1>
      <div className="order-number">{info.trackCode}</div>
      <p>Your order number is</p>

      <div className="track-details">
        <div className="detail-card">
          <h3>STATUS</h3>
          <p>
            <span className={`status-badge ${getStatusClass(info.status)}`}>{info.status}</span>
          </p>
        </div>

        <div className="detail-card">
          <h3>TIME CREATED</h3>
          <p>{formatDate(info.createdAt)}</p>
        </div>

        {info.estimatedReadyAt && (
          <div className="detail-card">
            <h3>ESTIMATED READY TIME</h3>
            <p>{formatDate(info.estimatedReadyAt)}</p>
          </div>
        )}
      </div>

      <div className="track-message">{getStatusMessage(info.status)}</div>
    </div>
  )
}
