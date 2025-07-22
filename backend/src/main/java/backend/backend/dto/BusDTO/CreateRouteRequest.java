package backend.backend.dto.BusDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateRouteRequest(
        @Size(max = 100) @NotNull String origin,
        @Size(max = 100) @NotNull String destination,
        Double distanceKm,
        Integer estimatedDurationMinutes
) {}
