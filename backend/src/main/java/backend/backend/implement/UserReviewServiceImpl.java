package backend.backend.implement;


import backend.backend.dao.BookingTourDAO;
import backend.backend.dao.ReviewDAO;
import backend.backend.dao.UserDAO;
import backend.backend.dto.CompletedServiceDTO;
import backend.backend.dto.ReviewRequestDTO;
import backend.backend.entity.BookingTour;
import backend.backend.entity.Review;
import backend.backend.entity.User;
import backend.backend.repository.*; 

import backend.backend.service.UserReviewService;
import org.springframework.transaction.annotation.Transactional; // <-- THÊM DÒNG NÀY
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserReviewServiceImpl implements UserReviewService {
    
   
    private final ReviewDAO reviewRepository;
    private final UserDAO userRepository; 
   
    private final BookingTourDAO bookingTourRepository;

    @Override
    public List<CompletedServiceDTO> getCompletedServicesForUser(Integer userId) {
    	 // Lấy ngày giờ hiện tại để so sánh
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // 1. Lấy tất cả review của user và đưa vào một Set để tra cứu nhanh
        Set<String> reviewedEntities = reviewRepository.findByUserId(userId).stream()
                .map(review -> review.getEntityType() + ":" + review.getEntityId())
                .collect(Collectors.toSet());

        // 2. Tạo một danh sách tổng hợp để chứa tất cả dịch vụ
        List<CompletedServiceDTO> allCompletedServices = new ArrayList<>();

        // 3. Xử lý Hotel Bookings
      

        // 4. Xử lý Flight Bookings
    
        
        // 5. Xử lý Bus Bookings
       
        
        // 6. Xử lý Tour Bookings
        List<BookingTour> completedTours = bookingTourRepository.findCompletedBookingsByCustomerId(userId, today);
        for (BookingTour booking : completedTours) {
             allCompletedServices.add(CompletedServiceDTO.builder()
                    .bookingId(booking.getId())
                    .serviceType("TOUR")
                    .serviceName("Tour " + booking.getDeparture().getTour().getName())
                    .completionDate(booking.getDeparture().getDepartureDate())
                    .icon("fas fa-route")
                    .isReviewed(reviewedEntities.contains("TOUR:" + booking.getDeparture().getTour().getId())) // Sửa lại logic isReviewed
                    .serviceId(booking.getDeparture().getTour().getId()) // <--- THÊM DÒNG NÀY
                    .build());
        }
        // 7. Sắp xếp danh sách theo ngày hoàn thành gần nhất lên đầu
        allCompletedServices.sort(Comparator.comparing(CompletedServiceDTO::getCompletionDate).reversed());
        
        return allCompletedServices;
    }

    @Override
    @Transactional
    public Review createReview(ReviewRequestDTO reviewRequest) {
        // Tìm User từ userId, nếu không có thì báo lỗi
        User user = userRepository.findById(reviewRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + reviewRequest.getUserId()));

        // Tạo một đối tượng Review mới từ DTO
        Review newReview = new Review();
        newReview.setUser(user);
        newReview.setEntityType(reviewRequest.getEntityType());
        newReview.setEntityId(reviewRequest.getEntityId());
        newReview.setRating(reviewRequest.getRating());
        newReview.setContent(reviewRequest.getContent());
   
        return reviewRepository.save(newReview);
    }

	
}