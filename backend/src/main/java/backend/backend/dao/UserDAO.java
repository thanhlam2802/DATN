package backend.backend.dao;

import backend.backend.dto.auth.UserRoleEnum;
import backend.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserDAO extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);


    @Query("SELECT u FROM User u JOIN u.userRoles r join r.role  rl WHERE rl.name = :roleName")
    List<User> findByRole(@Param("roleName") UserRoleEnum role);
}
