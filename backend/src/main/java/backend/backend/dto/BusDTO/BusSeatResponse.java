package backend.backend.dto.BusDTO;

import backend.backend.entity.enumBus.BusSeatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusSeatResponse {
    private Integer id;
    private String seatNumber;
    private Boolean isBooked;
    private BigDecimal price;
    private BusSeatType seatType;

    // Thêm thông tin BusSlot nếu cần
    private Integer busSlotId;

    // Thêm thông tin booking nếu cần
    private Integer bookingId; // ID của booking nếu ghế đã được đặt
    private String bookedBy; // Tên người đặt
    private String bookedAt; // Thời gian đặt
}
