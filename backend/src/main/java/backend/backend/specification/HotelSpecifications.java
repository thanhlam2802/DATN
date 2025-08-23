package backend.backend.specification;

import backend.backend.dto.Hotel.HotelSearchRequestDto;
import backend.backend.entity.Hotel;
import backend.backend.entity.Amenity;
import backend.backend.entity.HotelRoom;
import backend.backend.entity.HotelRoomVariant;
import backend.backend.entity.Province;
import backend.backend.entity.HotelBooking;
import backend.backend.entity.Order;
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

            if (requestDto.getCreatedAtFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), requestDto.getCreatedAtFrom().atStartOfDay()));
            }
            if (requestDto.getCreatedAtTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), requestDto.getCreatedAtTo().atTime(23, 59, 59)));
            }

            if (requestDto.getRoomStatus() != null && !requestDto.getRoomStatus().isEmpty()) {
                predicates.add(buildRoomStatusPredicate(root, query, criteriaBuilder, requestDto.getRoomStatus()));
            }

            if (requestDto.getIsAdminRequest() == null || !requestDto.getIsAdminRequest()) {
                String approvalStatus = requestDto.getApprovalStatus();
                if (approvalStatus == null || approvalStatus.isEmpty()) {
                    approvalStatus = "APPROVED";
                }
                predicates.add(criteriaBuilder.equal(root.get("approvalStatus"), approvalStatus));

                String status = requestDto.getStatus();
                if (status == null || status.isEmpty()) {
                    status = "ACTIVE";
                }
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            } else {
                if (requestDto.getApprovalStatus() != null && !requestDto.getApprovalStatus().isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("approvalStatus"), requestDto.getApprovalStatus()));
                }
                if (requestDto.getStatus() != null && !requestDto.getStatus().isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), requestDto.getStatus()));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate buildRoomStatusPredicate(Root<Hotel> root, jakarta.persistence.criteria.CriteriaQuery<?> query, 
                                                   jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder, String roomStatus) {
        Subquery<Long> roomStatusSubquery = query.subquery(Long.class);
        Root<HotelRoom> roomRoot = roomStatusSubquery.from(HotelRoom.class);
        
        Subquery<Long> bookedRoomsSubquery = roomStatusSubquery.subquery(Long.class);
        Root<HotelBooking> bookingRoot = bookedRoomsSubquery.from(HotelBooking.class);
        Join<HotelBooking, Order> orderJoin = bookingRoot.join("order");
        
        bookedRoomsSubquery.select(criteriaBuilder.count(bookingRoot.get("id")))
                .where(
                    criteriaBuilder.equal(bookingRoot.get("roomVariant").get("room"), roomRoot),
                    criteriaBuilder.equal(orderJoin.get("status"), "PAID"),
                    criteriaBuilder.lessThanOrEqualTo(bookingRoot.get("checkInDate"), criteriaBuilder.currentTimestamp()),
                    criteriaBuilder.greaterThan(bookingRoot.get("checkOutDate"), criteriaBuilder.currentTimestamp())
                );

        Expression<Long> bookedRooms = bookedRoomsSubquery.getSelection();
        Expression<Long> availableRooms = criteriaBuilder.diff(roomRoot.get("roomQuantity"), criteriaBuilder.coalesce(bookedRooms, 0L));

        roomStatusSubquery.select(criteriaBuilder.literal(1L));

        if ("out_of_stock".equals(roomStatus)) {
            roomStatusSubquery.where(
                criteriaBuilder.equal(roomRoot.get("hotel"), root),
                criteriaBuilder.lessThanOrEqualTo(availableRooms, 0L)
            );
        } else if ("nearly_out_of_stock".equals(roomStatus)) {
            roomStatusSubquery.where(
                criteriaBuilder.equal(roomRoot.get("hotel"), root),
                criteriaBuilder.greaterThan(availableRooms, 0L),
                criteriaBuilder.lessThanOrEqualTo(availableRooms, 5L)
            );
        }

        return criteriaBuilder.exists(roomStatusSubquery);
    }
}