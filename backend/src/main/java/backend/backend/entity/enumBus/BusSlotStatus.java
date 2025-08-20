package backend.backend.entity.enumBus;

public enum BusSlotStatus {
    SCHEDULED,     // Đã lên lịch
    IN_PROGRESS,   // Đang chạy (thay thế ACTIVE)
    COMPLETED,     // Hoàn thành
    CANCELLED,     // Đã hủy
    DELAYED,       // Bị trễ
    ARCHIVED       // Lưu trữ (cho old trips)
}
