package az.edu.ada.restaurant.controller;

import az.edu.ada.restaurant.model.Dish;
import az.edu.ada.restaurant.service.DishService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
public class DishController {
    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public List<Dish> getAll() {
        return dishService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dish> getById(@PathVariable Long id) {
        return dishService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Dish> create(@RequestBody Dish dish) {
        Dish saved = dishService.save(dish);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dish> update(@PathVariable Long id,
                                       @RequestBody Dish dish) {
        return dishService.update(id, dish)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (dishService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
