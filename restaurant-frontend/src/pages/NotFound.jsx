import { Link } from "react-router-dom"
import "../styles/NotFound.css"

const NotFound = () => {
  return (
    <div className="not-found-container">
      <h1>404</h1>
      <h2>Page Not Found</h2>
      <p>The page you are looking for does not exist.</p>
      <div className="not-found-links">
        <Link to="/" className="home-link">
          Go to Table 1
        </Link>
        <Link to="/admin" className="admin-link">
          Go to Admin
        </Link>
      </div>
    </div>
  )
}

export default NotFound
