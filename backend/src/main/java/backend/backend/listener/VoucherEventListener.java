package backend.backend.listener; // Bạn có thể tạo package mới này

import backend.backend.event.VoucherUsedUpEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class VoucherEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleVoucherUsedUp(VoucherUsedUpEvent event) {
        System.out.println("Voucher " + event.getVoucherCode() + " đã hết. Phát sóng cập nhật...");
        
        Map<String, String> payload = Map.of(
            "voucherCode", event.getVoucherCode(),
            "status", "UNAVAILABLE"
        );

        messagingTemplate.convertAndSend("/topic/vouchers/updates", payload);
    }
}
