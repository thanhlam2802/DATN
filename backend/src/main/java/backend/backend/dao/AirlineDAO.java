package backend.backend.dao;

import backend.backend.entity.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirlineDAO extends JpaRepository<Airline, Long> {
    // Có thể bổ sung các phương thức custom nếu cần
} 