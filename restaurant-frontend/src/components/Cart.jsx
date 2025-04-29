"use client"

import { useOrders } from "../contexts/OrderContext"
import "../styles/Cart.css"

const Cart = ({ items, tableId, onClose, onSubmit }) => {
  const { removeFromCart, updateQuantity, updateComment } = useOrders()

  const totalPrice = items.reduce((sum, item) => sum + item.price * item.quantity, 0)

  return (
    <div className="cart-overlay">
      <div className="cart-container">
        <div className="cart-header">
          <h2>Your Order - Table {tableId}</h2>
          <button className="close-button" onClick={onClose}>
            ×
          </button>
        </div>

        {items.length === 0 ? (
          <div className="empty-cart">
            <p>Your cart is empty</p>
          </div>
        ) : (
          <>
            <div className="cart-items">
              {items.map((item) => (
                <div key={item.id} className="cart-item">
                  <div className="item-info">
                    <h3>{item.name}</h3>
                    <p className="item-price">${(item.price * item.quantity).toFixed(2)}</p>
                  </div>

                  <div className="item-actions">
                    <div className="quantity-control">
                      <button
                        onClick={() => updateQuantity(tableId, item.id, item.quantity - 1)}
                        disabled={item.quantity <= 1}
                      >
                        -
                      </button>
                      <span>{item.quantity}</span>
                      <button onClick={() => updateQuantity(tableId, item.id, item.quantity + 1)}>+</button>
                    </div>

                    <button className="remove-button" onClick={() => removeFromCart(tableId, item.id)}>
                      Remove
                    </button>
                  </div>

                  <div className="item-comment">
                    <textarea
                      placeholder="Special instructions..."
                      value={item.comment || ""}
                      onChange={(e) => updateComment(tableId, item.id, e.target.value)}
                    />
                  </div>
                </div>
              ))}
            </div>

            <div className="cart-footer">
              <div className="cart-total">
                <span>Total:</span>
                <span>${totalPrice.toFixed(2)}</span>
              </div>

              <button className="place-order-button" onClick={onSubmit}>
                Place Order
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  )
}

export default Cart
