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
            List<Predicate> predicates = new ArrayList<>();

            // Lọc theo tourId
            if (params.containsKey("tourId")) {
                try {
                    Long tourId = Long.parseLong(params.get("tourId"));
                    Join<BookingTour, Departure> departureJoin = root.join("departure");
                    Join<Departure, Tour> tourJoin = departureJoin.join("tour");
                    predicates.add(criteriaBuilder.equal(tourJoin.get("id"), tourId));
                } catch (NumberFormatException e) {
                    // Bỏ qua
                }
            }

            // Lọc theo departureDate
            if (params.containsKey("departureDate")) {
                try {
                    LocalDate departureDate = LocalDate.parse(params.get("departureDate"));
                    Join<BookingTour, Departure> departureJoin = root.join("departure");
                    predicates.add(criteriaBuilder.equal(departureJoin.get("departureDate"), departureDate));
                } catch (DateTimeParseException e) {
                    // Bỏ qua
                }
            }

            // Lọc theo status
            if (params.containsKey("status")) {
                String status = params.get("status");
                if (status != null && !status.trim().isEmpty()) {
                    Join<BookingTour, Order> orderJoin = root.join("order");
                    predicates.add(criteriaBuilder.equal(orderJoin.get("status"), status));
                }
            }
            
            // --- NÂNG CẤP: Lọc theo khoảng ngày đặt tour (bookingDate) ---
            // Giả định entity BookingTour có trường `private LocalDate bookingDate;`
            if (params.containsKey("startDate")) {
                try {
                    LocalDate startDate = LocalDate.parse(params.get("startDate"));
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("bookingDate"), startDate));
                } catch (DateTimeParseException e) {
                    // Bỏ qua nếu ngày không hợp lệ
                }
            }

            if (params.containsKey("endDate")) {
                try {
                    LocalDate endDate = LocalDate.parse(params.get("endDate"));
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("bookingDate"), endDate));
                } catch (DateTimeParseException e) {
                   
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}