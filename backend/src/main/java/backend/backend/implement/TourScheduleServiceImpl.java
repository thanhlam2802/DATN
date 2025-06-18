package backend.backend.implement;



import backend.backend.dao.TourDAO;
import backend.backend.dao.TourScheduleDAO;
import backend.backend.dto.TourScheduleDto;
import backend.backend.entity.Tour;
import backend.backend.entity.TourSchedule;

import backend.backend.service.TourScheduleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourScheduleServiceImpl implements TourScheduleService {
	  @Autowired
 TourScheduleDAO tourScheduleRepository;
	  @Autowired
    TourDAO tourRepository;

	  
	  @Override
	  public List<TourScheduleDto> getSchedulesByTourId(Long tourId) {
	      List<TourSchedule> schedules = tourScheduleRepository.findByTourId(tourId);
	      return schedules.stream()
	                      .map(TourScheduleDto::fromEntity)
	                      .collect(Collectors.toList());
	  }


    @Override
    public List<TourScheduleDto> getSchedulesByTourIdAndDate(Long tourId, LocalDate scheduleDate) {
        List<TourSchedule> schedules = tourScheduleRepository.findByTourIdAndScheduleDate(tourId, scheduleDate);
        return schedules.stream()
                        .map(TourScheduleDto::fromEntity)
                        .collect(Collectors.toList());
    }

    @Override
    public TourScheduleDto createTourSchedule(TourScheduleDto tourScheduleDto) {
        if (tourScheduleDto.getTourId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tour ID is required to create a schedule.");
        }

        // Fetch the Tour entity to establish the relationship
        Tour tour = tourRepository.findById(tourScheduleDto.getTourId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tour not found with ID: " + tourScheduleDto.getTourId()));

        TourSchedule tourSchedule = tourScheduleDto.toEntity();
        tourSchedule.setTour(tour); // Set the actual Tour entity

        TourSchedule savedSchedule = tourScheduleRepository.save(tourSchedule);
        return TourScheduleDto.fromEntity(savedSchedule);
    }

    @Override
    public TourScheduleDto updateTourSchedule(Long id, TourScheduleDto tourScheduleDto) {
        TourSchedule existingSchedule = tourScheduleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tour schedule not found with ID: " + id));

        // Update fields (excluding ID, which is fixed)
        existingSchedule.setScheduleDate(tourScheduleDto.getScheduleDate() != null ? LocalDate.parse(tourScheduleDto.getScheduleDate()) : existingSchedule.getScheduleDate());
        existingSchedule.setActivity(tourScheduleDto.getActivity() != null ? tourScheduleDto.getActivity() : existingSchedule.getActivity());

        
        if (tourScheduleDto.getTourId() != null && !existingSchedule.getTour().getId().equals(tourScheduleDto.getTourId())) {
            Tour newTour = tourRepository.findById(tourScheduleDto.getTourId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "New Tour not found with ID: " + tourScheduleDto.getTourId()));
            existingSchedule.setTour(newTour);
        }

        TourSchedule updatedSchedule = tourScheduleRepository.save(existingSchedule);
        return TourScheduleDto.fromEntity(updatedSchedule);
    }

    @Override
    public void deleteTourSchedule(Long  id) {
        if (!tourScheduleRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tour schedule not found with ID: " + id);
        }
        tourScheduleRepository.deleteById(id);
    }


	@Override
	public List<TourSchedule> findByTourId(Long tourId) {
		// TODO Auto-generated method stub
		return null;
	}
}