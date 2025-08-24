package backend.backend.listener; // Bạn có thể tạo package mới này

import backend.backend.dto.OrderInfoDto;
import backend.backend.entity.Order;
import backend.backend.event.VoucherUsedUpEvent;
import backend.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OrderEventListener {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private OrderService orderService;

    @EventListener
    public void handleOrderCreated(Order event) {
        List<OrderInfoDto> top10 = orderService.getTop10NewOrders();
        messagingTemplate.convertAndSend("/topic/getTop10NewOrders", top10);
    }
}

