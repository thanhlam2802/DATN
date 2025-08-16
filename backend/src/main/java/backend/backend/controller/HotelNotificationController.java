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

    @PostMapping("/created")
    public ResponseEntity<String> notifyHotelCreated(@RequestBody HotelActionRequest request) {
        try {
            System.out.println("Received hotel created notification - HotelName: " + request.getHotelName() + ", UserName: " + request.getUserName());
            adminWebSocketController.sendHotelCreatedNotification(request.getHotelName(), request.getUserName());
            System.out.println("Hotel created notification sent successfully");
            return ResponseEntity.ok("Hotel created notification sent successfully");
        } catch (Exception e) {
            System.err.println("Failed to send hotel created notification: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to send hotel created notification: " + e.getMessage());
        }
    }

    @PostMapping("/updated")
    public ResponseEntity<String> notifyHotelUpdated(@RequestBody HotelActionRequest request) {
        try {
            System.out.println("Received hotel updated notification - HotelName: " + request.getHotelName() + ", UserName: " + request.getUserName());
            adminWebSocketController.sendHotelUpdatedNotification(request.getHotelName(), request.getUserName());
            System.out.println("Hotel updated notification sent successfully");
            return ResponseEntity.ok("Hotel updated notification sent successfully");
        } catch (Exception e) {
            System.err.println("Failed to send hotel updated notification: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to send hotel updated notification: " + e.getMessage());
        }
    }

    @PostMapping("/deleted")
    public ResponseEntity<String> notifyHotelDeleted(@RequestBody HotelActionRequest request) {
        try {
            System.out.println("Received hotel deleted notification - HotelName: " + request.getHotelName() + ", UserName: " + request.getUserName());
            adminWebSocketController.sendHotelDeletedNotification(request.getHotelName(), request.getUserName());
            System.out.println("Hotel deleted notification sent successfully");
            return ResponseEntity.ok("Hotel deleted notification sent successfully");
        } catch (Exception e) {
            System.err.println("Failed to send hotel deleted notification: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to send hotel deleted notification: " + e.getMessage());
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

    public static class HotelActionRequest {
        private String hotelName;
        private String userName;

        public String getHotelName() {
            return hotelName;
        }

        public void setHotelName(String hotelName) {
            this.hotelName = hotelName;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
} 