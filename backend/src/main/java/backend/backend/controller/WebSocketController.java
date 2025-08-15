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

@Controller
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @MessageMapping("/hotel/room-booking")
    @SendTo("/topic/hotels/{hotelId}/booking-notifications")
    public Map<String, Object> handleHotelRoomBooking(Map<String, Object> bookingNotification) {
        try {
            logger.info("Received hotel room booking notification: {}", bookingNotification);

            Object hotelIdObj = bookingNotification.get("hotelId");
            Integer hotelId = null;
            
            if (hotelIdObj instanceof String) {
                hotelId = Integer.valueOf((String) hotelIdObj);
            } else if (hotelIdObj instanceof Integer) {
                hotelId = (Integer) hotelIdObj;
            }
            
            if (hotelId != null) {
                messagingTemplate.convertAndSend(
                        "/topic/hotels/" + hotelId + "/booking-notifications",
                        bookingNotification);
            }

            return bookingNotification;
        } catch (Exception e) {
            logger.error("Error handling hotel room booking notification", e);
            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "Failed to process booking notification");
            return errorResponse;
        }
    }

    @MessageMapping("/hotel/room-update")
    @SendTo("/topic/hotels/{hotelId}/room-updates")
    public Map<String, Object> handleRoomUpdate(Map<String, Object> roomUpdate) {
        try {
            logger.info("Received room update notification: {}", roomUpdate);

            Object hotelIdObj = roomUpdate.get("hotelId");
            Integer hotelId = null;
            
            if (hotelIdObj instanceof String) {
                hotelId = Integer.valueOf((String) hotelIdObj);
            } else if (hotelIdObj instanceof Integer) {
                hotelId = (Integer) hotelIdObj;
            }
            
            if (hotelId != null) {
                messagingTemplate.convertAndSend(
                        "/topic/hotels/" + hotelId + "/room-updates",
                        roomUpdate);
            }

            return roomUpdate;
        } catch (Exception e) {
            logger.error("Error handling room update notification", e);
            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "Failed to process room update");
            return errorResponse;
        }
    }

    @MessageMapping("/hotel/viewer-notification")
    public void handleViewerNotification(Map<String, Object> viewerNotification) {
        try {
            logger.info("Received viewer notification: {}", viewerNotification);

            Object hotelIdObj = viewerNotification.get("hotelId");
            Integer hotelId = null;
            
            if (hotelIdObj instanceof String) {
                hotelId = Integer.valueOf((String) hotelIdObj);
            } else if (hotelIdObj instanceof Integer) {
                hotelId = (Integer) hotelIdObj;
            }
            
            if (hotelId != null) {
                messagingTemplate.convertAndSend(
                        "/topic/hotels/" + hotelId + "/viewer-notifications",
                        viewerNotification);
            }
        } catch (Exception e) {
            logger.error("Error handling viewer notification", e);
        }
    }

}