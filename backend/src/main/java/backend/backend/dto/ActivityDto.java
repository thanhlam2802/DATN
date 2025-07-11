package backend.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import backend.backend.entity.TourItineraryActivity;
import lombok.Data;

@Data
public class ActivityDto {
	 private Long id;
    private String time;
    private String activity;
    private String description;
    private String icon;

    /**
     * Phương thức tĩnh để chuyển đổi từ Entity sang DTO.
     * @param entity Đối tượng TourItineraryActivity từ database.
     * @return Đối tượng ActivityDto.
     */
    public static ActivityDto fromEntity(TourItineraryActivity entity) {
        ActivityDto dto = new ActivityDto();
        dto.setId(entity.getId());
        dto.setTime(entity.getActivityTime());
        dto.setActivity(entity.getActivityTitle());
        dto.setDescription(entity.getDescription());
        dto.setIcon(entity.getIcon());
        return dto;
    }
}