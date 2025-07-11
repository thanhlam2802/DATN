package backend.backend.dao;

import backend.backend.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface FlightDAO extends JpaRepository<Flight, Integer> {
    @Query("SELECT DISTINCT f FROM Flight f LEFT JOIN FETCH f.flightSlots")
    List<Flight> findAllWithSlots();
} 