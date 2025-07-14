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
public class FlightDto {
    private Integer id;
    private java.time.LocalDateTime arrivalTime;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime departureTime;
    private String flightNumber;
    private String name;
    private java.time.LocalDateTime updatedAt;
    private Integer categoryId;
    private FlightCategoryDto category;
    private Integer ownerId;
    private AirportDto arrivalAirport;
    private AirportDto departureAirport;

    private AirlineDto airline;
    // Images (primary image for list view)
    private List<FlightImageDto> images;
    
    // Flight slots with detailed pricing
    private List<FlightSlotDto> flightSlots;
    
    // Summary pricing information (for quick display)
    private Double minPrice;
    private Double maxPrice;
    private Integer totalAvailableSeats;
    private String availableSeatClasses;
    private Integer carryOnLuggage;
} 