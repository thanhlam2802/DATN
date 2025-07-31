package backend.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v1/hotel-notifications")
public class HotelNotificationController {

    @Autowired
    private AdminWebSocketController adminWebSocketController;

    @PostMapping("/cancellation")
    public ResponseEntity<String> notifyHotelCancellation(@RequestBody HotelCancellationRequest request) {
        try {
            System.out.println("Received hotel cancellation notification - OrderId: " + request.getOrderId() + ", BookingId: " + request.getBookingId());
            adminWebSocketController.sendCancellationNotification(request.getOrderId().toString());
            System.out.println("Hotel cancellation notification sent successfully");
            return ResponseEntity.ok("Hotel cancellation notification sent successfully");
        } catch (Exception e) {
            System.err.println("Failed to send hotel cancellation notification: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to send hotel cancellation notification: " + e.getMessage());
        }
    }

    public static class HotelCancellationRequest {
        private Long orderId;
        private Long bookingId;

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public Long getBookingId() {
            return bookingId;
        }

        public void setBookingId(Long bookingId) {
            this.bookingId = bookingId;
        }
    }
} 