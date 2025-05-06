package az.edu.ada.restaurant.service;

import az.edu.ada.restaurant.model.Dish;

import java.util.List;
import java.util.Optional;

public interface DishService {
    List<Dish> findAll();
    Optional<Dish> findById(Long id);
    Dish save(Dish dish);
    Optional<Dish> update(Long id, Dish dish);
    boolean delete(Long id);
}
