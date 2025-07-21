package backend.backend.implement;

import backend.backend.dao.OrderDAO;
import backend.backend.entity.Order;
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

    // Chạy mỗi 5 phút để kiểm tra và dọn dẹp
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void cancelExpiredOrders() {
        System.out.println("Running scheduled task to cancel expired orders...");
        List<Order> expiredOrders = orderDAO.findAllByStatusAndExpiresAtBefore("PENDING_PAYMENT", LocalDateTime.now());
        
        if (!expiredOrders.isEmpty()) {
            for (Order order : expiredOrders) {
                order.setStatus("CANCELLED");
                // TODO: Thêm logic hoàn lại số ghế/vé đã giữ
            }
            orderDAO.saveAll(expiredOrders);
            System.out.println("Cancelled " + expiredOrders.size() + " expired orders.");
        }
    }
}
