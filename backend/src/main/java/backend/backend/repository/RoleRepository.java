package backend.backend.repository;

import backend.backend.dto.auth.UserRoleEnum;
import backend.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(UserRoleEnum userRoleEnum);
}
