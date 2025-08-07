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
public class CreateFlightRequestDto {
    // Thông tin cơ bản
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
    @NotNull(message = "ID hãng bay không được để trống")
    @Positive(message = "ID hãng bay phải là số dương")
    private Integer airlineId;
    
    @NotNull(message = "ID danh mục chuyến bay không được để trống")
    @Positive(message = "ID danh mục chuyến bay phải là số dương")
    private Integer categoryId;
    
    @NotNull(message = "ID sân bay đi không được để trống")
    @Positive(message = "ID sân bay đi phải là số dương")
    private Integer departureAirportId;
    
    @NotNull(message = "ID sân bay đến không được để trống")
    @Positive(message = "ID sân bay đến phải là số dương")
    private Integer arrivalAirportId;
    
    // Thông tin vé và ghế
    @Valid
    @NotNull(message = "Thông tin vé không được để trống")
    private TicketInfoDto ticketInfo;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TicketInfoDto {
        @NotNull(message = "Tổng số ghế không được để trống")
        @Min(value = 1, message = "Tổng số ghế phải lớn hơn 0")
        @Max(value = 500, message = "Tổng số ghế không được vượt quá 500")
        private Integer total;
        
        @NotNull(message = "Số ghế phổ thông không được để trống")
        @Min(value = 0, message = "Số ghế phổ thông không được âm")
        private Integer economy;
        
        @NotNull(message = "Số ghế thương gia không được để trống")
        @Min(value = 0, message = "Số ghế thương gia không được âm")
        private Integer business;
        
        @NotNull(message = "Số ghế phổ thông cửa sổ không được để trống")
        @Min(value = 0, message = "Số ghế phổ thông cửa sổ không được âm")
        private Integer economyWindow;
        
        @NotNull(message = "Số ghế phổ thông lối đi không được để trống")
        @Min(value = 0, message = "Số ghế phổ thông lối đi không được âm")
        private Integer economyAisle;
        
        @NotNull(message = "Số ghế thương gia cửa sổ không được để trống")
        @Min(value = 0, message = "Số ghế thương gia cửa sổ không được âm")
        private Integer businessWindow;
        
        @NotNull(message = "Số ghế thương gia lối đi không được để trống")
        @Min(value = 0, message = "Số ghế thương gia lối đi không được âm")
        private Integer businessAisle;
        
        @NotNull(message = "Giá vé phổ thông không được để trống")
        @DecimalMin(value = "0.0", inclusive = false, message = "Giá vé phổ thông phải lớn hơn 0")
        @DecimalMax(value = "10000000.0", message = "Giá vé phổ thông không được vượt quá 10,000,000")
        private Double economyPrice;
        
        @NotNull(message = "Giá vé thương gia không được để trống")
        @DecimalMin(value = "0.0", inclusive = false, message = "Giá vé thương gia phải lớn hơn 0")
        @DecimalMax(value = "10000000.0", message = "Giá vé thương gia không được vượt quá 10,000,000")
        private Double businessPrice;
        
        @Min(value = 0, message = "Hành lý xách tay phổ thông không được âm")
        @Max(value = 50, message = "Hành lý xách tay phổ thông không được vượt quá 50kg")
        private Integer economyLuggage;
        
        @Min(value = 0, message = "Hành lý xách tay thương gia không được âm")
        @Max(value = 50, message = "Hành lý xách tay thương gia không được vượt quá 50kg")
        private Integer businessLuggage;
    }
} 