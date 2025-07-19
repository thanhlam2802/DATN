package backend.backend.dao;

import backend.backend.entity.FlightImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightImageDAO extends JpaRepository<FlightImage, Integer> {
    List<FlightImage> findByFlightId(Integer flightId);
    
    Optional<FlightImage> findByFlightIdAndImageId(Integer flightId, Integer imageId);
} 