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

            Integer hotelId = (Integer) roomUpdate.get("hotelId");
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

    // ✅ THÊM: Bus WebSocket endpoints
    @MessageMapping("/bus/status-update")
    public void handleBusStatusUpdate(Map<String, Object> statusUpdate) {
        try {
            logger.info("🚌 [WebSocket] Received bus status update: {}", statusUpdate);

            Integer busSlotId = (Integer) statusUpdate.get("busSlotId");
            Integer ownerId = (Integer) statusUpdate.get("ownerId");
            
            if (busSlotId != null && ownerId != null) {
                // Send to specific owner's dashboard
                messagingTemplate.convertAndSend(
                        "/topic/bus/owner/" + ownerId + "/status-updates",
                        statusUpdate);
                
                // Send to public bus tracking (for passengers)
                messagingTemplate.convertAndSend(
                        "/topic/bus/slots/" + busSlotId + "/status",
                        statusUpdate);
                
                logger.info("🚌 [WebSocket] Sent status update for BusSlot ID: {} to owner: {}", 
                           busSlotId, ownerId);
            }
        } catch (Exception e) {
            logger.error("❌ [WebSocket] Error handling bus status update", e);
        }
    }

    @MessageMapping("/bus/owner-connect")
    public void handleBusOwnerConnect(Map<String, Object> connectionInfo) {
        try {
            logger.info("🚌 [WebSocket] Bus owner connected: {}", connectionInfo);
            
            Integer ownerId = (Integer) connectionInfo.get("ownerId");
            if (ownerId != null) {
                Map<String, Object> welcomeMessage = new java.util.HashMap<>();
                welcomeMessage.put("type", "CONNECTION_CONFIRMED");
                welcomeMessage.put("message", "Kết nối thành công đến hệ thống bus real-time");
                welcomeMessage.put("timestamp", System.currentTimeMillis());
                
                messagingTemplate.convertAndSend(
                        "/topic/bus/owner/" + ownerId + "/notifications",
                        welcomeMessage);
            }
        } catch (Exception e) {
            logger.error("❌ [WebSocket] Error handling bus owner connection", e);
        }
    }

    // ✅ PUBLIC: Method để gửi bus status update từ Quartz Job
    public void sendBusStatusUpdate(Integer busSlotId, Integer ownerId, String status, 
                                   String busName, String routeInfo, String action) {
        try {
            Map<String, Object> statusUpdate = new java.util.HashMap<>();
            statusUpdate.put("busSlotId", busSlotId);
            statusUpdate.put("ownerId", ownerId);
            statusUpdate.put("status", status);
            statusUpdate.put("busName", busName);
            statusUpdate.put("routeInfo", routeInfo);
            statusUpdate.put("action", action);
            statusUpdate.put("timestamp", System.currentTimeMillis());

            // Send to owner dashboard
            messagingTemplate.convertAndSend(
                    "/topic/bus/owner/" + ownerId + "/status-updates",
                    statusUpdate);

            // Send to public tracking
            messagingTemplate.convertAndSend(
                    "/topic/bus/slots/" + busSlotId + "/status",
                    statusUpdate);

            logger.info("🚌 [WebSocket] Auto-sent status update: {} for BusSlot ID: {}", 
                       action, busSlotId);
        } catch (Exception e) {
            logger.error("❌ [WebSocket] Failed to send bus status update", e);
        }
    }

}