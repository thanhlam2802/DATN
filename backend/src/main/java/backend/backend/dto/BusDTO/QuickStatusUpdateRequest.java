package backend.backend.dto.BusDTO;

import backend.backend.entity.enumBus.BusSlotStatus;
import backend.backend.entity.enumBus.DelayReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuickStatusUpdateRequest {
    private BusSlotStatus status;
    private DelayReason delayReason;
    private String currentLocation;
    private String driverNotes; // Ghi chú từ tài xế (không có trong entity chính, có thể là trường tạm thời hoặc cần thêm)
    private Boolean autoSetActualTime;  // Tự động set actual time dựa trên status
} 