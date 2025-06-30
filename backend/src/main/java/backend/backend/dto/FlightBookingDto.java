package backend.backend.dto;

import java.util.List;

public class FlightBookingDto {
    private Long flightId;
    private Long userId;
    private List<PassengerInfo> passengerInfo;
    private ContactInfo contactInfo;
    private List<String> seatCodes;
    private double totalPrice;

    // Getters and setters
    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<PassengerInfo> getPassengerInfo() { return passengerInfo; }
    public void setPassengerInfo(List<PassengerInfo> passengerInfo) { this.passengerInfo = passengerInfo; }
    public ContactInfo getContactInfo() { return contactInfo; }
    public void setContactInfo(ContactInfo contactInfo) { this.contactInfo = contactInfo; }
    public List<String> getSeatCodes() { return seatCodes; }
    public void setSeatCodes(List<String> seatCodes) { this.seatCodes = seatCodes; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public static class PassengerInfo {
        private String name;
        private String gender;
        private String dob;
        private String passport;
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getDob() { return dob; }
        public void setDob(String dob) { this.dob = dob; }
        public String getPassport() { return passport; }
        public void setPassport(String passport) { this.passport = passport; }
    }

    public static class ContactInfo {
        private String name;
        private String phone;
        private String email;
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
} 