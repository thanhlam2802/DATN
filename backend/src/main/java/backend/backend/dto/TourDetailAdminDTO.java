package backend.backend.dto;

import backend.backend.entity.Tour;
import backend.backend.entity.TourStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO này đại diện cho toàn bộ thông tin chi tiết của một tour,
 * được sử dụng trong trang quản trị để xem, sửa, và tạo mới.
 * Nó tổng hợp dữ liệu từ nhiều entity liên quan thành một cấu trúc phẳng.
 */
@Data
public class TourDetailAdminDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationDays;
    private String departurePoint;
    private String destination;
    private TourStatus status; 
    private Integer owner_id;
    private List<TourImageDto> tourImages;
    private List<DepartureDto> departures;
    private Set<TagDto> tags;
    private long bookings_count;

    /**
     * Thay thế list TourScheduleDto cũ bằng ItineraryDayDto để có cấu trúc chi tiết hơn.
     * Tên biến vẫn giữ là 'tourSchedules' để tương thích với frontend nếu có thể.
     */
    private List<ItineraryDayDto> tourSchedules;

    /**
     * Constructor nhận vào một đối tượng Tour entity và chuyển đổi nó thành DTO.
     * Quá trình này sẽ gọi các phương thức 'fromEntity' từ các DTO con.
     *
     * @param tour Đối tượng Tour entity từ database.
     */
    public TourDetailAdminDTO(Tour tour) {
        this.id = tour.getId();
        this.name = tour.getName();
        this.description = tour.getDescription();
        this.price = tour.getPrice();
        this.durationDays = tour.getDurationDays();
        this.departurePoint = tour.getDeparturePoint();
        this.destination = tour.getDestination();
        this.status = tour.getStatus();
        this.owner_id = tour.getOwner() != null ? tour.getOwner().getId() : null;
        
        this.tourImages = tour.getTourImages() != null ?
                tour.getTourImages().stream()
                      
                        .map(tourImage -> new TourImageDto(
                                (long) tourImage.getImage().getId(),
                                tourImage.getImage().getUrl()     
                        ))
                        .collect(Collectors.toList()) : Collections.emptyList();

        // Chuyển đổi danh sách ngày khởi hành sử dụng fromEntity
        this.departures = tour.getDepartures() != null ?
                tour.getDepartures().stream()
                        .map(DepartureDto::fromEntity)
                        .collect(Collectors.toList()) : Collections.emptyList();

        // Chuyển đổi danh sách thẻ (tags)
        this.tags = tour.getTags() != null ?
                tour.getTags().stream()
                        .map(tag -> new TagDto(tag.getId(), tag.getName()))
                        .collect(Collectors.toSet()) : Collections.emptySet();

        // Chuyển đổi lịch trình chi tiết (sử dụng ItineraryDayDto)
        this.tourSchedules = tour.getTourSchedules() != null ?
                tour.getTourSchedules().stream()
                        .map(ItineraryDayDto::fromEntity)
                        .collect(Collectors.toList()) : Collections.emptyList();

        // Tính tổng số lượt đặt tour
        this.bookings_count = tour.getDepartures() != null ?
                tour.getDepartures().stream()
                        .mapToLong(dep -> dep.getBookingTours() != null ? dep.getBookingTours().size() : 0)
                        .sum() : 0;
    }
}