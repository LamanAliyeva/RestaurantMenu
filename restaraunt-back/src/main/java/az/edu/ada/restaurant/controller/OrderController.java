package az.edu.ada.restaurant.controller;

import az.edu.ada.restaurant.dto.OrderTrackingDto;
import az.edu.ada.restaurant.model.*;
import az.edu.ada.restaurant.repository.DishRepository;
import az.edu.ada.restaurant.repository.UserRepository;
import az.edu.ada.restaurant.service.OrderService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final DishRepository dishRepo;
    private final UserRepository userRepo;

    private RedisTemplate<String, Object> redis;

    public OrderController(OrderService orderService, DishRepository dishRepo, UserRepository userRepo, RedisTemplate<String, Object> redis) {
        this.orderService = orderService;
        this.dishRepo = dishRepo;
        this.userRepo = userRepo;
        this.redis = redis;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Map<String,Object>> createOrder(@RequestBody Map<String,Object> body) {
        // 1) Parse table number (handles numeric or string)
        Object rawTable = body.get("tableId");
        int tableNum;
        if (rawTable instanceof Number) {
            tableNum = ((Number) rawTable).intValue();
        } else {
            tableNum = Integer.parseInt(rawTable.toString());
        }

        // 2) Extract items payload
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> itemsData = (List<Map<String,Object>>) body.get("items");

        // 3) Build Order entity
        Order order = new Order();
        order.setTableNumber(tableNum);
        order.setStatus(OrderStatus.PENDING);

        // ← set the currently authenticated waiter
        List<User> waiters = userRepo.findByRolesName("WAITER");
        if (waiters.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No waiters available to assign");
        }
        Random rnd = new Random();
        User randomWaiter = waiters.get(rnd.nextInt(waiters.size()));
        order.setWaiter(randomWaiter);

        // 4) Map each payload entry to an OrderItem
        List<OrderItem> orderItems = itemsData.stream().map(itemMap -> {
            // parse dishId
            Object rawDishId = itemMap.get("id");
            long dishId = (rawDishId instanceof Number)
                    ? ((Number) rawDishId).longValue()
                    : Long.parseLong(rawDishId.toString());

            // parse quantity
            Object rawQty = itemMap.get("quantity");
            int qty = (rawQty instanceof Number)
                    ? ((Number) rawQty).intValue()
                    : Integer.parseInt(rawQty.toString());

            // fetch the Dish
            Dish dish = dishRepo.findById(dishId)
                    .orElseThrow(() -> new RuntimeException("Dish not found: " + dishId));

            // create item
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setDish(dish);
            oi.setQuantity(qty);
            oi.setPrice(dish.getPrice());

            // parse optional comment
            Object rawComment = itemMap.get("comment");
            if (rawComment != null) {
                oi.setComment(rawComment.toString());
            }

            return oi;
        }).collect(Collectors.toList());

        // 5) Attach and save
        order.setItems(orderItems);
        Order saved = orderService.createOrder(order);

        Map<String,Object> resp = new HashMap<>();
        resp.put("order", saved);
        // use the DB ID as the tracking code
        resp.put("trackCode", saved.getId().toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);

    }


    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String,String> body
    ) {
        // Map the incoming JSON string (e.g. "in_prep") back to the enum
        String raw = body.get("status");
        OrderStatus s = OrderStatus.valueOf(raw.toUpperCase());
        Order updated = orderService.updateStatus(id, s);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public List<Order> getOrders(@RequestParam(value="status", required=false) String status) {
        List<Order> result = (status != null)
                ? orderService.findByStatus(OrderStatus.valueOf(status.toUpperCase()))
                : orderService.findAll();

        System.out.println(">>> getOrders() returning " + result.size() +
                " orders: " + result.stream()
                .map(o -> o.getId().toString())
                .collect(Collectors.joining(",")));
        return result;
    }

    @GetMapping("/pending")
    public List<Order> getPendingOrders() {
        return orderService.findByStatus(OrderStatus.PENDING);
    }

    @GetMapping("/track/{code}")
    public ResponseEntity<OrderTrackingDto> trackOrder(@PathVariable String code) {
        Long orderId = Long.parseLong(code);
        OrderTrackingDto dto = orderService.getTrackingDto(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(dto);
    }



}
