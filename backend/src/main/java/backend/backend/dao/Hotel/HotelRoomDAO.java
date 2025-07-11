package backend.backend.dao.Hotel;

import backend.backend.entity.HotelRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRoomDAO extends JpaRepository<HotelRoom, Integer> {
    List<HotelRoom> findByHotelId(Integer hotelId);
} 