package backend.backend.controller;

import backend.backend.dto.ReviewDto; // Sửa import
import backend.backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/tours/owner/{ownerId}")
    public ResponseEntity<List<ReviewDto>> getTourReviewsByOwner(@PathVariable Long ownerId) { // Sửa kiểu trả về
        List<ReviewDto> reviewDtos = reviewService.getTourReviewsByOwner(ownerId);
        return ResponseEntity.ok(reviewDtos);
    }
}