package backend.backend.implement;

import backend.backend.dao.OrderDAO;
import backend.backend.dao.HotelBookingDAO;
import backend.backend.dao.Hotel.HotelRoomDAO;
import backend.backend.entity.Order;
import backend.backend.entity.HotelBooking;
import backend.backend.entity.HotelRoomVariant;
import backend.backend.entity.HotelRoom;
import backend.backend.service.OrderCleanupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 

import java.time.LocalDateTime;
import java.util.List;

@Service 
public class OrderCleanupServiceImpl implements OrderCleanupService {

    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    private HotelBookingDAO hotelBookingDAO;
    @Autowired
    private HotelRoomDAO hotelRoomDAO;

    // Chạy mỗi 5 phút để kiểm tra và dọn dẹp
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void cancelExpiredOrders() {
        System.out.println("Running scheduled task to cancel expired orders...");
        List<Order> expiredOrders = orderDAO.findAllByStatusAndExpiresAtBefore("PENDING_PAYMENT", LocalDateTime.now());
        
        if (!expiredOrders.isEmpty()) {
            for (Order order : expiredOrders) {
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
                    }
                }
                order.setStatus("CANCELLED");
            }
            orderDAO.saveAll(expiredOrders);
            System.out.println("Cancelled " + expiredOrders.size() + " expired orders.");
        }
    }
}
