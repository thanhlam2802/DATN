package backend.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
@Data
public class MyTourBookingDTO {

	 private Integer bookingId;
	    private Integer orderId;
	    private String orderStatus;
	    private String tourName;
	    private String tourImageUrl;
	    private LocalDate departureDate;
	    private Integer numberOfAdults;
	    private Integer numberOfChildren;
	    private BigDecimal totalPrice;
	    private LocalDate bookingDate;
	    private String phone; 
		 private String email;
		 private String note;
		 private String name;
}
