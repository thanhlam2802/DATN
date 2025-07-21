package backend.backend.implement;

import backend.backend.dao.BookingTourDAO;
import backend.backend.dao.DepartureDAO;
import backend.backend.dao.TourDAO;
import backend.backend.dao.UserDAO;
import backend.backend.dao.OrderDAO;
import backend.backend.dto.BookingTourDto;
import backend.backend.dto.BookingTourRequestDto;
import backend.backend.entity.BookingTour;
import backend.backend.entity.Departure;
import backend.backend.entity.Tour;
import backend.backend.entity.User;
import backend.backend.entity.Order;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.service.BookingTourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class BookingTourServiceImpl implements BookingTourService {

    @Autowired private DepartureDAO departureDAO;
    @Autowired private BookingTourDAO bookingTourDAO;
    @Autowired private UserDAO userDAO;
    @Autowired private TourDAO tourDAO;
    @Autowired private OrderDAO orderDAO;

    /**
     * Tạo một booking tour mới và cập nhật tổng tiền của giỏ hàng.
     */
    @Override
    @Transactional
    public BookingTourDto createBookingTour(BookingTourRequestDto request) {
        // 1. Lấy các đối tượng entity từ database
        User user = userDAO.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + request.getUserId()));

        Tour tour = tourDAO.findById(request.getTourId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tour với ID: " + request.getTourId()));

        Departure departure = departureDAO.findById(request.getDepartureId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ngày khởi hành với ID: " + request.getDepartureId()));
        
        // Lấy order (giỏ hàng hoặc đơn hàng đang chờ) đã tồn tại
        Order order;
        if (request.getOrderId() != null) {
            order = orderDAO.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy order với ID: " + request.getOrderId()));
        } else {
            throw new IllegalArgumentException("OrderId là bắt buộc để thêm sản phẩm.");
        }

        // 2. Kiểm tra bảo mật
        if (!order.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Bạn không có quyền thêm sản phẩm vào giỏ hàng này.");
        }
        
        // 3. Tính toán giá tiền cho booking tour MỚI
        BigDecimal adultPrice = departure.getAdultPrice();
        BigDecimal childPrice = departure.getChildPrice();
        BigDecimal totalAdultPrice = adultPrice.multiply(new BigDecimal(request.getNumberOfAdults()));
        BigDecimal totalChildPrice = childPrice.multiply(new BigDecimal(request.getNumberOfChildren()));
        BigDecimal newBookingPrice = totalAdultPrice.add(totalChildPrice);

        // 4. *** THAY ĐỔI QUAN TRỌNG: Cập nhật tổng tiền của Order cha ***
        BigDecimal currentOrderAmount = order.getAmount() != null ? order.getAmount() : BigDecimal.ZERO;
        order.setAmount(currentOrderAmount.add(newBookingPrice));
        orderDAO.save(order); 

        // 5. Tạo và lưu BookingTour mới
        BookingTour bookingTour = new BookingTour();
        bookingTour.setDeparture(departure);
        bookingTour.setOrder(order);
        bookingTour.setCustomerName(request.getCustomerName());
        bookingTour.setPhone(request.getPhone());
        bookingTour.setNumberOfAdults(request.getNumberOfAdults());
        bookingTour.setNumberOfChildren(request.getNumberOfChildren());
        bookingTour.setTotalPrice(newBookingPrice); 
        bookingTour.setBookingDate(LocalDate.now());
        bookingTour.setNotes(request.getNotes());

        bookingTourDAO.save(bookingTour);
        return toDto(bookingTour);
    }

    /**
     * Lấy thông tin chi tiết của một lần đặt tour bằng ID của nó.
     */
    @Override
    public BookingTourDto getBookingTourById(Integer id) {
        BookingTour booking = bookingTourDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking tour với ID: " + id));
        return toDto(booking);
    }
    
    /**
     * Phương thức nội bộ để chuyển đổi từ Entity sang DTO.
     */
    private BookingTourDto toDto(BookingTour entity) {
        Departure departure = entity.getDeparture();
        Tour tour = departure.getTour();
        return BookingTourDto.builder()
                .id(entity.getId())
                .tourId(tour.getId())
                .tourName(tour.getName())
                .departureDate(departure.getDepartureDate())
                .numberOfAdults(entity.getNumberOfAdults())
                .numberOfChildren(entity.getNumberOfChildren())
                .adultPrice(departure.getAdultPrice())
                .childPrice(departure.getChildPrice())
                .totalPrice(entity.getTotalPrice())
                .build();
    }
}