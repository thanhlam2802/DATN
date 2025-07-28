package backend.backend.dto.BusDTO;

import jakarta.validation.constraints.Size;

public record UpdateRouteRequest(
        // ĐÃ SỬA: Cho phép cập nhật thông tin location bằng CreateLocationInput (nếu null thì không cập nhật)
        CreateLocationInput originLocationDetails,
        CreateLocationInput destinationLocationDetails,
        Double distanceKm,
        Integer estimatedDurationMinutes
) {}

