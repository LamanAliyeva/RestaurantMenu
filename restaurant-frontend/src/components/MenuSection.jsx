"use client"

import { useOrders } from "../contexts/OrderContext"
import "../styles/MenuSection.css"

const MenuSection = ({ category, items, tableId }) => {
  const { addToCart } = useOrders()

  return (
    <section
      id={category.replace(/\s+/g, '-').toLowerCase()}
      className="menu-section"
    >
      <h2 className="category-title">{category}</h2>

      <div className="menu-items">
        {items.map((item) => (
          <div key={item.id} className="menu-item">
            <div className="item-image">
              <img src={item.image || "/placeholder.svg"} alt={item.name} />
            </div>

            <div className="item-details">
              <h3 className="item-name">{item.name}</h3>
              <p className="item-description">{item.description}</p>
              <div className="item-price-action">
                <span className="item-price">${item.price.toFixed(2)}</span>
                <button className="add-to-cart-button" onClick={() => addToCart(tableId, item)}>
                  Add to Cart
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </section>
  )
}

export default MenuSection
