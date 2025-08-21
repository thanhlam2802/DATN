package backend.backend.dto.BusDTO;

import java.time.Instant;

public record LocationRespone (
    Integer id,
    String name,
    String address,
    String city,
    String district,
    String province,
    String country,
    String postalCode,
    Instant createdAt,
    Instant updatedAt
){}
