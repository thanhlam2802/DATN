package backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightSearchRequestDto {
    private Integer departureAirportId;
    private Integer arrivalAirportId;
    private LocalDate departureDate;
    // Thêm các trường lọc mới
    private Integer airlineId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String timeWindow; // morning, afternoon, evening
    private Integer categoryId; // Thêm để lọc theo loại chuyến bay
} 