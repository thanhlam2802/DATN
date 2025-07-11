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
public class PaymentDto {
    private Long id;
    private BigDecimal amount;
    private String paymentMethod; // CREDIT_CARD, BANK_TRANSFER, CASH, E_WALLET
    private String paymentStatus; // PENDING, SUCCESS, FAILED, CANCELLED
    private LocalDateTime paymentDate;
    private String transactionId;
    private String currency;
    private String description;
    private String gatewayResponse;
} 