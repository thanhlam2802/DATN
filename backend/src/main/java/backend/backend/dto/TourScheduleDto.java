package backend.backend.dto;

import backend.backend.entity.TourSchedule;
import jakarta.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter; 


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TourScheduleDto {
    private Long  id;
    private Long tourId; // Assuming tour ID is Long
    private String scheduleDate; // Will be formatted as "YYYY-MM-DD"
    private String activity;

    // Static factory method to convert entity to DTO
    public static TourScheduleDto fromEntity(TourSchedule tourSchedule) {
        TourScheduleDto dto = new TourScheduleDto();
        dto.setId(tourSchedule.getId());
        dto.setTourId(tourSchedule.getTour() != null ? tourSchedule.getTour().getId() : null);
        dto.setScheduleDate(tourSchedule.getScheduleDate() != null ?
                             tourSchedule.getScheduleDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) :
                             null);
        dto.setActivity(tourSchedule.getActivity());
        return dto;
    }

  
    public TourSchedule toEntity() {
        TourSchedule entity = new TourSchedule();
        entity.setId(this.id);
        entity.setScheduleDate(this.scheduleDate != null ? LocalDate.parse(this.scheduleDate, DateTimeFormatter.ofPattern("yyyy-MM-DD")) : null);
        entity.setActivity(this.activity);
        // The 'tour' relationship needs to be handled in the service
        return entity;
    }
}