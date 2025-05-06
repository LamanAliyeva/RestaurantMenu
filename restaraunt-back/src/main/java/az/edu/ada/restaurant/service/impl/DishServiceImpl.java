package az.edu.ada.restaurant.service.impl;

import az.edu.ada.restaurant.model.Dish;
import az.edu.ada.restaurant.repository.DishRepository;
import az.edu.ada.restaurant.service.DishService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DishServiceImpl implements DishService {

    private final DishRepository repo;

    public DishServiceImpl(DishRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Dish> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<Dish> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public Dish save(Dish dish) {
        return repo.save(dish);
    }

    @Override
    public Optional<Dish> update(Long id, Dish dish) {
        return repo.findById(id)
                .map(existing -> {
                    existing.setName(dish.getName());
                    existing.setDescription(dish.getDescription());
                    existing.setPrice(dish.getPrice());
                    existing.setCategory(dish.getCategory());
                    return repo.save(existing);
                });
    }

    @Override
    public boolean delete(Long id) {
        return repo.findById(id)
                .map(existing -> {
                    repo.deleteById(id);
                    return true;
                })
                .orElse(false);
    }
}
