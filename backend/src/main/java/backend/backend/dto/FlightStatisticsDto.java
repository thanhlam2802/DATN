package backend.backend.dto;

import java.time.LocalDate;

public class FlightStatisticsDto {
    private Long flightId;
    private String flightCode;
    private LocalDate date;
    private int totalTickets;
    private int soldTickets;
    private double revenue;
    private double occupancyRate;

    // Getters and setters
    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }
    public String getFlightCode() { return flightCode; }
    public void setFlightCode(String flightCode) { this.flightCode = flightCode; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public int getTotalTickets() { return totalTickets; }
    public void setTotalTickets(int totalTickets) { this.totalTickets = totalTickets; }
    public int getSoldTickets() { return soldTickets; }
    public void setSoldTickets(int soldTickets) { this.soldTickets = soldTickets; }
    public double getRevenue() { return revenue; }
    public void setRevenue(double revenue) { this.revenue = revenue; }
    public double getOccupancyRate() { return occupancyRate; }
    public void setOccupancyRate(double occupancyRate) { this.occupancyRate = occupancyRate; }
} 