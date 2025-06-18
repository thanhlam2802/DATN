package backend.backend.utils;


import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date; // Để tương thích với các API cũ nếu cần

/**
 * Tiện ích xử lý ngày và giờ cho ứng dụng du lịch.
 * Sử dụng Java 8 Date and Time API (java.time.*) để có hiệu suất và khả năng đọc tốt hơn.
 */
public class DateTimeUtil {

    // --- Các định dạng ngày giờ phổ biến ---
    // Định dạng ngày (ví dụ: 2024-12-31)
    public static final String DATE_FORMAT_ISO = "yyyy-MM-dd";
    // Định dạng ngày giờ (ví dụ: 2024-12-31T10:30:00) - ISO 8601
    public static final String DATETIME_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss";
    // Định dạng ngày giờ thân thiện với người dùng (ví dụ: 31/12/2024 10:30)
    public static final String DATETIME_FORMAT_VN = "dd/MM/yyyy HH:mm";
    // Định dạng ngày thân thiện với người dùng (ví dụ: 31/12/2024)
    public static final String DATE_FORMAT_VN = "dd/MM/yyyy";
    // Định dạng thời gian (ví dụ: 10:30:00)
    public static final String TIME_FORMAT = "HH:mm:ss";

    // --- Zone IDs phổ biến (ví dụ cho ứng dụng du lịch) ---
    public static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Ho_Chi_Minh"); // Múi giờ mặc định cho Việt Nam
    public static final ZoneId UTC_ZONE = ZoneId.of("UTC"); // Múi giờ chuẩn quốc tế


    // -------------------------------------------------------------------------------------------------
    // --- PHƯƠNG THỨC CHUYỂN ĐỔI CHUỖI SANG ĐỐI TƯỢNG NGÀY GIỜ ---
    // -------------------------------------------------------------------------------------------------

