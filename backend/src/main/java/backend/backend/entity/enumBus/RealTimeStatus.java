package backend.backend.entity.enumBus;

public enum RealTimeStatus {
    ON_SCHEDULE("ON_SCHEDULE", "Đúng giờ"),
    EARLY("EARLY", "Sớm hơn dự kiến"),
    DELAYED("DELAYED", "Trễ hơn dự kiến"),
    UNKNOWN("UNKNOWN", "Chưa xác định");

    private final String code;
    private final String description;

    RealTimeStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static RealTimeStatus fromCode(String code) {
        for (RealTimeStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown RealTimeStatus code: " + code);
    }
} 