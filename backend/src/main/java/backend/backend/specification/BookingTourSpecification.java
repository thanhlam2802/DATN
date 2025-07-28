package backend.backend.specification;



import backend.backend.entity.BookingTour;
import backend.backend.entity.Departure;
import backend.backend.entity.Order;
import backend.backend.entity.Tour;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookingTourSpecification {

    public static Specification<BookingTour> findByCriteria(Map<String, String> params) {
        return (root, query, criteriaBuilder) -> {
            // Dùng List để chứa các điều kiện lọc (Predicate)
            List<Predicate> predicates = new ArrayList<>();

            // Lọc theo tourId
            if (params.containsKey("tourId")) {
                try {
                    Long tourId = Long.parseLong(params.get("tourId"));
                    // Join BookingTour -> Departure -> Tour để lấy tour.id
                    Join<BookingTour, Departure> departureJoin = root.join("departure");
                    Join<Departure, Tour> tourJoin = departureJoin.join("tour");
                    predicates.add(criteriaBuilder.equal(tourJoin.get("id"), tourId));
                } catch (NumberFormatException e) {
                    // Bỏ qua nếu tourId không phải là số
                }
            }

            // Lọc theo departureDate
            if (params.containsKey("departureDate")) {
                try {
                    LocalDate departureDate = LocalDate.parse(params.get("departureDate"));
                    // Join BookingTour -> Departure để lấy departure.departureDate
                    Join<BookingTour, Departure> departureJoin = root.join("departure");
                    predicates.add(criteriaBuilder.equal(departureJoin.get("departureDate"), departureDate));
                } catch (DateTimeParseException e) {
                    // Bỏ qua nếu ngày không đúng định dạng
                }
            }

            // Lọc theo status
            if (params.containsKey("status")) {
                String status = params.get("status");
                if (status != null && !status.trim().isEmpty()) {
                    // Join BookingTour -> Order để lấy order.status
                    Join<BookingTour, Order> orderJoin = root.join("order");
                    predicates.add(criteriaBuilder.equal(orderJoin.get("status"), status));
                }
            }

            // Kết hợp tất cả các điều kiện lọc bằng mệnh đề AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}