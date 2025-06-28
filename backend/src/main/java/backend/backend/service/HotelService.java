package backend.backend.service;

import backend.backend.dto.*;
import backend.backend.dto.Hotel.HotelDetailDto;
import backend.backend.dto.Hotel.HotelDto;
import backend.backend.dto.Hotel.HotelSearchRequestDto;

import java.util.List;

public interface HotelService {
    PageDto<HotelDto> searchHotels(HotelSearchRequestDto requestDto);

    HotelDetailDto getHotelDetails(Integer hotelId, HotelSearchRequestDto requestDto);

    List<ReviewDto> getReviewsForHotel(Integer hotelId);
}