package backend.backend.dto.BusDTO;

import backend.backend.entity.Bus;
// Không cần import BusCategory, User trực tiếp ở đây nếu chỉ dùng ID/Name
// import backend.backend.entity.BusCategory;
// import backend.backend.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList; // Thêm import này cho ArrayList
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
// @AllArgsConstructor // Tạm thời bỏ @AllArgsConstructor để tránh xung đột với constructor tùy chỉnh
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

    // CONSTRUCTOR MỚI ĐỂ ĐẢM BẢO TẤT CẢ CÁC TRƯỜNG ĐƯỢC KHỞI TẠO TỪ convertToBusResponse
    public BusResponse(Integer id, String name, String licensePlate, Integer totalSeats,
                       Integer categoryId, String categoryName, Integer ownerId, String ownerName,
                       List<BusImageResponse> busImages, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
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
    }


    // Constructor hiện tại của bạn nhận Bus entity, cần được sửa để chuyển đổi BusImage
    public BusResponse(Bus bus) {
        this.id = bus.getId();
        this.name = bus.getName();
        this.licensePlate = bus.getLicensePlate();
        this.totalSeats = bus.getTotalSeats();

        if (bus.getCategory() != null) {
            this.categoryId = bus.getCategory().getId();
            this.categoryName = bus.getCategory().getName();
        } else {
            this.categoryId = null; // Gán null nếu không có category
            this.categoryName = null;
        }

        if (bus.getOwner() != null) {
            this.ownerId = bus.getOwner().getId();
            this.ownerName = bus.getOwner().getName();
        } else {
            this.ownerId = null; // Gán null nếu không có owner
            this.ownerName = null;
        }

        this.createdAt = bus.getCreatedAt();
        this.updatedAt = bus.getUpdatedAt();

        if (bus.getBusImages() != null && !bus.getBusImages().isEmpty()) {
            this.busImages = bus.getBusImages().stream()
                    // Dòng này cần được sửa! BusImageResponse::new không đủ nếu nó không có constructor nhận BusImage
                    // Bạn cần một constructor trong BusImageResponse nhận BusImage entity.
                    // Hoặc bạn phải truyền các thành phần riêng lẻ (imageId, busId, ImageResponse)
                    .map(BusImageResponse::new) // <-- Dòng này giả định BusImageResponse có constructor (BusImage entity)
                    .collect(Collectors.toList());
        } else {
            this.busImages = new ArrayList<>(); // Khởi tạo danh sách rỗng nếu không có ảnh
        }
    }
}