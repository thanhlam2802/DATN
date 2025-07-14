package backend.backend.repository;

import backend.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name); // <- Phuong thuoc nay Nam add de luu thong tin nguoi dung neu tao xe bus
    Optional<User> findById(Integer userId); // <- Nam add
    Optional<User> findByPhone(String phone);
}
