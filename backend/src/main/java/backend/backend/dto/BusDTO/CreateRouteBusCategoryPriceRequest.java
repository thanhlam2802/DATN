package backend.backend.dto.BusDTO;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record CreateRouteBusCategoryPriceRequest(
        Integer routeId,
        Integer busCategoryId,
        BigDecimal basePrice,
        BigDecimal promotionPrice, // Thêm trường này
        LocalDate validFrom,        // Thêm trường này
        LocalDate validTo,          // Thêm trường này
        String notes                // Thêm trường này
) {}

