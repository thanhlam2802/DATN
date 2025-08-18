package backend.backend.dto.BusDTO;

import backend.backend.entity.enumBus.RecurringPattern;
import backend.backend.entity.enumBus.TripType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBusSlotRequest {
    private Integer busId;
    private Integer routeId;
    private Integer ownerId; // ✅ THÊM: ID doanh nghiệp sở hữu slot
    private LocalDate slotDate;
    private String departureTime; // String từ GraphQL
    private String arrivalTime;   // String từ GraphQL
    private BigDecimal price;
    private Integer totalSeats;

    // --- Real-time Management Fields (có thể có giá trị mặc định ở entity) ---
    private String currentLocation;
    private String driverContactInfo;
    private Boolean allowManualOverride;
    private Integer timeToleranceMinutes;

    // --- Auto-management Fields ---
    private TripType tripType;
    private RecurringPattern recurringPattern;
    private LocalDate recurringEndDate; // LocalDate cho input
    private Boolean autoStatusUpdate;
    private Boolean autoResetSeats;
}
