package backend.backend.service;

import java.util.List;

import backend.backend.dto.DepartureDto;
import backend.backend.dto.PageDto;
import backend.backend.dto.ReviewDto;
import backend.backend.dto.TourDetailDto;
import backend.backend.dto.TourDto;
import backend.backend.dto.TourSearchRequestDto;

/**
 * Interface cho các dịch vụ liên quan đến Tour.
 * Định nghĩa các hành vi mà một lớp service về tour phải có.
 */
public interface TourService {

    /**
     * Tìm kiếm, lọc và phân trang danh sách các tour dựa trên các tiêu chí đầu vào.
     *
     * @param requestDto Đối tượng chứa tất cả các tham số tìm kiếm, lọc, sắp xếp và phân trang.
     * @return Một đối tượng PageDto chứa danh sách các TourDto và thông tin phân trang.
     */
    PageDto<TourDto> searchTours(TourSearchRequestDto requestDto);

    // Add these new methods
    TourDetailDto getTourDetailsById(Long id);
    List<ReviewDto> getReviewsForTour(Integer tourId);
    List<DepartureDto> getDeparturesForTour(Long tourId);

//     String processBooking(BookingRequest bookingRequest);

}