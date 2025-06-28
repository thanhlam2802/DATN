package backend.backend.implement;

import backend.backend.dto.Hotel.AmenityDto;
import backend.backend.dao.Hotel.AmenityDAO;
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
}