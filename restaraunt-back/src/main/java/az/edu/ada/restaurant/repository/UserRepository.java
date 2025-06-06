package az.edu.ada.restaurant.repository;

import az.edu.ada.restaurant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByRolesName(String roleName);
    boolean existsByUsername(String username);
}
