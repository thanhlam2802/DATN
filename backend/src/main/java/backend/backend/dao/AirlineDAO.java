package backend.backend.dao;

import backend.backend.entity.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirlineDAO extends JpaRepository<Airline, Integer> {
    
    Optional<Airline> findByName(String name);
    
    List<Airline> findAllByOrderByName();
    
    boolean existsByName(String name);
} 