package backend.backend.dto.BusDTO;

import jakarta.validation.constraints.NotNull;

public record DeleteRouteRequest(
        @NotNull Integer id
) {}
