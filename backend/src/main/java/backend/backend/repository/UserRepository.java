package backend.backend.repository;

import backend.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name); // <- Phuong thuoc nay Nam add de luu thong tin nguoi dung neu tao xe bus
    Optional<User> findById(Integer userId); // <- Nam add
    Optional<User> findByPhone(String phone);

    @Query("SELECT DISTINCT u FROM User u " +
    	       "LEFT JOIN FETCH u.userRoles ur " +
    	       "LEFT JOIN FETCH ur.role " +
    	       "WHERE u.email = :email")
    	Optional<User> findByEmailWithRoles(@Param("email") String email);

    // Thêm các method mới cho BusManagement
    @Query("SELECT u FROM User u WHERE u.ownedBuses IS NOT EMPTY")
    List<User> findByBusesIsNotEmpty();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.ownedBuses IS NOT EMPTY")
    long countByBusesIsNotEmpty();
    
    @Query("SELECT u FROM User u WHERE u.name LIKE %:name% OR u.email LIKE %:email%")
    List<User> findByNameOrEmailContaining(@Param("name") String name, @Param("email") String email);
}