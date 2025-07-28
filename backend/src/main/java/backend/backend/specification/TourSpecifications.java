package backend.backend.specification;

import backend.backend.dto.TourSearchRequestDto;
import backend.backend.entity.Tour;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class TourSpecifications {

    public static Specification<Tour> from(TourSearchRequestDto request) {
     
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Lọc theo từ khóa chung (tên tour)
            request.getKeyword().ifPresent(keyword -> {
                if (!keyword.isBlank()) {
                    predicates.add(cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase().trim() + "%"));
                }
            });
            
            // 2. BỔ SUNG: Lọc theo điểm đến cụ thể
            request.getDestination().ifPresent(destination -> {
                if (!destination.isBlank()) {
                    predicates.add(cb.like(cb.lower(root.get("destination")), "%" + destination.toLowerCase().trim() + "%"));
                }
            });

            // 3. BỔ SUNG: Lọc theo ngày khởi hành
            // Giả sử Tour entity có trường 'startDate' kiểu LocalDate
            request.getStartDate().ifPresent(date -> {
                predicates.add(cb.equal(root.get("startDate"), date));
            });

            // 4. Lọc theo khoảng giá
            request.getMinPrice().ifPresent(min -> predicates.add(cb.greaterThanOrEqualTo(root.get("price"), min)));
            request.getMaxPrice().ifPresent(max -> predicates.add(cb.lessThanOrEqualTo(root.get("price"), max)));

            // 5. Lọc theo đánh giá
            request.getMinRating()
                    .ifPresent(rating -> predicates.add(cb.greaterThanOrEqualTo(root.get("averageRating"), rating)));

            // 6. Lọc theo tags
            request.getTags().ifPresent(tags -> {
                if (!tags.isEmpty()) {
                    predicates.add(root.join("tags").get("name").in(tags));
                    query.distinct(true);
                }
            });

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}