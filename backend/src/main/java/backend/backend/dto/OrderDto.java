package backend.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDto {
    private Integer id;
    private Integer userId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime payDate;
    private Integer voucherId; 
    private Integer destinationId;
    private LocalDateTime createdAt;
    private Integer ticketDetailId;
    private String mainProduct;
    private LocalDateTime expiresAt;

}