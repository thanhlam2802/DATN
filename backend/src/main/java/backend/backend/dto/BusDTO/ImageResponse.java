package backend.backend.dto.BusDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime; // <-- Thêm import này
import java.time.ZoneOffset;    // <-- Thêm import này cho ZoneOffset

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageResponse {
    private Integer id;
    private String url; // <-- Đã đổi tên từ 'imageUrl' thành 'url'
    private String altText;
    private OffsetDateTime uploadedAt;
    private String publicId;

    // CONSTRUCTOR MỚI: Nhận các giá trị thô (bao gồm LocalDateTime) và thực hiện chuyển đổi
    public ImageResponse(Integer id, String url, String altText, LocalDateTime uploadedAt, String publicId) {
        this.id = id;
        this.url = url; // <-- Gán giá trị vào trường 'url' mới
        this.altText = altText;
        // Thực hiện chuyển đổi từ LocalDateTime sang OffsetDateTime ở đây
        this.uploadedAt = uploadedAt != null ? uploadedAt.atOffset(ZoneOffset.ofHours(7)) : null; // Giả định +07:00
        this.publicId = publicId;
    }

    // Constructor từ Image Entity (nếu bạn dùng nó ở đâu đó, đảm bảo nó cũng chuyển đổi)
    public ImageResponse(backend.backend.entity.Image image) {
        this.id = image.getId();
        this.url = image.getUrl(); // <-- Gán giá trị từ entity.getUrl() vào trường 'url' mới
        this.altText = image.getAltText();
        this.uploadedAt = image.getUploadedAt() != null ? image.getUploadedAt().atOffset(ZoneOffset.ofHours(7)) : null;
        this.publicId = image.getPublicId();
    }
}