package backend.backend.dto.BusDTO;

import jakarta.validation.constraints.NotNull;

public record  DeleteRouteDTO(
        @NotNull Integer id
) {}
