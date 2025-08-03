package backend.backend.dto;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class DashboardTourStatsDTO {
	private long totalTours;
    private long activeTours;
    private long totalCustomers;
    private BigDecimal revenueThisMonth;
    private BigDecimal revenueThisWeek;
}
