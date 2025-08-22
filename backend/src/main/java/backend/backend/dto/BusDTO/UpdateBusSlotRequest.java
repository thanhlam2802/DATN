package backend.backend.dto.BusDTO;

import backend.backend.entity.enumBus.BusSlotStatus;
import backend.backend.entity.enumBus.DelayReason;
import backend.backend.entity.enumBus.RecurringPattern;
import backend.backend.entity.enumBus.TripType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime; // Cho actualDepartureTime/actualArrivalTime nếu có


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBusSlotRequest {
    private Integer busId;
    private Integer routeId;
    private Integer ownerId; // ✅ THÊM: ID doanh nghiệp sở hữu - cho multi-tenant
    private LocalDate slotDate;
    private String departureTime; // String từ GraphQL
    private String arrivalTime;   // String từ GraphQL
    private OffsetDateTime actualDepartureTime;
    private OffsetDateTime actualArrivalTime;
    private BigDecimal price;
    private Integer totalSeats;
    private Integer availableSeats;
    private BusSlotStatus status; // <-- Đổi từ String sang Enum

    // --- Real-time Management Fields ---
    private DelayReason delayReason;
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
