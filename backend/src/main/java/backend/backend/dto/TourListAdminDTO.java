package backend.backend.dto;

import backend.backend.entity.TourStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder // Dùng Builder để tạo đối tượng dễ dàng hơn
public class TourListAdminDTO {
    private Long id;
    private String name;
    private String vendorName; // Tên nhà cung cấp (Business Name)
    private String destination;
    private TourStatus status;
}