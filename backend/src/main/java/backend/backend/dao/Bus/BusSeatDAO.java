package backend.backend.dao.Bus;

import backend.backend.entity.BusSeat;
import backend.backend.entity.enumBus.BusSeatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BusSeatDAO extends JpaRepository<BusSeat, Integer> {

    // === BASIC QUERIES ===

    /**
     * Tìm tất cả ghế của một chuyến xe
     */
    @Query("SELECT bs FROM BusSeat bs LEFT JOIN FETCH bs.busSlot WHERE bs.busSlot.id = :busSlotId")
    List<BusSeat> findByBusSlotId(@Param("busSlotId") Integer busSlotId);

    /**
     * Tìm ghế theo ID với thông tin chi tiết
     */
    @Query("SELECT bs FROM BusSeat bs LEFT JOIN FETCH bs.busSlot LEFT JOIN FETCH bs.busSlot.bus LEFT JOIN FETCH bs.busSlot.route WHERE bs.id = :id")
    Optional<BusSeat> findByIdWithDetails(@Param("id") Integer id);

    // === AVAILABILITY QUERIES ===

    /**
     * Tìm tất cả ghế trống của một chuyến xe
     */
    @Query("SELECT bs FROM BusSeat bs WHERE bs.busSlot.id = :busSlotId AND bs.isBooked = false")
    List<BusSeat> findAvailableSeatsByBusSlotId(@Param("busSlotId") Integer busSlotId);

    /**
     * Tìm tất cả ghế đã đặt của một chuyến xe
     */
    @Query("SELECT bs FROM BusSeat bs WHERE bs.busSlot.id = :busSlotId AND bs.isBooked = true")
    List<BusSeat> findBookedSeatsByBusSlotId(@Param("busSlotId") Integer busSlotId);

    /**
     * Đếm số ghế trống của một chuyến xe
     */
    @Query("SELECT COUNT(bs) FROM BusSeat bs WHERE bs.busSlot.id = :busSlotId AND bs.isBooked = false")
    Long countAvailableSeatsByBusSlotId(@Param("busSlotId") Integer busSlotId);

    /**
     * Đếm số ghế đã đặt của một chuyến xe
     */
    @Query("SELECT COUNT(bs) FROM BusSeat bs WHERE bs.busSlot.id = :busSlotId AND bs.isBooked = true")
    Long countBookedSeatsByBusSlotId(@Param("busSlotId") Integer busSlotId);

    // === SEAT NUMBER QUERIES ===

    /**
     * Tìm ghế theo số ghế và chuyến xe
     */
    @Query("SELECT bs FROM BusSeat bs WHERE bs.busSlot.id = :busSlotId AND bs.seatNumber = :seatNumber")
    Optional<BusSeat> findByBusSlotIdAndSeatNumber(@Param("busSlotId") Integer busSlotId, @Param("seatNumber") String seatNumber);

    /**
     * Kiểm tra ghế có tồn tại không
     */
    @Query("SELECT COUNT(bs) > 0 FROM BusSeat bs WHERE bs.busSlot.id = :busSlotId AND bs.seatNumber = :seatNumber")
    Boolean existsByBusSlotIdAndSeatNumber(@Param("busSlotId") Integer busSlotId, @Param("seatNumber") String seatNumber);

    // === PRICE QUERIES ===

    /**
     * Tìm ghế theo khoảng giá
     */
    @Query("SELECT bs FROM BusSeat bs WHERE bs.busSlot.id = :busSlotId AND bs.price BETWEEN :minPrice AND :maxPrice")
    List<BusSeat> findByBusSlotIdAndPriceRange(@Param("busSlotId") Integer busSlotId,
                                               @Param("minPrice") BigDecimal minPrice,
                                               @Param("maxPrice") BigDecimal maxPrice);

    // === SEAT TYPE QUERIES ===

    /**
     * Tìm ghế theo loại ghế
     */
    @Query("SELECT bs FROM BusSeat bs WHERE bs.busSlot.id = :busSlotId AND bs.seatType = :seatType")
    List<BusSeat> findByBusSlotIdAndSeatType(@Param("busSlotId") Integer busSlotId, @Param("seatType") BusSeatType seatType);

    /**
     * Tìm ghế trống theo loại ghế
     */
    @Query("SELECT bs FROM BusSeat bs WHERE bs.busSlot.id = :busSlotId AND bs.seatType = :seatType AND bs.isBooked = false")
    List<BusSeat> findAvailableSeatsByBusSlotIdAndSeatType(@Param("busSlotId") Integer busSlotId, @Param("seatType") BusSeatType seatType);

    // === BULK OPERATIONS ===

    /**
     * Tìm nhiều ghế theo danh sách ID
     */
    @Query("SELECT bs FROM BusSeat bs LEFT JOIN FETCH bs.busSlot WHERE bs.id IN :seatIds")
    List<BusSeat> findByIds(@Param("seatIds") List<Integer> seatIds);

    /**
     * Cập nhật trạng thái đặt ghế cho nhiều ghế
     */
    @Query("UPDATE BusSeat bs SET bs.isBooked = :isBooked WHERE bs.id IN :seatIds")
    void updateBookingStatusByIds(@Param("seatIds") List<Integer> seatIds, @Param("isBooked") Boolean isBooked);

    // === STATISTICS QUERIES ===

    /**
     * Thống kê ghế theo trạng thái
     */
    @Query("SELECT bs.seatType, COUNT(bs) FROM BusSeat bs WHERE bs.busSlot.id = :busSlotId GROUP BY bs.seatType")
    List<Object[]> getSeatStatisticsByBusSlotId(@Param("busSlotId") Integer busSlotId);

    /**
     * Thống kê ghế theo trạng thái đặt
     */
    @Query("SELECT bs.isBooked, COUNT(bs) FROM BusSeat bs WHERE bs.busSlot.id = :busSlotId GROUP BY bs.isBooked")
    List<Object[]> getBookingStatisticsByBusSlotId(@Param("busSlotId") Integer busSlotId);
}
