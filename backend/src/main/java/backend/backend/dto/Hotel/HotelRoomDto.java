package backend.backend.dto.Hotel;

import backend.backend.entity.HotelRoom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelRoomDto {
        private Integer id;
        private String roomType;
        private String bedType;
        private Short maxAdults;
        private Short maxChildren;
        private Short roomQuantity;
        private Integer roomArea;
        private List<String> imageUrls;
        private List<AmenityDto> amenities;
        private List<HotelRoomVariantDto> availableVariants;

        public static HotelRoomDto fromEntity(HotelRoom room, Set<Integer> bookedVariantIds) {
                List<String> images = room.getRoomImages() != null ? room.getRoomImages().stream()
                                .map(img -> img.getImage().getUrl())
                                .collect(Collectors.toList()) : Collections.emptyList();

                List<AmenityDto> amenityList = room.getAmenities() != null ? room.getAmenities().stream()
                                .map(AmenityDto::fromEntity)
                                .collect(Collectors.toList()) : Collections.emptyList();

                List<HotelRoomVariantDto> availableVariantList = room.getRoomVariants() != null
                                ? room.getRoomVariants().stream()
                                                .filter(variant -> !bookedVariantIds.contains(variant.getId()))
                                                .map(HotelRoomVariantDto::fromEntity)
                                                .collect(Collectors.toList())
                                : Collections.emptyList();

                return new HotelRoomDto(
                                room.getId(),
                                room.getRoomType(),
                                room.getBedType(),
                                room.getMaxAdults(),
                                room.getMaxChildren(),
                                room.getRoomQuantity(),
                                room.getRoomArea(),
                                images,
                                amenityList,
                                availableVariantList);
        }
}