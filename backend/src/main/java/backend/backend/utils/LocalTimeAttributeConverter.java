package backend.backend.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.security.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;


/**
 * Custom JPA AttributeConverter để chuyển đổi giữa LocalTime (Java)
 * và Timestamp (SQL), đặc biệt hữu ích khi cột DB là DATETIME
 * nhưng chỉ phần thời gian là quan trọng.
 *
 * Khi chuyển đổi LocalTime sang Timestamp, chúng ta cần một thành phần ngày.
 * Chúng ta sẽ sử dụng một ngày cố định (ví dụ: 2000-01-01) để đảm bảo tính nhất quán
 * cho phần ngày của Timestamp.
 */
@Converter(autoApply = false) // Đặt là false để áp dụng thủ công nơi cần thiết
public class LocalTimeAttributeConverter implements AttributeConverter<LocalTime, Timestamp> {


    // Ngày cố định được sử dụng để tạo Timestamp từ LocalTime
    private static final LocalDate FIXED_DATE_FOR_CONVERSION = LocalDate.of(2000, 1, 1);

    @Override
    public Timestamp convertToDatabaseColumn(LocalTime locTime) {
        if (locTime == null) {
            return null;
        }
        // Kết hợp LocalTime với một ngày cố định để tạo LocalDateTime
        LocalDateTime combinedDateTime = LocalDateTime.of(FIXED_DATE_FOR_CONVERSION, locTime);
        // Chuyển LocalDateTime thành Instant với ZoneOffset, sau đó lấy số mili giây để tạo Timestamp
        return new Timestamp(combinedDateTime.toInstant(ZoneOffset.ofHours(7)).toEpochMilli()); // <-- ĐÃ SỬA LỖI Ở ĐÂY
    }

    @Override
    public LocalTime convertToEntityAttribute(Timestamp sqlTimestamp) {
        if (sqlTimestamp == null) {
            return null;
        }
        // Chuyển đổi Timestamp từ DB thành LocalDateTime, sau đó lấy phần LocalTime
        return sqlTimestamp.toLocalDateTime().toLocalTime();
    }
}