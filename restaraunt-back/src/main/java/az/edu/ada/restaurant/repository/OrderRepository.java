package az.edu.ada.restaurant.repository;

import az.edu.ada.restaurant.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
