package backend.backend.implement;

import backend.backend.dao.ReviewDAO;
import backend.backend.dao.Hotel.HotelDAO;
import backend.backend.dao.Hotel.HotelRoomVariantDAO;
import backend.backend.dto.*;
import backend.backend.dto.Hotel.HotelDetailDto;
import backend.backend.dto.Hotel.HotelDto;
import backend.backend.dto.Hotel.HotelSearchRequestDto;
import backend.backend.entity.Hotel;
import backend.backend.entity.Review;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.service.HotelService;
import backend.backend.specification.HotelSpecifications;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HotelServiceImpl implements HotelService {

    @Autowired
    private HotelDAO hotelDAO;
    @Autowired
    private ReviewDAO reviewDAO;
    @Autowired
    private HotelRoomVariantDAO hotelRoomVariantDAO;

    @Override
    @Transactional(readOnly = true)
    public PageDto<HotelDto> searchHotels(HotelSearchRequestDto requestDto) {
        Specification<Hotel> spec = HotelSpecifications.from(requestDto);
        Pageable pageable = createPageable(requestDto);

        Page<HotelDto> hotelDtoPage = hotelDAO.findWithFiltersAndProjection(spec, pageable);

        return new PageDto<>(
                hotelDtoPage.getContent(),
                hotelDtoPage.getNumber(),
                hotelDtoPage.getSize(),
                hotelDtoPage.getTotalElements(),
                hotelDtoPage.getTotalPages());
    }

    @Override
    @Transactional(readOnly = true)
    public HotelDetailDto getHotelDetails(Integer hotelId, HotelSearchRequestDto requestDto) {
        Hotel hotel = hotelDAO.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách sạn với ID: " + hotelId));

        Double rating = reviewDAO.getAverageRatingByEntity("Hotel", hotelId);
        Integer reviewCount = reviewDAO.countByEntity("Hotel", hotelId);

        Set<Integer> bookedVariantIds = getBookedVariantIds(hotelId, requestDto);

        return HotelDetailDto.fromEntity(
                hotel,
                rating != null ? rating : 0.0,
                reviewCount != null ? reviewCount : 0,
                bookedVariantIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsForHotel(Integer hotelId) {
        if (!hotelDAO.existsById(hotelId)) {
            throw new ResourceNotFoundException("Không tìm thấy khách sạn với ID: " + hotelId);
        }
        List<Review> reviews = reviewDAO.findByEntityTypeAndEntityId("Hotel", hotelId);
        return reviews.stream().map(ReviewDto::fromEntity).collect(Collectors.toList());
    }

    private Set<Integer> getBookedVariantIds(Integer hotelId, HotelSearchRequestDto requestDto) {
        if (requestDto.getCheckInDate() != null && requestDto.getCheckOutDate() != null) {
            return hotelRoomVariantDAO.findBookedVariantIdsByHotelAndDateRange(
                    hotelId, requestDto.getCheckInDate(), requestDto.getCheckOutDate());
        }
        return Collections.emptySet();
    }

    private Pageable createPageable(HotelSearchRequestDto requestDto) {
        String sortBy = requestDto.getSortBy();
        Sort sort;
        switch (sortBy != null ? sortBy.toLowerCase() : "default") {
            case "priceasc":
                sort = Sort.by("startingPrice").ascending();
                break;
            case "pricedesc":
                sort = Sort.by("startingPrice").descending();
                break;
            case "ratingdesc":
                sort = Sort.by("rating").descending();
                break;
            case "popular":
            case "default":
            default:
                sort = Sort.by("rating").descending().and(Sort.by("createdAt").descending());
                break;
        }
        return PageRequest.of(requestDto.getPage(), requestDto.getSize(), sort);
    }
}