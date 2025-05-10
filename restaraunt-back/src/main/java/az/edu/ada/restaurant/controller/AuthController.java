package az.edu.ada.restaurant.controller;

import az.edu.ada.restaurant.model.User;
import az.edu.ada.restaurant.repository.UserRepository;
import az.edu.ada.restaurant.security.JwtUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationConfiguration authConfig;
    private final RedisTemplate<String,Object> redis;
    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AuthController(
            AuthenticationConfiguration authConfig,
            RedisTemplate<String,Object> redis,
            UserRepository users,
            PasswordEncoder encoder
    ) {
        this.authConfig = authConfig;
        this.redis      = redis;
        this.users      = users;
        this.encoder    = encoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) throws Exception {
        String username = body.get("username");
        String password = body.get("password");

        // Lookup the AuthenticationManager on demand
        AuthenticationManager authManager = authConfig.getAuthenticationManager();
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        User u = users.findByUsername(username).get();


        // return token + user info
        return ResponseEntity.ok(Map.of(
                "user", Map.of(
                        "username", u.getUsername(),
                        "role", u.getRoles().iterator().next().getName().toLowerCase()
                )
        ));

    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String header) {
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            // remove from whitelist
            redis.opsForSet().remove("jwt:whitelist", token);
            // add to blacklist so it cannot be used if someone re-issues
            redis.opsForSet().add("jwt:blacklist", token);
            redis.expire("jwt:blacklist", Duration.ofMillis(null));
        }
        return ResponseEntity.noContent().build();
    }

}
