package backend.backend.dao;

import backend.backend.entity.Flight;
import backend.backend.entity.FlightSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlightDAO extends JpaRepository<Flight, Integer> {
    @Query("SELECT DISTINCT f FROM Flight f LEFT JOIN FETCH f.flightSlots")
    List<Flight> findAllWithSlots();

    @Query("SELECT COUNT(fs) FROM FlightSlot fs WHERE fs.flight.id = :flightId  AND fs.id NOT IN ( SELECT fb.flightSlot.id FROM FlightBooking fb)")
    Integer countByBookingId(@Param("flightId")Integer flightId);

    @Query("SELECT fs FROM FlightSlot fs WHERE fs.flight.id = :flightId  AND fs.id  IN ( SELECT fb.flightSlot.id FROM FlightBooking fb)")
    List<FlightSlot> findslotByBooked(@Param("flightId")Integer flightId);

    @Query("SELECT fs FROM FlightSlot fs " +
            "WHERE fs.flight.id = :flightId " +
            "AND fs.isAisle = :isAisle " +
            "AND fs.isWindow = :isWindow " +
            "AND fs.isBusiness = :isBusiness " +
            "AND fs.id NOT IN (SELECT fb.flightSlot.id FROM FlightBooking fb)")
    List<FlightSlot> findUnbookedSlotByConditions(
            @Param("flightId") Integer flightId,
            @Param("isAisle") boolean isAisle,
            @Param("isWindow") boolean isWindow,
            @Param("isBusiness") boolean isBusiness
    );
    @Query(value = """
SELECT DISTINCT f.*
FROM flights f
JOIN flight_slots fs ON f.id = fs.flight_id
WHERE (:departureId IS NULL OR f.departure_airport_id = :departureId)
AND (:arrivalId IS NULL OR f.arrival_airport_id = :arrivalId)
AND (:airlineId IS NULL OR f.airline_id = :airlineId)
AND (:categoryId IS NULL OR f.category_id = :categoryId)
AND (CAST(f.departure_time AS DATE) >= :departureDate OR :departureDate IS NULL)
AND (
     :startHour IS NULL OR :endHour IS NULL
     OR DATEPART(HOUR, f.departure_time) BETWEEN :startHour AND :endHour
)
AND (:minPrice IS NULL OR fs.price >= :minPrice)
AND (:maxPrice IS NULL OR fs.price <= :maxPrice)
""", nativeQuery = true)
    List<Flight> searchFlights(
            @Param("departureId") Integer departureId,
            @Param("arrivalId") Integer arrivalId,
            @Param("airlineId") Integer airlineId,
            @Param("categoryId") Integer categoryId,
            @Param("departureDate") LocalDate departureDate,
            @Param("startHour") Integer startHour,
            @Param("endHour") Integer endHour,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    @Query("""
SELECT f FROM Flight f
WHERE f.departureTime >= CURRENT_TIMESTAMP
""")
    List<Flight> findAllUpcomingFlights();
}