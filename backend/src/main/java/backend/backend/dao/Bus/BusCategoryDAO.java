package backend.backend.dao.Bus;

import backend.backend.entity.BusCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusCategoryDAO extends JpaRepository<BusCategory, Integer> {

    Optional<BusCategory> findByName(String name);
    boolean existsByName(String name);
}
