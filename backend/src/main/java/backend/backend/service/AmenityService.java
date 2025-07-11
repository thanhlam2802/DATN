package backend.backend.service;

import java.util.List;

import backend.backend.dto.Hotel.AmenityDto;

public interface AmenityService {
    List<AmenityDto> getAllAmenities();
}
