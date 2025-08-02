package backend.backend.dto;


import lombok.Data;
import java.time.LocalDate;
import java.util.List;

import ServiceType.ServiceType;

@Data
public class AddItemRequestDto {

 
    private Long itemId;      
    private ServiceType itemType; 


    private int numberOfAdults;
    private int numberOfChildren;

  
    private Long departureId;

    // Dành cho Hotel
    private Integer roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private Integer customerId;

    private java.math.BigDecimal totalPrice;
    private Integer numberOfRooms;

    // Dành cho Flight
    private Integer flightSlotId;




    private Integer busSlotId;
    private List<Integer> selectedSeatIds;    // Specific to Bus (like roomId for Hotel)
    private String notes;                     // General purpose notes

    // ✅ NEW: Passenger info cho Bus (like customer info for other services)
    private String passengerName;
    private String passengerPhone;
    private String passengerEmail;
}