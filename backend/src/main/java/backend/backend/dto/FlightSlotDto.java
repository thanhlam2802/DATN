package backend.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightSlotDto {
    private Integer id;
    
    @NotNull(message = "Giá vé không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá vé phải lớn hơn 0")
    @DecimalMax(value = "10000000.0", message = "Giá vé không được vượt quá 10,000,000")
    private BigDecimal price;
    
    @NotNull(message = "Loại ghế không được để trống")
    private Boolean isBusiness;
    
    @NotBlank(message = "Số ghế không được để trống")
    private String seatNumber;
    
    @NotNull(message = "Vị trí cửa sổ không được để trống")
    private Boolean isWindow;
    
    @NotNull(message = "Vị trí lối đi không được để trống")
    private Boolean isAisle;
    
    @Positive(message = "ID chuyến bay phải là số dương")
    private Integer flightId;
    
    @Min(value = 0, message = "Hành lý xách tay không được âm")
    @Max(value = 50, message = "Hành lý xách tay không được vượt quá 50kg")
    private Integer carryOnLuggage;
}
