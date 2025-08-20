package backend.backend.dto.Hotel;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class ReviewResponseRequestDto {
    
    @NotBlank(message = "Nội dung phản hồi không được để trống")
    @Size(min = 1, max = 1000, message = "Nội dung phản hồi phải từ 1 đến 1000 ký tự")
    private String response;
} 