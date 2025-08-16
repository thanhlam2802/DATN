package backend.backend.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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


}
