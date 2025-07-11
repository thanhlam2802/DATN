package backend.backend.implement;



import backend.backend.dao.TourDAO;
import backend.backend.dao.TourItineraryActivityDAO;
import backend.backend.dao.TourScheduleDAO;
import backend.backend.dto.TourScheduleDto;
import backend.backend.entity.Tour;
import backend.backend.entity.TourItineraryActivity;
import backend.backend.entity.TourSchedule;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.service.TourScheduleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class TourScheduleServiceImpl implements TourScheduleService {
	  @Autowired
 TourScheduleDAO tourScheduleRepository;
	  @Autowired
    TourDAO tourRepository;
	  @Autowired
	  TourItineraryActivityDAO tourItineraryActivityRepository;
	  
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
    @Transactional 
    public TourScheduleDto createTourSchedule(TourScheduleDto dto) {
        Tour tour = tourRepository.findById(dto.getTourId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Tour với ID: " + dto.getTourId()));

      
        TourSchedule schedule = new TourSchedule();
       
        schedule.setTour(tour);
       
        schedule.setDayNumber(dto.getDay());
        schedule.setTitle(dto.getTitle());
       
        
        TourSchedule savedSchedule = tourScheduleRepository.save(schedule);

        // 3. Tạo và lưu các TourItineraryActivity con
        List<TourItineraryActivity> activities = new ArrayList<>();
        if (dto.getActivities() != null) {
            dto.getActivities().forEach(activityDto -> {
                TourItineraryActivity activity = new TourItineraryActivity();
                activity.setTourSchedule(savedSchedule); 
                activity.setActivityTime(activityDto.getTime());
                activity.setActivityTitle(activityDto.getActivity());
                activity.setDescription(activityDto.getDescription());
                activity.setIcon(activityDto.getIcon());
                activities.add(activity);
            });
            tourItineraryActivityRepository.saveAll(activities);
        }
        
        savedSchedule.setActivities(activities);
        return TourScheduleDto.fromEntity(savedSchedule);
    }

    @Override
    @Transactional
    public TourScheduleDto updateTourSchedule(Long id, TourScheduleDto dto) {
    
        TourSchedule existingSchedule = tourScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch trình với ID: " + id));

        existingSchedule.setDayNumber(dto.getDay());
        existingSchedule.setTitle(dto.getTitle());

     
        existingSchedule.getActivities().clear();
       
        if (dto.getActivities() != null) {
      
            dto.getActivities().forEach(activityDto -> {
                TourItineraryActivity activity = new TourItineraryActivity();
                activity.setTourSchedule(existingSchedule); 
                activity.setActivityTime(activityDto.getTime());
                activity.setActivityTitle(activityDto.getActivity());
                activity.setDescription(activityDto.getDescription());
                activity.setIcon(activityDto.getIcon());
              
                existingSchedule.getActivities().add(activity); 
            });
        }

     
        TourSchedule savedSchedule = tourScheduleRepository.save(existingSchedule);
        
        return TourScheduleDto.fromEntity(savedSchedule);
    }

    @Override
    @Transactional
    public void deleteTourSchedule(Long id) {
        TourSchedule schedule = tourScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch trình với ID: " + id));

        // Do có quan hệ, chúng ta cần xóa các 'con' (activities) trước
        tourItineraryActivityRepository.deleteAll(schedule.getActivities());
        
        // Sau đó xóa 'cha' (schedule)
        tourScheduleRepository.delete(schedule);
    }


	@Override
	public List<TourSchedule> findByTourId(Long tourId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void deleteTour(Long id) {
		// TODO Auto-generated method stub
		
	}
}