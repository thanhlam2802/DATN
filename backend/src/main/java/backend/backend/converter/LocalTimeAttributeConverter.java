package backend.backend.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset; // Cần thiết cho việc xác định múi giờ

@Converter(autoApply = true) // Tự động áp dụng cho tất cả các trường LocalTime trong các entity
public class LocalTimeAttributeConverter implements AttributeConverter<LocalTime, Timestamp> {

    // Ngày cố định được sử dụng để kết hợp với LocalTime khi chuyển đổi sang LocalDateTime
    // Chọn một ngày cũ không có khả năng xung đột với dữ liệu thực tế
    private static final LocalDate FIXED_DATE_FOR_CONVERSION = LocalDate.of(2000, 1, 1);

    /**
     * Chuyển đổi LocalTime từ Java entity sang Timestamp để lưu vào database.
     * Cần kết hợp LocalTime với một ngày cố định và một múi giờ để tạo LocalDateTime,
     * sau đó chuyển sang Timestamp.
     */
    @Override
    public Timestamp convertToDatabaseColumn(LocalTime entityValue) {
        if (entityValue == null) {
            return null;
        }
        // Kết hợp LocalTime với một ngày cố định để tạo LocalDateTime
        // Giả định múi giờ là +07:00 (Việt Nam) - Điều chỉnh nếu múi giờ database của bạn khác
        LocalDateTime combinedDateTime = LocalDateTime.of(FIXED_DATE_FOR_CONVERSION, entityValue);

        // Chuyển đổi LocalDateTime sang Timestamp
        // Sử dụng constructor của Timestamp nhận số mili giây từ Epoch
        return new Timestamp(combinedDateTime.toInstant(ZoneOffset.ofHours(7)).toEpochMilli());
    }

    /**
     * Chuyển đổi Timestamp từ database sang LocalTime để sử dụng trong Java entity.
     * Chỉ lấy phần thời gian từ Timestamp.
     */
    @Override
    public LocalTime convertToEntityAttribute(Timestamp databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        // Chuyển đổi Timestamp sang LocalDateTime, sau đó lấy LocalTime
        return databaseValue.toLocalDateTime().toLocalTime();
    }
}