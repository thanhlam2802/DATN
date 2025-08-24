package backend.backend.implement;

import backend.backend.dto.Hotel.AmenityDto;
import backend.backend.dao.Hotel.AmenityDAO;
import backend.backend.entity.Amenity;
import backend.backend.service.AmenityService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AmenityServiceImpl implements AmenityService {

    @Autowired
    private AmenityDAO amenityDAO;

    @Override
    @Transactional(readOnly = true)
    public List<AmenityDto> getAllAmenities() {
        return amenityDAO.findAll().stream()
                .map(AmenityDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AmenityDto createAmenity(AmenityDto amenityDto) {
        Amenity amenity = new Amenity();
        amenity.setName(amenityDto.getName());
        amenity.setIcon(amenityDto.getIcon());
        amenity.setStatus(amenityDto.getStatus() != null ? amenityDto.getStatus() : "ACTIVE");
        
        Amenity savedAmenity = amenityDAO.save(amenity);
        return AmenityDto.fromEntity(savedAmenity);
    }

    @Override
    @Transactional
    public AmenityDto updateAmenity(AmenityDto amenityDto) {
        Amenity existingAmenity = amenityDAO.findById(amenityDto.getId())
                .orElseThrow(() -> new RuntimeException("Tiện ích không tồn tại"));
        
        existingAmenity.setName(amenityDto.getName());
        existingAmenity.setIcon(amenityDto.getIcon());
        if (amenityDto.getStatus() != null) {
            existingAmenity.setStatus(amenityDto.getStatus());
        }
        
        Amenity updatedAmenity = amenityDAO.save(existingAmenity);
        return AmenityDto.fromEntity(updatedAmenity);
    }

    @Override
    @Transactional
    public void deleteAmenity(Integer id) {
        if (!amenityDAO.existsById(id)) {
            throw new RuntimeException("Tiện ích không tồn tại");
        }
        amenityDAO.deleteById(id);
    }
}