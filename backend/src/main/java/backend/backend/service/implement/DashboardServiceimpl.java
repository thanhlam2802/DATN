package backend.backend.service.implement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import backend.backend.dao.BookingTourDAO;
import backend.backend.dao.BusBookingDAO;
import backend.backend.dao.FlightBookingDAO;
import backend.backend.dao.HotelBookingDAO;
import backend.backend.dao.OrderDAO;
import backend.backend.dto.BookingDataPointDTO;
import backend.backend.dto.DashboardSummaryDTO;
import backend.backend.dto.RevenueDataPointDTO;
import backend.backend.service.DashboardService;


@Service
public class DashboardServiceimpl implements DashboardService {
	@Autowired
    private OrderDAO orderRepository;
	 @Autowired private BookingTourDAO bookingTourDAO;
	    @Autowired private HotelBookingDAO hotelBookingDAO;
	    @Autowired private FlightBookingDAO flightBookingDAO;
	    @Autowired private BusBookingDAO busBookingDAO;
	

	@Override
	public List<RevenueDataPointDTO> getRevenueData(String period) {
		 LocalDate today = LocalDate.now();
	        switch (period.toLowerCase()) {
	            case "month":
	                return orderRepository.findRevenueByMonthInYear(today.getYear());
	            case "year":
	                LocalDateTime fiveYearsAgo = today.minusYears(5).atStartOfDay();
	                return orderRepository.findRevenueByYear(fiveYearsAgo);
	            case "day":
	            default:
	                LocalDateTime sevenDaysAgo = today.minusDays(6).atStartOfDay();
	                return orderRepository.findRevenueLast7Days(sevenDaysAgo);
	        }
	}
	@Override
    public DashboardSummaryDTO getDashboardSummary(String filterType, String value) {
        LocalDateTime startDate;
        LocalDateTime endDate;

        try {
            switch (filterType.toLowerCase()) {
                case "month":
                    // Xử lý giá trị "YYYY-MM"
                    YearMonth yearMonth = YearMonth.parse(value, DateTimeFormatter.ofPattern("yyyy-MM"));
                    startDate = yearMonth.atDay(1).atStartOfDay();
                    endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
                    break;
                case "year":
                    // Xử lý giá trị "YYYY"
                    int year = Integer.parseInt(value);
                    startDate = LocalDate.of(year, 1, 1).atStartOfDay();
                    endDate = LocalDate.of(year, 12, 31).atTime(23, 59, 59);
                    break;
                case "day":
                default:
                    // Xử lý giá trị "YYYY-MM-DD"
                    LocalDate date = LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    startDate = date.atStartOfDay();
                    endDate = date.atTime(23, 59, 59);
                    break;
            }
        } catch (Exception e) {
            // Nếu có lỗi parse, mặc định về ngày hôm nay
            LocalDate today = LocalDate.now();
            startDate = today.atStartOfDay();
            endDate = today.atTime(23, 59, 59);
        }

        // Gọi các phương thức DAO mới
        BigDecimal revenue = orderRepository.findRevenueBetween(startDate, endDate);
        long newBookings = orderRepository.countNewOrdersBetween(startDate, endDate);
        long newCustomers = orderRepository.countNewCustomersBetween(startDate, endDate);
        long pendingReviews = 0; // Giữ nguyên vì chưa có logic

        return new DashboardSummaryDTO(revenue, newBookings, newCustomers, pendingReviews);
    }
	@Override
	public List<BookingDataPointDTO> getBookingChartData() {
	    List<BookingDataPointDTO> data = new ArrayList<>();
	    
	    
	    data.add(new BookingDataPointDTO("Tour", bookingTourDAO.count()));
	    data.add(new BookingDataPointDTO("Khách sạn", hotelBookingDAO.count()));
	    data.add(new BookingDataPointDTO("Chuyến bay", flightBookingDAO.count()));
	    data.add(new BookingDataPointDTO("Xe bus", busBookingDAO.count()));
	    
	    return data;
	}
   
    
	
}
