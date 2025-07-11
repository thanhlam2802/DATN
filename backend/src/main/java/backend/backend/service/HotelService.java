package backend.backend.service;

import backend.backend.dto.*;
import backend.backend.dto.Hotel.HotelDetailDto;
import backend.backend.dto.Hotel.HotelDto;
import backend.backend.dto.Hotel.HotelSearchRequestDto;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

public interface HotelService {
    PageDto<HotelDto> searchHotels(HotelSearchRequestDto requestDto);

    HotelDetailDto getHotelDetails(Integer hotelId, HotelSearchRequestDto requestDto);

    List<ReviewDto> getReviewsForHotel(Integer hotelId);

    HotelDetailDto createHotel(HotelDetailDto hotelDto, List<MultipartFile> images,
            Map<String, List<MultipartFile>> roomImagesMap);

    HotelDetailDto updateHotel(Integer id, HotelDetailDto hotelDto, List<MultipartFile> images,
            List<String> deleteImageUrls, Map<String, List<MultipartFile>> roomImagesMap,
            Map<String, List<String>> deleteRoomImageUrlsMap);

    void deleteHotel(Integer id);
}