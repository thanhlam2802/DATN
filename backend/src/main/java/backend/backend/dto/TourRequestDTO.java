package backend.backend.dto;

import backend.backend.entity.TourStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class TourRequestDTO {

    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationDays;
    private String departurePoint;
    private String destination;
    private TourStatus status;

    
    private List<ScheduleRequestDTO> schedules;
    private List<DepartureRequestDTO> departures;

    // SỬA #2: Thay đổi từ Set<Long> tagIds thành List<TagDTO> để nhận cả tag mới và cũ
    private List<TagDTO> tags;

    // SỬA #3: Thêm trường này để xử lý việc cập nhật ảnh
    private List<String> imageUrls; 

    /**
     * DTO con cho Tags, chứa id (cho tag cũ) hoặc name (cho tag mới)
     */
    @Data
    public static class TagDTO {
        private Long id;
        private String name;
    }
}