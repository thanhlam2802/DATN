package backend.backend.dto;


import lombok.Data;
import java.time.LocalDate;

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
    

    // Dành cho Bus
    private Integer busSlotId;
    
    private String fullName;
    private String email;
    private String phone;
}