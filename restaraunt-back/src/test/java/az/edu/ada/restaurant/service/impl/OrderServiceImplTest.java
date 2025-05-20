package az.edu.ada.restaurant.service.impl;

import az.edu.ada.restaurant.model.Order;
import az.edu.ada.restaurant.model.OrderStatus;
import az.edu.ada.restaurant.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock
    private OrderRepository repo;

    @InjectMocks
    private OrderServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_setsPendingAndSaves() {
        Order input = new Order();
        input.setTableNumber(5);
        // stub save to return the same object
        when(repo.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = service.createOrder(input);

        assertEquals(OrderStatus.PENDING, result.getStatus());
        verify(repo, times(1)).save(result);
    }


    @Test
    void findById_returnsOptionalFromRepo() {
        Order o = new Order();
        when(repo.findById(1L)).thenReturn(Optional.of(o));

        Optional<Order> opt = service.findById(1L);

        assertTrue(opt.isPresent());
        assertSame(o, opt.get());
    }

    @Test
    void findByIdOrThrow_missing_throws404() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> service.findByIdOrThrow(99L)
        );
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void findByIdOrThrow_present_returnsEntity() {
        Order o = new Order();
        o.setId(7L);
        when(repo.findById(7L)).thenReturn(Optional.of(o));

        Order result = service.findByIdOrThrow(7L);
        assertSame(o, result);
    }

    @Test
    void updateStatus_nonexistent_throws404() {
        when(repo.findById(42L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> service.updateStatus(42L, OrderStatus.READY)
        );
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void updateStatus_ready_setsReadyAtAndStatus() {
        Order o = new Order();
        o.setId(1L);
        o.setStatus(OrderStatus.PENDING);
        when(repo.findById(1L)).thenReturn(Optional.of(o));
        when(repo.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order updated = service.updateStatus(1L, OrderStatus.READY);

        assertEquals(OrderStatus.READY, updated.getStatus());
        assertNotNull(updated.getReadyAt());
        // other timestamps should still be null
        assertNull(updated.getServedAt());
        assertNull(updated.getCompletedAt());
        verify(repo).save(updated);
    }

    @Test
    void updateStatus_served_setsServedAtAndStatus() {
        Order o = new Order();
        o.setId(2L);
        o.setStatus(OrderStatus.READY);
        when(repo.findById(2L)).thenReturn(Optional.of(o));
        when(repo.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order updated = service.updateStatus(2L, OrderStatus.SERVED);

        assertEquals(OrderStatus.SERVED, updated.getStatus());
        assertNotNull(updated.getServedAt());
        verify(repo).save(updated);
    }

    @Test
    void updateStatus_completed_setsCompletedAtAndStatus() {
        Order o = new Order();
        o.setId(3L);
        o.setStatus(OrderStatus.SERVED);
        when(repo.findById(3L)).thenReturn(Optional.of(o));
        when(repo.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order updated = service.updateStatus(3L, OrderStatus.COMPLETED);

        assertEquals(OrderStatus.COMPLETED, updated.getStatus());
        assertNotNull(updated.getCompletedAt());
        verify(repo).save(updated);
    }

    @Test
    void findByStatus_delegatesToRepository() {
        List<Order> stub = List.of(new Order());
        when(repo.findByStatus(OrderStatus.PENDING)).thenReturn(stub);

        List<Order> result = service.findByStatus(OrderStatus.PENDING);

        assertSame(stub, result);
        verify(repo).findByStatus(OrderStatus.PENDING);
    }

    @Test
    void deleteOrder_nonexistent_throws404() {
        when(repo.findById(88L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> service.deleteOrder(88L)
        );
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void deleteOrder_existing_deletes() {
        Order o = new Order();
        o.setId(9L);
        when(repo.findById(9L)).thenReturn(Optional.of(o));

        // no exception thrown
        service.deleteOrder(9L);

        verify(repo).delete(o);
    }
}
