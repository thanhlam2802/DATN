package backend.backend.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
public class CustomerDto {
    private Integer id;
    private String fullName;
    private Boolean gender;
    private LocalDate dob;
    private String passport;
    private String email;
    private String phone;
    private Long bookingCount;
    private LocalDateTime latestBookingDate;

    public CustomerDto(Integer id, String fullName, Boolean gender, LocalDate dob, String passport, String email, String phone, Long bookingCount, LocalDateTime latestBookingDate) {
        this.id = id;
        this.fullName = fullName;
        this.gender = gender;
        this.dob = dob;
        this.passport = passport;
        this.email = email;
        this.phone = phone;
        this.bookingCount = bookingCount;
        this.latestBookingDate = latestBookingDate;
    }
} 