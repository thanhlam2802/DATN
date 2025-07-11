package backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFlightRequestDto {
    // Thông tin cơ bản
    private String flightNumber;
    private String name;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    
    // ID của các entity quan hệ
    private Integer airlineId;
    private Integer categoryId;
    private Integer departureAirportId;
    private Integer arrivalAirportId;
    
    // Thông tin vé và ghế
    private TicketInfoDto ticketInfo;
    
    // Ảnh (sẽ được xử lý riêng)
    private List<MultipartFile> images;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TicketInfoDto {
        private Integer total;
        private Integer economy;
        private Integer business;
        private Integer economyWindow;
        private Integer economyAisle;
        private Integer businessWindow;
        private Integer businessAisle;
        private Double economyPrice;
        private Double businessPrice;
        private Integer economyLuggage;
        private Integer businessLuggage;
    }
} 