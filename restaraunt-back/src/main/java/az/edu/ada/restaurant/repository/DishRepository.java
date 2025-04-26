package az.edu.ada.restaurant.repository;

import az.edu.ada.restaurant.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishRepository extends JpaRepository<Dish, Long> {}

