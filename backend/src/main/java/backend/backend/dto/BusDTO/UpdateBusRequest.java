package backend.backend.dto.BusDTO;

import jakarta.validation.constraints.Min; // Import mới cho @Min
import jakarta.validation.constraints.Size;
import java.util.List; // Import mới cho List

public record UpdateBusRequest(
        @Size(max = 200, message = "Tên xe không được vượt quá 200 ký tự")
        String name,

        @Size(max = 20, message = "Biển số xe không được vượt quá 20 ký tự") // Thêm biển số xe
        String licensePlate,

        @Min(value = 1, message = "Tổng số ghế phải lớn hơn 0") // Thêm tổng số ghế
        Integer totalSeats,

        String categoryId,

        String ownerId,

        List<String> imageIds // Danh sách ID của các ảnh để cập nhật (có thể thay thế hoặc thêm bớt)
) {}