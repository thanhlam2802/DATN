package backend.backend.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.backend.entity.HotelBooking;



public interface HotelBookingDAO extends JpaRepository<HotelBooking, Integer> {

	List<HotelBooking> findByOrderId(Integer id);

}
