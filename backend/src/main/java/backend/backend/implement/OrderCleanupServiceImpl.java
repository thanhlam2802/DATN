package backend.backend.implement;

import backend.backend.dao.FlightBookingDAO;
import backend.backend.dao.OrderDAO;
import backend.backend.entity.FlightBooking;
import backend.backend.entity.Order;
import backend.backend.service.FlightBookingService;
import backend.backend.dao.HotelBookingDAO;
import backend.backend.dao.Hotel.HotelRoomDAO;
import backend.backend.entity.HotelBooking;
import backend.backend.entity.HotelRoomVariant;
import backend.backend.entity.HotelRoom;
import backend.backend.service.OrderCleanupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 
import backend.backend.controller.AdminWebSocketController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service 
public class OrderCleanupServiceImpl implements OrderCleanupService {

    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    private FlightBookingDAO flightBookingDAO;
    @Autowired
    private FlightBookingService flightBookingService;
    @Autowired
    private HotelBookingDAO hotelBookingDAO;
    @Autowired
    private HotelRoomDAO hotelRoomDAO;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private AdminWebSocketController adminWebSocketController;
    
//    @Scheduled(fixedRate = 300000)
@Scheduled(fixedDelay = 30000)
@Transactional
    public void cancelExpiredOrders() {
        System.out.println("Running scheduled task to cancel expired orders...");
        List<Order> expiredOrders = orderDAO.findAllByStatusAndExpiresAtBefore("PENDING_PAYMENT", LocalDateTime.now());
        
        if (!expiredOrders.isEmpty()) {
            for (Order order : expiredOrders) {
                List<FlightBooking> flightBooking = flightBookingDAO.findByOrderId(order.getId());
                System.out.println("size của booking: " + flightBooking.size());
                for (FlightBooking flightBooking1 : flightBooking) {
                    System.out.println("id cancel booking: " + flightBooking1.getId());
                    flightBookingService.cancelBooking(flightBooking1.getId());
                }
            
                System.out.println("Expired order: " + order.getId());
                List<HotelBooking> hotelBookings = hotelBookingDAO.findByOrderId(order.getId());
                for (HotelBooking booking : hotelBookings) {
                    System.out.println("  Booking id: " + booking.getId() + ", rooms: " + booking.getRooms());
                    HotelRoomVariant variant = booking.getRoomVariant();
                    if (variant != null && variant.getRoom() != null && booking.getRooms() != null) {
                        HotelRoom room = variant.getRoom();
                        Short currentQty = room.getRoomQuantity();
                        short roomsBooked = booking.getRooms();
                        room.setRoomQuantity((short) (currentQty + roomsBooked));
                        hotelRoomDAO.save(room);
                        System.out.println("Order " + order.getId() + " hoàn lại " + roomsBooked + " phòng cho roomId=" + room.getId());
                        
                        try {
                            Integer hotelId = room.getHotel().getId();
                            Map<String, Object> roomUpdate = new java.util.HashMap<>();
                            roomUpdate.put("hotelId", hotelId);
                            roomUpdate.put("roomId", room.getId());
                            roomUpdate.put("previousQuantity", currentQty);
                            roomUpdate.put("newQuantity", room.getRoomQuantity());
                            roomUpdate.put("action", "ROOM_RESTORED");
                            messagingTemplate.convertAndSend("/topic/hotels/" + hotelId + "/room-updates", roomUpdate);
                            System.out.println("Đã gửi WebSocket notification cho hotelId=" + hotelId + ", roomId=" + room.getId());
                            
                            adminWebSocketController.sendExpiredOrderNotification(order.getId().toString());
                        } catch (Exception e) {
                            System.out.println("Không thể gửi WebSocket notification: " + e.getMessage());
                        }
                    }
                }
                order.setStatus("CANCELLED");
                publisher.publishEvent(order);
            }
            orderDAO.saveAll(expiredOrders);

            System.out.println("Cancelled " + expiredOrders.size() + " expired orders.");
        }
    }
}
