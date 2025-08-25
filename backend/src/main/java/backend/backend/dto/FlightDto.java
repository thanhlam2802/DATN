package backend.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
public class FlightDto {
    private Integer id;
    
    @NotNull(message = "Thời gian đến không được để trống")
    @Future(message = "Thời gian đến phải là thời gian trong tương lai")
    private java.time.LocalDateTime arrivalTime;
    
    private java.time.LocalDateTime createdAt;
    
    @NotNull(message = "Thời gian khởi hành không được để trống")
    @Future(message = "Thời gian khởi hành phải là thời gian trong tương lai")
    private java.time.LocalDateTime departureTime;
    
    @NotBlank(message = "Số hiệu chuyến bay không được để trống")

    private String flightNumber;
    
    @NotBlank(message = "Tên chuyến bay không được để trống")
    @Size(min = 5, max = 200, message = "Tên chuyến bay phải có từ 5 đến 200 ký tự")
    private String name;
    
    private java.time.LocalDateTime updatedAt;
    
    @Valid
    private FlightCategoryDto category;
    
    @Positive(message = "ID chủ sở hữu phải là số dương")
    private Integer ownerId;
    
    @Valid
    @NotNull(message = "Sân bay đến không được để trống")
    private AirportDto arrivalAirport;
    
    @Valid
    @NotNull(message = "Sân bay đi không được để trống")
    private AirportDto departureAirport;

    @Valid
    private AirlineDto airline;
    
    // Images (primary image for list view)
    private List<FlightImageDto> images;
    
    // Flight slots with detailed pricing
    private List<FlightSlotDto> flightSlots;
    
    // Summary pricing information (for quick display)
    @DecimalMin(value = "0.0", message = "Giá tối thiểu không được âm")
    private Double minPrice;
    
    @DecimalMin(value = "0.0", message = "Giá tối đa không được âm")
    private Double maxPrice;
    
    @Min(value = 0, message = "Tổng số ghế có sẵn không được âm")
    private Integer totalAvailableSeats;
    
    private String availableSeatClasses;
    
    @Min(value = 0, message = "Hành lý xách tay không được âm")
    @Max(value = 50, message = "Hành lý xách tay không được vượt quá 50kg")
    private Integer carryOnLuggage;
} 