package backend.backend.dto.BusDTO;

import lombok.Builder;
import java.math.BigDecimal;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate; // Thêm import này

@Builder
public record UpdateRouteBusCategoryPriceRequest(
        Integer routeId,
        Integer busCategoryId,
        BigDecimal basePrice,
        BigDecimal promotionPrice, // Thêm trường này
        LocalDate validFrom,        // Thêm trường này
        LocalDate validTo,          // Thêm trường này
        String notes                // Thêm trường này
) {}