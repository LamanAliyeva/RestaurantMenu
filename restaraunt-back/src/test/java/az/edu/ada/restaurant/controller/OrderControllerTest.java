package az.edu.ada.restaurant.controller;

import az.edu.ada.restaurant.dto.OrderItemDto;
import az.edu.ada.restaurant.dto.OrderTrackingDto;
import az.edu.ada.restaurant.model.*;
import az.edu.ada.restaurant.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Instant;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest {

    private MockMvc mvc;

    @Mock private OrderService orderService;
    @Mock private az.edu.ada.restaurant.repository.DishRepository dishRepo;
    @Mock private az.edu.ada.restaurant.repository.UserRepository userRepo;
    @Mock private RedisTemplate<String,Object> redis;

    @InjectMocks
    private OrderController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createOrder_returns201AndBody() throws Exception {
        // arrange: stub userRepo.findByRolesName(...)
        User waiter = User.builder()
                .id(5L)
                .username("w")
                .roles(Set.of(Role.builder().name("WAITER").build()))
                .build();
        when(userRepo.findByRolesName("WAITER"))
                .thenReturn(List.of(waiter));

        // stub dishRepo.findById(...)
        Dish dish = Dish.builder().id(3L).name("Pizza").price(10.0).build();
        when(dishRepo.findById(3L)).thenReturn(Optional.of(dish));

        // stub orderService.createOrder(...)
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        Order savedOrder = new Order();
        savedOrder.setId(42L);
        savedOrder.setTableNumber(2);
        savedOrder.setStatus(OrderStatus.PENDING);
        savedOrder.setWaiter(waiter);
        savedOrder.setItems(List.of());
        when(orderService.createOrder(captor.capture()))
                .thenReturn(savedOrder);

        // act & assert
        String payload = """
          {
            "tableId": 2,
            "items": [
              {"id": 3, "quantity": 1}
            ]
          }
          """;

        mvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.trackCode").value("42"))
                .andExpect(jsonPath("$.order.id").value(42))
                .andExpect(jsonPath("$.order.tableNumber").value(2));

        // verify service got correct Order
        Order toCreate = captor.getValue();
        assert toCreate.getTableNumber() == 2;
        assert toCreate.getItems().size() == 1;
        assert toCreate.getItems().get(0).getDish().getId() == 3L;
    }

    @Test
    void updateStatus_returns200AndOrder() throws Exception {
        Order o = new Order();
        o.setId(7L);
        o.setStatus(OrderStatus.SERVED);

        when(orderService.updateStatus(7L, OrderStatus.COMPLETED))
                .thenReturn(o);

        mvc.perform(put("/api/orders/7/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"completed\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.status").value("served"));
    }

    @Test
    void getOrders_withoutFilter_returnsAll() throws Exception {
        Order o1 = new Order(); o1.setId(1L);
        Order o2 = new Order(); o2.setId(2L);
        when(orderService.findAll()).thenReturn(List.of(o1, o2));

        mvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getOrders_withFilter_returnsFiltered() throws Exception {
        Order o = new Order(); o.setId(5L);
        when(orderService.findByStatus(OrderStatus.PENDING))
                .thenReturn(List.of(o));

        mvc.perform(get("/api/orders")
                        .param("status", "pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5));
    }

    @Test
    void getPendingOrders_returnsPending() throws Exception {
        Order o = new Order(); o.setId(8L);
        when(orderService.findByStatus(OrderStatus.PENDING))
                .thenReturn(List.of(o));

        mvc.perform(get("/api/orders/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(8));
    }

    @Test
    void trackOrder_returnsDto() throws Exception {
        OrderItemDto itemDto = new OrderItemDto(3L, "Steak", 1, "", 18.0);  // adjust constructor as needed

        OrderTrackingDto dto = new OrderTrackingDto(
                "99",
                "PENDING",
                Instant.parse("2025-05-20T10:00:00Z"),
                Instant.parse("2025-05-20T10:15:00Z"),
                List.of(itemDto)
        );

        when(orderService.getTrackingDto(99L))
                .thenReturn(Optional.of(dto));

        mvc.perform(get("/api/orders/track/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackCode").value("99"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.estimatedReadyAt").exists());
    }

    @Test
    void trackOrder_notFound_returns404() throws Exception {
        when(orderService.getTrackingDto(123L))
                .thenReturn(Optional.empty());

        mvc.perform(get("/api/orders/track/123"))
                .andExpect(status().isNotFound());
    }
}
