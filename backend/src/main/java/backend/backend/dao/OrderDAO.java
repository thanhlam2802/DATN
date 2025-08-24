package backend.backend.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.backend.dto.RevenueDataPointDTO;
import backend.backend.entity.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderDAO extends JpaRepository<Order, Integer> {

	List<Order> findByUserIdOrderByCreatedAtDesc(Integer userId);

	@Query(value = "select  top 10  * from orders order by created_at desc",nativeQuery = true)
	List<Order> findTop10NewOrder();
	List<Order> findAllByStatusAndExpiresAtBefore(String status, LocalDateTime expiryTime);
	Order findFirstByUserIdAndStatus(Integer userId, String status);

	// ✅ FIX: Sử dụng query đơn giản, load collections khi cần
	@Query("SELECT o FROM Order o WHERE o.id = :orderId")
	Optional<Order> findByIdWithDetails(@Param("orderId") Integer orderId);

	// ✅ NEW: Tách riêng từng collection để tránh MultipleBagFetchException
	@Query("SELECT o FROM Order o LEFT JOIN FETCH o.busBookings WHERE o.id = :orderId")
	Optional<Order> findByIdWithBusBookings(@Param("orderId") Integer orderId);

	@Query("SELECT o FROM Order o LEFT JOIN FETCH o.flightBookings WHERE o.id = :orderId")
	Optional<Order> findByIdWithFlightBookings(@Param("orderId") Integer orderId);

	@Query("SELECT o FROM Order o LEFT JOIN FETCH o.hotelBookings WHERE o.id = :orderId")
	Optional<Order> findByIdWithHotelBookings(@Param("orderId") Integer orderId);

	@Query("SELECT o FROM Order o LEFT JOIN FETCH o.bookingTours WHERE o.id = :orderId")
	Optional<Order> findByIdWithTourBookings(@Param("orderId") Integer orderId);

    @Query("SELECT COALESCE(SUM(o.amount), 0) FROM Order o WHERE o.status = 'PAID' AND o.payDate BETWEEN :startDate AND :endDate")
    BigDecimal findRevenueBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    long countNewOrdersBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(DISTINCT o.user) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    long countNewCustomersBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


    // --- Các phương thức cũ cho biểu đồ (vẫn cần thiết) ---
    @Query("SELECT new backend.backend.dto.RevenueDataPointDTO(CONCAT(YEAR(o.payDate), '-', MONTH(o.payDate), '-', DAY(o.payDate)), SUM(o.amount)) " +
           "FROM Order o " +
           "WHERE o.status = 'PAID' AND o.payDate >= :startDate " +
           "GROUP BY YEAR(o.payDate), MONTH(o.payDate), DAY(o.payDate) " +
           "ORDER BY YEAR(o.payDate), MONTH(o.payDate), DAY(o.payDate) ASC")
    List<RevenueDataPointDTO> findRevenueLast7Days(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT new backend.backend.dto.RevenueDataPointDTO(CONCAT(YEAR(o.payDate), '-', MONTH(o.payDate)), SUM(o.amount)) " +
           "FROM Order o " +
           "WHERE o.status = 'PAID' AND YEAR(o.payDate) = :year " +
           "GROUP BY YEAR(o.payDate), MONTH(o.payDate) " +
           "ORDER BY YEAR(o.payDate), MONTH(o.payDate) ASC")
    List<RevenueDataPointDTO> findRevenueByMonthInYear(@Param("year") int year);

    @Query("SELECT new backend.backend.dto.RevenueDataPointDTO(CONCAT(YEAR(o.payDate), ''), SUM(o.amount)) " +
           "FROM Order o " +
           "WHERE o.status = 'PAID' AND o.payDate >= :startDate " +
           "GROUP BY YEAR(o.payDate) " +
           "ORDER BY YEAR(o.payDate) ASC")
    List<RevenueDataPointDTO> findRevenueByYear(@Param("startDate") LocalDateTime startDate);
    
    
    @Query("SELECT count(bt) FROM BookingTour bt")
    long countTourBookings();

    @Query("SELECT count(hb) FROM HotelBooking hb")
    long countHotelBookings();

    @Query("SELECT count(fb) FROM FlightBooking fb")
    long countFlightBookings();

    @Query("SELECT count(bb) FROM BusBooking bb")
    long countBusBookings();
}
