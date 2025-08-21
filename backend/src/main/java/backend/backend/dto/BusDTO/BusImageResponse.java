package backend.backend.dto.BusDTO;

import backend.backend.entity.BusImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusImageResponse {
    private Integer busId;
    private Integer imageId;
    private ImageResponse image; // Đối tượng DTO của Image

    public BusImageResponse(BusImage busImage) {
        this.busId = busImage.getBus().getId();
        this.imageId = busImage.getImage().getId();
        // ImageResponse constructor đã tự động chuyển đổi LocalDateTime sang OffsetDateTime
        this.image = new ImageResponse(
                busImage.getImage().getId(),
                busImage.getImage().getUrl(),
                busImage.getImage().getAltText(),
                busImage.getImage().getUploadedAt(),// Đây là LocalDateTime từ Entity
                busImage.getImage().getPublicId()
        );
    }
}