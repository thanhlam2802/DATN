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

            Integer hotelId = (Integer) bookingNotification.get("hotelId");
            if (hotelId != null) {
                messagingTemplate.convertAndSend(
                        "/topic/hotels/" + hotelId + "/booking-notifications",
                        bookingNotification);
            }

            return bookingNotification;
        } catch (Exception e) {
            logger.error("Error handling hotel room booking notification", e);
            return Map.of("error", "Failed to process booking notification");
        }
    }

    @MessageMapping("/hotel/room-update")
    @SendTo("/topic/hotels/{hotelId}/room-updates")
    public Map<String, Object> handleRoomUpdate(Map<String, Object> roomUpdate) {
        try {
            logger.info("Received room update notification: {}", roomUpdate);

            Integer hotelId = (Integer) roomUpdate.get("hotelId");
            if (hotelId != null) {
                messagingTemplate.convertAndSend(
                        "/topic/hotels/" + hotelId + "/room-updates",
                        roomUpdate);
            }

            return roomUpdate;
        } catch (Exception e) {
            logger.error("Error handling room update notification", e);
            return Map.of("error", "Failed to process room update");
        }
    }

    @MessageMapping("/hotel/viewer-notification")
    public void handleViewerNotification(Map<String, Object> viewerNotification) {
        try {
            logger.info("Received viewer notification: {}", viewerNotification);

            Integer hotelId = (Integer) viewerNotification.get("hotelId");
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