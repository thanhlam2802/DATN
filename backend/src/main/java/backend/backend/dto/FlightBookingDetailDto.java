package backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightBookingDetailDto {
    private Integer bookingId;
    private LocalDateTime createdAt;
    private FlightDto flight;
    private Double totalPrice;
    private String status;
} 