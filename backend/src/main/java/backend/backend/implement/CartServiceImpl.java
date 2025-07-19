package backend.backend.implement;

import backend.backend.dao.*;
import backend.backend.dto.*;
import backend.backend.entity.*;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired private TicketDetailDAO ticketDetailDAO;
    @Autowired private UserDAO userDAO;

    // Các DAO cho từng loại sản phẩm trong giỏ hàng
    @Autowired private FlightBookingDAO flightBookingDAO;
    @Autowired private HotelBookingDAO hotelBookingDAO;
    @Autowired private BusBookingDAO busBookingDAO;
    @Autowired private BookingTourDAO bookingTourDAO;

    /**
     * Tạo một giỏ hàng trống mới cho người dùng.
     */
    @Override
    @Transactional
    public TicketDetailDto createCartForUser(Integer userId) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        // Kiểm tra xem người dùng đã có giỏ hàng nào đang ở trạng thái 'CART' chưa
        // để tránh tạo nhiều giỏ hàng song song. (Tùy chọn nhưng nên có)
        // ticketDetailDAO.findByUserAndStatus(user, "CART").ifPresent(cart -> {
        //     throw new IllegalStateException("Người dùng đã có một giỏ hàng đang hoạt động.");
        // });

        TicketDetail newCart = new TicketDetail();
        newCart.setUser(user);
        // Trạng thái và createdAt được tự động gán bởi @PrePersist trong Entity

        TicketDetail savedCart = ticketDetailDAO.save(newCart);
        // Trả về DTO của giỏ hàng trống
        return toTicketDetailDTO(savedCart);
    }

    /**
     * Lấy thông tin chi tiết của một giỏ hàng, bao gồm tất cả sản phẩm bên trong.
     */
    @Override
    public TicketDetailDto getCartById(Integer cartId) {
        TicketDetail cart = ticketDetailDAO.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng với ID: " + cartId));
        return toTicketDetailDTO(cart);
    }

    /**
     * Xóa một sản phẩm khỏi giỏ hàng.
     */
    @Override
    @Transactional
    public TicketDetailDto removeItemFromCart(Integer itemId, String itemType) {
        Integer cartId;
        switch (itemType.toUpperCase()) {
            case "FLIGHT":
                FlightBooking flight = flightBookingDAO.findById(itemId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vé máy bay với ID: " + itemId));
                cartId = flight.getTicketDetail().getId();
                flightBookingDAO.delete(flight);
                break;
            case "HOTEL":
                HotelBooking hotel = hotelBookingDAO.findById(itemId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đặt phòng khách sạn với ID: " + itemId));
                cartId = hotel.getTicketDetail().getId();
                hotelBookingDAO.delete(hotel);
                break;
            case "BUS":
                 BusBooking bus = busBookingDAO.findById(itemId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vé xe buýt với ID: " + itemId));
                cartId = bus.getTicketDetail().getId();
                busBookingDAO.delete(bus);
                break;
            case "TOUR":
                 BookingTour tour = bookingTourDAO.findById(itemId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đặt tour với ID: " + itemId));
                cartId = tour.getTicketDetail().getId();
                bookingTourDAO.delete(tour);
                break;
            default:
                throw new IllegalArgumentException("Loại sản phẩm không hợp lệ: " + itemType);
        }

        // Tải lại và trả về giỏ hàng sau khi đã xóa sản phẩm
        return getCartById(cartId);
    }

    // =================================================================
    //  PRIVATE HELPER METHODS (CHUYỂN ĐỔI SANG DTO)
    // =================================================================

    /**
     * Chuyển đổi TicketDetail Entity thành DTO, bao gồm tất cả các sản phẩm con.
     * Đây là thay đổi quan trọng nhất.
     */
    private TicketDetailDto toTicketDetailDTO(TicketDetail entity) {
        TicketDetailDto dto = new TicketDetailDto();
        dto.setId(entity.getId());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());

        // Thay vì ticketBookingId, ta lấy orderId (nếu có)
        if (entity.getOrder() != null) {
            dto.setOrderId(entity.getOrder().getId());
        }

        // Lấy danh sách các sản phẩm có trong giỏ hàng
        // Giả sử entity TicketDetail của bạn đã có các List<> này
        dto.setFlightBookings(entity.getFlightBookings() != null ?
                entity.getFlightBookings().stream().map(this::toFlightBookingDTO).collect(Collectors.toList()) :
                Collections.emptyList());

        dto.setHotelBookings(entity.getHotelBookings() != null ?
                entity.getHotelBookings().stream().map(this::toHotelBookingDTO).collect(Collectors.toList()) :
                Collections.emptyList());

        dto.setBusBookings(entity.getBusBookings() != null ?
                entity.getBusBookings().stream().map(this::toBusBookingDTO).collect(Collectors.toList()) :
                Collections.emptyList());

        dto.setTourBookings(entity.getTourBookings() != null ?
                entity.getTourBookings().stream().map(this::toBookingTourDTO).collect(Collectors.toList()) :
                Collections.emptyList());

        return dto;
    }
    
    
    // viết thêm cái của minh vao nhan mấy ông 

    private FlightBookingDto toFlightBookingDTO(FlightBooking entity) {
        FlightBookingDto dto = new FlightBookingDto();
      
        return dto;
    }

    private HotelBookingDto toHotelBookingDTO(HotelBooking entity) {
        HotelBookingDto dto = new HotelBookingDto();
       
        return dto;
    }

    private BusBookingDto toBusBookingDTO(BusBooking entity) {
        BusBookingDto dto = new BusBookingDto();
      
        return dto;
    }

    private BookingTourDto toBookingTourDTO(BookingTour entity) {
        BookingTourDto dto = new BookingTourDto(null, null, null, null, 0, 0, null, null, null);
      
        return dto;
    }
}