package backend.backend.dao.Hotel;

import backend.backend.dto.Hotel.AmenityDto;
import backend.backend.dto.Hotel.HotelDto;
import backend.backend.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.Order;

public class HotelDAOImpl implements HotelDAOCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<HotelDto> findWithFiltersAndProjection(Specification<Hotel> spec, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<HotelDto> query = cb.createQuery(HotelDto.class);
        Root<Hotel> hotelRoot = query.from(Hotel.class);

        Subquery<String> imageUrlSubquery = query.subquery(String.class);
        Root<HotelImage> hiRoot = imageUrlSubquery.from(HotelImage.class);
        Subquery<Integer> minImageIdSubquery = imageUrlSubquery.subquery(Integer.class);
        Root<HotelImage> minHiRoot = minImageIdSubquery.from(HotelImage.class);
        minImageIdSubquery.select(cb.min(minHiRoot.get("id").get("imageId")))
                .where(cb.equal(minHiRoot.get("hotel"), hotelRoot));
        imageUrlSubquery.select(hiRoot.get("image").get("url"))
                .where(cb.equal(hiRoot.get("id").get("imageId"), minImageIdSubquery));

        Subquery<Double> avgRatingSubquery = query.subquery(Double.class);
        Root<Review> reviewRootAvg = avgRatingSubquery.from(Review.class);
        avgRatingSubquery.select(cb.coalesce(cb.avg(reviewRootAvg.get("rating")), 0.0)).where(
                cb.equal(reviewRootAvg.get("entityId"), hotelRoot.get("id")),
                cb.equal(reviewRootAvg.get("entityType"), "Hotel"));

        Subquery<Long> reviewCountSubquery = query.subquery(Long.class);
        Root<Review> reviewRootCt = reviewCountSubquery.from(Review.class);
        reviewCountSubquery.select(cb.count(reviewRootCt.get("id"))).where(
                cb.equal(reviewRootCt.get("entityId"), hotelRoot.get("id")),
                cb.equal(reviewRootCt.get("entityType"), "Hotel"));

        Subquery<BigDecimal> minPriceSubquery = query.subquery(BigDecimal.class);
        Root<HotelRoomVariant> variantRoot = minPriceSubquery.from(HotelRoomVariant.class);
        minPriceSubquery.select(cb.min(variantRoot.get("price")))
                .where(cb.equal(variantRoot.get("room").get("hotel"), hotelRoot));

        Join<Hotel, Province> provinceJoin = hotelRoot.join("province", JoinType.LEFT);

        query.select(cb.construct(
                HotelDto.class,
                hotelRoot.get("id"),
                hotelRoot.get("name"),
                imageUrlSubquery.getSelection(),
                hotelRoot.get("address"),
                provinceJoin.get("name"),
                hotelRoot.get("starRating"),
                avgRatingSubquery.getSelection(),
                reviewCountSubquery.getSelection().as(Integer.class),
                minPriceSubquery.getSelection(),
                hotelRoot.get("createdAt"),
                hotelRoot.get("updatedAt")
        ));

        Predicate filterPredicate = spec.toPredicate(hotelRoot, query, cb);
        if (filterPredicate != null) {
            query.where(filterPredicate);
        }

        if (pageable.getSort().isSorted()) {
            List<Order> orders = new ArrayList<>();
            pageable.getSort().forEach(order -> {
                Expression<?> sortExpression;
                if ("startingPrice".equalsIgnoreCase(order.getProperty())) {
                    sortExpression = minPriceSubquery.getSelection();
                } else if ("rating".equalsIgnoreCase(order.getProperty())) {
                    sortExpression = avgRatingSubquery.getSelection();
                } else {
                    sortExpression = hotelRoot.get(order.getProperty());
                }

                if (order.isAscending()) {
                    orders.add(cb.asc(sortExpression));
                } else {
                    orders.add(cb.desc(sortExpression));
                }
            });
            query.orderBy(orders);
        }

        TypedQuery<HotelDto> typedQuery = em.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<HotelDto> resultList = typedQuery.getResultList();

        if (!resultList.isEmpty()) {
            List<Integer> hotelIds = resultList.stream().map(HotelDto::getId).collect(Collectors.toList());

            Map<Integer, List<AmenityDto>> amenitiesByHotelId = findAmenitiesForHotels(hotelIds);

            Map<Integer, List<String>> imagesByHotelId = findImagesForHotels(hotelIds);

            Map<Integer, BigDecimal> minDiscountedPriceByHotelId = new HashMap<>();
            for (Integer hotelId : hotelIds) {
                TypedQuery<backend.backend.entity.HotelRoomVariant> q = em.createQuery(
                    "SELECT v FROM HotelRoomVariant v WHERE v.room.hotel.id = :hotelId", backend.backend.entity.HotelRoomVariant.class);
                q.setParameter("hotelId", hotelId);
                List<backend.backend.entity.HotelRoomVariant> variants = q.getResultList();
                BigDecimal min = null;
                for (backend.backend.entity.HotelRoomVariant v : variants) {
                    BigDecimal price = v.getPrice();
                    if (v.getDiscountType() != null && v.getDiscountValue() != null && v.getDiscountValue().compareTo(BigDecimal.ZERO) > 0) {
                        if ("amount".equals(v.getDiscountType())) {
                            price = price.subtract(v.getDiscountValue());
                        } else if ("percent".equals(v.getDiscountType())) {
                            price = price.multiply(BigDecimal.ONE.subtract(v.getDiscountValue().divide(new BigDecimal("100"))));
                        }
                        if (price.compareTo(BigDecimal.ZERO) < 0) price = BigDecimal.ZERO;
                    }
                    if (min == null || price.compareTo(min) < 0) min = price;
                }
                minDiscountedPriceByHotelId.put(hotelId, min);
            }

            resultList.forEach(hotelDto -> {
                hotelDto.setAmenities(amenitiesByHotelId.getOrDefault(hotelDto.getId(), Collections.emptyList()));
                hotelDto.setImageUrls(imagesByHotelId.getOrDefault(hotelDto.getId(), Collections.emptyList()));
                hotelDto.setMinDiscountedPrice(minDiscountedPriceByHotelId.getOrDefault(hotelDto.getId(), null));
            });
        }
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Hotel> countRoot = countQuery.from(Hotel.class);
        Predicate countPredicate = spec.toPredicate(countRoot, countQuery, cb);
        if (countPredicate != null) {
            countQuery.where(countPredicate);
        }
        countQuery.select(cb.countDistinct(countRoot));
        Long total = em.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(resultList, pageable, total);
    }

    private Map<Integer, List<AmenityDto>> findAmenitiesForHotels(List<Integer> hotelIds) {
        if (hotelIds == null || hotelIds.isEmpty()) {
            return Collections.emptyMap();
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Hotel> hotelRoot = query.from(Hotel.class);

        Join<Hotel, HotelRoom> roomJoin = hotelRoot.join("hotelRooms");
        Join<HotelRoom, Amenity> amenityJoin = roomJoin.join("amenities");

        query.multiselect(hotelRoot.get("id"), amenityJoin)
                .where(hotelRoot.get("id").in(hotelIds))
                .distinct(true);

        List<Object[]> results = em.createQuery(query).getResultList();
        return results.stream()
                .collect(Collectors.groupingBy(
                        row -> (Integer) row[0],
                        Collectors.mapping(
                                row -> AmenityDto.fromEntity((Amenity) row[1]),
                                Collectors.toList())));
    }

    private Map<Integer, List<String>> findImagesForHotels(List<Integer> hotelIds) {
        if (hotelIds == null || hotelIds.isEmpty()) {
            return Collections.emptyMap();
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<HotelImage> hotelImageRoot = query.from(HotelImage.class);
        Join<HotelImage, Image> imageJoin = hotelImageRoot.join("image");

        query.multiselect(
                hotelImageRoot.get("hotel").get("id"),
                imageJoin.get("url"),
                hotelImageRoot.get("id").get("imageId")).where(hotelImageRoot.get("hotel").get("id").in(hotelIds))
                .orderBy(cb.asc(hotelImageRoot.get("hotel").get("id")),
                        cb.asc(hotelImageRoot.get("id").get("imageId")));

        List<Object[]> results = em.createQuery(query).getResultList();

        Map<Integer, List<String>> imagesByHotelId = new HashMap<>();

        for (Object[] row : results) {
            Integer hotelId = (Integer) row[0];
            String imageUrl = (String) row[1];
            imagesByHotelId.computeIfAbsent(hotelId, k -> new ArrayList<>());
            if (imagesByHotelId.get(hotelId).size() < 3) {
                imagesByHotelId.get(hotelId).add(imageUrl);
            }
        }
        return imagesByHotelId;
    }
}