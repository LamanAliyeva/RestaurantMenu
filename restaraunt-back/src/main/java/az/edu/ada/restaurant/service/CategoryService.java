package az.edu.ada.restaurant.service;

import az.edu.ada.restaurant.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Category> findAll();
    Optional<Category> findById(Long id);
    Category save(Category category);
    Optional<Category> update(Long id, Category category);
    boolean delete(Long id);
}
