package backend.backend.dao;

import backend.backend.entity.FlightBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FlightBookingDAO extends JpaRepository<FlightBooking, Long> {
    List<FlightBooking> findByUserId(Long userId);
    List<FlightBooking> findByFlightSlotId(Long slotId);
    FlightBooking findById(long id);
} 