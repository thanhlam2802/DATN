package backend.backend.dao.Hotel;

import backend.backend.dto.Hotel.HotelDto;
import backend.backend.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface HotelDAOCustom {
    Page<HotelDto> findWithFiltersAndProjection(Specification<Hotel> spec, Pageable pageable);
}