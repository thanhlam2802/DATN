package backend.backend.entity.enumBus;

public enum TripType {
    ONE_TIME("ONE_TIME", "Chuyến một lần"),
    RECURRING("RECURRING", "Chuyến định kỳ");

    private final String code;
    private final String description;

    TripType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TripType fromCode(String code) {
        for (TripType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown TripType code: " + code);
    }
} 