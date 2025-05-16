"use client"

import api from "../api"
import { useState, useEffect } from "react"
import { useParams, useNavigate } from "react-router-dom"
import { useOrders } from "../contexts/OrderContext"
import MenuSection from "../components/MenuSection"
import Cart from "../components/Cart"
import "../styles/TableMenu.css"

const TableMenu = () => {
  const { tableId } = useParams()
  const navigate = useNavigate()
  const { getTableCart, placeOrder } = useOrders()
  const [menuCategories, setMenuCategories] = useState({})
  const [cartItems, setCartItems] = useState([])
  const [isCartOpen, setIsCartOpen] = useState(false)
  const [activeCategory, setActiveCategory] = useState(Object.keys(menuCategories)[0] || '')


  useEffect(() => {
    // fetch real menu from backend
    api.get("/categories")
      .then(({ data }) => {
        console.log("📦 categories from server:", data)      // ← add this
        const byCat = {}
        data.forEach(cat => (byCat[cat.name] = cat.dishes))
        setMenuCategories(byCat)
      })
      .catch(err => {
        console.error("Error fetching categories:", err)
      })
  }, [])


  // Load cart data for this table
  useEffect(() => {
    if (tableId) {
      setCartItems(getTableCart(tableId))
    }
  }, [tableId, getTableCart])

  // Validate table ID
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
        // Scrolling down & past the initial 100px
        navbar.classList.add('hidden');
      } else {
        // Scrolling up
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
    ; (async () => {
      const items = getTableCart(tableId)
      if (items.length === 0) return

      try {
        const code = await placeOrder(tableId, items)
        setIsCartOpen(false)
        // redirect to /track/:code
        navigate(`/track/${code}`)
      } catch (err) {
        console.error("Order placement failed:", err)
        alert("Could not place your order.")
      }
    })()
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
          Cart ({getTableCart(tableId).reduce((s, i) => s + i.quantity, 0)})
        </button>
      </header>

      <main className="menu-content">
        {Object.entries(menuCategories).map(([category, items]) => (
          <MenuSection key={category} category={category} items={items} tableId={tableId} />
        ))}
      </main>

      {isCartOpen && (
        <Cart
          items={getTableCart(tableId)}        // always fresh array
          tableId={tableId}
          onClose={() => setIsCartOpen(false)}
          onSubmit={handleSubmitOrder}
        />
      )}
    </div>
  )
}

export default TableMenu
