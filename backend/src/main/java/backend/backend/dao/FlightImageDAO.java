package backend.backend.dao;

import backend.backend.entity.FlightImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightImageDAO extends JpaRepository<FlightImage, Integer> {
    List<FlightImage> findByFlightId(Integer flightId);
} 