package backend.backend.dto.BusDTO;

import jakarta.validation.constraints.Size;

public record UpdateRouteRequest(
        @Size(max = 100) String origin,
        @Size(max = 100) String destination,
        Double distanceKm,
        Integer estimatedDurationMinutes
) {}

