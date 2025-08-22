package backend.backend.dto.BusDTO;

import jakarta.validation.constraints.NotBlank;

public record CreateImageInput(
        @NotBlank(message = "URL không được để trống")
        String url,
        String altText,
        @NotBlank(message = "Public ID không được để trống")
        String publicId
) {}