package az.edu.ada.restaurant.service.impl;

import az.edu.ada.restaurant.model.Category;
import az.edu.ada.restaurant.model.Dish;
import az.edu.ada.restaurant.repository.DishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DishServiceImplTest {

    @Mock
    private DishRepository repo;

    @InjectMocks
    private DishServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_returnsAllDishes() {
        // 1) Create ONE stub list and reuse it
        List<Dish> stubList = List.of(
                Dish.builder().id(1L).name("A").description("desc").price(1.00).build(),
                Dish.builder().id(2L).name("B").description("desc").price(2.00).build()
        );
        // 2) Stub the repo to return that exact list
        when(repo.findAll()).thenReturn(stubList);

        // 3) Invoke the service
        List<Dish> result = service.findAll();

        // 4) Assert that the *same* list instance was returned
        assertSame(stubList, result);

        // 5) Verify interaction
        verify(repo).findAll();
    }


    @Test
    void findById_existing_returnsOptional() {
        Dish d = Dish.builder().id(5L).name("X").description("x").price(5.00).build();
        when(repo.findById(5L)).thenReturn(Optional.of(d));

        Optional<Dish> opt = service.findById(5L);

        assertTrue(opt.isPresent());
        assertSame(d, opt.get());
    }

    @Test
    void findById_missing_returnsEmpty() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        Optional<Dish> opt = service.findById(99L);

        assertTrue(opt.isEmpty());
        verify(repo).findById(99L);
    }

    @Test
    void save_delegatesToRepository() {
        Dish toSave = Dish.builder()
                .name("New")
                .description("d")
                .price(3.50)
                .build();
        Dish saved  = Dish.builder()
                .id(10L)
                .name("New")
                .description("d")
                .price(3.50)
                .build();
        when(repo.save(toSave)).thenReturn(saved);

        Dish result = service.save(toSave);

        assertSame(saved, result);
        verify(repo).save(toSave);
    }

    @Test
    void update_existing_updatesFields() {
        Category originalCat = Category.builder().id(1L).name("C1").build();
        Category newCat      = Category.builder().id(2L).name("C2").build();

        Dish existing = Dish.builder()
                .id(3L)
                .name("Old")
                .description("old")
                .price(4.00)
                .category(originalCat)
                .build();

        Dish updatedPayload = Dish.builder()
                .name("New")
                .description("new")
                .price(5.50)
                .category(newCat)
                .build();

        Dish saved = Dish.builder()
                .id(3L)
                .name("New")
                .description("new")
                .price(5.50)
                .category(newCat)
                .build();

        when(repo.findById(3L)).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(saved);

        Optional<Dish> opt = service.update(3L, updatedPayload);

        assertTrue(opt.isPresent());
        Dish result = opt.get();
        assertEquals("New", result.getName());
        assertEquals("new", result.getDescription());
        assertEquals(5.50, result.getPrice());
        assertSame(newCat, result.getCategory());

        verify(repo).findById(3L);
        verify(repo).save(existing);
    }

    @Test
    void update_missing_returnsEmpty() {
        Dish payload = Dish.builder().name("X").description("x").price(1.00).build();
        when(repo.findById(42L)).thenReturn(Optional.empty());

        Optional<Dish> opt = service.update(42L, payload);

        assertTrue(opt.isEmpty());
        verify(repo).findById(42L);
        verify(repo, never()).save(any());
    }

    @Test
    void delete_existing_deletesAndReturnsTrue() {
        Dish existing = Dish.builder()
                .id(7L)
                .name("D")
                .description("d")
                .price(2.00)
                .build();
        when(repo.findById(7L)).thenReturn(Optional.of(existing));

        boolean result = service.delete(7L);

        assertTrue(result);
        verify(repo).findById(7L);
        verify(repo).deleteById(7L);
    }

    @Test
    void delete_missing_returnsFalse() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        boolean result = service.delete(99L);

        assertFalse(result);
        verify(repo).findById(99L);
        verify(repo, never()).deleteById(any());
    }
}
