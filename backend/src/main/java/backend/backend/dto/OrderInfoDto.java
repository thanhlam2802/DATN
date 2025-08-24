package backend.backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderInfoDto {
    private Integer id;
    private BigDecimal amount;
    private String customer;
    private String status;
    private String service;
}
