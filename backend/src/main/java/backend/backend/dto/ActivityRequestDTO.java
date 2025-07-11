package backend.backend.dto;

import lombok.Data;

@Data
public class ActivityRequestDTO {
	
    private String time;
    private String activityTitle;
    private String description;
    private String icon;
}