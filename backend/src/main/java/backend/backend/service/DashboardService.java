package backend.backend.service;

import java.util.List;

import backend.backend.dto.BookingDataPointDTO;
import backend.backend.dto.DashboardSummaryDTO;
import backend.backend.dto.RevenueDataPointDTO;

public interface DashboardService {
	List<BookingDataPointDTO> getBookingChartData();
	 List<RevenueDataPointDTO> getRevenueData(String period);
	 DashboardSummaryDTO getDashboardSummary(String filterType, String value);
	 
}
