package backend.backend.dao;

import backend.backend.entity.FlightSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface FlightSlotDAO extends JpaRepository<FlightSlot, Integer> {
    // Có thể bổ sung các phương thức custom nếu cần
    @Query("SELECT COUNT(fs) FROM FlightSlot fs WHERE fs.flight.id = :flightId AND fs.id NOT IN (SELECT fb.flightSlot.id FROM FlightBooking fb)")
    int countAvailableSlotsByFlightId(@Param("flightId") Integer flightId);

    @Query("SELECT COUNT(fs) FROM FlightSlot fs WHERE fs.flight.id = :flightId AND fs.isBusiness = false AND fs.id NOT IN (SELECT fb.flightSlot.id FROM FlightBooking fb)")
    int countAvailableEconomySlotsByFlightId(@Param("flightId") Integer flightId);

    @Query("SELECT COUNT(fs) FROM FlightSlot fs WHERE fs.flight.id = :flightId AND fs.isBusiness = true AND fs.id NOT IN (SELECT fb.flightSlot.id FROM FlightBooking fb)")
    int countAvailableBusinessSlotsByFlightId(@Param("flightId") Integer flightId);

    @Query("SELECT COUNT(fs) FROM FlightSlot fs WHERE fs.flight.id = :flightId AND fs.isBusiness = false AND fs.isWindow = true AND fs.id NOT IN (SELECT fb.flightSlot.id FROM FlightBooking fb)")
    int countAvailableEconomyWindowSlotsByFlightId(@Param("flightId") Integer flightId);

    @Query("SELECT COUNT(fs) FROM FlightSlot fs WHERE fs.flight.id = :flightId AND fs.isBusiness = false AND fs.isAisle = true AND fs.id NOT IN (SELECT fb.flightSlot.id FROM FlightBooking fb)")
    int countAvailableEconomyAisleSlotsByFlightId(@Param("flightId") Integer flightId);

    @Query("SELECT COUNT(fs) FROM FlightSlot fs WHERE fs.flight.id = :flightId AND fs.isBusiness = true AND fs.isWindow = true AND fs.id NOT IN (SELECT fb.flightSlot.id FROM FlightBooking fb)")
    int countAvailableBusinessWindowSlotsByFlightId(@Param("flightId") Integer flightId);

    @Query("SELECT COUNT(fs) FROM FlightSlot fs WHERE fs.flight.id = :flightId AND fs.isBusiness = true AND fs.isAisle = true AND fs.id NOT IN (SELECT fb.flightSlot.id FROM FlightBooking fb)")
    int countAvailableBusinessAisleSlotsByFlightId(@Param("flightId") Integer flightId);

    @Query(value =
            "SELECT TOP 1 * " +
                    "FROM flight_slots fs " +
                    "WHERE fs.flight_id    = :flightId " +
                    "AND (:isAisle    IS NULL OR fs.isAisle    = :isAisle) " +
                    "AND (:isWindow   IS NULL OR fs.isWindow   = :isWindow) " +
                    "AND (:isBusiness IS NULL OR fs.is_business = :isBusiness) " +
                    "AND fs.id NOT IN (SELECT fb.flight_slot_id FROM flight_bookings fb) " +
                    "ORDER BY fs.id ASC",
            nativeQuery = true
    )
    Optional<FlightSlot> findFirstAvailableSlotByCriteria(
            @Param("flightId")   Integer flightId,
            @Param("isAisle")    Boolean isAisle,
            @Param("isWindow")   Boolean isWindow,
            @Param("isBusiness") Boolean isBusiness
    );
} 