package backend.backend.implement;

import backend.backend.dao.BookingTourDAO;
import backend.backend.dao.DepartureDAO;
import backend.backend.dao.TicketDetailDAO;
import backend.backend.dao.TourDAO;
import backend.backend.dao.UserDAO;
import backend.backend.dto.BookingTourDto;
import backend.backend.dto.BookingTourRequestDto;
import backend.backend.entity.BookingTour;
import backend.backend.entity.Departure;
import backend.backend.entity.TicketDetail;
import backend.backend.entity.Tour;
import backend.backend.entity.User;
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
    @Autowired private TicketDetailDAO ticketDetailDAO;

    /**
     * Tạo một booking tour mới (thêm sản phẩm tour vào giỏ hàng).
     */
    @Override
    @Transactional
    public BookingTourDto createBookingTour(BookingTourRequestDto request) {
        // 1. Lấy các đối tượng entity từ database dựa trên ID trong request
        User user = userDAO.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + request.getUserId()));

        Tour tour = tourDAO.findById(request.getTourId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tour với ID: " + request.getTourId()));
        Departure departure = departureDAO.findById(request.getDepartureId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ngày khởi hành với ID: " + request.getDepartureId()));
        // Lấy giỏ hàng (TicketDetail) đã tồn tại
        TicketDetail cart = ticketDetailDAO.findById(request.getCartId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng với ID: " + request.getCartId()));

        // 2. Kiểm tra bảo mật: Đảm bảo người dùng chỉ có thể thêm vào giỏ hàng của chính mình
        if (!cart.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Bạn không có quyền thêm sản phẩm vào giỏ hàng này.");
        }
        
     // 3. Tính toán tổng giá tiền cho booking này theo logic mới
        BigDecimal adultPrice = departure.getAdultPrice(); // Lấy giá người lớn từ Departure
        BigDecimal childPrice = departure.getChildPrice(); // Lấy giá trẻ em từ Departure

        // Tính tổng tiền cho người lớn
        BigDecimal totalAdultPrice = adultPrice.multiply(new BigDecimal(request.getNumberOfAdults()));

        // Tính tổng tiền cho trẻ em
        BigDecimal totalChildPrice = childPrice.multiply(new BigDecimal(request.getNumberOfChildren()));

        // Tổng giá tiền cuối cùng là tổng của hai loại trên
        BigDecimal totalPrice = totalAdultPrice.add(totalChildPrice);
     // 4. Tạo đối tượng BookingTour mới theo entity đã sửa
     BookingTour newBooking = new BookingTour();

     // Gán các giá trị từ request DTO đã được cập nhật
     newBooking.setCustomerName(request.getCustomerName());
     newBooking.setPhone(request.getPhone());
     newBooking.setNumberOfAdults(request.getNumberOfAdults());
     newBooking.setNumberOfChildren(request.getNumberOfChildren());
     newBooking.setNotes(request.getNotes());

     // Gán các đối tượng và giá trị đã được xử lý trong service
     newBooking.setDeparture(departure); 
     newBooking.setTicketDetail(cart);  
     newBooking.setTotalPrice(totalPrice);
     newBooking.setBookingDate(LocalDate.now()); 

        // 5. Lưu booking mới vào database
        BookingTour savedBooking = bookingTourDAO.save(newBooking);

        // 6. Chuyển đổi sang DTO để trả về cho client
        return toDto(savedBooking);
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
     * Phương thức nội bộ để chuyển đổi từ Entity sang DTO (phiên bản đã sửa).
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