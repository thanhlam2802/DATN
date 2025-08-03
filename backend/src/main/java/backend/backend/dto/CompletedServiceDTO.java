
package backend.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder 
public class CompletedServiceDTO {

    // ID của chính booking đó (ví dụ: hotel_booking_id, flight_booking_id)
    private Integer bookingId;

    // Loại dịch vụ: "HOTEL", "FLIGHT", "BUS", "TOUR"
    private String serviceType;

    // Tên dịch vụ để hiển thị
    private String serviceName;

    // Ngày dịch vụ kết thúc (check-out, ngày khởi hành,...)
    private LocalDate completionDate;
    
    // Biểu tượng FontAwesome để frontend hiển thị
    private String icon;

    // Trạng thái đã đánh giá hay chưa
    private boolean isReviewed;
    
    private Long serviceId;
}