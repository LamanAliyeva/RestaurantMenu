package az.edu.ada.restaurant.service.impl;

import az.edu.ada.restaurant.model.Category;
import az.edu.ada.restaurant.repository.CategoryRepository;
import az.edu.ada.restaurant.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repo;

    public CategoryServiceImpl(CategoryRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Category> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<Category> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public Category save(Category category) {
        return repo.save(category);
    }

    @Override
    public Optional<Category> update(Long id, Category category) {
        return repo.findById(id)
                .map(existing -> {
                    existing.setName(category.getName());
                    return repo.save(existing);
                });
    }

    @Override
    public boolean delete(Long id) {
        return repo.findById(id)
                .map(existing -> {
                    repo.delete(existing);
                    return true;
                })
                .orElse(false);
    }

}
