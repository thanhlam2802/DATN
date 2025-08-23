package backend.backend.dao;

import backend.backend.entity.Flight;
import backend.backend.entity.FlightBooking;
import backend.backend.entity.FlightSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlightDAO extends JpaRepository<Flight, Integer> {
    @Query("SELECT DISTINCT f FROM Flight f LEFT JOIN FETCH f.flightSlots")
    List<Flight> findAllWithSlots();

    @Query("SELECT COUNT(fs) FROM FlightSlot fs WHERE fs.flight.id = :flightId  AND fs.id NOT IN ( SELECT fb.flightSlot.id FROM FlightBooking fb)")
    Integer countByBookingId(@Param("flightId")Integer flightId);
    
    // Thống kê theo tháng
    @Query("SELECT COUNT(f) FROM Flight f WHERE YEAR(f.departureTime) = :year AND MONTH(f.departureTime) = :month")
    Long countFlightsByMonth(@Param("year") Integer year, @Param("month") Integer month);
    
    @Query("SELECT COUNT(f) FROM Flight f WHERE YEAR(f.departureTime) = :year AND MONTH(f.departureTime) = :month")
    Long countFlightsByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);

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
AND f.departure_time >= CURRENT_TIMESTAMP
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

    @Query("SELECT f FROM Flight f WHERE f.departureTime >= CURRENT_TIMESTAMP")
    List<Flight> findAllUpcomingFlights();
    @Query("SELECT f FROM Flight f WHERE f.owner.id = :ownerId")
    List<Flight>  findByOwnerId(@Param("ownerId") Integer ownerId);

    // ===== Super Admin Dashboard =====
    @Query("SELECT COUNT(f) FROM Flight f WHERE f.owner.id = :ownerId")
    int countByOwnerId(@Param("ownerId") Integer ownerId);
    
    @Query("SELECT f FROM Flight f WHERE f.owner.id = :ownerId ORDER BY f.createdAt DESC")
    List<Flight> findRecentFlightsByOwnerId(@Param("ownerId") Integer ownerId, @Param("limit") int limit);
    
    @Query(value = """
      SELECT AVG(CASE WHEN fs.total_slots > 0  THEN (COALESCE(fb.booked_seats,0) * 100.0 / fs.total_slots) ELSE 0 END)
        FROM ( SELECT f.id, COUNT(fs.id) AS total_slots FROM flights f LEFT JOIN flight_slots fs ON fs.flight_id = f.id WHERE f.owner_id = :ownerId  GROUP BY f.id) fs
      LEFT JOIN ( SELECT f.id, COUNT(b.id) AS booked_seats
        FROM flights f LEFT JOIN flight_slots fs ON fs.flight_id = f.id LEFT JOIN flight_bookings b ON b.flight_slot_id = fs.id
        WHERE f.owner_id = :ownerId GROUP BY f.id ) fb ON fs.id = fb.id
    """, nativeQuery = true)
    Double getAverageOccupancyRateByOwnerId(@Param("ownerId") Integer ownerId);
    @Query(value = """
        SELECT AVG(CASE WHEN totals.total_slots > 0 THEN (COALESCE(sold.sold_seats,0) * 100.0 / totals.total_slots) ELSE 0 END)
        FROM (
            SELECT f.id AS flight_id, COUNT(fs.id) AS total_slots
            FROM flights f
            LEFT JOIN flight_slots fs ON fs.flight_id = f.id
            WHERE EXTRACT(YEAR FROM f.created_at) = :year AND EXTRACT(MONTH FROM f.created_at) = :month
            GROUP BY f.id
        ) totals
        LEFT JOIN (
            SELECT f.id AS flight_id, COUNT(b.id) AS sold_seats
            FROM flights f
            LEFT JOIN flight_slots fs ON fs.flight_id = f.id
            LEFT JOIN flight_bookings b ON b.flight_slot_id = fs.id
            WHERE EXTRACT(YEAR FROM f.created_at) = :year AND EXTRACT(MONTH FROM f.created_at) = :month
            GROUP BY f.id
        ) sold ON totals.flight_id = sold.flight_id
      """, nativeQuery = true)
      Double getAverageOccupancyRateByMonth(@Param("year") Integer year, @Param("month") Integer month);
      
    @Query("SELECT f FROM Flight f WHERE f.owner.id = :ownerId")
    Page<Flight> findByOwnerIdWithPagination(@Param("ownerId") Integer ownerId, Pageable pageable);
}