package backend.backend.dao.Hotel;

import backend.backend.entity.HotelRoomVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Repository
public interface HotelRoomVariantDAO extends JpaRepository<HotelRoomVariant, Integer> {

    @Query("SELECT MIN(v.price) FROM HotelRoomVariant v WHERE v.room.hotel.id = :hotelId")
    Optional<BigDecimal> findMinPriceByHotelId(@Param("hotelId") Integer hotelId);

    @Query("SELECT b.roomVariant.id FROM HotelBooking b WHERE b.roomVariant.room.hotel.id = :hotelId AND " +
            "(b.checkInDate < :checkOut AND b.checkOutDate > :checkIn)")
    Set<Integer> findBookedVariantIdsByHotelAndDateRange(
            @Param("hotelId") Integer hotelId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut);
}