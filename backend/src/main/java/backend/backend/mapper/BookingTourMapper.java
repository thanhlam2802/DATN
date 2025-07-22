package backend.backend.mapper;

import backend.backend.dto.BookingTourDto;
import backend.backend.entity.BookingTour;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BookingTourMapper {

    BookingTourMapper INSTANCE = Mappers.getMapper(BookingTourMapper.class);

    @Mapping(source = "departure.tour.id", target = "tourId")
    @Mapping(source = "departure.tour.name", target = "tourName")
    @Mapping(source = "departure.departureDate", target = "departureDate")
    @Mapping(source = "departure.adultPrice", target = "adultPrice")
    @Mapping(source = "departure.childPrice", target = "childPrice")
    @Mapping(source = "order.id", target = "orderId")
    BookingTourDto toDto(BookingTour bookingTour);
}