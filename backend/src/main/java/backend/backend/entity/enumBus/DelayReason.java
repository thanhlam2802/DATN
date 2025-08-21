package backend.backend.entity.enumBus;

public enum DelayReason {
    TRAFFIC_JAM("TRAFFIC_JAM", "Kẹt xe"),
    WEATHER("WEATHER", "Thời tiết xấu"),
    VEHICLE_ISSUE("VEHICLE_ISSUE", "Sự cố xe"),
    PASSENGER_DELAY("PASSENGER_DELAY", "Khách trễ"),
    ROAD_ACCIDENT("ROAD_ACCIDENT", "Tai nạn giao thông"),
    FUEL_STOP("FUEL_STOP", "Dừng đổ xăng"),
    DRIVER_BREAK("DRIVER_BREAK", "Nghỉ giải lao"),
    EARLY_ARRIVAL("EARLY_ARRIVAL", "Đến sớm"),
    OTHER("OTHER", "Lý do khác");

    private final String code;
    private final String description;

    DelayReason(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static DelayReason fromCode(String code) {
        for (DelayReason reason : values()) {
            if (reason.code.equals(code)) {
                return reason;
            }
        }
        throw new IllegalArgumentException("Unknown DelayReason code: " + code);
    }
} 