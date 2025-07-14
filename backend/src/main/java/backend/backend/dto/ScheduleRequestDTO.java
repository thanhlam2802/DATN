package backend.backend.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ScheduleRequestDTO {
	
    private Integer dayNumber;
    private String title;
    private LocalDate scheduleDate;
    private List<ActivityRequestDTO> activities;
}