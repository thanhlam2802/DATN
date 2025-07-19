package backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketDetailDto {

    private Integer id;
    private LocalDateTime createdAt;
    private String status;

    /**
     * ID của hóa đơn (Order) được tạo sau khi thanh toán giỏ hàng.
     * Sẽ là null nếu giỏ hàng chưa được thanh toán.
     */
    private Integer orderId;

    /**
     * Danh sách các sản phẩm có trong giỏ hàng.
     */
    private List<FlightBookingDto> flightBookings;
    private List<HotelBookingDto> hotelBookings;
    private List<BusBookingDto> busBookings;
    private List<BookingTourDto> tourBookings; 
}