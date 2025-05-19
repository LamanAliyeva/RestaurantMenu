// src/api.js
import axios from "axios"

const api = axios.create({
  baseURL: "/api"
})

// automatically attach the JWT on every request
api.interceptors.request.use(cfg => {
  const user = JSON.parse(localStorage.getItem("restaurantUser"))
  if (user?.token) {
    cfg.headers.Authorization = `Bearer ${user.token}`
  }
  return cfg
})

export default api
