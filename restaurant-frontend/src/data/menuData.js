// Mock menu data
const menuItems = [
  {
    id: 1,
    name: "Classic Burger",
    description: "Juicy beef patty with lettuce, tomato, and special sauce",
    price: 12.99,
    category: "Main",
    image:
      "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&h=500&q=80",
  },
  {
    id: 2,
    name: "Margherita Pizza",
    description: "Traditional pizza with tomato sauce, mozzarella, and basil",
    price: 14.99,
    category: "Main",
    image:
      "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&h=500&q=80",
  },
  {
    id: 3,
    name: "Caesar Salad",
    description: "Crisp romaine lettuce with Caesar dressing, croutons, and parmesan",
    price: 9.99,
    category: "Starter",
    image:
      "https://images.unsplash.com/photo-1550304943-4f24f54ddde9?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&h=500&q=80",
  },
  {
    id: 4,
    name: "French Fries",
    description: "Crispy golden fries served with ketchup",
    price: 4.99,
    category: "Side",
    image:
      "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&h=500&q=80",
  },
  {
    id: 5,
    name: "Chocolate Brownie",
    description: "Warm chocolate brownie served with vanilla ice cream",
    price: 7.99,
    category: "Dessert",
    image:
      "https://images.unsplash.com/photo-1564355808539-22fda35bed7e?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&h=500&q=80",
  },
  {
    id: 6,
    name: "Grilled Salmon",
    description: "Fresh salmon fillet grilled to perfection with lemon butter sauce",
    price: 18.99,
    category: "Main",
    image:
      "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&h=500&q=80",
  },
  {
    id: 7,
    name: "Pasta Carbonara",
    description: "Spaghetti with creamy sauce, pancetta, and parmesan",
    price: 13.99,
    category: "Main",
    image:
      "https://images.unsplash.com/photo-1612874742237-6526221588e3?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&h=500&q=80",
  },
  {
    id: 8,
    name: "Chicken Wings",
    description: "Spicy buffalo wings served with blue cheese dip",
    price: 10.99,
    category: "Starter",
    image:
      "https://images.unsplash.com/photo-1567620832903-9fc6debc209f?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&h=500&q=80",
  },
  {
    id: 9,
    name: "Tiramisu",
    description: "Classic Italian dessert with coffee-soaked ladyfingers and mascarpone",
    price: 8.99,
    category: "Dessert",
    image:
      "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&h=500&q=80",
  },
  {
    id: 10,
    name: "Vegetable Stir Fry",
    description: "Mixed vegetables stir-fried in a savory sauce",
    price: 11.99,
    category: "Main",
    image:
      "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&h=500&q=80",
  },
]

// Group menu items by category
export const getMenuByCategory = () => {
  const categories = {}

  menuItems.forEach((item) => {
    if (!categories[item.category]) {
      categories[item.category] = []
    }
    categories[item.category].push(item)
  })

  return categories
}

// Get all menu items
export const getAllMenuItems = () => menuItems

// Get a single menu item by ID
export const getMenuItemById = (id) => {
  return menuItems.find((item) => item.id === Number.parseInt(id))
}
