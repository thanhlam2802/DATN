package backend.backend.dto.BusDTO;

import java.math.BigDecimal;

/**
 * DTO đại diện cho thông tin hiển thị trên thẻ (card) của một tuyến xe
 * phổ biến ở trang chủ.
 *
 * @param id          ID của tuyến xe.
 * @param origin      Tên điểm đi.
 * @param destination Tên điểm đến.
 * @param imageUrl    URL hình ảnh đại diện cho tuyến.
 * @param price       Giá vé (có thể là giá thấp nhất hoặc giá tham khảo).
 */
public record RouteCard(
        Integer id,
        String origin,
        String destination,
        String imageUrl,
        BigDecimal price
) {
}