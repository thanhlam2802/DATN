package backend.backend.specification;

import backend.backend.dto.TourSearchRequestDto;
import backend.backend.entity.Tour;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class TourSpecifications {

    /**
     * Tạo một đối tượng Specification<Tour> từ DTO tìm kiếm.
     * Specification này sẽ được dùng để xây dựng câu lệnh WHERE động.
     *
     * @param request DTO chứa các tiêu chí lọc, sắp xếp và phân trang.
     * @return một Specification để truy vấn Tour.
     */
    public static Specification<Tour> from(TourSearchRequestDto request) {
     
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

     
            request.getKeyword().ifPresent(keyword -> {
                if (!keyword.isBlank()) {
                    String pattern = "%" + keyword.toLowerCase().trim() + "%";
                    Predicate nameMatch = cb.like(cb.lower(root.get("name")), pattern);
                    Predicate destinationMatch = cb.like(cb.lower(root.get("destination")), pattern);
                    predicates.add(cb.or(nameMatch, destinationMatch));
                }
            });

       
            request.getMinPrice().ifPresent(min -> predicates.add(cb.greaterThanOrEqualTo(root.get("price"), min)));
            request.getMaxPrice().ifPresent(max -> predicates.add(cb.lessThanOrEqualTo(root.get("price"), max)));


            request.getMinRating()
                    .ifPresent(rating -> predicates.add(cb.greaterThanOrEqualTo(root.get("averageRating"), rating)));

            request.getTags().ifPresent(tags -> {
                if (!tags.isEmpty()) {
                    predicates.add(root.join("tags").get("name").in(tags));
                    query.distinct(true);
                }
            });

            // Kết hợp tất cả các điều kiện lọc bằng mệnh đề AND
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}