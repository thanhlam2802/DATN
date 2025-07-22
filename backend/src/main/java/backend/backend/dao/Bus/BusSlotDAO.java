package backend.backend.dao.Bus;

import backend.backend.entity.Bus;
import backend.backend.entity.enumBus.BusSlotStatus;
import backend.backend.entity.BusSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BusSlotDAO extends JpaRepository<BusSlot, Integer> {
    // Phương thức tìm BusSlot theo ID với JOIN FETCH các mối quan hệ cần thiết
    @Query("SELECT bs FROM BusSlot bs LEFT JOIN FETCH bs.bus LEFT JOIN FETCH bs.route WHERE bs.id = :id")
    Optional<BusSlot> findByIdWithDetails(@Param("id") Integer id);

    // Phương thức tìm tất cả BusSlot với JOIN FETCH các mối quan hệ cần thiết
    @Query("SELECT bs FROM BusSlot bs LEFT JOIN FETCH bs.bus LEFT JOIN FETCH bs.route")
    List<BusSlot> findAllWithDetails();

    // Tìm kiếm BusSlot theo Bus ID với JOIN FETCH
    @Query("SELECT bs FROM BusSlot bs LEFT JOIN FETCH bs.bus LEFT JOIN FETCH bs.route WHERE bs.bus.id = :busId")
    List<BusSlot> findByBusIdWithDetails(@Param("busId") Integer busId);

    // Tìm kiếm BusSlot theo Route ID với JOIN FETCH
    @Query("SELECT bs FROM BusSlot bs LEFT JOIN FETCH bs.bus LEFT JOIN FETCH bs.route WHERE bs.route.id = :routeId")
    List<BusSlot> findByRouteIdWithDetails(@Param("routeId") Integer routeId);

    // Tìm kiếm BusSlot theo Status với JOIN FETCH
    @Query("SELECT bs FROM BusSlot bs LEFT JOIN FETCH bs.bus LEFT JOIN FETCH bs.route WHERE bs.status = :status")
    List<BusSlot> findByStatusWithDetails(@Param("status") BusSlotStatus status); // Sử dụng enum BusSlotStatus

    // Tìm kiếm BusSlot nâng cao (searchBusSlots)
    @Query("SELECT bs FROM BusSlot bs " +
            "LEFT JOIN FETCH bs.bus b " +
            "LEFT JOIN FETCH bs.route r " +
            "WHERE r.origin = :departureLocationId AND r.destination = :arrivalLocationId " +
            "AND bs.slotDate = :slotDate " +
            "AND (:busCategoryId IS NULL OR b.category.id = :busCategoryId) " +
            "AND (:minPrice IS NULL OR bs.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR bs.price <= :maxPrice) " +
            "AND (:minAvailableSeats IS NULL OR bs.availableSeats >= :minAvailableSeats) " +
            "AND (:status IS NULL OR bs.status = :status)")
    List<BusSlot> searchBusSlotsWithDetails(
            @Param("departureLocationId") String departureLocationId,
            @Param("arrivalLocationId") String arrivalLocationId,
            @Param("slotDate") LocalDate slotDate,
            @Param("busCategoryId") Integer busCategoryId,
            @Param("minPrice") java.math.BigDecimal minPrice,
            @Param("maxPrice") java.math.BigDecimal maxPrice,
            @Param("minAvailableSeats") Integer minAvailableSeats,
            @Param("status") BusSlotStatus status
    );

}
