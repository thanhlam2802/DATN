package backend.backend.dto.BusDTO;

import lombok.Builder;

@Builder
public record CreateBusAmenityRequest(
        String name,
                                      String description
){}
