package backend.backend.dto.BusDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List; // Import mới cho List

public record CreateBusRequest(
        @NotBlank(message = "Tên xe không được để trống")
        @Size(max = 200, message = "Tên xe không được vượt quá 200 ký tự")
        String name,

        @NotBlank(message = "Biển số xe không được để trống") // Thêm biển số xe
        @Size(max = 20, message = "Biển số xe không được vượt quá 20 ký tự")
        String licensePlate,

        @NotNull(message = "Tổng số ghế không được để trống") // Thêm tổng số ghế
        Integer totalSeats,

        @NotBlank(message = "Mã danh mục xe không được để trống") // <-- THAY ĐỔI TỪ Integer SANG String
        String categoryId,

        @NotBlank(message = "Mã chủ sở hữu không được để trống") // <-- THAY ĐỔI TỪ Integer SANG String
        String ownerId,

        List<String> imageIds // <-- THAY ĐỔI TỪ List<Integer> SANG List<String>
) {}