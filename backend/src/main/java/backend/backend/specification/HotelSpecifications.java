package backend.backend.specification;

import backend.backend.dto.Hotel.HotelSearchRequestDto;
import backend.backend.entity.Hotel;
import backend.backend.entity.HotelRoom;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class HotelSpecifications {

    public static Specification<Hotel> from(HotelSearchRequestDto requestDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (requestDto.getKeyword() != null && !requestDto.getKeyword().isEmpty()) {
                String pattern = "%" + requestDto.getKeyword().toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern);
                Predicate addressPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), pattern);
                predicates.add(criteriaBuilder.or(namePredicate, addressPredicate));
            }

            if (requestDto.getProvinceId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("province").get("id"), requestDto.getProvinceId()));
            }

            if (requestDto.getMinStarRating() != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("starRating"), requestDto.getMinStarRating()));
            }

            if (requestDto.getNumAdults() != null && requestDto.getNumAdults() > 0) {
                Join<Hotel, HotelRoom> roomJoin = root.join("hotelRooms", JoinType.INNER);
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(roomJoin.get("maxAdults"), requestDto.getNumAdults()));
                query.distinct(true);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}