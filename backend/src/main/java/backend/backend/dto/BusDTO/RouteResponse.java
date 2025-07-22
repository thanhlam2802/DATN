package backend.backend.dto.BusDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResponse {
    private Integer id;
    private String origin;
    private String destination;
    private Double distanceKm;
    private Integer estimatedDurationMinutes;
    private Instant createdAt; // <-- Thay đổi thành Instant
    private Instant updatedAt; // <-- Thay đổi thành Instant

    // Constructor để chuyển đổi từ Route entity sang RouteResponse DTO
    public RouteResponse(backend.backend.entity.Route route) {
        this.id = route.getId();
        this.origin = route.getOrigin();
        this.destination = route.getDestination();
        this.distanceKm = route.getDistanceKm();
        this.estimatedDurationMinutes = route.getEstimatedDurationMinutes();
        // Gán trực tiếp Instant từ entity
        this.createdAt = route.getCreatedAt();
        this.updatedAt = route.getUpdatedAt();
    }
}
