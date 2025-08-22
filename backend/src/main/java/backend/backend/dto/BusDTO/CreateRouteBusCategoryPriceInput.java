package backend.backend.dto.BusDTO;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreateRouteBusCategoryPriceInput(
        Integer routeId,
        Integer busCategoryId,
        BigDecimal basePrice,
        BigDecimal promotionPrice,
        String validFrom,        // String format (YYYY-MM-DD) to match GraphQL schema
        String validTo,          // String format (YYYY-MM-DD) to match GraphQL schema
        String notes
) {} 