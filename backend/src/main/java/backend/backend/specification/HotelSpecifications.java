package backend.backend.specification;

import backend.backend.dto.Hotel.HotelSearchRequestDto;
import backend.backend.entity.Hotel;
import backend.backend.entity.HotelRoom;
import backend.backend.entity.Province;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class HotelSpecifications {

    public static Specification<Hotel> from(HotelSearchRequestDto requestDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (requestDto.getKeyword() != null && !requestDto.getKeyword().isEmpty()) {
                String pattern = "%" + requestDto.getKeyword().toLowerCase() + "%";
                Join<Hotel, Province> provinceJoin = root.join("province", JoinType.LEFT);
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern);
                Predicate addressPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), pattern);
                Predicate provinceNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(provinceJoin.get("name")),
                        pattern);
                predicates.add(criteriaBuilder.or(namePredicate, addressPredicate, provinceNamePredicate));
            }

            if (requestDto.getProvinceId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("province").get("id"), requestDto.getProvinceId()));
            }

            if (requestDto.getMinStarRating() != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("starRating"), requestDto.getMinStarRating()));
            }

            if (requestDto.getNumAdults() != null && requestDto.getNumAdults() > 0) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<HotelRoom> subRoot = subquery.from(HotelRoom.class);
                subquery.select(criteriaBuilder.literal(1L));

                Predicate hotelMatchPredicate = criteriaBuilder.equal(subRoot.get("hotel"), root);
                                                                                    
                Predicate capacityPredicate = criteriaBuilder.greaterThanOrEqualTo(subRoot.get("maxAdults"),
                        requestDto.getNumAdults());

                subquery.where(hotelMatchPredicate, capacityPredicate);

                predicates.add(criteriaBuilder.exists(subquery));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}