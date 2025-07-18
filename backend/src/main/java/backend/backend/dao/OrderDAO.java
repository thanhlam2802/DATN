package backend.backend.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import backend.backend.entity.Order;

public interface OrderDAO extends JpaRepository<Order, Integer> {

	List<Order> findByUserIdOrderByCreatedAtDesc(Integer userId);

	List<Order> findAllByStatusAndExpiresAtBefore(String status, LocalDateTime expiryTime);

}
