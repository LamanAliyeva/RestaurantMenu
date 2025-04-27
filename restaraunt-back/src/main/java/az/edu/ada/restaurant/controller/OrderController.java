package az.edu.ada.restaurant.controller;

import az.edu.ada.restaurant.model.*;
import az.edu.ada.restaurant.repository.DishRepository;
import az.edu.ada.restaurant.repository.UserRepository;
import az.edu.ada.restaurant.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final DishRepository dishRepo;
    private final UserRepository userRepo;


    public OrderController(OrderService orderService, DishRepository dishRepo, UserRepository userRepo) {
        this.orderService = orderService;
        this.dishRepo = dishRepo;
        this.userRepo = userRepo;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
//      To-Do
        return null;
    }

    @PostMapping
    public ResponseEntity<Map<String,Object>> createOrder(@RequestBody Map<String,Object> body) {
//        To-Do
        return null;
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus() {
//        To-do
        return null;
    }

    @GetMapping
    public List<Order> getOrders(@RequestParam(value="status", required=false) String status) {
//        To-Do
        return null;
    }


}
