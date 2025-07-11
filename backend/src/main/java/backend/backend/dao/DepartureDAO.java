package backend.backend.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.backend.entity.Departure;

public interface DepartureDAO  extends JpaRepository<Departure, Long>{
	
	List<Departure> findByTourId(Long tourId);
    List<Departure> findByDepartureDateBetween(LocalDate startDate, LocalDate endDate);

    boolean existsByTourId(Long tourId);

    // Phương thức để kiểm tra xem có booking nào không (dựa vào bookedSeats > 0)
    boolean existsByTourIdAndBookedSeatsGreaterThan(Long tourId, int bookedSeats);
}
