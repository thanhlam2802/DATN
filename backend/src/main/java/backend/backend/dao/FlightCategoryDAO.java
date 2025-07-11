package backend.backend.dao;

import backend.backend.entity.FlightCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightCategoryDAO extends JpaRepository<FlightCategory, Integer> {
    
    Optional<FlightCategory> findByName(String name);
    
    List<FlightCategory> findAllByOrderByName();
    
    boolean existsByName(String name);
} 