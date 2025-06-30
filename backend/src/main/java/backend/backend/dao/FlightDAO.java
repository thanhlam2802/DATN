package backend.backend.dao;

import backend.backend.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightDAO extends JpaRepository<Flight, Long> {
    // Có thể bổ sung các phương thức custom nếu cần
} 