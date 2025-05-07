package az.edu.ada.restaurant.service.impl;

import az.edu.ada.restaurant.dto.OrderItemDto;
import az.edu.ada.restaurant.dto.OrderTrackingDto;
import az.edu.ada.restaurant.model.Order;
import az.edu.ada.restaurant.model.OrderStatus;
import az.edu.ada.restaurant.repository.OrderRepository;
import az.edu.ada.restaurant.service.OrderService;
import jakarta.persistence.Column;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository repo;

    @Column(name = "table_number", nullable = false)
    private Integer tableNumber;


    public OrderServiceImpl(OrderRepository repo) {
        this.repo = repo;
    }

    @Override
    @CacheEvict(cacheNames = "orderTracking", key = "#order.id")
    public Order createOrder(Order order) {
        // assume `order` already has tableNumber, status, and List<OrderItem> items populated
        order.setStatus(OrderStatus.PENDING);
        return repo.save(order);
    }

    @Override
    public List<Order> findAll() {
        List<Order> all = repo.findAll();
        System.out.println(">>> findAll() returned " + all.size() + " orders: " +
                all.stream().map(o->o.getId().toString()).collect(Collectors.joining(",")));
        return all;
    }

    @Override
    @Cacheable(cacheNames = "orderTracking", key = "#orderId")
    public Optional<Order> findById(Long orderId) {
        Optional<Order> opt = repo.findById(orderId);
        opt.ifPresent(o -> {
            // force initialization of the items list
            Hibernate.initialize(o.getItems());
            // if you need nested things (like dish or waiter.roles), initialize them too:
            o.getItems().forEach(item -> Hibernate.initialize(item.getDish()));
        });
        return opt;
    }

    @Override
    @Cacheable(cacheNames="orderTracking", key="#orderId")
    public Optional<OrderTrackingDto> getTrackingDto(Long orderId) {
        return repo.findById(orderId).map(o -> {
            List<OrderItemDto> itemDtos = o.getItems().stream()
                    .map(i -> new OrderItemDto(
                            i.getId(),
                            i.getDish().getName(),
                            i.getQuantity(),
                            i.getComment(),
                            i.getPrice()
                    ))
                    .toList();

            Instant created   = o.getCreatedAt();
            Instant estimated = created.plus(Duration.ofMinutes(15));

            return new OrderTrackingDto(
                    orderId.toString(),
                    o.getStatus().name().toLowerCase(),
                    created,
                    estimated,
                    itemDtos
            );
        });
    }

    public Order findByIdOrThrow(Long id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + id)
                );
    }

    @CacheEvict(cacheNames = "orderTracking", key = "#id")
    public Order updateStatus(Long id, OrderStatus newStatus) {
        Order o = findByIdOrThrow(id);
        switch (newStatus) {
            case READY      -> o.setReadyAt(Instant.now());
            case SERVED     -> o.setServedAt(Instant.now());
            case COMPLETED -> o.setCompletedAt(Instant.now());
            default         -> {}
        }
        o.setStatus(newStatus);
        return repo.save(o);
    }


    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return repo.findByStatus(status);
    }

    @Override
    @CacheEvict(cacheNames = "orderTracking", key = "#orderId")
    public void deleteOrder(Long orderId) {
        // will throw 404 if not found
        Order order = repo.findById(orderId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + orderId)
                );
        repo.delete(order);
    }

    @Override
    public Optional<Order> findByTrackingCode(String trackingCode) {
        return repo.findByTrackingCode(trackingCode);
    }

}
