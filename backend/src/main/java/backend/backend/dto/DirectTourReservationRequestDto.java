package backend.backend.dto;

import lombok.Data;

/**
 * DTO để nhận yêu cầu "Mua ngay" (thanh toán trực tiếp) cho một tour cụ thể,
 * bỏ qua giỏ hàng.
 */
@Data
public class DirectTourReservationRequestDto {
    
  
    private Integer userId;

   
    private Long tourId;
    private Long departureId;

 
    private int numberOfAdults;
    private int numberOfChildren;

    // Thông tin liên hệ của khách hàng
    private String customerName;
    private String phone;
    private String email;
    private String notes;
}
