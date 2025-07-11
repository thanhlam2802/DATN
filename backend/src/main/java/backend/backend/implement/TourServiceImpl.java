package backend.backend.implement;

import backend.backend.dao.DepartureDAO;
import backend.backend.dao.ReviewDAO;

import backend.backend.dao.TourDAO;
import backend.backend.dao.TourScheduleDAO;
import backend.backend.dto.DepartureDto;
import backend.backend.dto.ItineraryDayDto;
import backend.backend.dto.PageDto;
import backend.backend.dto.ReviewDto;
import backend.backend.dto.TourDetailDto;
import backend.backend.dto.TourDto;
import backend.backend.dto.TourSearchRequestDto;
import backend.backend.entity.Departure;
import backend.backend.entity.Review;
import backend.backend.entity.Tour;
import backend.backend.entity.TourSchedule;
import backend.backend.service.TourService;
import backend.backend.specification.TourSpecifications;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TourServiceImpl implements TourService {

	  @Autowired
	  TourDAO tourRepository;
	  @Autowired
	  ReviewDAO reviewRepository;
	  @Autowired
	  DepartureDAO departureRepository;
	  @Autowired
	    private TourScheduleDAO tourScheduleDAO;
	  @Override
	    @Transactional(readOnly = true)
	    public List<ItineraryDayDto> getStructuredItinerary(Long tourId) {
	    
	        List<TourSchedule> days = tourScheduleDAO.findByTourIdOrderByDayNumberAsc(tourId);

	        return days.stream()
	                   .map(ItineraryDayDto::fromEntity)
	                   .collect(Collectors.toList());
	    }
    @Override
    @Transactional(readOnly = true)
    public PageDto<TourDto> searchTours(TourSearchRequestDto requestDto) {
        // 1. Tạo Specification để xây dựng câu lệnh WHERE động
        Specification<Tour> spec = TourSpecifications.from(requestDto);

        // 2. Tạo Pageable để phân trang và sắp xếp
        Pageable pageable = createPageable(requestDto);

        // 3. Gọi Repository. Spring Data JPA sẽ tự tạo câu lệnh SQL tối ưu
        Page<Tour> tourPage = tourRepository.findAll(spec, pageable);

        // 4. Chuyển đổi Page<Tour> (Entity) sang PageDto<TourDto> (DTO)
        // Việc tính toán rating chỉ xảy ra cho các tour trên trang hiện tại -> hiệu quả hơn nhiều
        return new PageDto<>(
                tourPage.getContent().stream().map(this::toDto).toList(),
                tourPage.getNumber(),
                tourPage.getSize(),
                tourPage.getTotalElements(),
                tourPage.getTotalPages()
        );
    }

    /**
     * Phương thức helper để tạo đối tượng Pageable từ request DTO
     */
    private Pageable createPageable(TourSearchRequestDto requestDto) {
        String sortBy = requestDto.getSortBy();
        Sort sort;

        switch (sortBy) {
            case "price-asc":
                sort = Sort.by("price").ascending();
                break;
            case "price-desc":
                sort = Sort.by("price").descending();
                break;
            case "rating":
            case "popular":
            default:
                sort = Sort.by("createdAt").descending();
                break;
        }
        return PageRequest.of(requestDto.getPage(), requestDto.getSize(), sort);
    }
    
    /**
     * Phương thức này giờ nhận Tour Entity và tính toán các giá trị liên quan.
     */
    private TourDto toDto(Tour tour) {
        String imageUrl = (tour.getTourImages() != null && !tour.getTourImages().isEmpty())
                        ? tour.getTourImages().get(0).getImage().getUrl()
                        : null;

        // Các lời gọi này giờ sẽ thực thi các câu lệnh SQL đã được cập nhật
        Double rating = reviewRepository.getAverageRatingByTourId(tour.getId());
        Integer reviewCount = reviewRepository.countByTourId(tour.getId());

        return new TourDto(
                tour.getId().longValue(),
                tour.getName(),
                imageUrl,
                tour.getPrice(),
                rating,
                reviewCount,
                tour.getDestination()
        );
    }
    @Override
    @Transactional(readOnly = true)
    public TourDetailDto getTourDetailsById(Long id) {
        Optional<Tour> tourOptional = tourRepository.findById(id);
        if (tourOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tour not found with ID: " + id);
        }
        Tour tour = tourOptional.get();

        List<Departure> departures = departureRepository.findByTourId(id);

        // Convert entity to DTO
        return TourDetailDto.fromEntity(tour, departures);
    }

    @Override
    public List<ReviewDto> getReviewsForTour(Integer tourId) {
    
        List<Review> reviews = reviewRepository.findByEntityTypeAndEntityId("Tour", tourId);
        return reviews.stream()
                      .map(ReviewDto::fromEntity)
                      .collect(Collectors.toList());
    }

    @Override
    public List<DepartureDto> getDeparturesForTour(Long tourId) {
        List<Departure> departures = departureRepository.findByTourId(tourId);
        return departures.stream()
                      .map(DepartureDto::fromEntity)
                      .collect(Collectors.toList());
    }
}