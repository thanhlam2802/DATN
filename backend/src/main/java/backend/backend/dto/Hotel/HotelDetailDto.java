package backend.backend.dto.Hotel;

import backend.backend.entity.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelDetailDto {
        private Integer id;
        private String name;
        private String address;
        private String description;
        private Short starRating;
        private String email;
        private String phone;

        private Integer provinceId;
        private String provinceName;

        private List<String> imageUrls;
        private double rating;
        private int reviewCount;
        private List<HotelRoomDto> availableRooms;
        private List<AmenityDto> amenities;
        
        private String approvalStatus;
        private String approvalReason;
        private java.time.LocalDateTime approvedAt;
        private String approvedBy;
        private String status;

        public static HotelDetailDto fromEntity(Hotel hotel, double rating, int reviewCount,
                        Set<Integer> bookedVariantIds) {
                List<String> images = hotel.getHotelImages() != null ? hotel.getHotelImages().stream()
                                .map(img -> img.getImage().getUrl())
                                .collect(Collectors.toList()) : Collections.emptyList();

                List<HotelRoomDto> roomDtoList = hotel.getHotelRooms() != null ? hotel.getHotelRooms().stream()
                                .map(room -> HotelRoomDto.fromEntity(room, bookedVariantIds))
                                .filter(roomDto -> !roomDto.getAvailableVariants().isEmpty())
                                .collect(Collectors.toList()) : Collections.emptyList();

                List<AmenityDto> allAmenities = hotel.getHotelRooms() != null ? hotel.getHotelRooms().stream()
                                .flatMap(room -> room.getAmenities().stream())
                                .distinct()
                                .map(AmenityDto::fromEntity)
                                .sorted(Comparator.comparing(AmenityDto::getId))
                                .collect(Collectors.toList())
                                : Collections.emptyList();

                Integer pId = (hotel.getProvince() != null) ? hotel.getProvince().getId() : null;
                String pName = (hotel.getProvince() != null) ? hotel.getProvince().getName() : null;

                return new HotelDetailDto(
                                hotel.getId(),
                                hotel.getName(),
                                hotel.getAddress(),
                                hotel.getDescription(),
                                hotel.getStarRating(),
                                hotel.getEmail(),
                                hotel.getPhone(),
                                pId,
                                pName,
                                images,
                                rating,
                                reviewCount,
                                roomDtoList,
                                allAmenities,
                                hotel.getApprovalStatus(),
                                hotel.getApprovalReason(),
                                hotel.getApprovedAt(),
                                hotel.getApprovedBy(),
                                hotel.getStatus());
        }
}