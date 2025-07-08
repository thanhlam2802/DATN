package backend.backend.service;

import java.time.LocalDate;
import java.util.List;

import backend.backend.dto.TourScheduleDto;
import backend.backend.entity.TourSchedule;

public interface TourScheduleService {

	List<TourScheduleDto> getSchedulesByTourId(Long tourId);
	 List<TourSchedule> findByTourId(Long tourId); 
    List<TourScheduleDto> getSchedulesByTourIdAndDate(Long tourId, LocalDate scheduleDate);
    TourScheduleDto createTourSchedule(TourScheduleDto tourScheduleDto);

    TourScheduleDto updateTourSchedule(Long  id, TourScheduleDto tourScheduleDto);

    void deleteTourSchedule(Long  id);
}
