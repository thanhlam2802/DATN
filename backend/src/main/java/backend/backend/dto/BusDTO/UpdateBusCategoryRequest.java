package backend.backend.dto.BusDTO;

import jakarta.validation.constraints.Size;

// DTO để cập nhật một BusCategory hiện có
public record UpdateBusCategoryRequest(
        // ID của danh mục cần cập nhật thường được truyền qua tham số riêng biệt trong API/GraphQL
        // Ví dụ: updateBusCategory(id: ID!, input: UpdateBusCategoryInput)

        @Size(max = 100, message = "Tên danh mục không được vượt quá 100 ký tự")
        String name // Tên mới của danh mục (có thể null nếu không muốn cập nhật tên)
) {}