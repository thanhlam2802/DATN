package backend.backend.service;

import java.util.List;

import backend.backend.dto.ReviewDto;
import backend.backend.entity.Review;

public interface ReviewService {

	 /**
     * Lấy danh sách đánh giá cho các tour của một người sở hữu cụ thể.
     *
     * @param ownerId ID của người sở hữu.
     * @return Danh sáchs các đối tượng Review.
     */
    List<ReviewDto> getTourReviewsByOwner(Long ownerId);
}
