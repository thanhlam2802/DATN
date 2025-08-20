
package backend.backend.dto.BusDTO;
import backend.backend.entity.Bus;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
// Nếu bạn muốn Lombok tự động tạo constructor với TẤT CẢ các trường, hãy bỏ comment dòng dưới và XÓA constructor tùy chỉnh
// @AllArgsConstructor
public class BusResponse {
    private Integer id;
    private String name;
    private String licensePlate;
    private Integer totalSeats;
    private Integer categoryId;
    private String categoryName;
    private Integer ownerId;
    private String ownerName;
    private List<BusImageResponse> busImages; // Danh sách BusImageResponse DTO
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<BusAmenityResponse> amenities; // <-- BỔ SUNG: Danh sách tiện ích

    // CONSTRUCTOR MỚI ĐỂ ĐẢM BẢO TẤT CẢ CÁC TRƯỜNG ĐƯỢC KHỞI TẠO TỪ convertToBusResponse
    // Constructor này phải khớp với các tham số mà BusServiceImpl.convertToBusResponse truyền vào
    public BusResponse(Integer id, String name, String licensePlate, Integer totalSeats,
                       Integer categoryId, String categoryName, Integer ownerId, String ownerName,
                       List<BusImageResponse> busImages, OffsetDateTime createdAt, OffsetDateTime updatedAt,
                       List<BusAmenityResponse> amenities) { // <-- THÊM THAM SỐ amenities
        this.id = id;
        this.name = name;
        this.licensePlate = licensePlate;
        this.totalSeats = totalSeats;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.busImages = busImages;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.amenities = amenities; // <-- GÁN GIÁ TRỊ CHO TRƯỜNG amenities
    }

    // Constructor public BusResponse(Bus bus) cũ đã được XÓA bỏ.
    // Logic chuyển đổi từ Bus Entity sang BusResponse DTO nằm hoàn toàn trong BusServiceImpl.

    public BusResponse(Bus bus) {
        this.id = bus.getId();
        this.name = bus.getName();
        this.licensePlate = bus.getLicensePlate();
        this.totalSeats = bus.getTotalSeats();

        if (bus.getCategory() != null) {
            this.categoryId = bus.getCategory().getId();
            this.categoryName = bus.getCategory().getName();
        } else {
            this.categoryId = null;
            this.categoryName = null;
        }

        if (bus.getOwner() != null) {
            this.ownerId = bus.getOwner().getId();
            this.ownerName = bus.getOwner().getName();
        } else {
            this.ownerId = null;
            this.ownerName = null;
        }

        // Chuyển đổi BusImage entities sang BusImageResponse DTOs
        // Đảm bảo BusImageResponse có constructor public BusImageResponse(BusImage img)
        if (bus.getBusImages() != null) {
            this.busImages = bus.getBusImages().stream()
                    .map(backend.backend.dto.BusDTO.BusImageResponse::new) // Gọi constructor BusImageResponse(BusImage)
                    .collect(java.util.stream.Collectors.toList());
        } else {
            this.busImages = new java.util.ArrayList<>();
        }

        // Chuyển đổi BusAmenity entities sang BusAmenityResponse DTOs
        // Đảm bảo BusAmenityResponse có constructor public BusAmenityResponse(BusAmenity amenity)
        if (bus.getAmenities() != null) {
            this.amenities = bus.getAmenities().stream()
                    .map(backend.backend.dto.BusDTO.BusAmenityResponse::new) // Gọi constructor BusAmenityResponse(BusAmenity)
                    .collect(java.util.stream.Collectors.toList());
        } else {
            this.amenities = new java.util.ArrayList<>();
        }

        this.createdAt = bus.getCreatedAt();
        this.updatedAt = bus.getUpdatedAt();
    }
}
