package backend.backend.dto;

import backend.backend.entity.TourItineraryActivity;
import lombok.Data;

@Data
public class ActivityDto {
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
        dto.setTime(entity.getActivityTime());
        dto.setActivity(entity.getActivityTitle());
        dto.setDescription(entity.getDescription());
        dto.setIcon(entity.getIcon());
        return dto;
    }
}