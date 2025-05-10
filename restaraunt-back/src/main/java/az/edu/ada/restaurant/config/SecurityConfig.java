package az.edu.ada.restaurant.config;

import az.edu.ada.restaurant.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authProvider
    ) throws Exception {
        http
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/health", "/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/orders").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/orders/*/status").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/orders/track/*").permitAll()  // allow tracking

                        // Chef/Waiter/Manager/Admin can view and update orders
                        .requestMatchers(HttpMethod.GET, "/api/orders").hasAnyRole("CHEF","WAITER","MANAGER","ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/orders/*/status").hasAnyRole("CHEF","WAITER","MANAGER","ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/orders/*").hasRole("ADMIN")

                        // Admin: full access to any other endpoints
                        .anyRequest().hasRole("ADMIN")
                )
                .authenticationProvider(authProvider)
                .addFilterBefore(null, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

