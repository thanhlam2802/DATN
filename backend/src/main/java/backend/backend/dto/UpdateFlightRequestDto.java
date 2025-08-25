package backend.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateFlightRequestDto {
    private Integer id;
    
    @NotBlank(message = "Số hiệu chuyến bay không được để trống")
    private String flightNumber;
    
    @NotBlank(message = "Tên chuyến bay không được để trống")
    @Size(min = 5, max = 200, message = "Tên chuyến bay phải có từ 5 đến 200 ký tự")
    private String name;
    
    @NotNull(message = "Thời gian khởi hành không được để trống")
    @Future(message = "Thời gian khởi hành phải là thời gian trong tương lai")
    private LocalDateTime departureTime;
    
    @NotNull(message = "Thời gian đến không được để trống")
    @Future(message = "Thời gian đến phải là thời gian trong tương lai")
    private LocalDateTime arrivalTime;
    
    // ID của các entity quan hệ
    @Positive(message = "ID hãng bay phải là số dương")
    private Integer airlineId;
    
    @Positive(message = "ID danh mục chuyến bay phải là số dương")
    private Integer categoryId;
    
    @Positive(message = "ID sân bay đi phải là số dương")
    private Integer departureAirportId;
    
    @Positive(message = "ID sân bay đến phải là số dương")
    private Integer arrivalAirportId;
}
