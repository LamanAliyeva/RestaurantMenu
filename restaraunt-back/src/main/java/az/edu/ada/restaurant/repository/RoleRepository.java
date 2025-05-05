package az.edu.ada.restaurant.repository;

import az.edu.ada.restaurant.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    // Allows finding a role by its name
    Optional<Role> findByName(String name);
}
