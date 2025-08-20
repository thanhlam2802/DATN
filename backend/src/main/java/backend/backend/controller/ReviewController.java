package backend.backend.controller;

import backend.backend.dto.CompletedServiceDTO;
import backend.backend.dto.ReviewDto;
import backend.backend.dto.ReviewRequestDTO;
import backend.backend.entity.Review;
import backend.backend.service.ReviewService;
import backend.backend.service.UserReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// Import HttpStatus của Spring
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserReviewService userReviewService;

    @GetMapping("/tours/owner/{ownerId}")
    public ResponseEntity<List<ReviewDto>> getTourReviewsByOwner(@PathVariable Long ownerId) {
        List<ReviewDto> reviewDtos = reviewService.getTourReviewsByOwner(ownerId);
        return ResponseEntity.ok(reviewDtos);
    }
    
    /**
     * API để lấy danh sách các dịch vụ đã hoàn thành mà người dùng có thể đánh giá.
     */
    @GetMapping("/user/{userId}/completed-services")
    public ResponseEntity<List<CompletedServiceDTO>> getServicesToReview(@PathVariable Integer userId) {
        List<CompletedServiceDTO> services = userReviewService.getCompletedServicesForUser(userId);
        return ResponseEntity.ok(services);
    }

    /**
     * API để tạo một đánh giá mới.
     */
    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@Valid @RequestBody ReviewRequestDTO reviewRequest) {
        // 1. Gọi service để tạo và lưu review vào database
        Review savedReview = userReviewService.createReview(reviewRequest);

        // 2. Chuyển đổi entity Review đã lưu thành ReviewDto
        ReviewDto responseDto = ReviewDto.fromEntity(savedReview);
        
        // 3. Trả về response với status 201 (Created) của Spring
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}