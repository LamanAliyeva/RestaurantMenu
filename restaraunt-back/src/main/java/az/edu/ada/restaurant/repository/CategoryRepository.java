package az.edu.ada.restaurant.repository;

import az.edu.ada.restaurant.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {}