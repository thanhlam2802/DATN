package backend.backend.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import backend.backend.entity.*;

public interface TourScheduleDAO extends JpaRepository<TourSchedule, Long > {
	@Query("SELECT ts FROM TourSchedule ts WHERE ts.tour.id = :tourId")
    List<TourSchedule> getSchedulesByTourId(@Param("tourId") Long tourId);


	List<TourSchedule> findByTourId(Long tourId); 
    List<TourSchedule> findByTourIdAndScheduleDate(Long tourId, LocalDate scheduleDate); 
    List<TourSchedule> findByScheduleDate(LocalDate scheduleDate);


	
	
}
