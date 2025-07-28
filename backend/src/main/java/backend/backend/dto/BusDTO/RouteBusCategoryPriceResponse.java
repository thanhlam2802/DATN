package backend.backend.dto.BusDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteBusCategoryPriceResponse {
    private Integer id;
    private RouteResponse route;
    private BusCategoryResponse busCategory;
    private BigDecimal basePrice;
    private BigDecimal promotionPrice; // Thêm trường này
    private LocalDate validFrom;        // Thêm trường này
    private LocalDate validTo;          // Thêm trường này
    private String notes;               // Thêm trường này
    private Instant createdAt;
    private Instant updatedAt;
}
