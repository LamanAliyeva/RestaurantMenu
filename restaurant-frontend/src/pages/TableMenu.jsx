"use client"

import { useState, useEffect } from "react"
import { useParams, useNavigate } from "react-router-dom"
import { getMenuByCategory } from "../data/menuData"
import MenuSection from "../components/MenuSection"
import Cart from "../components/Cart"

const TableMenu = () => {
  const { tableId } = useParams()
  const navigate = useNavigate()
  const { getTableCart, placeOrder } = useOrders()
  const [menuCategories, setMenuCategories] = useState({})
  const [cartItems, setCartItems] = useState([])
  const [isCartOpen, setIsCartOpen] = useState(false)
  const [activeCategory, setActiveCategory] = useState(Object.keys(menuCategories)[0] || '');

  useEffect(() => {
    setMenuCategories(getMenuByCategory())
  }, [])

  useEffect(() => {
    if (tableId) {
      setCartItems(getTableCart(tableId))
    }
  }, [tableId, getTableCart])

  useEffect(() => {
    const tableNum = Number.parseInt(tableId)
    if (isNaN(tableNum) || tableNum < 1 || tableNum > 8) {
      navigate("/1", { replace: true })
    }
  }, [tableId, navigate])

  useEffect(() => {
    let lastScrollY = window.scrollY;

    const handleScroll = () => {
      const navbar = document.querySelector('.category-nav');
      const currentScrollY = window.scrollY;

      if (currentScrollY > lastScrollY && currentScrollY > 100) {
        navbar.classList.add('hidden');
      } else {
        navbar.classList.remove('hidden');
      }

      lastScrollY = currentScrollY;
    };

    window.addEventListener('scroll', handleScroll);

    return () => {
      window.removeEventListener('scroll', handleScroll);
    };
  }, []);

  const handleSubmitOrder = () => {
    if (cartItems.length === 0) return

    placeOrder(tableId, cartItems)
    setIsCartOpen(false)

    alert("Your order has been placed successfully!")
  }

  const totalItems = cartItems.reduce((sum, item) => sum + item.quantity, 0)

  return (
    <div className="table-menu-container">
      <header className="menu-header">
        <h1>Restaurant Menu</h1>
        <h2>Table {tableId}</h2>

        <nav className="category-nav">
          <div className="category-nav-container">
            {Object.keys(menuCategories).map((cat) => (
              <button
                key={cat}
                className={activeCategory === cat ? "active" : ""}
                onClick={() => {
                  setActiveCategory(cat);
                  const element = document.getElementById(cat.replace(/\s+/g, '-').toLowerCase());
                  if (element) {
                    element.scrollIntoView({ behavior: 'smooth' });
                  }
                }}
              >
                {cat}
              </button>
            ))}
          </div>
        </nav>

        <button className="cart-button" onClick={() => setIsCartOpen(true)}>
          Cart ({totalItems})
        </button>
      </header>

      <main className="menu-content">
        {Object.entries(menuCategories).map(([category, items]) => (
          <MenuSection key={category} category={category} items={items} tableId={tableId} />
        ))}
      </main>

      {isCartOpen && (
        <Cart items={cartItems} tableId={tableId} onClose={() => setIsCartOpen(false)} onSubmit={handleSubmitOrder} />
      )}
    </div>
  )
}

export default TableMenu
