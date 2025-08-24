package backend.backend.controller;

import backend.backend.dto.BookingDataPointDTO;
import backend.backend.dto.DashboardSummaryDTO;
import backend.backend.dto.RevenueDataPointDTO;
import backend.backend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDTO> getSummary(
            @RequestParam String filterType,
            @RequestParam String value) {
        return ResponseEntity.ok(dashboardService.getDashboardSummary(filterType, value));
    }

    @GetMapping("/revenue-chart")
    public ResponseEntity<List<RevenueDataPointDTO>> getRevenueChartData(
            @RequestParam(defaultValue = "day") String period) {
        return ResponseEntity.ok(dashboardService.getRevenueData(period));
    }
    @GetMapping("/booking-chart")
    public ResponseEntity<List<BookingDataPointDTO>> getBookingChartData() {
        return ResponseEntity.ok(dashboardService.getBookingChartData());
    }

}