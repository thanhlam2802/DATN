package backend.backend.dao.Bus;

import backend.backend.entity.BusCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusCategoryDAO extends JpaRepository<BusCategory, Integer> {

    Optional<BusCategory> findByName(String name);
    boolean existsByName(String name);
    
    // ❌ REMOVED: BusCategory là global, không cần filter theo ownerId
    // Sử dụng findAll() để lấy tất cả categories
}
