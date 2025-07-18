package backend.backend.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.backend.entity.BusBooking;


public interface BusBookingDAO extends JpaRepository<BusBooking, Integer> {

	List<BusBooking> findByTicketDetailId(Integer id);

}
