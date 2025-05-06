package az.edu.ada.restaurant.service;

import az.edu.ada.restaurant.dto.OrderTrackingDto;
import az.edu.ada.restaurant.model.Order;
import az.edu.ada.restaurant.model.OrderStatus;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(Order order);
    List<Order> findAll();
    Optional<Order> findById(Long id);


    Order updateStatus(Long orderId, OrderStatus status);
    List<Order> findByStatus(OrderStatus status);

    void deleteOrder(Long id);
    Optional<Order> findByTrackingCode(String trackingCode);
}
