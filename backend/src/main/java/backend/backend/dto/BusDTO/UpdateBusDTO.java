package backend.backend.dto.BusDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record UpdateBusDTO(
        // ID của Bus cần cập nhật (thường được truyền qua Path Variable hoặc Argument trong GraphQL)
        // Integer id, // Thường không cần trong DTO nếu id được truyền qua URL/GraphQL argument

        @Size(max = 200, message = "Tên xe không được vượt quá 200 ký tự")
        String name, // Có thể null nếu không muốn cập nhật tên

        Integer categoryId, // Có thể null nếu không muốn cập nhật danh mục

        @Size(max = 100, message = "Điểm xuất phát không được vượt quá 100 ký tự")
        String origin, // Có thể null nếu không muốn cập nhật điểm xuất phát

        @Size(max = 100, message = "Điểm đến không được vượt quá 100 ký tự")
        String destination, // Có thể null nếu không muốn cập nhật điểm đến

        LocalDateTime departureTime, // Có thể null nếu không muốn cập nhật thời gian khởi hành

        LocalDateTime arrivalTime, // Có thể null nếu không muốn cập nhật thời gian đến

        Integer ownerId // Có thể null nếu không muốn cập nhật chủ sở hữu
) {}