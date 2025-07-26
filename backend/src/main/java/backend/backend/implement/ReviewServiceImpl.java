// trong file backend/implement/ReviewServiceImpl.java

package backend.backend.implement;

import backend.backend.dao.ReviewDAO;
import backend.backend.dao.TourDAO; // Import TourDAO
import backend.backend.dto.ReviewDto;
import backend.backend.entity.Review;
import backend.backend.entity.Tour; // Import Tour entity
import backend.backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewDAO reviewDAO;

    @Autowired
    private TourDAO tourDAO; // Inject TourDAO

    private static final String TOUR_ENTITY_TYPE = "TOUR";

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDto> getTourReviewsByOwner(Long ownerId) {
        // 1. Lấy tất cả review như cũ
        List<Review> reviews = reviewDAO.findReviewsForToursByOwner(ownerId, TOUR_ENTITY_TYPE);
        if (reviews.isEmpty()) {
            return List.of();
        }

        // 2. Lấy danh sách ID của các tour cần tìm
        List<Long> tourIds = reviews.stream()
                .map(review -> review.getEntityId().longValue())
                .distinct()
                .collect(Collectors.toList());

        // 3. Lấy tất cả tour tương ứng trong MỘT LẦN truy vấn và đưa vào Map
        Map<Long, String> tourNamesMap = tourDAO.findAllById(tourIds).stream()
                .collect(Collectors.toMap(Tour::getId, Tour::getName));

        // 4. Ánh xạ Review sang ReviewDto, sử dụng Map để tra cứu tên tour
        return reviews.stream().map(review -> {
            ReviewDto dto = new ReviewDto();
            dto.setId(review.getId());
            dto.setRating(review.getRating() != null ? review.getRating().intValue() : null);
            dto.setContent(review.getContent());
            dto.setDate(review.getCreatedAt() != null ? review.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : null);
            dto.setImages(List.of());

            // Lấy tên tác giả
            if (review.getUser() != null) {
                String authorName = review.getUser().getName();
                dto.setAuthor(authorName != null && !authorName.isBlank() ? authorName : review.getUser().getEmail());
            } else {
                dto.setAuthor("Anonymous");
            }
            
            // Lấy tên tour từ Map, rất nhanh và không cần query lại
            dto.setTourName(tourNamesMap.get(review.getEntityId().longValue()));

            return dto;
        }).collect(Collectors.toList());
    }
}