    /**
     * Chuyển đổi chuỗi ngày thành LocalDate.
     *
     * @param dateString Chuỗi ngày (ví dụ: "2024-06-13", "13/06/2024").
     * @param formatPattern Định dạng của chuỗi ngày (ví dụ: "yyyy-MM-dd", "dd/MM/yyyy").
     * @return Đối tượng LocalDate, hoặc null nếu chuỗi không hợp lệ.
     */
    public static LocalDate parseDate(String dateString, String formatPattern) {
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(formatPattern));
        } catch (DateTimeParseException e) {
            // Log lỗi hoặc ném một ngoại lệ tùy chỉnh nếu cần
            System.err.println("Lỗi khi parse ngày '" + dateString + "' với định dạng '" + formatPattern + "': " + e.getMessage());
            return null;
        }
    }

    /**
     * Chuyển đổi chuỗi ngày giờ thành LocalDateTime.
     *
     * @param dateTimeString Chuỗi ngày giờ (ví dụ: "2024-06-13T14:30:00", "13/06/2024 14:30").
     * @param formatPattern Định dạng của chuỗi ngày giờ.
     * @return Đối tượng LocalDateTime, hoặc null nếu chuỗi không hợp lệ.
     */
    public static LocalDateTime parseDateTime(String dateTimeString, String formatPattern) {
        try {
            return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern(formatPattern));
        } catch (DateTimeParseException e) {
            System.err.println("Lỗi khi parse ngày giờ '" + dateTimeString + "' với định dạng '" + formatPattern + "': " + e.getMessage());
            return null;
        }
    }

    /**
     * Chuyển đổi chuỗi ngày giờ có múi giờ thành ZonedDateTime.
     *
     * @param zonedDateTimeString Chuỗi ngày giờ có múi giờ (ví dụ: "2024-06-13T14:30:00+07:00[Asia/Ho_Chi_Minh]").
     * @return Đối tượng ZonedDateTime, hoặc null nếu chuỗi không hợp lệ.
     */
    public static ZonedDateTime parseZonedDateTime(String zonedDateTimeString) {
        try {
            return ZonedDateTime.parse(zonedDateTimeString);
        } catch (DateTimeParseException e) {
            System.err.println("Lỗi khi parse ZonedDateTime '" + zonedDateTimeString + "': " + e.getMessage());
            return null;
        }
    }

    // -------------------------------------------------------------------------------------------------
    // --- PHƯƠNG THỨC CHUYỂN ĐỔI ĐỐI TƯỢNG NGÀY GIỜ SANG CHUỖI ---
    // -------------------------------------------------------------------------------------------------

    /**
     * Định dạng LocalDate thành chuỗi theo định dạng mong muốn.
     *
     * @param date Đối tượng LocalDate.
     * @param formatPattern Định dạng chuỗi mong muốn (ví dụ: "yyyy-MM-dd", "dd/MM/yyyy").
     * @return Chuỗi ngày đã định dạng, hoặc null nếu date là null.
     */
    public static String formatDate(LocalDate date, String formatPattern) {
        if (date == null) return null;
        return date.format(DateTimeFormatter.ofPattern(formatPattern));
    }

    /**
     * Định dạng LocalDateTime thành chuỗi theo định dạng mong muốn.
     *
     * @param dateTime Đối tượng LocalDateTime.
     * @param formatPattern Định dạng chuỗi mong muốn.
     * @return Chuỗi ngày giờ đã định dạng, hoặc null nếu dateTime là null.
     */
    public static String formatDateTime(LocalDateTime dateTime, String formatPattern) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern(formatPattern));
    }

    /**
     * Định dạng ZonedDateTime thành chuỗi theo định dạng ISO_OFFSET_DATE_TIME
     * (ví dụ: 2024-06-13T14:30:00+07:00).
     *
     * @param zonedDateTime Đối tượng ZonedDateTime.
     * @return Chuỗi ZonedDateTime đã định dạng, hoặc null nếu zonedDateTime là null.
     */
    public static String formatZonedDateTime(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) return null;
        return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
    
    /**
     * Định dạng ZonedDateTime thành chuỗi theo định dạng tùy chỉnh.
     *
     * @param zonedDateTime Đối tượng ZonedDateTime.
     * @param formatPattern Định dạng chuỗi mong muốn.
     * @return Chuỗi ZonedDateTime đã định dạng, hoặc null nếu zonedDateTime là null.
     */
    public static String formatZonedDateTime(ZonedDateTime zonedDateTime, String formatPattern) {
        if (zonedDateTime == null) return null;
        return zonedDateTime.format(DateTimeFormatter.ofPattern(formatPattern));
    }

    // -------------------------------------------------------------------------------------------------
    // --- PHƯƠNG THỨC CHUYỂN ĐỔI GIỮA CÁC ĐỐI TƯỢNG NGÀY GIỜ VÀ MÚI GIỜ ---
    // -------------------------------------------------------------------------------------------------

    /**
     * Chuyển đổi LocalDateTime từ múi giờ này sang múi giờ khác.
     *
     * @param localDateTime Đối tượng LocalDateTime ban đầu (không có thông tin múi giờ).
     * @param fromZone Múi giờ gốc của localDateTime.
     * @param toZone Múi giờ đích muốn chuyển đổi sang.
     * @return LocalDateTime đã được chuyển đổi sang múi giờ đích.
     */
    public static LocalDateTime convertZone(LocalDateTime localDateTime, ZoneId fromZone, ZoneId toZone) {
        if (localDateTime == null) return null;
        ZonedDateTime fromZoned = localDateTime.atZone(fromZone);
        ZonedDateTime toZoned = fromZoned.withZoneSameInstant(toZone);
        return toZoned.toLocalDateTime();
    }

    /**
     * Chuyển đổi LocalDateTime tại múi giờ mặc định của hệ thống sang ZonedDateTime tại một múi giờ cụ thể.
     *
     * @param localDateTime LocalDateTime cần chuyển đổi.
     * @param targetZone Múi giờ đích.
     * @return ZonedDateTime tại múi giờ đích.
     */
    public static ZonedDateTime toZonedDateTime(LocalDateTime localDateTime, ZoneId targetZone) {
        if (localDateTime == null) return null;
        return localDateTime.atZone(targetZone);
    }

    /**
     * Chuyển đổi ZonedDateTime sang LocalDateTime (bỏ qua thông tin múi giờ).
     *
     * @param zonedDateTime ZonedDateTime cần chuyển đổi.
     * @return LocalDateTime tương ứng.
     */
    public static LocalDateTime toLocalDateTime(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) return null;
        return zonedDateTime.toLocalDateTime();
    }

    /**
     * Chuyển đổi java.util.Date cũ sang LocalDateTime.
     *
     * @param date Đối tượng java.util.Date.
     * @param zoneId Múi giờ để chuyển đổi (quan trọng vì Date không có múi giờ).
     * @return LocalDateTime tương ứng.
     */
    public static LocalDateTime toLocalDateTime(Date date, ZoneId zoneId) {
        if (date == null) return null;
        return date.toInstant().atZone(zoneId).toLocalDateTime();
    }

    /**
     * Chuyển đổi LocalDateTime sang java.util.Date cũ.
     *
     * @param localDateTime LocalDateTime cần chuyển đổi.
     * @param zoneId Múi giờ để chuyển đổi (quan trọng để xác định instant).
     * @return Đối tượng java.util.Date.
     */
    public static Date toDate(LocalDateTime localDateTime, ZoneId zoneId) {
        if (localDateTime == null) return null;
        return Date.from(localDateTime.atZone(zoneId).toInstant());
    }

    // -------------------------------------------------------------------------------------------------
    // --- PHƯƠNG THỨC TÍNH TOÁN NGÀY GIỜ ---
    // -------------------------------------------------------------------------------------------------

    /**
     * Tính số ngày giữa hai LocalDate (bao gồm ngày bắt đầu, không bao gồm ngày kết thúc).
     * Ví dụ: startDate=13/06, endDate=15/06 -> 2 ngày (13, 14)
     *
     * @param startDate Ngày bắt đầu.
     * @param endDate Ngày kết thúc.
     * @return Số ngày giữa hai ngày, hoặc 0 nếu startDate sau endDate.
     */
    public static long calculateDaysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu và ngày kết thúc không được null.");
        }
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * Thêm số ngày vào một LocalDate.
     *
     * @param date Ngày gốc.
     * @param days Số ngày muốn thêm (có thể là số âm để lùi ngày).
     * @return LocalDate mới sau khi thêm ngày.
     */
    public static LocalDate addDays(LocalDate date, long days) {
        if (date == null) return null;
        return date.plusDays(days);
    }

    /**
     * Kiểm tra xem một ngày có nằm giữa hai ngày khác không (bao gồm cả ngày bắt đầu và kết thúc).
     *
     * @param dateToCheck Ngày cần kiểm tra.
     * @param startDate Ngày bắt đầu của khoảng.
     * @param endDate Ngày kết thúc của khoảng.
     * @return True nếu ngày nằm trong khoảng, ngược lại False.
     */
    public static boolean isDateBetween(LocalDate dateToCheck, LocalDate startDate, LocalDate endDate) {
        if (dateToCheck == null || startDate == null || endDate == null) {
            return false;
        }
        return !dateToCheck.isBefore(startDate) && !dateToCheck.isAfter(endDate);
    }

    /**
     * Kiểm tra xem hai khoảng thời gian có chồng chéo nhau không.
     * Hữu ích cho việc kiểm tra xung đột lịch trình hoặc đặt phòng.
     *
     * @param start1 Thời gian bắt đầu của khoảng 1.
     * @param end1 Thời gian kết thúc của khoảng 1.
     * @param start2 Thời gian bắt đầu của khoảng 2.
     * @param end2 Thời gian kết thúc của khoảng 2.
     * @return True nếu hai khoảng thời gian chồng chéo, ngược lại False.
     */
    public static boolean doPeriodsOverlap(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            throw new IllegalArgumentException("Các đối số thời gian không được null.");
        }
        // Hai khoảng thời gian chồng chéo nếu (start1 <= end2) AND (start2 <= end1)
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    /**
     * Lấy thời gian hiện tại theo múi giờ mặc định.
     *
     * @return LocalDateTime hiện tại.
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(DEFAULT_ZONE);
    }

    /**
     * Lấy ngày hiện tại theo múi giờ mặc định.
     *
     * @return LocalDate hiện tại.
     */
    public static LocalDate today() {
        return LocalDate.now(DEFAULT_ZONE);
    }

    /**
     * Lấy thời gian hiện tại theo múi giờ UTC.
     * @return ZonedDateTime hiện tại theo UTC.
     */
    public static ZonedDateTime nowUtc() {
        return ZonedDateTime.now(UTC_ZONE);
    }
}