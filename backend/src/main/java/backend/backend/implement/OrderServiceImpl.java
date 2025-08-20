package backend.backend.implement;

import backend.backend.controller.OrderController;
import backend.backend.dao.*;
import backend.backend.dto.*;
import backend.backend.entity.*;
import backend.backend.event.VoucherUsedUpEvent;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired private BusBookingDAO busBookingDAO;
    @Autowired private BookingTourDAO bookingTourDAO;
    @Autowired private CustomerDAO customerDAO;
    @Autowired private DepartureDAO departureDAO;
    @Autowired private FlightBookingDAO flightBookingDAO;
    @Autowired private FlightSlotDAO flightSlotDAO;
    @Autowired private HotelBookingDAO hotelBookingDAO;
    @Autowired private OrderDAO orderDAO;
    @Autowired private TourDAO tourDAO;
    @Autowired private UserDAO userDAO;
    @Autowired private VoucherDAO voucherDAO;
    
    @Autowired private  ApplicationEventPublisher eventPublisher;

    

    @Override
    @Transactional
    public OrderDto placeOrder(CheckoutDto checkoutDto) {
        Order order = orderDAO.findById(checkoutDto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + checkoutDto.getOrderId()));

        if (!"CART".equalsIgnoreCase(order.getStatus()) && !"PENDING_PAYMENT".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalStateException("Đơn hàng này đã được xử lý hoặc không hợp lệ.");
        }

        if (checkoutDto.getVoucherId() != null) {
            Voucher voucher = voucherDAO.findById(checkoutDto.getVoucherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Voucher không hợp lệ."));
            order.setVoucher(voucher);
        }
        order.setStatus("PAID");
        order.setPayDate(LocalDateTime.now());
        orderDAO.save(order);

        return toOrderDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Integer id) {
        Order order = orderDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với ID: " + id));
        return toDetailedOrderDto(order);
    }

    @Override
    @Transactional
    public OrderDto createDirectTourReservation(DirectTourReservationRequestDto directRequest) {
        User user = userDAO.findById(directRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + directRequest.getUserId()));

        Departure departure = departureDAO.findById(directRequest.getDepartureId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ngày khởi hành với ID: " + directRequest.getDepartureId()));

        int seatsToBook = directRequest.getNumberOfAdults() + directRequest.getNumberOfChildren();
        int availableSeats = departure.getSeatCount() - departure.getBookedSeats();
        if (seatsToBook > availableSeats) {
            throw new IllegalStateException("Không đủ chỗ trống cho chuyến đi này. Chỉ còn lại " + availableSeats + " chỗ.");
        }
        
        departure.setBookedSeats(departure.getBookedSeats() + seatsToBook);

        BigDecimal adultPrice = departure.getAdultPrice();
        BigDecimal childPrice = departure.getChildPrice();
        BigDecimal totalAdultPrice = adultPrice.multiply(new BigDecimal(directRequest.getNumberOfAdults()));
        BigDecimal totalChildPrice = childPrice.multiply(new BigDecimal(directRequest.getNumberOfChildren()));
        BigDecimal totalPrice = totalAdultPrice.add(totalChildPrice);

        Order temporaryOrder = new Order();
        temporaryOrder.setUser(user);
        temporaryOrder.setAmount(totalPrice);
        temporaryOrder.setStatus("PENDING_PAYMENT");
        temporaryOrder.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        temporaryOrder.setCreatedAt(LocalDateTime.now());
        Order savedOrder = orderDAO.save(temporaryOrder);

        BookingTour bookingTour = new BookingTour();
        bookingTour.setDeparture(departure);
        bookingTour.setOrder(savedOrder);
        bookingTour.setCustomerName(directRequest.getCustomerName());
        bookingTour.setEmail(directRequest.getEmail());  
        bookingTour.setPhone(directRequest.getPhone());
        bookingTour.setNumberOfAdults(directRequest.getNumberOfAdults());
        bookingTour.setNumberOfChildren(directRequest.getNumberOfChildren());
        bookingTour.setNotes(directRequest.getNotes());
        bookingTour.setTotalPrice(totalPrice);
        bookingTour.setBookingDate(LocalDate.now());
        bookingTourDAO.save(bookingTour);

        return toOrderDTO(savedOrder);
    }
    
    @Override
    @Transactional
    public Integer createDirectFlightReservation(DirectFlightReservationRequestDto directRequest) {
        // 1. Lấy user từ context (chuẩn):
        logger.info("fl status sau khi save: {}",SecurityContextHolder.getContext().getAuthentication());

        User user =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        // 2. Lấy slot và flight
        FlightSlot slot = flightSlotDAO.findById(directRequest.getFlightSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy slot ghế."));
        Flight flight = slot.getFlight();

        // 3. Kiểm tra slot đã được đặt chưa
        boolean slotBooked = slot.getStatus().equalsIgnoreCase("used");
        if (slotBooked) {
            throw new IllegalStateException("Vé này đã có người khác đặt. Bạn đã thao tác chậm, vui lòng chọn vé khác!");
        }

        // 4. Tính tổng tiền
        BigDecimal totalPrice = slot.getPrice();

        // 5. Tính expiresAt
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime departureTime = flight.getDepartureTime();
        LocalDateTime expiresAt = now.plusMinutes(30);
        // 6. Tạo order
        Order order = new Order();
        order.setUser(user);
        order.setAmount(totalPrice);
        order.setStatus("PENDING_PAYMENT");
        order.setExpiresAt(expiresAt);
        order.setCreatedAt(now);
        Order savedOrder = orderDAO.save(order);

        // 7. Lưu thông tin khách hàng
        Customer customer = new Customer();
        customer.setFullName(directRequest.getCustomerName());
        customer.setPhone(directRequest.getPhone());
        customer.setEmail(directRequest.getEmail());
        customer.setPassport(directRequest.getPassport());
        customer.setGender("male".equalsIgnoreCase(directRequest.getGender()));
        if (directRequest.getDob() != null && !directRequest.getDob().isEmpty()) {
            customer.setDob(java.time.LocalDate.parse(directRequest.getDob()));
        }
        Customer savedCustomer = customerDAO.save(customer);

        // 8. Tạo booking flight
        FlightBooking booking = new FlightBooking();
        booking.setFlightSlot(slot);
        booking.setOrder(savedOrder);
        booking.setBookingDate(now);
        booking.setTotalPrice(totalPrice);
        booking.setCustomer(savedCustomer);
        flightBookingDAO.save(booking);
        slot.setStatus("USED");
        FlightSlot fl =  flightSlotDAO.save(slot);
        logger.info("fl status sau khi save: {}",fl.getStatus());
        return flightBookingDAO.save(booking).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByUserId(Integer userId) {
        List<Order> orders = orderDAO.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream().map(this::toOrderDTOWithProductInfo).collect(Collectors.toList());
    }

    @Override
    public OrderDto paidOrder(Integer id, String transactionId) {
        Order order = orderDAO.findById(id).orElse(null);
        logger.info("order : {} được ghi nhận.", id);
        if (order != null) {
            order.setTransactionId(transactionId);
            order.setStatus("PAID");
            order.setPayDate(LocalDateTime.now());
          
            
            orderDAO.save(order);
            logger.info("––– Order Saved –––");
        }
        return toOrderDTO(order);
    }
    // --- Mapper Methods ---

    private OrderDto toOrderDTOWithProductInfo(Order entity) {
        OrderDto dto = toOrderDTO(entity);
        String mainProductName = null;

        List<BookingTour> tourBookings = bookingTourDAO.findByOrderId(entity.getId());
        List<FlightBooking> flightBookings = flightBookingDAO.findByOrderId(entity.getId());
        List<HotelBooking> hotelBookings = hotelBookingDAO.findByOrderId(entity.getId());

        int totalItems = tourBookings.size() + flightBookings.size() + hotelBookings.size();

        if (totalItems == 1) {
            if (!tourBookings.isEmpty()) {
                mainProductName = tourBookings.get(0).getDeparture().getTour().getName();
            } else if (!flightBookings.isEmpty()) {
                mainProductName = flightBookings.get(0).getFlightSlot().getFlight().getName();
            } else if (!hotelBookings.isEmpty()) {
                HotelBooking hotel = hotelBookings.get(0);
                String hotelName = "Khách sạn";
                if (hotel.getRoomVariant() != null && hotel.getRoomVariant().getRoom() != null && hotel.getRoomVariant().getRoom().getHotel() != null) {
                    hotelName = hotel.getRoomVariant().getRoom().getHotel().getName();
                }
                String variantName = hotel.getRoomVariant() != null ? hotel.getRoomVariant().getVariantName() : "";
                mainProductName = hotelName + (variantName.isEmpty() ? "" : (" - " + variantName));
            }
        }

        dto.setMainProduct(totalItems > 1 ? "Nhiều dịch vụ" : mainProductName);
        return dto;
    }

    private OrderDto toOrderDTO(Order entity) {
        if (entity == null) return null;
        OrderDto dto = new OrderDto();
        dto.setId(entity.getId());
        dto.setAmount(entity.getAmount());
        dto.setStatus(entity.getStatus());
        dto.setPayDate(entity.getPayDate());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setExpiresAt(entity.getExpiresAt());
        dto.setTransactionId(entity.getTransactionId());
        if (entity.getUser() != null) dto.setUserId(entity.getUser().getId());
        if (entity.getVoucher() != null) dto.setVoucherId(entity.getVoucher().getId());
        if (entity.getDestination() != null) dto.setDestinationId(entity.getDestination().getId());
        return dto;
    }

    private OrderDto toDetailedOrderDto(Order entity) {
        OrderDto dto = toOrderDTO(entity);
        dto.setTourBookings(bookingTourDAO.findByOrderId(entity.getId()).stream().map(this::toBookingTourDto).collect(Collectors.toList()));
        dto.setFlightBookings(flightBookingDAO.findByOrderId(entity.getId()).stream().map(this::toFlightBookingDto).collect(Collectors.toList()));
        dto.setHotelBookings(hotelBookingDAO.findByOrderId(entity.getId()).stream().map(this::toHotelBookingDto).collect(Collectors.toList()));
        return dto;
    }

    private BookingTourDto toBookingTourDto(BookingTour tourBooking) {
        Departure departure = tourBooking.getDeparture();
        Tour tour = departure.getTour();
        return BookingTourDto.builder()
                .id(tourBooking.getId())
                .tourId(tour.getId())
                .tourName(tour.getName())
                .departureDate(departure.getDepartureDate())
                .numberOfAdults(tourBooking.getNumberOfAdults())
                .numberOfChildren(tourBooking.getNumberOfChildren())
                .adultPrice(departure.getAdultPrice())
                .childPrice(departure.getChildPrice())
                .totalPrice(tourBooking.getTotalPrice())
                .orderId(tourBooking.getOrder() != null ? tourBooking.getOrder().getId() : null)
                .build();
    }

    private FlightBookingDto toFlightBookingDto(FlightBooking flightBooking) {
        FlightSlot slot = flightBooking.getFlightSlot();
        Flight flight = slot.getFlight();
        Order order = flightBooking.getOrder();
        Customer customer = flightBooking.getCustomer();

        FlightBookingDto dto = new FlightBookingDto();
        dto.setId(flightBooking.getId());
        dto.setBookingDate(flightBooking.getBookingDate());
        dto.setTotalPrice(flightBooking.getTotalPrice().doubleValue());
        dto.setFlightSlotId(slot.getId());
        dto.setFlightId(flight.getId());
        if (order != null) {
            dto.setOrderId(order.getId());
            if (order.getUser() != null) {
                dto.setUserId(order.getUser().getId());
            }
        }
        if (customer != null) {
            dto.setCustomerId(customer.getId());
        }
        if (slot.getSeatNumber() != null && !slot.getSeatNumber().isEmpty()) {
            dto.setSeatCodes(Collections.singletonList(slot.getSeatNumber()));
        }
        return dto;
    }

    private backend.backend.dto.HotelBookingDto toHotelBookingDto(HotelBooking hotelBooking) {
        backend.backend.dto.HotelBookingDto dto = new backend.backend.dto.HotelBookingDto();
        dto.setId(hotelBooking.getId());
        dto.setRoomVariantId(hotelBooking.getRoomVariant().getId());
        dto.setCheckInDate(hotelBooking.getCheckInDate());
        dto.setCheckOutDate(hotelBooking.getCheckOutDate());
        dto.setNumAdults(hotelBooking.getNumAdults());
        dto.setNumChildren(hotelBooking.getNumChildren());
        dto.setTotalPrice(hotelBooking.getTotalPrice());
        dto.setCreatedAt(hotelBooking.getCreatedAt());
        dto.setOrderId(hotelBooking.getOrder() != null ? hotelBooking.getOrder().getId() : null);
        if (hotelBooking.getOrder() != null && hotelBooking.getOrder().getUser() != null) {
            dto.setUserId(hotelBooking.getOrder().getUser().getId());
        }
        if (hotelBooking.getRoomVariant() != null) {
            dto.setVariantName(hotelBooking.getRoomVariant().getVariantName());
            if (hotelBooking.getRoomVariant().getRoom() != null) {
                dto.setRoomType(hotelBooking.getRoomVariant().getRoom().getRoomType());
                if (hotelBooking.getRoomVariant().getRoom().getHotel() != null) {
                    dto.setHotelName(hotelBooking.getRoomVariant().getRoom().getHotel().getName());
                }
                if (hotelBooking.getRoomVariant().getRoom().getRoomImages() != null && !hotelBooking.getRoomVariant().getRoom().getRoomImages().isEmpty()) {
                    var roomImages = hotelBooking.getRoomVariant().getRoom().getRoomImages();
                    var img = roomImages.get(0);
                    if (img.getImage() != null) {
                        dto.setImageUrl(img.getImage().getUrl());
                    }
                    java.util.List<String> urls = new java.util.ArrayList<>();
                    for (var ri : roomImages) {
                        if (ri.getImage() != null && ri.getImage().getUrl() != null) {
                            urls.add(ri.getImage().getUrl());
                        }
                    }
                    dto.setImageUrls(urls);
                }
            }
        }
        dto.setRooms(hotelBooking.getRooms());
        return dto;
    }

    /**
     * Logic chính để áp dụng voucher vào đơn hàng.
     *
     * @param orderId ID của đơn hàng.
     * @param voucherCode Mã voucher.
     * @return OrderDTO đã được cập nhật.
     */
    @Transactional
    public OrderDto applyVoucherToOrder(Integer orderId, String voucherCode) {
        Order order = orderDAO.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

        if (!"PENDING_PAYMENT".equals(order.getStatus())) {
            throw new RuntimeException("Chỉ có thể áp dụng voucher cho đơn hàng đang chờ thanh toán.");
        }

        if (order.getVoucher() != null) {
            throw new RuntimeException("Mỗi đơn hàng chỉ được áp dụng một mã giảm giá.");
        }

        Voucher voucher = voucherDAO.findByCode(voucherCode)
                .orElseThrow(() -> new RuntimeException("Mã giảm giá '" + voucherCode + "' không tồn tại."));

    
        validateVoucher(voucher, order.getAmount());
     
        BigDecimal originalAmount = order.getAmount();
        BigDecimal discount = calculateEffectiveDiscount(voucher, originalAmount);
        BigDecimal newAmount = originalAmount.subtract(discount);
        
        // 7. Cập nhật thông tin đơn hàng
        order.setOriginalAmount(originalAmount); 
        order.setAmount(newAmount);              
        order.setVoucher(voucher);      
        voucher.setUsageCount(voucher.getUsageCount() + 1);
        Voucher updatedVoucher = voucherDAO.save(voucher);

        // KIỂM TRA VÀ PHÁT SỰ KIỆN NẾU VOUCHER ĐÃ HẾT
        if (updatedVoucher.getUsageLimit() != null && updatedVoucher.getUsageCount() >= updatedVoucher.getUsageLimit()) {
            eventPublisher.publishEvent(new VoucherUsedUpEvent(this, updatedVoucher.getCode()));
        }

        Order updatedOrder = orderDAO.save(order);

        return convertToDto(updatedOrder); 
    }
    
    
    private void validateVoucher(Voucher voucher, BigDecimal orderAmount) {
        if (voucher.getStatus() != VoucherStatus.ACTIVE) {
            throw new RuntimeException("Mã giảm giá này không còn hoạt động.");
        }
        LocalDate today = LocalDate.now();
        if (today.isBefore(voucher.getStartDate()) || today.isAfter(voucher.getExpiryDate())) {
            throw new RuntimeException("Mã giảm giá đã hết hạn hoặc chưa đến ngày sử dụng.");
        }
        if (voucher.getUsageLimit() != null && voucher.getUsageCount() >= voucher.getUsageLimit()) {
            throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng.");
        }
        if (voucher.getConditionMinAmount() != null && orderAmount.compareTo(voucher.getConditionMinAmount()) < 0) {
            throw new RuntimeException("Đơn hàng chưa đủ điều kiện tối thiểu để áp dụng mã này.");
        }
    }
    /**
     * Hàm helper để tính toán số tiền giảm giá thực tế của một voucher.
     * VỊ TRÍ ĐÚNG CỦA NÓ LÀ Ở ĐÂY.
     */
    private BigDecimal calculateEffectiveDiscount(Voucher voucher, BigDecimal orderAmount) {
        if (voucher.getType() == VoucherType.FIXED_AMOUNT) {
            return voucher.getDiscountAmount();
        }
        if (voucher.getType() == VoucherType.PERCENTAGE) {
            BigDecimal discount = orderAmount.multiply(BigDecimal.valueOf(voucher.getDiscountPercentage() / 100.0));
            if (voucher.getMaxDiscountAmount() != null && discount.compareTo(voucher.getMaxDiscountAmount()) > 0) {
                return voucher.getMaxDiscountAmount();
            }
            return discount;
        }
        return BigDecimal.ZERO;
    }

    private OrderDto convertToDto(Order order) {
        if (order == null) return null;
        // This method combines the logic of toOrderDTO and toDetailedOrderDto,
        // and also maps the 'originalAmount' which is crucial after applying a voucher.
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setAmount(order.getAmount());
        dto.setOriginalAmount(order.getOriginalAmount());
        dto.setStatus(order.getStatus());
        dto.setPayDate(order.getPayDate());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setExpiresAt(order.getExpiresAt());

        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
        }
        if (order.getVoucher() != null) {
            dto.setVoucherId(order.getVoucher().getId());
        }
        if (order.getDestination() != null) {
            dto.setDestinationId(order.getDestination().getId());
        }
        dto.setTourBookings(bookingTourDAO.findByOrderId(order.getId()).stream().map(this::toBookingTourDto).collect(Collectors.toList()));
        dto.setFlightBookings(flightBookingDAO.findByOrderId(order.getId()).stream().map(this::toFlightBookingDto).collect(Collectors.toList()));
        dto.setHotelBookings(hotelBookingDAO.findByOrderId(order.getId()).stream().map(this::toHotelBookingDto).collect(Collectors.toList()));
        return dto;
    }

    @Override
    @Transactional
    public OrderDto cancelOrderAfterRefund(Integer orderId) {
        logger.info("Bắt đầu hủy đơn hàng sau hoàn tiền cho Order ID: {}", orderId);
        
        Order order = orderDAO.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

        if (!"PAID".equals(order.getStatus())) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng đã thanh toán.");
        }

        // Cập nhật trạng thái đơn hàng thành CANCELLED
        order.setStatus("CANCELLED");
        orderDAO.save(order);

        // Xử lý các booking trong đơn hàng
        // 1. Flight bookings - cập nhật flight slots thành AVAILABLE
        if (order.getFlightBookings() != null && !order.getFlightBookings().isEmpty()) {
            for (FlightBooking flightBooking : order.getFlightBookings()) {
                FlightSlot flightSlot = flightBooking.getFlightSlot();
                if (flightSlot != null) {
                    flightSlot.setStatus("AVAILABLE");
                    flightSlotDAO.save(flightSlot);
                    logger.info("Đã cập nhật flight slot {} thành AVAILABLE", flightSlot.getId());
                }
            }
        }

        // 2. Hotel bookings - placeholder logic
        if (order.getHotelBookings() != null && !order.getHotelBookings().isEmpty()) {
            logger.info("Đơn hàng có {} hotel bookings - xử lý placeholder", order.getHotelBookings().size());
            // TODO: Implement hotel booking cancellation logic
        }

        // 3. Tour bookings - placeholder logic
        if (order.getBookingTours() != null && !order.getBookingTours().isEmpty()) {
            logger.info("Đơn hàng có {} tour bookings - xử lý placeholder", order.getBookingTours().size());
            // TODO: Implement tour booking cancellation logic
        }

        // 4. Bus bookings - placeholder logic
        if (order.getBusBookings() != null && !order.getBusBookings().isEmpty()) {
            logger.info("Đơn hàng có {} bus bookings - xử lý placeholder", order.getBusBookings().size());
            // TODO: Implement bus booking cancellation logic
        }

        logger.info("Hủy đơn hàng thành công cho Order ID: {}", orderId);
        return toOrderDTO(order);
    }


   
    @Override
    @Transactional(readOnly = true)
    public byte[] generateInvoicePdf(Integer id) {
        Order order = orderDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + id));

        // Dùng NumberFormat để định dạng số, không bao gồm ký tự tiền tệ
        NumberFormat numberFormatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont fontRegular = PdfFontFactory.createFont("fonts/DejaVuSerif.ttf", PdfEncodings.IDENTITY_H, pdf);
            PdfFont fontBold = PdfFontFactory.createFont("fonts/DejaVuSerif-Bold.ttf", PdfEncodings.IDENTITY_H, pdf);


            // --- TÍNH TOÁN LẠI TỔNG TIỀN ĐỂ ĐẢM BẢO CHÍNH XÁC ---
            BigDecimal calculatedSubtotal = BigDecimal.ZERO;
            for (FlightBooking booking : order.getFlightBookings()) {
                calculatedSubtotal = calculatedSubtotal.add(booking.getTotalPrice() != null ? booking.getTotalPrice() : BigDecimal.ZERO);
            }
            for (HotelBooking booking : order.getHotelBookings()) {
                calculatedSubtotal = calculatedSubtotal.add(booking.getTotalPrice() != null ? booking.getTotalPrice() : BigDecimal.ZERO);
            }
            for (BookingTour booking : order.getBookingTours()) {
                calculatedSubtotal = calculatedSubtotal.add(booking.getTotalPrice() != null ? booking.getTotalPrice() : BigDecimal.ZERO);
            }

            // A. Phần Header
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
            headerTable.setBorder(Border.NO_BORDER).setMarginBottom(20f);

            Cell leftCell = new Cell().setBorder(Border.NO_BORDER);
            leftCell.add(new Paragraph("Đi Muôn Nơi").setFont(fontBold).setFontSize(20));
            leftCell.add(new Paragraph("\n"));
            leftCell.add(new Paragraph("GỬI ĐẾN:").setFont(fontBold));
            if (order.getUser() != null) {
                leftCell.add(new Paragraph(safeString(order.getUser().getName())).setFont(fontRegular));
                leftCell.add(new Paragraph(safeString(order.getUser().getPhone())).setFont(fontRegular));
                leftCell.add(new Paragraph(safeString(order.getUser().getAddress())).setFont(fontRegular));
            }
            headerTable.addCell(leftCell);

            Cell rightCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
            rightCell.add(new Paragraph("HÓA ĐƠN").setFont(fontBold).setFontSize(36));
            rightCell.add(new Paragraph("Số Hóa Đơn: " + (order.getTransactionId() != null ? order.getTransactionId() : String.valueOf(order.getId()))).setFont(fontRegular));
            rightCell.add(new Paragraph("Ngày: " + (order.getPayDate() != null ? order.getPayDate().format(DateTimeFormatter.ofPattern("dd 'tháng' MM, yyyy", new Locale("vi", "VN"))) : "")).setFont(fontRegular));
            headerTable.addCell(rightCell);
            document.add(headerTable);

            // B. Bảng chi tiết các mục
            Table itemTable = new Table(UnitValue.createPercentArray(new float[]{55, 15, 15, 15})).useAllAvailableWidth();
            float cellPadding = 10f;

            itemTable.addHeaderCell(new Cell().add(new Paragraph("Sản phẩm/Dịch vụ").setFont(fontBold)).setPadding(cellPadding).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(ColorConstants.BLACK, 1.5f)));
            itemTable.addHeaderCell(new Cell().add(new Paragraph("Số lượng").setFont(fontBold)).setTextAlignment(TextAlignment.CENTER).setPadding(cellPadding).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(ColorConstants.BLACK, 1.5f)));
            itemTable.addHeaderCell(new Cell().add(new Paragraph("Đơn giá").setFont(fontBold)).setTextAlignment(TextAlignment.RIGHT).setPadding(cellPadding).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(ColorConstants.BLACK, 1.5f)));
            itemTable.addHeaderCell(new Cell().add(new Paragraph("Tổng").setFont(fontBold)).setTextAlignment(TextAlignment.RIGHT).setPadding(cellPadding).setBorder(Border.NO_BORDER).setBorderBottom(new SolidBorder(ColorConstants.BLACK, 1.5f)));

            for (FlightBooking booking : order.getFlightBookings()) {
                String flightName = (booking.getFlightSlot() != null && booking.getFlightSlot().getFlight() != null) ? safeString(booking.getFlightSlot().getFlight().getName()) : "Chuyến bay";
                itemTable.addCell(new Cell().add(new Paragraph("Vé máy bay: " + flightName).setFont(fontRegular)).setPadding(cellPadding).setBorder(Border.NO_BORDER));
                itemTable.addCell(new Cell().add(new Paragraph("1").setFont(fontRegular)).setTextAlignment(TextAlignment.CENTER).setPadding(cellPadding).setBorder(Border.NO_BORDER));
                itemTable.addCell(new Cell().add(new Paragraph(numberFormatter.format(booking.getTotalPrice() != null ? booking.getTotalPrice() : BigDecimal.ZERO)).setFont(fontRegular)).setTextAlignment(TextAlignment.RIGHT).setPadding(cellPadding).setBorder(Border.NO_BORDER));
                itemTable.addCell(new Cell().add(new Paragraph(numberFormatter.format(booking.getTotalPrice() != null ? booking.getTotalPrice() : BigDecimal.ZERO)).setFont(fontRegular)).setTextAlignment(TextAlignment.RIGHT).setPadding(cellPadding).setBorder(Border.NO_BORDER));
            }
            for (HotelBooking booking : order.getHotelBookings()) {
                String hotelName = (booking.getRoomVariant() != null) ? safeString(booking.getRoomVariant().getVariantName()) : "Phòng khách sạn";
                short numberOfRooms = booking.getRooms();
                BigDecimal totalPrice = booking.getTotalPrice() != null ? booking.getTotalPrice() : BigDecimal.ZERO;
                BigDecimal unitPrice = (numberOfRooms > 0) ? totalPrice.divide(BigDecimal.valueOf(numberOfRooms), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                
                itemTable.addCell(new Cell().add(new Paragraph("Khách sạn: " + hotelName).setFont(fontRegular)).setPadding(cellPadding).setBorder(Border.NO_BORDER));
                itemTable.addCell(new Cell().add(new Paragraph(String.valueOf(numberOfRooms)).setFont(fontRegular)).setTextAlignment(TextAlignment.CENTER).setPadding(cellPadding).setBorder(Border.NO_BORDER));
                itemTable.addCell(new Cell().add(new Paragraph(numberFormatter.format(unitPrice)).setFont(fontRegular)).setTextAlignment(TextAlignment.RIGHT).setPadding(cellPadding).setBorder(Border.NO_BORDER));
                itemTable.addCell(new Cell().add(new Paragraph(numberFormatter.format(totalPrice)).setFont(fontRegular)).setTextAlignment(TextAlignment.RIGHT).setPadding(cellPadding).setBorder(Border.NO_BORDER));
            }
            for (BookingTour booking : order.getBookingTours()) {
                String tourName = "Tour không xác định";
                if (booking.getDeparture() != null && booking.getDeparture().getTour() != null) {
                    tourName = safeString(booking.getDeparture().getTour().getName());
                }
                itemTable.addCell(new Cell().add(new Paragraph("Tour: " + tourName).setFont(fontRegular)).setPadding(cellPadding).setBorder(Border.NO_BORDER));
                itemTable.addCell(new Cell().add(new Paragraph("1").setFont(fontRegular)).setTextAlignment(TextAlignment.CENTER).setPadding(cellPadding).setBorder(Border.NO_BORDER));
                itemTable.addCell(new Cell().add(new Paragraph(numberFormatter.format(booking.getTotalPrice() != null ? booking.getTotalPrice() : BigDecimal.ZERO)).setFont(fontRegular)).setTextAlignment(TextAlignment.RIGHT).setPadding(cellPadding).setBorder(Border.NO_BORDER));
                itemTable.addCell(new Cell().add(new Paragraph(numberFormatter.format(booking.getTotalPrice() != null ? booking.getTotalPrice() : BigDecimal.ZERO)).setFont(fontRegular)).setTextAlignment(TextAlignment.RIGHT).setPadding(cellPadding).setBorder(Border.NO_BORDER));
            }
            document.add(itemTable);

            // C. Phần tổng kết
            Table totalTable = new Table(UnitValue.createPercentArray(new float[]{70, 30})).useAllAvailableWidth().setMarginTop(20f);
            totalTable.setBorder(Border.NO_BORDER);

            BigDecimal amount = order.getAmount() != null ? order.getAmount() : BigDecimal.ZERO;
            
            totalTable.addCell(new Cell().add(new Paragraph("Tổng phụ").setFont(fontRegular)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT).setPadding(2f));
            totalTable.addCell(new Cell().add(new Paragraph(numberFormatter.format(calculatedSubtotal)).setFont(fontRegular)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT).setPadding(2f));

            BigDecimal discount = calculatedSubtotal.subtract(amount);
            if (discount.compareTo(BigDecimal.ZERO) > 0) {
                 totalTable.addCell(new Cell().add(new Paragraph("Giảm giá").setFont(fontRegular)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT).setPadding(2f));
                 totalTable.addCell(new Cell().add(new Paragraph("- " + numberFormatter.format(discount)).setFont(fontRegular)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT).setPadding(2f));
            }

            totalTable.addCell(new Cell().add(new Paragraph("Thuế (0%)").setFont(fontRegular)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT).setPadding(2f));
            totalTable.addCell(new Cell().add(new Paragraph(numberFormatter.format(0)).setFont(fontRegular)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT).setPadding(2f));

            Cell totalLabelCell = new Cell().add(new Paragraph("Tổng cộng").setFont(fontBold).setFontSize(14)).setPaddingTop(10f).setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(ColorConstants.BLACK, 1.5f)).setTextAlignment(TextAlignment.RIGHT);
            Cell totalValueCell = new Cell().add(new Paragraph(numberFormatter.format(amount)).setFont(fontBold).setFontSize(14)).setPaddingTop(10f).setBorder(Border.NO_BORDER).setBorderTop(new SolidBorder(ColorConstants.BLACK, 1.5f)).setTextAlignment(TextAlignment.RIGHT);
            totalTable.addCell(totalLabelCell);
            totalTable.addCell(totalValueCell);
            document.add(totalTable);

            // D. Lời cảm ơn và Chân trang
            document.add(new Paragraph("\n\nCảm ơn bạn đã sử dụng dịch vụ!").setFont(fontRegular).setTextAlignment(TextAlignment.CENTER).setMarginTop(30f).setMarginBottom(30f));
            
            Table footerTable = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth().setMarginTop(20f);
         
            footerTable.setBorder(Border.NO_BORDER);
            
            Cell paymentCell = new Cell().setBorder(Border.NO_BORDER);
            paymentCell.add(new Paragraph("THÔNG TIN THANH TOÁN").setBold());
            paymentCell.add(new Paragraph("Tên ngân hàng: Your Bank Name"));
            paymentCell.add(new Paragraph("Tên tài khoản: Your Business Name"));
            paymentCell.add(new Paragraph("Số tài khoản: 987-654-321"));
            footerTable.addCell(paymentCell);

            Cell companyCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
            companyCell.add(new Paragraph("Your Business Name").setBold());
            companyCell.add(new Paragraph("987 Anywhere St, Any City"));
            companyCell.add(new Paragraph("Any State 987655"));
            footerTable.addCell(companyCell);
            document.add(footerTable);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo hóa đơn PDF", e);
        }
    }

    // Hàm trợ giúp để tránh lỗi NullPointerException
    private String safeString(String input) {
        return input != null ? input : "";
    }
    
    

}