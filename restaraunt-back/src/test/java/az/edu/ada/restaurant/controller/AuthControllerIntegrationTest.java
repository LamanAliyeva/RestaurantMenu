package az.edu.ada.restaurant.controller;

import az.edu.ada.restaurant.model.Role;
import az.edu.ada.restaurant.model.User;
import az.edu.ada.restaurant.repository.UserRepository;
import az.edu.ada.restaurant.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerIntegrationTest {

    private MockMvc mvc;

    @Mock private AuthenticationConfiguration authConfig;
    @Mock private AuthenticationManager authManager;
    @Mock private JwtUtil jwtUtil;
    @Mock private RedisTemplate<String,Object> redis;
    @Mock private SetOperations<String,Object> setOps;
    @Mock private UserRepository userRepo;

    @InjectMocks
    private AuthController controller;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();

        when(authConfig.getAuthenticationManager()).thenReturn(authManager);
        when(redis.opsForSet()).thenReturn(setOps);

        User u = User.builder()
                .username("alice")
                .password("dummy")
                .roles(Set.of(Role.builder().name("WAITER").build()))
                .build();
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(u));

        when(jwtUtil.generateToken("alice")).thenReturn("token123");
        when(jwtUtil.getExpirationMs()).thenReturn(60_000L);
    }

    @Test
    void login_withValidCredentials_returns200AndJson() throws Exception {
        Authentication success = new UsernamePasswordAuthenticationToken(
                "alice", null, Set.of(new SimpleGrantedAuthority("ROLE_WAITER"))
        );
        when(authManager.authenticate(any())).thenReturn(success);

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"pass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.user.username").value("alice"))
                .andExpect(jsonPath("$.user.role").value("waiter"));

        verify(setOps).add("jwt:whitelist", "token123");
        verify(redis).expire("jwt:whitelist", Duration.ofMillis(60_000L));
    }

    @Test
    void login_withBadCredentials_returns401() throws Exception {
        // stub authentication to throw
        when(authManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad"));

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());

        // ensure no Redis operations on failure
        verifyNoInteractions(setOps, redis);
    }

    @Test
    void logout_withBearerHeader_returns204AndBlacklist() throws Exception {
        mvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer token123"))
                .andExpect(status().isNoContent());

        verify(setOps).remove("jwt:whitelist", "token123");
        verify(setOps).add("jwt:blacklist", "token123");
        verify(redis).expire("jwt:blacklist", Duration.ofMillis(60_000L));
    }
}
