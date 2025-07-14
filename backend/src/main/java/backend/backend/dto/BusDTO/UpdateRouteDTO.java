package backend.backend.dto.BusDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateRouteDTO(
        @NotNull Integer id,
        @Size(max = 100) String origin,
        @Size(max = 100) String destination,
        Double distanceKm,
        Integer estimatedDurationMinutes
) {}

