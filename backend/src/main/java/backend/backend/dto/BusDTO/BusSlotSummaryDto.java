package backend.backend.dto.BusDTO;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BusSlotSummaryDto {
    private Integer id;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private String departureLocation;
    private String arrivalLocation;
    private String busName;
    private String busLicensePlate;
}
