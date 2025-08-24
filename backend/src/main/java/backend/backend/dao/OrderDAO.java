package backend.backend.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


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
}
