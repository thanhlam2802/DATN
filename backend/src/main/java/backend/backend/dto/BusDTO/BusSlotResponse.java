package backend.backend.dto.BusDTO;

import backend.backend.entity.BusSlot;

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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusSlotResponse {
    private Integer id;
    private BusResponse bus;
    private RouteResponse route;
    private LocalDate slotDate;
    private OffsetDateTime departureTime;
    private OffsetDateTime arrivalTime;
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
    private LocalDate recurringEndDate; // LocalDate cho DTO
    private Boolean autoStatusUpdate;
    private Boolean autoResetSeats;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public BusSlotResponse(BusSlot busSlot) {
        this.id = busSlot.getId();
        this.bus = Optional.ofNullable(busSlot.getBus()).map(BusResponse::new).orElse(null);
        this.route = Optional.ofNullable(busSlot.getRoute()).map(RouteResponse::new).orElse(null);

        this.slotDate = busSlot.getSlotDate();

        this.departureTime = Optional.ofNullable(busSlot.getDepartureTime())
                .map(lt -> lt.atDate(busSlot.getSlotDate()).atOffset(ZoneOffset.ofHours(7)))
                .orElse(null);
        this.arrivalTime = Optional.ofNullable(busSlot.getArrivalTime())
                .map(lt -> lt.atDate(busSlot.getSlotDate()).atOffset(ZoneOffset.ofHours(7)))
                .orElse(null);

        this.actualDepartureTime = Optional.ofNullable(busSlot.getActualDepartureTime())
                .map(ldt -> ldt.atOffset(ZoneOffset.ofHours(7)))
                .orElse(null);
        this.actualArrivalTime = Optional.ofNullable(busSlot.getActualArrivalTime())
                .map(ldt -> ldt.atOffset(ZoneOffset.ofHours(7)))
                .orElse(null);

        this.price = busSlot.getPrice();
        this.totalSeats = busSlot.getTotalSeats();
        this.availableSeats = busSlot.getAvailableSeats();
        this.status = busSlot.getStatus(); // <-- Gán trực tiếp enum

        // --- Real-time Management Fields ---
        this.delayReason = busSlot.getDelayReason();
        this.currentLocation = busSlot.getCurrentLocation();
        this.driverContactInfo = busSlot.getDriverContactInfo();
        this.allowManualOverride = busSlot.getAllowManualOverride();
        this.timeToleranceMinutes = busSlot.getTimeToleranceMinutes();

        // --- Auto-management Fields ---
        this.tripType = busSlot.getTripType();
        this.recurringPattern = busSlot.getRecurringPattern();
        this.recurringEndDate = busSlot.getRecurringEndDate();
        this.autoStatusUpdate = busSlot.getAutoStatusUpdate();
        this.autoResetSeats = busSlot.getAutoResetSeats();

        this.createdAt = Optional.ofNullable(busSlot.getCreatedAt())
                .map(ldt -> ldt.atOffset(ZoneOffset.ofHours(7)))
                .orElse(null);
        this.updatedAt = Optional.ofNullable(busSlot.getUpdatedAt())
                .map(ldt -> ldt.atOffset(ZoneOffset.ofHours(7)))
                .orElse(null);
    }
}
