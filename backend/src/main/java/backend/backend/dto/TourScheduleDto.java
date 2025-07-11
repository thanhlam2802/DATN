package backend.backend.dto;

import backend.backend.entity.TourSchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors; 


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TourScheduleDto {
	private Long id;
    private Long tourId; 
    private int day; 
    private String title;
    private List<ActivityDto> activities;

    // Static factory method to convert entity to DTO
    public static TourScheduleDto fromEntity(TourSchedule tourSchedule) {
        TourScheduleDto dto = new TourScheduleDto();
        dto.setId(tourSchedule.getId());
        dto.setTourId(tourSchedule.getTour() != null ? tourSchedule.getTour().getId() : null);
        dto.setDay(tourSchedule.getDayNumber());
                             
        dto.setTitle(tourSchedule.getTitle());
        if (tourSchedule.getActivities() != null) {
            dto.setActivities(tourSchedule.getActivities().stream().map(activityEntity -> {
                ActivityDto activityDto = new ActivityDto();
                activityDto.setId(activityEntity.getId());
                activityDto.setTime(activityEntity.getActivityTime());
                activityDto.setActivity(activityEntity.getActivityTitle());
                activityDto.setDescription(activityEntity.getDescription());
                activityDto.setIcon(activityEntity.getIcon());
                return activityDto;
            }).collect(Collectors.toList()));
        }
        return dto;
    }

  
    public TourSchedule toEntity() {
        TourSchedule entity = new TourSchedule();
        entity.setId(this.id);
        entity.setDayNumber(this.day);
        entity.setTitle(this.title);
        
        return entity;
    }


	


	
}