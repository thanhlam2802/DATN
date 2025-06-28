package backend.backend.specification;

import backend.backend.dto.Hotel.HotelSearchRequestDto;
import backend.backend.entity.Hotel;
import backend.backend.entity.Amenity;
import backend.backend.entity.HotelRoom;
import backend.backend.entity.HotelRoomVariant;
import backend.backend.entity.Province;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

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
                predicates.add(criteriaBuilder.equal(root.get("starRating"), requestDto.getMinStarRating()));
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

            if (requestDto.getMinPrice() != null || requestDto.getMaxPrice() != null) {
                Subquery<BigDecimal> minPriceSubquery = query.subquery(BigDecimal.class);
                Root<HotelRoomVariant> variantRoot = minPriceSubquery.from(HotelRoomVariant.class);
                minPriceSubquery.select(criteriaBuilder.min(variantRoot.get("price")))
                        .where(criteriaBuilder.equal(variantRoot.get("room").get("hotel"), root));

                Expression<BigDecimal> hotelMinPrice = minPriceSubquery.getSelection();

                if (requestDto.getMinPrice() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(hotelMinPrice, requestDto.getMinPrice()));
                }
                if (requestDto.getMaxPrice() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(hotelMinPrice, requestDto.getMaxPrice()));
                }
            }

            if (requestDto.getAmenities() != null && !requestDto.getAmenities().isEmpty()) {
                for (String amenityName : requestDto.getAmenities()) {
                    Subquery<Long> amenitySubquery = query.subquery(Long.class);
                    Root<HotelRoom> subRootRoom = amenitySubquery.from(HotelRoom.class);
                    Join<HotelRoom, Amenity> amenityJoin = subRootRoom.join("amenities");

                    amenitySubquery.select(criteriaBuilder.literal(1L))
                            .where(
                                    criteriaBuilder.equal(subRootRoom.get("hotel"), root),
                                    criteriaBuilder.equal(amenityJoin.get("name"), amenityName));
                    predicates.add(criteriaBuilder.exists(amenitySubquery));
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}