package backend.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v1/review-notifications")
public class ReviewNotificationController {

    @Autowired
    private AdminWebSocketController adminWebSocketController;

    @PostMapping("/new")
    public ResponseEntity<String> notifyNewReview(@RequestBody NewReviewRequest request) {
        try {
            adminWebSocketController.sendReviewNotification(
                request.getHotelName(),
                request.getRating()
            );
            return ResponseEntity.ok("Review notification sent successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to send review notification: " + e.getMessage());
        }
    }

    public static class NewReviewRequest {
        private String hotelName;
        private Integer rating;

        public String getHotelName() {
            return hotelName;
        }

        public void setHotelName(String hotelName) {
            this.hotelName = hotelName;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }
    }
} 