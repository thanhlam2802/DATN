package backend.backend.dto;



import backend.backend.entity.Departure; 
import backend.backend.entity.Tour;      
import lombok.Data;                    

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data // This annotation automatically generates getters, setters, equals, hashCode, and toString methods
public class TourDetailDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationDays;
    private String departurePoint;
    private String destination;
    private List<DepartureDto> availableDepartures; 
    private List<String> imageUrls; 

    // Static helper method to convert a Tour entity to a TourDetailDto
    public static TourDetailDto fromEntity(Tour tour, List<Departure> departures) {
 
        TourDetailDto dto = new TourDetailDto();
        dto.setId(tour.getId());
        dto.setName(tour.getName());
        dto.setDescription(tour.getDescription());
        dto.setPrice(tour.getPrice());
        dto.setDurationDays(tour.getDurationDays());
        dto.setDeparturePoint(tour.getDeparturePoint());
        dto.setDestination(tour.getDestination());

      
        dto.setAvailableDepartures(
            departures.stream()
                      .map(DepartureDto::fromEntity)
                      .collect(Collectors.toList())
        );

        
        dto.setImageUrls(
                tour.getTourImages() != null ?
                    tour.getTourImages().stream()
                        
                        .map(tourImage -> tourImage.getImage() != null ? tourImage.getImage().getUrl() : null) // <--- Corrected!
                        .filter(java.util.Objects::nonNull) // Filter out any null URLs if an image association is missing
                        .collect(Collectors.toList()) :
                    List.of()
            );


        return dto;
    }
}