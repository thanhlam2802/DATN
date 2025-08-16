package backend.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object (DTO) để nhận yêu cầu tạo một review mới từ client.
 * Chứa các thông tin cần thiết để tạo một bản ghi Review.
 */
@Data
public class ReviewRequestDTO {

    @NotNull(message = "User ID không được để trống")
    private Integer userId;

    @NotNull(message = "Entity ID không được để trống")
    private Integer entityId; // ID của booking (tour, hotel, flight, bus)

    @NotBlank(message = "Entity type không được để trống")
    private String entityType; // Loại dịch vụ: "HOTEL", "FLIGHT", "BUS", "TOUR"

    @NotNull(message = "Rating không được để trống")
    @Min(value = 1, message = "Rating phải ít nhất là 1")
    @Max(value = 5, message = "Rating không được lớn hơn 5")
    private Short rating; // Số sao đánh giá

    private String content; // Nội dung bình luận (có thể không bắt buộc)
}