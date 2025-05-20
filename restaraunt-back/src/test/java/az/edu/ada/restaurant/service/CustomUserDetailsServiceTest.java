package az.edu.ada.restaurant.service;

import az.edu.ada.restaurant.model.Role;
import az.edu.ada.restaurant.model.User;
import az.edu.ada.restaurant.repository.UserRepository;
import az.edu.ada.restaurant.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private CustomUserDetailsService svc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_exists_returnsUserDetails() {
        // Build a Role via Lombok
        Role waiterRole = Role.builder()
                .name("WAITER")
                .build();

        User u = new User();
        u.setUsername("alice");
        u.setPassword("hash");
        u.setRoles(Set.of(waiterRole));  // now compiles

        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(u));

        var userDetails = svc.loadUserByUsername("alice");

        assertEquals("alice", userDetails.getUsername());
        assertEquals("hash", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_WAITER")));
    }

    @Test
    void loadUserByUsername_missing_throws() {
        when(userRepo.findByUsername("bob")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> svc.loadUserByUsername("bob"));
    }
}
