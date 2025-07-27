package backend.backend.dao.Hotel;

import backend.backend.entity.HotelRoomImage;
import backend.backend.entity.HotelRoomImageId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRoomImageDAO extends JpaRepository<HotelRoomImage, HotelRoomImageId> {
    List<HotelRoomImage> findByIdImageId(Integer imageId);
    long countByIdImageId(Integer imageId);
} 