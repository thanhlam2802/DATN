package backend.backend.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import backend.backend.entity.BusBooking;


public interface BusBookingDAO extends JpaRepository<BusBooking, Integer> {

	List<BusBooking> findByOrderId(Integer id);
	
	
}
