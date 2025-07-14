package backend.backend.dto.BusDTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;


public record CreateBusDTO(
        @NotBlank(message = "Tên xe không được để trống")
        @Size(max = 200, message = "Tên xe không được vượt quá 200 ký tự")
        String name,

        @NotNull(message = "Mã danh mục xe không được để trống")
        Integer categoryId,

        @NotBlank(message = "Điểm xuất phát không được để trống")
        @Size(max = 100, message = "Điểm xuất phát không được vượt quá 100 ký tự")
        String origin,

        @NotBlank(message = "Điểm đến không được để trống")
        @Size(max = 100, message = "Điểm đến không được vượt quá 100 ký tự")
        String destination,

        @NotNull(message = "Thời gian khởi hành không được để trống")
        LocalDateTime departureTime,

        @NotNull(message = "Thời gian đến không được để trống")
        LocalDateTime arrivalTime,

        @NotNull(message = "Mã chủ sở hữu không được để trống")
        Integer ownerId
) {}