package backend.backend.dto;

import backend.backend.entity.TourSchedule;
import lombok.Data;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ItineraryDayDto {
    private int day;
    private String title;
    private List<ActivityDto> activities;

    /**
     * Phương thức tĩnh để chuyển đổi từ Entity sang DTO.
     * @param entity Đối tượng TourSchedule (đại diện cho một ngày) từ database.
     * @return Đối tượng ItineraryDayDto với cấu trúc lồng nhau.
     */
    public static ItineraryDayDto fromEntity(TourSchedule entity) {
        ItineraryDayDto dto = new ItineraryDayDto();
        dto.setDay(entity.getDayNumber());
        dto.setTitle(entity.getActivity()); 

        // Chuyển đổi danh sách các hoạt động con
        if (entity.getActivities() != null) {
            dto.setActivities(
                entity.getActivities().stream()
                      .map(ActivityDto::fromEntity)
                      .collect(Collectors.toList())
            );
        }
        return dto;
    }
}
