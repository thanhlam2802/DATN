package backend.backend.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import backend.backend.entity.Order;
import org.springframework.data.jpa.repository.Query;

public interface OrderDAO extends JpaRepository<Order, Integer> {

	List<Order> findByUserIdOrderByCreatedAtDesc(Integer userId);

	@Query(value = "select  top 10  * from orders order by created_at desc",nativeQuery = true)
	List<Order> findTop10NewOrder();
	List<Order> findAllByStatusAndExpiresAtBefore(String status, LocalDateTime expiryTime);
	Order findFirstByUserIdAndStatus(Integer userId, String status);
}
