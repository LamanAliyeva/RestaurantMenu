"use client"

import { formatDistanceToNow } from "date-fns"
import "../styles/OrderCard.css"

const OrderCard = ({
  order,
  actionLabel,
  onAction,
  showComments = false,
  showTable = false,
  showStatus = false,
  showTimeSince = false,
  showReadyTime = false,
  showAllTimes = false,
}) => {
  const formatTime = (timestamp) => {
    if (!timestamp) return "N/A"
    return formatDistanceToNow(new Date(timestamp), { addSuffix: true })
  }

  const getStatusClass = (status) => {
    switch (status) {
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

  return (
    <div className={`order-card ${getStatusClass(order.status)}`}>
      <div className="order-header">
        <h3>Order #{order.id.split("-")[1]}</h3>
        {showTable && <span className="table-number">Table {order.tableId}</span>}
        {showStatus && (
          <span className={`order-status ${getStatusClass(order.status)}`}>
            {order.status.charAt(0).toUpperCase() + order.status.slice(1)}
          </span>
        )}
      </div>

      <div className="order-times">
        {(showTimeSince || showAllTimes) && (
          <div className="time-info">
            <span className="time-label">Ordered:</span>
            <span className="time-value">{formatTime(order.createdAt)}</span>
          </div>
        )}

        {(showReadyTime || showAllTimes) && order.readyAt && (
          <div className="time-info">
            <span className="time-label">Ready:</span>
            <span className="time-value">{formatTime(order.readyAt)}</span>
          </div>
        )}

        {showAllTimes && order.servedAt && (
          <div className="time-info">
            <span className="time-label">Served:</span>
            <span className="time-value">{formatTime(order.servedAt)}</span>
          </div>
        )}

        {showAllTimes && order.completedAt && (
          <div className="time-info">
            <span className="time-label">Completed:</span>
            <span className="time-value">{formatTime(order.completedAt)}</span>
          </div>
        )}
      </div>

      <div className="order-items">
        <h4>Items:</h4>
        <ul>
          {order.items.map((item, index) => (
            <li key={index}>
              <div className="item-details">
                <span className="item-quantity">{item.quantity}x</span>
                <span className="item-name">{item.name}</span>
                <span className="item-price">${(item.price * item.quantity).toFixed(2)}</span>
              </div>

              {showComments && item.comment && (
                <div className="item-comment">
                  <span className="comment-label">Note:</span>
                  <span className="comment-text">{item.comment}</span>
                </div>
              )}
            </li>
          ))}
        </ul>
      </div>

      {actionLabel && onAction && (
        <button className="action-button" onClick={onAction}>
          {actionLabel}
        </button>
      )}
    </div>
  )
}

export default OrderCard
