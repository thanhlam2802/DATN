package backend.backend.dto.Hotel;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminReviewDto {
    private Integer id;
    private String customerName;
    private String customerEmail;
    private String hotelName;
    private String hotelAddress;
    private Short rating;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 