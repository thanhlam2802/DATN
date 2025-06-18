package backend.backend.specification;



import backend.backend.dto.TourSearchRequestDto;
import backend.backend.entity.Tour;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class TourSpecifications {

    public static Specification<Tour> from(TourSearchRequestDto request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Lọc theo keyword (tên tour hoặc điểm đến)
            if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
                String keywordPattern = "%" + request.getKeyword().toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), keywordPattern);
                Predicate destPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("destination")), keywordPattern);
                predicates.add(criteriaBuilder.or(namePredicate, destPredicate));
            }

            // Lọc theo khoảng giá
            if (request.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), request.getMinPrice()));
            }
            if (request.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), request.getMaxPrice()));
            }
            
            // TODO: Bạn có thể thêm các điều kiện lọc khác ở đây (ví dụ: category, duration...)

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}