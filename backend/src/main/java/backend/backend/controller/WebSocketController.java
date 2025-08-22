package backend.backend.controller;

import backend.backend.dto.OrderInfoDto;
import backend.backend.service.OrderService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.math.BigDecimal;
import java.util.Map;

@Controller
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ObjectMapper objectMapper;

    @MessageMapping("/hotel/room-booking")
    public void handleHotelRoomBooking(Map<String, Object> bookingNotification) {
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
        } catch (Exception e) {
            logger.error("Error handling hotel room booking notification", e);
        }
    }

    @MessageMapping("/hotel/room-update")
    public void handleRoomUpdate(Map<String, Object> roomUpdate) {
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
        } catch (Exception e) {
            logger.error("Error handling room update notification", e);
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

    @MessageMapping("/getTop10NewOrders")
    public void getTop10NewOrders() {
        try {
            logger.info("GET_TOP_10_NEW_ORDERS");
            List<OrderInfoDto> orderInfoDtos = orderService.getTop10NewOrders();
            logger.info("GET_TOP_10_NEW_ORDERS_DATA {}", orderInfoDtos.size());
            messagingTemplate.convertAndSend("/topic/getTop10NewOrders", orderInfoDtos);
        } catch (Exception e) {
            logger.error("Error handling viewer notification", e);
        }
    }


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

    /**
     * 🔔 Gửi bus booking notification đến admin
     */
    public void sendBusBookingNotification(String customerName, Integer seatCount,
                                           String routeName, Integer routeId,
                                           BigDecimal amount, Integer orderId) {
        try {
            Map<String, Object> notification = new java.util.HashMap<>();
            notification.put("type", "BUS_BOOKING");
            notification.put("customerName", customerName);
            notification.put("seatCount", seatCount);
            notification.put("routeName", routeName);
            notification.put("routeId", routeId);
            notification.put("amount", amount);
            notification.put("orderId", orderId);
            notification.put("timestamp", System.currentTimeMillis());
            notification.put("service", "BUS");

            // Gửi đến admin dashboard
            messagingTemplate.convertAndSend("/topic/admin/bus/bookings", notification);

            logger.info("🔔 [Admin Notification] Sent bus booking notification for order: {}", orderId);
        } catch (Exception e) {
            logger.error("❌ [Admin Notification] Failed to send bus booking notification", e);
        }
    }

    /**
     * 💰 Gửi payment notification đến admin
     */
    public void sendPaymentNotification(Integer orderId, BigDecimal amount,
                                        String status, String paymentMethod, String service) {
        try {
            Map<String, Object> notification = new java.util.HashMap<>();
            notification.put("type", status.equals("SUCCESS") ? "PAYMENT_SUCCESS" : "PAYMENT_FAILED");
            notification.put("orderId", orderId);
            notification.put("amount", amount);
            notification.put("status", status);
            notification.put("paymentMethod", paymentMethod);
            notification.put("service", service);
            notification.put("timestamp", System.currentTimeMillis());

            // Gửi đến admin dashboard
            messagingTemplate.convertAndSend("/topic/admin/payments", notification);

            logger.info("🔔 [Admin Notification] Sent payment notification for order: {} - Status: {}",
                    orderId, status);
        } catch (Exception e) {
            logger.error("❌ [Admin Notification] Failed to send payment notification", e);
        }
    }

    /**
     * 📊 Gửi admin connection mapping
     */
    @MessageMapping("/admin/connect")
    public void handleAdminConnect(Map<String, Object> connectionInfo) {
        try {
            logger.info("🔔 [Admin WebSocket] Admin connected: {}", connectionInfo);

            String adminId = (String) connectionInfo.get("adminId");
            if (adminId != null) {
                Map<String, Object> welcomeMessage = new java.util.HashMap<>();
                welcomeMessage.put("type", "ADMIN_CONNECTION_CONFIRMED");
                welcomeMessage.put("message", "Kết nối thành công đến hệ thống admin real-time");
                welcomeMessage.put("timestamp", System.currentTimeMillis());

                messagingTemplate.convertAndSend(
                        "/topic/admin/notifications/" + adminId,
                        welcomeMessage);
            }
        } catch (Exception e) {
            logger.error("❌ [Admin WebSocket] Error handling admin connection", e);
        }
    }

}