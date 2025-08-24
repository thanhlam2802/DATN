package backend.backend.dao.Bus;

import backend.backend.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationDAO extends JpaRepository<Location, Integer> {
    Optional<Location> findByName(String name); // THÊM: Phương thức tìm kiếm theo tên

}
