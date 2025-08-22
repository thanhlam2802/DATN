package backend.backend.dao.Bus;

import backend.backend.entity.Bus;
import backend.backend.entity.Route;
import backend.backend.entity.enumBus.BusSlotStatus;
import backend.backend.entity.BusSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    // Giữ phương thức này nếu bạn vẫn cần kiểm tra sự tồn tại chính xác,
    // nhưng lưu ý rằng các phương thức mới bên dưới sẽ xử lý kiểm tra trùng lặp thời gian hiệu quả hơn.
    // SỬA ĐỔI QUAN TRỌNG TẠI ĐÂY: Sử dụng nativeQuery và CONVERT
    // Phương thức kiểm tra sự tồn tại chính xác theo Bus, Route, Ngày và Thời gian khởi hành
    @Query(value = "SELECT * FROM bus_slots bs " +
            "WHERE bs.bus_id = :busId " +
            "AND bs.route_id = :routeId " +
            "AND bs.slot_date = :slotDate " +
            "AND CONVERT(VARCHAR(8), bs.departure_time, 108) = :departureTimeStr", // So sánh string với string
            nativeQuery = true)
    Optional<BusSlot> findByBusAndRouteAndSlotDateAndDepartureTime(
            @Param("busId") Integer busId,
            @Param("routeId") Integer routeId,
            @Param("slotDate") LocalDate slotDate,
            @Param("departureTimeStr") String departureTimeStr);


    // PHƯƠNG THỨC MỚI QUAN TRỌNG: Tìm các BusSlot đang hoạt động (SCHEDULED/IN_PROGRESS) bị chồng chéo thời gian
    @Query(value = "SELECT * FROM bus_slots bs " +
            "WHERE bs.bus_id = :busId " +
            "AND bs.slot_date = :slotDate " +
            "AND bs.status IN ('SCHEDULED', 'IN_PROGRESS') " +
            "AND ( (CONVERT(VARCHAR(8), bs.departure_time, 108) <= CONVERT(VARCHAR(8), :newArrivalTime, 108) " +
            "AND CONVERT(VARCHAR(8), bs.arrival_time, 108) >= CONVERT(VARCHAR(8), :newDepartureTime, 108)) )",
            nativeQuery = true)
    List<BusSlot> findOverlappingActiveBusSlots(
            @Param("busId") Integer busId,
            @Param("slotDate") LocalDate slotDate,
            @Param("newDepartureTime") LocalTime newDepartureTime,
            @Param("newArrivalTime") LocalTime newArrivalTime);

    // PHƯƠNG THỨC MỚI QUAN TRỌNG: Tìm các BusSlot đang hoạt động bị chồng chéo thời gian, loại trừ một BusSlot cụ thể (dùng khi cập nhật)
    @Query(value = "SELECT * FROM bus_slots bs " +
            "WHERE bs.bus_id = :busId " +
            "AND bs.slot_date = :slotDate " +
            "AND bs.id != :currentSlotId " + // Loại trừ slot hiện tại
            "AND bs.status IN ('SCHEDULED', 'IN_PROGRESS') " +
            "AND ( (CONVERT(VARCHAR(8), bs.departure_time, 108) <= CONVERT(VARCHAR(8), :newArrivalTime, 108) " +
            "AND CONVERT(VARCHAR(8), bs.arrival_time, 108) >= CONVERT(VARCHAR(8), :newDepartureTime, 108)) )",
            nativeQuery = true)
    List<BusSlot> findOverlappingActiveBusSlotsExcludingCurrent(
            @Param("busId") Integer busId,
            @Param("slotDate") LocalDate slotDate,
            @Param("newDepartureTime") LocalTime newDepartureTime,
            @Param("newArrivalTime") LocalTime newArrivalTime,
            @Param("currentSlotId") Integer currentSlotId);


    // PHƯƠNG THỨC KIỂM TRA TRÙNG THỜI GIAN - Logic chính xác cho việc chồng chéo thời gian
    @Query(value = "SELECT * FROM bus_slots bs " +
            "WHERE bs.bus_id = :busId " +
            "AND bs.slot_date = :slotDate " +
            "AND bs.status IN ('SCHEDULED', 'IN_PROGRESS') " +
            "AND NOT ( " +
            "  CONVERT(VARCHAR(8), :newEnd, 108) <= CONVERT(VARCHAR(8), bs.departure_time, 108) " +
            "  OR CONVERT(VARCHAR(8), :newStart, 108) >= CONVERT(VARCHAR(8), bs.arrival_time, 108) " +
            ")",
            nativeQuery = true)
    List<BusSlot> findConflictingSlots(
            @Param("busId") Integer busId,
            @Param("slotDate") LocalDate slotDate,
            @Param("newStart") LocalTime newStart,
            @Param("newEnd") LocalTime newEnd);

    @Query("SELECT DISTINCT bs FROM BusSlot bs " +
            "LEFT JOIN FETCH bs.bus b " +
            "LEFT JOIN FETCH bs.route r " +
            "LEFT JOIN FETCH r.originLocation ol " +
            "LEFT JOIN FETCH r.destinationLocation dl " +
            "WHERE bs.slotDate = :slotDate " +
            "AND (:busCategoryId IS NULL OR b.category.id = :busCategoryId) " +
            "AND (:minPrice IS NULL OR bs.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR bs.price <= :maxPrice) " +
            "AND (:minAvailableSeats IS NULL OR bs.availableSeats >= :minAvailableSeats) " +
            "AND (:status IS NULL OR bs.status = :status) " +
            "AND ol.provinceCity = :departureProvince " +
            "AND (:departureDistrict IS NULL OR ol.district = :departureDistrict) " +
            "AND dl.provinceCity = :arrivalProvince " +
            "AND (:arrivalDistrict IS NULL OR dl.district = :arrivalDistrict)")
    List<BusSlot> searchBusSlotsDetailed(
            @Param("slotDate") LocalDate slotDate,
            @Param("departureProvince") String departureProvince,
            @Param("departureDistrict") String departureDistrict,
            @Param("arrivalProvince") String arrivalProvince,
            @Param("arrivalDistrict") String arrivalDistrict,
            @Param("busCategoryId") Integer busCategoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minAvailableSeats") Integer minAvailableSeats,
            @Param("status") BusSlotStatus status
    );

    void deleteByRouteId(Integer routeId);
    void deleteByBusId(Integer busId);

    // ✅ THÊM MỚI: Owner-specific queries
    @Query("SELECT bs FROM BusSlot bs LEFT JOIN FETCH bs.bus LEFT JOIN FETCH bs.route WHERE bs.owner.id = :ownerId")
    List<BusSlot> findByOwnerIdWithDetails(@Param("ownerId") Integer ownerId);

    @Query("SELECT bs FROM BusSlot bs LEFT JOIN FETCH bs.bus LEFT JOIN FETCH bs.route " +
           "WHERE bs.owner.id = :ownerId AND bs.status = :status")
    List<BusSlot> findByOwnerIdAndStatusWithDetails(@Param("ownerId") Integer ownerId, @Param("status") BusSlotStatus status);

    @Query("SELECT bs FROM BusSlot bs LEFT JOIN FETCH bs.bus LEFT JOIN FETCH bs.route " +
           "WHERE bs.owner.id = :ownerId AND bs.slotDate = :slotDate")
    List<BusSlot> findByOwnerIdAndSlotDateWithDetails(@Param("ownerId") Integer ownerId, @Param("slotDate") LocalDate slotDate);

    // 📊 STATISTICS METHODS
    @Query("SELECT COUNT(bs) FROM BusSlot bs WHERE bs.owner.id = :ownerId")
    Long countByOwnerId(@Param("ownerId") Integer ownerId);

    @Query("SELECT COUNT(bs) FROM BusSlot bs WHERE bs.owner.id = :ownerId AND bs.slotDate BETWEEN :startDate AND :endDate")
    Long countByOwnerIdAndDateRange(@Param("ownerId") Integer ownerId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 📊 Thống kê chuyến đi theo ngày trong khoảng thời gian
    @Query("SELECT CAST(bs.slotDate AS DATE) as slotDate, " +
           "COUNT(bs) as tripCount " +
           "FROM BusSlot bs " +
           "WHERE bs.owner.id = :ownerId " +
           "AND CAST(bs.slotDate AS DATE) BETWEEN :startDate AND :endDate " +
           "GROUP BY CAST(bs.slotDate AS DATE) " +
           "ORDER BY CAST(bs.slotDate AS DATE)")
    List<Object[]> getDailyTripStatsByDateRange(@Param("ownerId") Integer ownerId, 
                                               @Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(bs) FROM BusSlot bs WHERE bs.route.id = :routeId")
    Long countByRouteId(@Param("routeId") Integer routeId);

}
