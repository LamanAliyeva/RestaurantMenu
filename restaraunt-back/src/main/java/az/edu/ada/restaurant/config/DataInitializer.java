package az.edu.ada.restaurant.config;

import az.edu.ada.restaurant.model.*;
import az.edu.ada.restaurant.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner init(RoleRepository rr, UserRepository ur, PasswordEncoder pe) {
        return args -> {

            for (String roleName : new String[] {"ADMIN", "WAITER", "CHEF", "MANAGER"}) {
                rr.findByName(roleName)
                        .orElseGet(() -> rr.save(Role.builder()
                                .name(roleName)
                                .build()));
            }

            // 2) Grab back the Role entities
            Role adminRole   = rr.findByName("ADMIN").get();
            Role waiterRole  = rr.findByName("WAITER").get();
            Role chefRole    = rr.findByName("CHEF").get();
            Role managerRole = rr.findByName("MANAGER").get();

            // 3) Ensure each user exists
            if (!ur.existsByUsername("admin")) {
                ur.save(new User(null, "admin", pe.encode("pass"), Set.of(adminRole)));
            }
            if (!ur.existsByUsername("w")) {
                ur.save(new User(null, "w", pe.encode("pass"), Set.of(waiterRole)));
            }
            if (!ur.existsByUsername("chef")) {
                ur.save(new User(null, "chef", pe.encode("pass"), Set.of(chefRole)));
            }
            if (!ur.existsByUsername("mgr")) {
                ur.save(new User(null, "mgr", pe.encode("pass"), Set.of(managerRole)));
            }

            if (rr.count() == 0) {
                rr.save(Role.builder()
                        .name("ADMIN")
                        .build());
                rr.save(Role.builder()
                        .name("MANAGER")
                        .build());
                rr.save(Role.builder()
                        .name("WAITER")
                        .build());
                rr.save(Role.builder()
                        .name("CHEF")
                        .build());
            }
            if (ur.count() == 0) {
                Role admin = rr.findByName("ADMIN").get();
                Role manager = rr.findByName("MANAGER").get();
                Role waiter = rr.findByName("WAITER").get();
                Role chef   = rr.findByName("CHEF").get();

                ur.save(new User(null,"admin",pe.encode("pass"), Set.of(admin)));
                ur.save(new User(null,"mgr",pe.encode("pass"), Set.of(manager)));
                ur.save(new User(null,"w",pe.encode("pass"),Set.of(waiter)));
                ur.save(new User(null,"chef",pe.encode("pass"),Set.of(chef)));
            }


        };
    }
}
