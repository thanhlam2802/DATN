package backend.backend.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDate;

import backend.backend.entity.enumBus.BusBookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import backend.backend.entity.BusBooking;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface BusBookingDAO extends JpaRepository<BusBooking, Integer> {
	// Existing method
	List<BusBooking> findByOrderId(Integer orderId);

	// ✅ NEW: Required methods for BusBookingServiceImpl
	List<BusBooking> findByCustomerId(Integer customerId);

	@Query("SELECT bb FROM BusBooking bb WHERE bb.customer.id = :customerId ORDER BY bb.bookingDate DESC")
	List<BusBooking> findByCustomerIdOrderByBookingDateDesc(@Param("customerId") Integer customerId);

	@Query("SELECT b FROM BusBooking b LEFT JOIN FETCH b.order LEFT JOIN FETCH b.selectedSeats WHERE b.status = :status AND b.expiresAt < :now")
	List<BusBooking> findExpiredReservations(@Param("status") BusBookingStatus status, @Param("now") LocalDateTime now);

	@Query("SELECT b FROM BusBooking b LEFT JOIN FETCH b.selectedSeats LEFT JOIN FETCH b.busSlot bs LEFT JOIN FETCH bs.bus LEFT JOIN FETCH bs.route LEFT JOIN FETCH bs.bus.category WHERE b.order.id = :orderId")
	List<BusBooking> findByOrderIdWithSeats(@Param("orderId") Integer orderId);

	// ✅ THÊM: Method để tìm booking với ghế (needed by BusBookingServiceImpl)
	@Query("SELECT b FROM BusBooking b LEFT JOIN FETCH b.selectedSeats WHERE b.id = :bookingId")
	Optional<BusBooking> findByIdWithSeats(@Param("bookingId") Integer bookingId);

	// ✅ THÊM: Methods để debug và admin operations
	@Query("SELECT bb FROM BusBooking bb WHERE bb.status = :status")
	List<BusBooking> findByStatus(@Param("status") BusBookingStatus status);

	// 📊 STATISTICS METHODS
	@Query("SELECT COUNT(bb) FROM BusBooking bb WHERE bb.busSlot.owner.id = :ownerId")
	Long countByOwnerId(@Param("ownerId") Integer ownerId);

	@Query("SELECT COUNT(DISTINCT bb.customer.id) FROM BusBooking bb WHERE bb.busSlot.owner.id = :ownerId")
	Long countDistinctCustomersByOwnerId(@Param("ownerId") Integer ownerId);

	@Query("SELECT SUM(bb.totalPrice) FROM BusBooking bb WHERE bb.busSlot.owner.id = :ownerId")
	BigDecimal getTotalRevenueByOwnerId(@Param("ownerId") Integer ownerId);

	@Query("SELECT COUNT(bb) FROM BusBooking bb WHERE bb.busSlot.owner.id = :ownerId AND bb.bookingDate BETWEEN :startDate AND :endDate")
	Long countByOwnerIdAndDateRange(@Param("ownerId") Integer ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	@Query("SELECT COUNT(DISTINCT bb.customer.id) FROM BusBooking bb WHERE bb.busSlot.owner.id = :ownerId AND bb.bookingDate BETWEEN :startDate AND :endDate")
	Long countDistinctCustomersByOwnerIdAndDateRange(@Param("ownerId") Integer ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	@Query("SELECT SUM(bb.totalPrice) FROM BusBooking bb WHERE bb.busSlot.owner.id = :ownerId AND bb.bookingDate BETWEEN :startDate AND :endDate")
	BigDecimal getRevenueByOwnerIdAndDateRange(@Param("ownerId") Integer ownerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	@Query("SELECT COUNT(bb) FROM BusBooking bb WHERE bb.busSlot.route.id = :routeId")
	Long countByRouteId(@Param("routeId") Integer routeId);

	@Query("SELECT SUM(bb.totalPrice) FROM BusBooking bb WHERE bb.busSlot.route.id = :routeId")
	BigDecimal getTotalRevenueByRouteId(@Param("routeId") Integer routeId);

	@Query("SELECT AVG(bb.totalPrice) FROM BusBooking bb WHERE bb.busSlot.route.id = :routeId")
	Double getAverageOccupancyByRouteId(@Param("routeId") Integer routeId);

	@Query("SELECT AVG(bb.totalPrice) FROM BusBooking bb WHERE bb.busSlot.bus.id = :busId")
	Double getAverageOccupancyByBusId(@Param("busId") Integer busId);

	// 📊 OPTIMIZED: Get average occupancy for all buses of an owner in one query
	@Query("SELECT bb.busSlot.bus.id as busId, AVG(bb.totalPrice) as avgOccupancy " +
		   "FROM BusBooking bb " +
		   "WHERE bb.busSlot.owner.id = :ownerId " +
		   "GROUP BY bb.busSlot.bus.id")
	List<Object[]> getAverageOccupancyByOwnerIdRaw(@Param("ownerId") Integer ownerId);

	@Query("SELECT bb FROM BusBooking bb WHERE bb.customer.id = :customerId AND bb.status = :status")
	List<BusBooking> findByCustomerIdAndStatus(@Param("customerId") Integer customerId, @Param("status") BusBookingStatus status);

	@Query("SELECT COUNT(bb) FROM BusBooking bb WHERE bb.busSlot.id = :busSlotId AND bb.status IN ('RESERVED', 'CONFIRMED', 'PAID')")
	Long countActiveBusBookingsByBusSlotId(@Param("busSlotId") Integer busSlotId);

	@Query("SELECT SUM(bb.numPassengers) FROM BusBooking bb WHERE bb.busSlot.id = :busSlotId AND bb.status IN ('RESERVED', 'CONFIRMED', 'PAID')")
	Long countTotalPassengersByBusSlotId(@Param("busSlotId") Integer busSlotId);

	@Query("SELECT bb FROM BusBooking bb WHERE bb.bookingReference = :bookingReference")
	Optional<BusBooking> findByBookingReference(@Param("bookingReference") String bookingReference);

	@Query("SELECT bb FROM BusBooking bb WHERE bb.bookingDate BETWEEN :startDate AND :endDate")
	List<BusBooking> findByBookingDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	// ✅ NEW: Method để lấy bus booking với đầy đủ thông tin chi tiết
	@Query("SELECT bb FROM BusBooking bb " +
		   "LEFT JOIN FETCH bb.selectedSeats " +
		   "LEFT JOIN FETCH bb.busSlot bs " +
		   "LEFT JOIN FETCH bs.bus " +
		   "LEFT JOIN FETCH bs.route r " +
		   "LEFT JOIN FETCH r.originLocation " +
		   "LEFT JOIN FETCH r.destinationLocation " +
		   "LEFT JOIN FETCH bb.customer " +
		   "WHERE bb.id = :bookingId")
	Optional<BusBooking> findByIdWithFullDetails(@Param("bookingId") Integer bookingId);

	@Query("SELECT bb FROM BusBooking bb WHERE bb.customer.id = :customerId ORDER BY bb.bookingDate DESC LIMIT 10")
	List<BusBooking> findRecentBookingsByCustomerId(@Param("customerId") Integer customerId);

	// 📊 HELPER: Convert raw query result to Map
	default Map<String, Double> getAverageOccupancyByOwnerId(Integer ownerId) {
		List<Object[]> results = getAverageOccupancyByOwnerIdRaw(ownerId);
		Map<String, Double> occupancyRates = new HashMap<>();
		
		for (Object[] result : results) {
			Integer busId = (Integer) result[0];
			Double avgOccupancy = (Double) result[1];
			occupancyRates.put(busId.toString(), avgOccupancy != null ? avgOccupancy : 0.0);
		}
		
		return occupancyRates;
	}

	    // 📊 Thống kê theo ngày trong khoảng thời gian
    @Query("SELECT CAST(bb.bookingDate AS DATE) as bookingDate, " +
           "COUNT(bb) as bookingCount, " +
           "SUM(bb.totalPrice) as totalRevenue, " +
           "COUNT(DISTINCT bb.customer.id) as customerCount " +
           "FROM BusBooking bb " +
           "WHERE bb.busSlot.owner.id = :ownerId " +
           "AND CAST(bb.bookingDate AS DATE) BETWEEN :startDate AND :endDate " +
           "GROUP BY CAST(bb.bookingDate AS DATE) " +
           "ORDER BY CAST(bb.bookingDate AS DATE)")
    List<Object[]> getDailyStatsByDateRange(@Param("ownerId") Integer ownerId, 
                                           @Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate);

	

}
