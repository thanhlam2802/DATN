package backend.backend.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.backend.entity.BookingTour;


public interface BookingTourDAO extends JpaRepository<BookingTour, Integer> {

	List<BookingTour> findByOrderId(Integer id);

}
