package backend.backend.service;

import backend.backend.dto.CompletedServiceDTO;
import backend.backend.dto.ReviewRequestDTO;
import backend.backend.entity.Review; // Giả sử bạn đã có entity Review

import java.util.List;

/**
 * Interface cho các dịch vụ liên quan đến việc đánh giá của người dùng.
 */
public interface UserReviewService {

    /**
     * Lấy danh sách tất cả các dịch vụ đã hoàn thành (tour, khách sạn, xe, máy bay)
     * cho một người dùng cụ thể, kèm theo trạng thái đã đánh giá hay chưa.
     *
     * @param userId ID của người dùng (Customer/User)
     * @return Danh sách các dịch vụ đã hoàn thành để hiển thị cho người dùng đánh giá.
     */
    List<CompletedServiceDTO> getCompletedServicesForUser(Integer userId);

    /**
     * Tạo một đánh giá mới cho một dịch vụ.
     *
     * @param reviewRequest DTO chứa thông tin về đánh giá mới.
     * @return Đối tượng Review đã được tạo và lưu vào cơ sở dữ liệu.
     */
    Review createReview(ReviewRequestDTO  reviewRequest);

    // Bạn có thể thêm các hàm khác nếu cần, ví dụ:
    //
    // /**
    //  * Lấy danh sách các đánh giá đã được gửi bởi một người dùng.
    //  *
    //  * @param userId ID của người dùng.
    //  * @return Danh sách các review.
    //  */
    // List<Review> getReviewsByUserId(Integer userId);
    //
    // /**
    //  * Xóa một đánh giá.
    //  *
    //  * @param reviewId ID của đánh giá cần xóa.
    //  * @param userId ID của người dùng thực hiện xóa (để kiểm tra quyền).
    //  */
    // void deleteReview(Integer reviewId, Integer userId);
}