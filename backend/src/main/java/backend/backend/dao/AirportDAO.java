package backend.backend.dao;

import backend.backend.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirportDAO extends JpaRepository<Airport, Integer> {
    // Có thể bổ sung các phương thức custom nếu cần
} 