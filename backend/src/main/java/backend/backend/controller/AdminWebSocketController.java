package backend.backend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;

@Controller
public class AdminWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(AdminWebSocketController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @MessageMapping("/admin/hotel/status")
    @SendTo("/topic/admin/hotel/status")
    public Map<String, Object> handleAdminStatus(Map<String, Object> statusUpdate) {
        try {
            logger.info("Received admin status update: {}", statusUpdate);
            return statusUpdate;
        } catch (Exception e) {
            logger.error("Error handling admin status update", e);
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("error", "Failed to process admin status update");
            return errorMap;
        }
    }

    public void sendBookingNotification(String customerName, String hotelName, Short rooms) {
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("customerName", customerName);
            notification.put("hotelName", hotelName);
            notification.put("rooms", rooms);
            notification.put("timestamp", System.currentTimeMillis());
            
            messagingTemplate.convertAndSend("/topic/admin/hotel/bookings", notification);
            logger.info("Sent booking notification to admin: {}", notification);
        } catch (Exception e) {
            logger.error("Error sending booking notification to admin", e);
        }
    }

    public void sendPaymentNotification(String orderId, String amount) {
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("orderId", orderId);
            notification.put("amount", amount);
            notification.put("timestamp", System.currentTimeMillis());
            
            messagingTemplate.convertAndSend("/topic/admin/hotel/payments", notification);
            logger.info("Sent payment notification to admin: {}", notification);
        } catch (Exception e) {
            logger.error("Error sending payment notification to admin", e);
        }
    }

    public void sendReviewNotification(String hotelName, Integer rating) {
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("hotelName", hotelName);
            notification.put("rating", rating);
            notification.put("timestamp", System.currentTimeMillis());
            
            messagingTemplate.convertAndSend("/topic/admin/hotel/reviews", notification);
            logger.info("Sent review notification to admin: {}", notification);
        } catch (Exception e) {
            logger.error("Error sending review notification to admin", e);
        }
    }

    public void sendCancellationNotification(String orderId) {
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("orderId", orderId);
            notification.put("timestamp", System.currentTimeMillis());
            notification.put("type", "HOTEL_CANCELLATION");
            
            messagingTemplate.convertAndSend("/topic/admin/hotel/cancellations", notification);
            logger.info("Sent cancellation notification to admin: {}", notification);
        } catch (Exception e) {
            logger.error("Error sending cancellation notification to admin", e);
        }
    }

    public void sendExpiredOrderNotification(String orderId) {
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("orderId", orderId);
            notification.put("timestamp", System.currentTimeMillis());
            notification.put("type", "ORDER_EXPIRED");
            
            messagingTemplate.convertAndSend("/topic/admin/hotel/cancellations", notification);
            logger.info("Sent expired order notification to admin: {}", notification);
        } catch (Exception e) {
            logger.error("Error sending expired order notification to admin", e);
        }
    }

    public void sendSystemNotification(String title, String message) {
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("title", title);
            notification.put("message", message);
            notification.put("timestamp", System.currentTimeMillis());
            
            messagingTemplate.convertAndSend("/topic/admin/hotel/system", notification);
            logger.info("Sent system notification to admin: {}", notification);
        } catch (Exception e) {
            logger.error("Error sending system notification to admin", e);
        }
    }
} 