package backend.backend.dto.BusDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBusCategoryRequest(
        @NotBlank(message = "Tên danh mục không được để trống")
        @Size(max = 100, message = "Tên danh mục không được vượt quá 100 ký tự")
        String name, // Tên của danh mục xe bus
        
        @NotNull(message = "Owner ID không được để trống")
        Integer ownerId // ID của doanh nghiệp sở hữu category
) {}