package az.edu.ada.restaurant.service;

import az.edu.ada.restaurant.model.Order;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(Order order);
    List<Order> findAll();
    Optional<Order> findById(Long id);

    Order updateStatus(Long orderId);

    void deleteOrder(Long id);
    Optional<Order> findByTrackingCode(String trackingCode);
}
