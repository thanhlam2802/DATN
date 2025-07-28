package backend.backend.mapper;

import backend.backend.dto.FlightBookingDto;
import backend.backend.entity.FlightBooking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FlightBookingMapper {

    FlightBookingMapper INSTANCE = Mappers.getMapper(FlightBookingMapper.class);

    @Mapping(source = "flightSlot.id", target = "flightSlotId")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "order.id", target = "orderId")
    FlightBookingDto toDto(FlightBooking flightBooking);
}