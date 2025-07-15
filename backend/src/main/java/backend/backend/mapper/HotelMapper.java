package backend.backend.mapper;

import backend.backend.dto.Hotel.HotelDetailDto;
import backend.backend.entity.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface HotelMapper {
    @Mappings({
        @Mapping(target = "id", source = "id"),
        @Mapping(target = "name", source = "name"),
        @Mapping(target = "address", source = "address"),
        @Mapping(target = "description", source = "description"),
        @Mapping(target = "starRating", source = "starRating"),
        @Mapping(target = "email", source = "email"),
        @Mapping(target = "phone", source = "phone")
    })
    Hotel toEntity(HotelDetailDto dto);

    HotelDetailDto toDto(Hotel entity);
} 