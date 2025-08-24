package backend.backend.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class BusManagementDTO {

    // ==================== OVERVIEW TAB ====================
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataQualityAlert {
        private String type;
        private String ownerName;
        private String description;
        private String suggestion;
        private String at;
    }

    // ==================== PROVIDERS TAB ====================
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProviderSummary {
        private Integer id;
        private String code;
        private String name;
        private String status;
        private Integer routesCount;
        private Integer next7dSlots;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateProviderRequest {
        private String code;
        private String name;
        private String status;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateProviderRequest {
        private String code;
        private String name;
        private String status;
    }

    // ==================== ROUTES TAB ====================
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RouteSummary {
        private Integer id;
        private String ownerCode;
        private String routeCode;
        private String origin;
        private String destination;
        private String status;
        private Integer futureSlots;
        private Double distanceKm;
        private Integer estimatedDurationMinutes;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateRouteRequest {
        private String ownerCode;
        private String routeCode;
        private String origin;
        private String destination;
        private String status;
        private Double distanceKm;
        private Integer estimatedDurationMinutes;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateRouteRequest {
        private String ownerCode;
        private String routeCode;
        private String origin;
        private String destination;
        private String status;
        private Double distanceKm;
        private Integer estimatedDurationMinutes;
    }

    // ==================== SLOTS TAB ====================
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SlotSummary {
        private Integer id;
        private String ownerCode;
        private String routeCode;
        private String departTime;
        private String busPlate;
        private Integer sold;
        private Integer total;
        private String status;
        private BigDecimal price;
        private LocalDate slotDate;
        private LocalTime departureTime;
        private LocalTime arrivalTime;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SlotDetail {
        private Integer id;
        private String ownerCode;
        private String routeCode;
        private String busPlate;
        private LocalDate slotDate;
        private LocalTime departureTime;
        private LocalTime arrivalTime;
        private BigDecimal price;
        private Integer totalSeats;
        private Integer availableSeats;
        private String status;
        private String delayReason;
        private String currentLocation;
        private String driverContactInfo;
        private List<SeatInfo> seats;
        private List<BookingInfo> bookings;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SeatInfo {
        private Integer id;
        private String seatNumber;
        private Boolean isBooked;
        private BigDecimal price;
        private String seatType;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookingInfo {
        private Integer id;
        private String bookingReference;
        private String customerName;
        private Integer numPassengers;
        private BigDecimal totalPrice;
        private String status;
        private LocalDateTime bookingDate;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BulkGenerateRequest {
        private String ownerCode;
        private String routeCode;
        private LocalDate startDate;
        private LocalDate endDate;
        private List<Integer> daysOfWeek;
        private LocalTime departTime;
        private Integer durationMinutes;
        private BigDecimal price;
        private Integer busId;
    }

    // ==================== MODERATION TAB ====================
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ModerationItem {
        private Integer id;
        private String type;
        private String ownerCode;
        private String submittedBy;
        private String submittedAt;
        private String status;
        private String content;
    }
}
