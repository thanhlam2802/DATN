package backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightDetailDto {
    private Long id;
    private String flightNumber;
    private String name;
    private String departureAirport;
    private String arrivalAirport;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String flightCategory;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Flight slots with detailed pricing
    private List<FlightSlotDto> flightSlots;
    
    // Images
    private List<FlightImageDto> images;
    
    // Reviews and ratings
    private List<ReviewDto> reviews;
    private Double averageRating;
    private Integer totalReviews;
    
    // Statistics
    private Integer totalBookings;
    private Double occupancyRate;

    private Integer ownerId;
    private Integer arrivalAirportId;
    private Integer departureAirportId;

    private AirlineDto airline;
} 