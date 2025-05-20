package az.edu.ada.restaurant.service.impl;

import az.edu.ada.restaurant.model.Category;
import az.edu.ada.restaurant.repository.CategoryRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @Mock
    private CategoryRepository repo;

    @InjectMocks
    private CategoryServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_returnsAllCategories() {
        List<Category> cats = List.of(
                Category.builder().id(1L).name("A").build(),
                Category.builder().id(2L).name("B").build()
        );
        when(repo.findAll()).thenReturn(cats);

        List<Category> result = service.findAll();

        assertSame(cats, result);
        verify(repo).findAll();
    }

    @Test
    void findById_existing_returnsOptionalOfCategory() {
        Category c = Category.builder().id(5L).name("X").build();
        when(repo.findById(5L)).thenReturn(Optional.of(c));

        Optional<Category> opt = service.findById(5L);

        assertTrue(opt.isPresent());
        assertSame(c, opt.get());
    }

    @Test
    void findById_missing_returnsEmptyOptional() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        Optional<Category> opt = service.findById(99L);

        assertTrue(opt.isEmpty());
    }

    @Test
    void save_delegatesToRepository() {
        Category toSave = Category.builder().name("New").build();
        Category saved  = Category.builder().id(10L).name("New").build();
        when(repo.save(toSave)).thenReturn(saved);

        Category result = service.save(toSave);

        assertSame(saved, result);
        verify(repo).save(toSave);
    }

    @Test
    void update_existing_updatesNameAndReturnsUpdated() {
        Category existing = Category.builder().id(3L).name("Old").build();
        Category newData  = Category.builder().name("Fresh").build();
        Category updated  = Category.builder().id(3L).name("Fresh").build();

        when(repo.findById(3L)).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(updated);

        Optional<Category> opt = service.update(3L, newData);

        assertTrue(opt.isPresent());
        assertEquals("Fresh", opt.get().getName());
        verify(repo).findById(3L);
        verify(repo).save(existing);
    }

    @Test
    void update_missing_returnsEmptyOptional() {
        when(repo.findById(42L)).thenReturn(Optional.empty());

        Optional<Category> opt = service.update(42L,
                Category.builder().name("Doesn't matter").build());

        assertTrue(opt.isEmpty());
        verify(repo).findById(42L);
        verify(repo, never()).save(any());
    }

    @Test
    void delete_existing_deletesAndReturnsTrue() {
        Category existing = Category.builder().id(7L).name("Z").build();
        when(repo.findById(7L)).thenReturn(Optional.of(existing));

        boolean result = service.delete(7L);

        assertTrue(result);
        verify(repo).findById(7L);
        verify(repo).delete(existing);
    }

    @Test
    void delete_missing_returnsFalse() {
        when(repo.findById(88L)).thenReturn(Optional.empty());

        boolean result = service.delete(88L);

        assertFalse(result);
        verify(repo).findById(88L);
        verify(repo, never()).delete(any());
    }
}
