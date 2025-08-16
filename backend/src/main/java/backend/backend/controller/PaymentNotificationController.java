package backend.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/payment-notifications")
public class PaymentNotificationController {

    @Autowired
    private AdminWebSocketController adminWebSocketController;

    @PostMapping("/success")
    public ResponseEntity<String> notifyPaymentSuccess(@RequestBody PaymentSuccessRequest request) {
        try {
            adminWebSocketController.sendPaymentNotification(
                request.getOrderId(),
                request.getAmount().toString()
            );
            return ResponseEntity.ok("Payment notification sent successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to send payment notification: " + e.getMessage());
        }
    }

    public static class PaymentSuccessRequest {
        private String orderId;
        private BigDecimal amount;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }
} 