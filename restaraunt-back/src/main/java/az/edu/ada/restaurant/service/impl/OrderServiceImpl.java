package az.edu.ada.restaurant.service.impl;

import az.edu.ada.restaurant.model.Order;
import az.edu.ada.restaurant.model.OrderStatus;
import az.edu.ada.restaurant.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {


    @Override
    public Order createOrder(Order order) {
        return null;
    }

    @Override
    public List<Order> findAll() {
        return null;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Order updateStatus(Long orderId, OrderStatus status) {
        return null;
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return null;
    }

    @Override
    public Order updateStatus(Long orderId) {
        return null;
    }

    @Override
    public void deleteOrder(Long id) {

    }

    @Override
    public Optional<Order> findByTrackingCode(String trackingCode) {
        return Optional.empty();
    }
}
