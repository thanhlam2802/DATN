package backend.backend.dao.Hotel;

import backend.backend.entity.HotelImage;
import backend.backend.entity.HotelImageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelImageDAO extends JpaRepository<HotelImage, HotelImageId> {
    long countByIdImageId(Integer imageId);
} 