package backend.backend.implement;

import backend.backend.dao.BookingTourDAO;
import backend.backend.dto.TopTourDTO;
import backend.backend.dao.DepartureDAO;
import backend.backend.dao.TourDAO;
import backend.backend.dao.UserDAO;
import backend.backend.dao.OrderDAO;
import backend.backend.dto.BookingTourDto;
import backend.backend.dto.BookingTourRequestDto;
import backend.backend.dto.MyTourBookingDTO;
import backend.backend.dto.StatsDTO;
import backend.backend.entity.BookingTour;
import backend.backend.entity.Departure;
import backend.backend.entity.Tour;
import backend.backend.entity.User;
import backend.backend.entity.Order;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.service.BookingTourService;
import backend.backend.specification.BookingTourSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    
    @Override
    @Transactional
    public BookingTourDto updateBookingTour(Integer id, BookingTourRequestDto requestDto) {
        // 1. Tìm booking tour entity từ database
        BookingTour bookingTour = bookingTourDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy booking tour với ID: " + id));
        
        Departure departure = bookingTour.getDeparture();
        Order order = bookingTour.getOrder();

        // 2. Xử lý cập nhật số chỗ đã đặt (bookedSeats)
        int oldSeats = bookingTour.getNumberOfAdults() + bookingTour.getNumberOfChildren();
        int newSeats = requestDto.getNumberOfAdults() + requestDto.getNumberOfChildren();
        int seatDifference = newSeats - oldSeats;

        if (seatDifference != 0) {
            int availableSeats = departure.getSeatCount() - departure.getBookedSeats();
            if (seatDifference > availableSeats) {
                throw new RuntimeException("Không đủ chỗ trống cho tour. Chỉ còn " + availableSeats + " chỗ.");
            }
            departure.setBookedSeats(departure.getBookedSeats() + seatDifference);
            departureDAO.save(departure);
        }

        // 3. Cập nhật thông tin cơ bản của booking
        bookingTour.setCustomerName(requestDto.getCustomerName());
        bookingTour.setPhone(requestDto.getPhone());
        bookingTour.setEmail(requestDto.getEmail());
        bookingTour.setNotes(requestDto.getNotes());
        bookingTour.setNumberOfAdults(requestDto.getNumberOfAdults());
        bookingTour.setNumberOfChildren(requestDto.getNumberOfChildren());
        
        // 4. Tính toán lại tổng tiền của booking tour này
        BigDecimal oldTotalPrice = bookingTour.getTotalPrice();
        BigDecimal newTotalPrice = calculateTotalPrice(departure, requestDto.getNumberOfAdults(), requestDto.getNumberOfChildren());
        bookingTour.setTotalPrice(newTotalPrice);
        
        // 5. Cập nhật tổng tiền của cả đơn hàng (Order)
        BigDecimal priceDifference = newTotalPrice.subtract(oldTotalPrice);
        order.setAmount(order.getAmount().add(priceDifference));
        if (order.getOriginalAmount() != null) {
            order.setOriginalAmount(order.getOriginalAmount().add(priceDifference));
        }
        orderDAO.save(order);

        // 6. Lưu booking tour đã cập nhật và trả về DTO
        BookingTour savedBookingTour = bookingTourDAO.save(bookingTour);
        
        return toDto(savedBookingTour);
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
    /**
     * Lấy và lọc danh sách tất cả booking cho trang quản trị.
     * Phương pháp này sử dụng JPA Specifications để tạo câu lệnh query động,
     * giúp lọc hiệu quả ở tầng cơ sở dữ liệu.
     *
     * @param params Các tham số để lọc (ví dụ: tourId, departureDate, status).
     * @return Một danh sách các booking đã được lọc và chuyển đổi sang DTO.
     */
    @Override
    @Transactional(readOnly = true)
    public List<MyTourBookingDTO> filterAdminBookings(Map<String, String> params) {
        // 1. Tạo đối tượng Specification từ các tham số lọc.
        Specification<BookingTour> spec = BookingTourSpecification.findByCriteria(params);

        // 2. Dùng DAO để thực thi câu lệnh query.
        // GHI CHÚ TỐI ƯU: Để tránh vấn đề N+1 query, bạn có thể sử dụng EntityGraph
        // hoặc Fetch Join trong Specification để lấy các thực thể liên quan trong 1 câu lệnh.
        List<BookingTour> filteredBookings = bookingTourDAO.findAll(spec);

        // 3. Chuyển đổi kết quả sang DTO và trả về.
        return filteredBookings.stream()
                .map(this::mapToMyBookingDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách các booking của người dùng đang đăng nhập.
     *
     * @return Danh sách booking của người dùng hiện tại.
     * @throws ResourceNotFoundException nếu không tìm thấy người dùng.
     * @throws IllegalStateException nếu không có người dùng nào được xác thực.
     */
    @Override
    @Transactional(readOnly = true)
    public List<MyTourBookingDTO> getMyTourBookings() {
        // CẢI TIẾN: Kiểm tra trạng thái xác thực trước khi lấy thông tin
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new IllegalStateException("Không có người dùng nào được xác thực để thực hiện hành động này.");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userDAO.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với email: " + userDetails.getUsername()));

        List<BookingTour> bookings = bookingTourDAO.findByOrder_User(currentUser);

        return bookings.stream()
                .map(this::mapToMyBookingDTO)
                .collect(Collectors.toList());
    }

    /**
     * Phương thức helper để chuyển đổi một Entity BookingTour sang MyTourBookingDTO.
     * Đã được làm cho an toàn hơn bằng cách kiểm tra null.
     *
     * @param booking Đối tượng Entity BookingTour.
     * @return Đối tượng DTO tương ứng.
     */
    private MyTourBookingDTO mapToMyBookingDTO(BookingTour booking) {
        MyTourBookingDTO dto = new MyTourBookingDTO();
        dto.setBookingId(booking.getId());

        // CẢI TIẾN: Thêm các kiểm tra null để tránh NullPointerException
        if (booking.getOrder() != null) {
            dto.setOrderStatus(booking.getOrder().getStatus());
        }

        if (booking.getDeparture() != null) {
            dto.setDepartureDate(booking.getDeparture().getDepartureDate());
            if (booking.getDeparture().getTour() != null) {
                dto.setTourName(booking.getDeparture().getTour().getName());
            }
        }

        dto.setTotalPrice(booking.getTotalPrice());
        dto.setNumberOfAdults(booking.getNumberOfAdults());
        dto.setNumberOfChildren(booking.getNumberOfChildren());
        dto.setBookingDate(booking.getBookingDate());
        dto.setName(booking.getCustomerName());
        dto.setPhone(booking.getPhone());
        dto.setNote(booking.getNotes());
        dto.setEmail(booking.getEmail());

        return dto;
    }

    @Override
    public StatsDTO getStatsByDateRange(Long userId,LocalDate startDate, LocalDate endDate) {
    	  LocalDate endDatePlusOne = null;
          if (endDate != null) {
              endDatePlusOne = endDate.plusDays(1);
          }

          // 2. Gọi DAO với tham số đã được sửa lại
          return bookingTourDAO.getStatsByDateRange(userId, startDate, endDatePlusOne);
      
    }

    @Override
    public List<TopTourDTO> getTopSellingTours(Long userId,LocalDate startDate, LocalDate endDate, int limit) {
        // Tạo Pageable để chỉ lấy `limit` kết quả đầu tiên
        Pageable pageable = PageRequest.of(0, limit);
        return bookingTourDAO.findTopSellingTours(userId, startDate, endDate, pageable);
    }
    
    
    @Override
    @Transactional 
    public Page<MyTourBookingDTO> getPaidBookingsByDateRange(Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        try {
            Map<String, String> params = new HashMap<>();
            if (userId != null) {
                params.put("ownerId", userId.toString());
            }
            if (startDate != null) {
                params.put("startDate", startDate.toString());
            }
            if (endDate != null) {
                params.put("endDate", endDate.toString());
            }
            params.put("status", "PAID,CONFIRMED");

            // 👉 Log tham số
            System.out.println("==== [getPaidBookingsByDateRange] Params ====");
            params.forEach((k, v) -> System.out.println(k + " = " + v));

            Specification<BookingTour> spec = BookingTourSpecification.findByCriteria(params);

            Page<BookingTour> bookingTourPage = bookingTourDAO.findAll(spec, pageable);

           

            return bookingTourPage.map(this::mapToMyBookingDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    private BigDecimal calculateTotalPrice(Departure departure, int numAdults, int numChildren) {
        BigDecimal adultTotal = departure.getAdultPrice().multiply(BigDecimal.valueOf(numAdults));
        BigDecimal childTotal = departure.getChildPrice().multiply(BigDecimal.valueOf(numChildren));
        return adultTotal.add(childTotal);
    }
    
}