package backend.backend.dto.BusDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateRouteRequest(
        // CHỈ SỬ DỤNG @NotNull cho đối tượng CreateLocationInput
        @NotNull(message = "Origin location details must not be null")
        CreateLocationInput originLocationDetails,

        // CHỈ SỬ DỤNG @NotNull cho đối tượng CreateLocationInput
        @NotNull(message = "Destination location details must not be null")
        CreateLocationInput destinationLocationDetails,

        // ✅ THÊM MỚI: Route phải thuộc về doanh nghiệp
        @NotNull(message = "Owner ID must not be null")
        Integer ownerId,

        Double distanceKm,
        Integer estimatedDurationMinutes
) {}
