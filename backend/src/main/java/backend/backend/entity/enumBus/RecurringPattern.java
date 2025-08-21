package backend.backend.entity.enumBus;

public enum RecurringPattern {
    DAILY("DAILY", "Hàng ngày"),
    WEEKLY("WEEKLY", "Hàng tuần"),
    WEEKDAYS("WEEKDAYS", "Thứ 2-6"),
    WEEKENDS("WEEKENDS", "Thứ 7, CN");

    private final String code;
    private final String description;

    RecurringPattern(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static RecurringPattern fromCode(String code) {
        for (RecurringPattern pattern : values()) {
            if (pattern.code.equals(code)) {
                return pattern;
            }
        }
        throw new IllegalArgumentException("Unknown RecurringPattern code: " + code);
    }
} 