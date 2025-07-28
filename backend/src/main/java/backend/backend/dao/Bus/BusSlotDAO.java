package backend.backend.dao.Bus;

import backend.backend.entity.Bus;
import backend.backend.entity.Route;
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
            "WHERE r.originLocation.name = :departureLocationName " +
            "AND r.destinationLocation.name = :arrivalLocationName " +
            "AND bs.slotDate = :slotDate " +
            "AND (:busCategoryId IS NULL OR b.category.id = :busCategoryId) " +
            "AND (:minPrice IS NULL OR bs.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR bs.price <= :maxPrice) " +
            "AND (:minAvailableSeats IS NULL OR bs.availableSeats >= :minAvailableSeats) " +
            "AND (:status IS NULL OR bs.status = :status)")
    List<BusSlot> searchBusSlotsWithDetails(
            @Param("departureLocationName") String departureLocationName,
            @Param("arrivalLocationName") String arrivalLocationName,
            @Param("slotDate") LocalDate slotDate,
            @Param("busCategoryId") Integer busCategoryId,
            @Param("minPrice") java.math.BigDecimal minPrice,
            @Param("maxPrice") java.math.BigDecimal maxPrice,
            @Param("minAvailableSeats") Integer minAvailableSeats,
            @Param("status") BusSlotStatus status
    );

    // SỬA ĐỔI QUAN TRỌNG TẠI ĐÂY: Sử dụng nativeQuery và CONVERT
    @Query(value = "SELECT * FROM bus_slots bs " + // Sử dụng tên bảng vật lý "bus_slots"
            "WHERE bs.bus_id = :busId " + // Truyền ID thay vì đối tượng Bus
            "AND bs.route_id = :routeId " + // Truyền ID thay vì đối tượng Route
            "AND bs.slot_date = :slotDate " +
            "AND CONVERT(VARCHAR(8), bs.departure_time, 108) = CONVERT(VARCHAR(8), :departureTime, 108)", // So sánh chuỗi thời gian
            nativeQuery = true) // Đặt nativeQuery thành true
    Optional<BusSlot> findByBusAndRouteAndSlotDateAndDepartureTime(
            @Param("busId") Integer busId,
            @Param("routeId") Integer routeId,
            @Param("slotDate") LocalDate slotDate,
            @Param("departureTime") LocalTime departureTime);


    void deleteByRouteId(Integer routeId);
    void deleteByBusId(Integer busId);
}