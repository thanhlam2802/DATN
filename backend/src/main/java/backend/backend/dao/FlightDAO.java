package backend.backend.dao;

import backend.backend.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface FlightDAO extends JpaRepository<Flight, Integer> {
    @Query("SELECT DISTINCT f FROM Flight f LEFT JOIN FETCH f.flightSlots")
    List<Flight> findAllWithSlots();

    @Query("SELECT COUNT(fs) FROM FlightSlot fs WHERE fs.flight.id = :flightId  AND fs.id NOT IN ( SELECT fb.flightSlot.id FROM FlightBooking fb)")
    Integer countByBookingId(@Param("flightId")Integer flightId);
}