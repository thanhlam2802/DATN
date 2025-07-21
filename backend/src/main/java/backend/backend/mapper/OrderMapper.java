package backend.backend.mapper;

import backend.backend.dto.OrderDto;
import backend.backend.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {
    BookingTourMapper.class,
    FlightBookingMapper.class
    // Thêm các mapper khác (Hotel, Bus) vào đây khi bạn tạo chúng
})
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "voucher.id", target = "voucherId")
    @Mapping(source = "destination.id", target = "destinationId")
    @Mapping(source = "bookingTours", target = "tourBookings") 
    @Mapping(source = "flightBookings", target = "flightBookings")
    OrderDto toDto(Order order);
}