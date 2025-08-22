package backend.backend.dto.BusDTO;

import backend.backend.entity.Location;
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
    private Location originLocation;      // ĐÃ SỬA: Chứa đối tượng Location
    private Location destinationLocation; // ĐÃ SỬA: Chứa đối tượng Location
    private Integer ownerId;              // ✅ THÊM: ID doanh nghiệp sở hữu route
    private Double distanceKm;
    private Integer estimatedDurationMinutes;
    private Instant createdAt;
    private Instant updatedAt;

    // Constructor để chuyển đổi từ Route entity sang RouteResponse DTO
    public RouteResponse(backend.backend.entity.Route route) {
        this.id = route.getId();
        this.originLocation = route.getOriginLocation();      // Gán trực tiếp đối tượng Location
        this.destinationLocation = route.getDestinationLocation(); // Gán trực tiếp đối tượng Location
        
        // ✅ THÊM: Map owner information (handle null safely)
        if (route.getOwner() != null) {
            this.ownerId = route.getOwner().getId();
        }
        
        this.distanceKm = route.getDistanceKm();
        this.estimatedDurationMinutes = route.getEstimatedDurationMinutes();
        this.createdAt = route.getCreatedAt();
        this.updatedAt = route.getUpdatedAt();
    }
}
