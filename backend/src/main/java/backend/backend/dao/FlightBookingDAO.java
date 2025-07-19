package backend.backend.dao;

import backend.backend.entity.FlightBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightBookingDAO extends JpaRepository<FlightBooking, Integer> {
	
	
    List<FlightBooking> findByCustomerId(Integer customerId);
    List<FlightBooking> findByFlightSlotId(Integer slotId);
    Optional<FlightBooking> findById(Integer id);
    @Query("SELECT COUNT(fb) FROM FlightBooking fb WHERE fb.flightSlot.flight.id = :flightId")
    int countSoldSeatsByFlightId(@Param("flightId") Integer flightId);
    @Query("SELECT COALESCE(SUM(fb.flightSlot.price), 0) "
            + "FROM FlightBooking fb "
            + "WHERE fb.flightSlot.flight.id = :flightId")
    Double sumRevenueByFlightId(@Param("flightId") Integer flightId);
	List<FlightBooking> findByTicketDetailId(Integer id);
} 