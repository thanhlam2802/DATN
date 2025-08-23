package backend.backend.service;

import java.util.List;

import backend.backend.dto.Hotel.AmenityDto;

public interface AmenityService {
    List<AmenityDto> getAllAmenities();
    AmenityDto createAmenity(AmenityDto amenityDto);
    AmenityDto updateAmenity(AmenityDto amenityDto);
    void deleteAmenity(Integer id);
}
