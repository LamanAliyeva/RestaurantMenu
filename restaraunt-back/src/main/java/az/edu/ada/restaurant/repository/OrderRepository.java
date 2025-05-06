package az.edu.ada.restaurant.repository;

import az.edu.ada.restaurant.model.Order;
import az.edu.ada.restaurant.model.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
    Optional<Order> findByTrackingCode(String trackingCode);
    @Override
    @EntityGraph(attributePaths = {
            "items",          // fetch the order-items
            "waiter",         // fetch the waiter
            "waiter.roles"    // fetch that waiter’s roles
    })
    Optional<Order> findById(Long id);
}
