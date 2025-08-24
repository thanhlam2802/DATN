package backend.backend.service;

import backend.backend.entity.*;
import backend.backend.repository.*;
import backend.backend.dto.BusManagementDTO;
import backend.backend.entity.enumBus.BusSlotStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BusManagementService {
    private final UserRepository userRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final BusSlotRepository busSlotRepository;
    private final BusBookingRepository busBookingRepository;
    private final LocationRepository locationRepository;

    // ==================== OVERVIEW TAB ====================
    public Map<String, Object> getOverviewKPIs() {
        LocalDate today = LocalDate.now();
        
        // Count bookings today
        long bookingsToday = busBookingRepository.countByBookingDateBetween(
            today.atStartOfDay(), today.atTime(LocalTime.MAX));
        
        // Calculate occupancy rate
        long totalSeats = busSlotRepository.findAll().stream()
            .mapToLong(slot -> slot.getTotalSeats() != null ? slot.getTotalSeats() : 0)
            .sum();
        long soldSeats = busSlotRepository.findAll().stream()
            .mapToLong(slot -> {
                int total = slot.getTotalSeats() != null ? slot.getTotalSeats() : 0;
                int available = slot.getAvailableSeats() != null ? slot.getAvailableSeats() : 0;
                return total - available;
            })
            .sum();
        double occupancyRate = totalSeats > 0 ? (double) soldSeats / totalSeats * 100 : 0;
        
        // Count cancellations
        long cancellations = busSlotRepository.findAll().stream()
            .filter(slot -> BusSlotStatus.CANCELLED.equals(slot.getStatus()))
            .count();
        
        // Count active providers (users with buses)
        long activeProviders = userRepository.findAll().stream()
            .filter(user -> !user.getOwnedBuses().isEmpty())
            .count();

        Map<String, Object> kpis = new HashMap<>();
        kpis.put("bookingsToday", (int) bookingsToday);
        kpis.put("occupancyRate", Math.round(occupancyRate));
        kpis.put("cancellations", (int) cancellations);
        kpis.put("activeProviders", (int) activeProviders);
        return kpis;
    }

    public List<BusManagementDTO.DataQualityAlert> getDataQualityAlerts(String query) {
        List<BusManagementDTO.DataQualityAlert> alerts = new ArrayList<>();
        
        // Check for routes without proper locations
        List<Route> routes = routeRepository.findAll();
        for (Route route : routes) {
            if (route.getOriginLocation() == null || route.getDestinationLocation() == null) {
                alerts.add(BusManagementDTO.DataQualityAlert.builder()
                    .type("MISSING_LOCATION")
                    .ownerName(route.getOwner().getName())
                    .description("Route missing origin or destination location")
                    .suggestion("Add proper location information")
                    .at(LocalDateTime.now().toString())
                    .build());
            }
        }
        
        // Check for slots with invalid dates
        List<BusSlot> slots = busSlotRepository.findAll();
        for (BusSlot slot : slots) {
            if (slot.getSlotDate() != null && slot.getSlotDate().isBefore(LocalDate.now())) {
                alerts.add(BusManagementDTO.DataQualityAlert.builder()
                    .type("PAST_SLOT")
                    .ownerName(slot.getOwner().getName())
                    .description("Slot scheduled in the past")
                    .suggestion("Update slot date or remove past slots")
                    .at(LocalDateTime.now().toString())
                    .build());
            }
        }
        
        return alerts.stream()
            .filter(alert -> query == null || query.trim().isEmpty() || 
                          alert.getDescription().toLowerCase().contains(query.toLowerCase()) ||
                          alert.getOwnerName().toLowerCase().contains(query.toLowerCase()))
            .collect(Collectors.toList());
    }

    // ==================== PROVIDERS TAB ====================
    public List<BusManagementDTO.ProviderSummary> getProviders(String query, String status) {
        List<User> users = userRepository.findAll();

        return users.stream()
            .filter(user -> user.getOwnedBuses() != null && !user.getOwnedBuses().isEmpty())
            .filter(user -> query == null || query.trim().isEmpty() || 
                          user.getName().toLowerCase().contains(query.toLowerCase()) ||
                          user.getEmail().toLowerCase().contains(query.toLowerCase()))
            .filter(user -> status == null || status.trim().isEmpty() || 
                          "ACTIVE".equals(status)) // User không có status, coi như ACTIVE
            .map(user -> {
                // Count routes for this user
                long routesCount = routeRepository.findByOwner(user).size();
                
                // Count next 7 days slots
                long next7dSlots = busSlotRepository.findAll().stream()
                    .filter(slot -> slot.getOwner().equals(user))
                    .filter(slot -> slot.getSlotDate() != null && 
                                 slot.getSlotDate().isAfter(LocalDate.now()) &&
                                 slot.getSlotDate().isBefore(LocalDate.now().plusDays(8)))
                    .count();

                return BusManagementDTO.ProviderSummary.builder()
                    .id(user.getId())
                    .code(user.getEmail())
                    .name(user.getName())
                    .status("ACTIVE") // User không có status, coi như ACTIVE
                    .routesCount((int) routesCount)
                    .next7dSlots((int) next7dSlots)
                    .build();
            })
            .collect(Collectors.toList());
    }

    public BusManagementDTO.ProviderSummary createProvider(BusManagementDTO.CreateProviderRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getCode());
        user = userRepository.save(user);

        return BusManagementDTO.ProviderSummary.builder()
            .id(user.getId())
            .code(user.getEmail())
            .name(user.getName())
            .status("ACTIVE")
            .routesCount(0)
            .next7dSlots(0)
            .build();
    }

    public BusManagementDTO.ProviderSummary updateProvider(Integer id, BusManagementDTO.UpdateProviderRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Provider not found"));

        if (request.getName() != null) user.setName(request.getName());
        if (request.getCode() != null) user.setEmail(request.getCode());

        User savedUser = userRepository.save(user);

        long routesCount = routeRepository.findByOwner(savedUser).size();
        long next7dSlots = busSlotRepository.findAll().stream()
            .filter(slot -> slot.getOwner().equals(savedUser))
            .filter(slot -> slot.getSlotDate() != null && 
                         slot.getSlotDate().isAfter(LocalDate.now()) &&
                         slot.getSlotDate().isBefore(LocalDate.now().plusDays(8)))
            .count();

        return BusManagementDTO.ProviderSummary.builder()
            .id(savedUser.getId())
            .code(savedUser.getEmail())
            .name(savedUser.getName())
            .status("ACTIVE")
            .routesCount((int) routesCount)
            .next7dSlots((int) next7dSlots)
            .build();
    }

    public void banProvider(Integer id) {
        // Delete all buses owned by this user (simulate ban)
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Provider not found"));
        
        List<Bus> userBuses = new ArrayList<>(user.getOwnedBuses());
        for (Bus bus : userBuses) {
            busRepository.delete(bus);
        }
    }

    public void unbanProvider(Integer id) {
        // Không thể unban vì User không có status field
        throw new RuntimeException("Cannot unban provider - User entity does not have status field");
    }

    // ==================== ROUTES TAB ====================
    public List<BusManagementDTO.RouteSummary> getRoutes(String owner, String origin, String destination, String status) {
        List<Route> routes = routeRepository.findAll();

        return routes.stream()
            .filter(route -> owner == null || owner.trim().isEmpty() || 
                          route.getOwner().getName().toLowerCase().contains(owner.toLowerCase()) ||
                          route.getOwner().getEmail().toLowerCase().contains(owner.toLowerCase()))
            .filter(route -> origin == null || origin.trim().isEmpty() || 
                          route.getOriginLocation().getName().toLowerCase().contains(origin.toLowerCase()))
            .filter(route -> destination == null || destination.trim().isEmpty() || 
                          route.getDestinationLocation().getName().toLowerCase().contains(destination.toLowerCase()))
            .filter(route -> status == null || status.trim().isEmpty() || 
                          "ACTIVE".equals(status)) // Route không có status, coi như ACTIVE
            .map(route -> {
                // Count future slots
                long futureSlots = busSlotRepository.countByRouteAndSlotDateAfter(route, LocalDate.now());

                return BusManagementDTO.RouteSummary.builder()
                    .id(route.getId())
                    .ownerCode(route.getOwner().getEmail())
                    .routeCode(route.getOwner().getEmail() + "-" + route.getOriginLocation().getName() + "-" + route.getDestinationLocation().getName() + "-" + route.getId())
                    .origin(route.getOriginLocation().getName())
                    .destination(route.getDestinationLocation().getName())
                    .status("ACTIVE") // Route không có status, coi như ACTIVE
                    .futureSlots((int) futureSlots)
                    .distanceKm(route.getDistanceKm())
                    .estimatedDurationMinutes(route.getEstimatedDurationMinutes())
                    .build();
            })
            .collect(Collectors.toList());
    }

    public BusManagementDTO.RouteSummary createRoute(BusManagementDTO.CreateRouteRequest request) {
        User owner = userRepository.findByEmail(request.getOwnerCode())
            .orElseThrow(() -> new RuntimeException("Owner not found"));

        Location origin = locationRepository.findByName(request.getOrigin())
            .orElseThrow(() -> new RuntimeException("Origin location not found"));

        Location destination = locationRepository.findByName(request.getDestination())
            .orElseThrow(() -> new RuntimeException("Destination location not found"));

        Route route = new Route();
        route.setOriginLocation(origin);
        route.setDestinationLocation(destination);
        route.setOwner(owner);
        route.setDistanceKm(request.getDistanceKm());
        route.setEstimatedDurationMinutes(request.getEstimatedDurationMinutes());
        // Route không có status field, bỏ qua
        route = routeRepository.save(route);

        return BusManagementDTO.RouteSummary.builder()
            .id(route.getId())
            .ownerCode(route.getOwner().getEmail())
            .routeCode(route.getOwner().getEmail() + "-" + route.getOriginLocation().getName() + "-" + route.getDestinationLocation().getName() + "-" + route.getId())
            .origin(route.getOriginLocation().getName())
            .destination(route.getDestinationLocation().getName())
            .status("ACTIVE") // Route không có status, coi như ACTIVE
            .futureSlots(0)
            .distanceKm(route.getDistanceKm())
            .estimatedDurationMinutes(route.getEstimatedDurationMinutes())
            .build();
    }

    public BusManagementDTO.RouteSummary updateRoute(Integer id, BusManagementDTO.UpdateRouteRequest request) {
        Route route = routeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Route not found"));

        if (request.getOrigin() != null) {
            Location origin = locationRepository.findByName(request.getOrigin())
                .orElseThrow(() -> new RuntimeException("Origin location not found"));
            route.setOriginLocation(origin);
        }

        if (request.getDestination() != null) {
            Location destination = locationRepository.findByName(request.getDestination())
                .orElseThrow(() -> new RuntimeException("Destination location not found"));
            route.setDestinationLocation(destination);
        }

        if (request.getDistanceKm() != null) route.setDistanceKm(request.getDistanceKm());
        if (request.getEstimatedDurationMinutes() != null) route.setEstimatedDurationMinutes(request.getEstimatedDurationMinutes());
        // Route không có status field, bỏ qua

        route = routeRepository.save(route);

        long futureSlots = busSlotRepository.countByRouteAndSlotDateAfter(route, LocalDate.now());

        return BusManagementDTO.RouteSummary.builder()
            .id(route.getId())
            .ownerCode(route.getOwner().getEmail())
            .routeCode(route.getOwner().getEmail() + "-" + route.getOriginLocation().getName() + "-" + route.getDestinationLocation().getName() + "-" + route.getId())
            .origin(route.getOriginLocation().getName())
            .destination(route.getDestinationLocation().getName())
            .status("ACTIVE") // Route không có status, coi như ACTIVE
            .futureSlots((int) futureSlots)
            .distanceKm(route.getDistanceKm())
            .estimatedDurationMinutes(route.getEstimatedDurationMinutes())
            .build();
    }

    public void submitRouteReview(Integer id) {
        Route route = routeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Route not found"));
        // Route không có status field, bỏ qua
        routeRepository.save(route);
    }

    public void approveRoute(Integer id) {
        Route route = routeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Route not found"));
        // Route không có status field, bỏ qua
        routeRepository.save(route);
    }

    public void rejectRoute(Integer id) {
        Route route = routeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Route not found"));
        // Route không có status field, bỏ qua
        routeRepository.save(route);
    }

    // ==================== SLOTS TAB ====================
    public List<BusManagementDTO.SlotSummary> getSlots(String owner, String route, String date) {
        List<BusSlot> slots = busSlotRepository.findAll();

        return slots.stream()
            .filter(slot -> owner == null || owner.trim().isEmpty() || 
                          slot.getOwner().getName().toLowerCase().contains(owner.toLowerCase()) ||
                          slot.getOwner().getEmail().toLowerCase().contains(owner.toLowerCase()))
            .filter(slot -> route == null || route.trim().isEmpty() || 
                          slot.getRoute().getId().toString().contains(route))
            .filter(slot -> date == null || date.trim().isEmpty() || 
                          slot.getSlotDate().toString().equals(date))
            .map(slot -> {
                int sold = slot.getTotalSeats() != null && slot.getAvailableSeats() != null ? 
                          slot.getTotalSeats() - slot.getAvailableSeats() : 0;

                return BusManagementDTO.SlotSummary.builder()
                    .id(slot.getId())
                    .ownerCode(slot.getOwner().getEmail())
                    .routeCode(slot.getRoute().getId().toString())
                    .departTime(slot.getSlotDate() + " " + slot.getDepartureTime())
                    .busPlate(slot.getBus().getLicensePlate())
                    .sold(sold)
                    .total(slot.getTotalSeats() != null ? slot.getTotalSeats() : 0)
                    .status(slot.getStatus().name())
                    .price(slot.getPrice())
                    .slotDate(slot.getSlotDate())
                    .departureTime(slot.getDepartureTime())
                    .arrivalTime(slot.getArrivalTime())
                    .build();
            })
            .collect(Collectors.toList());
    }

    public List<BusManagementDTO.SlotSummary> bulkGenerateSlots(BusManagementDTO.BulkGenerateRequest request) {
        Route route = routeRepository.findById(Integer.parseInt(request.getRouteCode()))
            .orElseThrow(() -> new RuntimeException("Route not found"));

        Bus bus = busRepository.findById(request.getBusId())
            .orElseThrow(() -> new RuntimeException("Bus not found"));

        List<BusManagementDTO.SlotSummary> generatedSlots = new ArrayList<>();

        LocalDate currentDate = request.getStartDate();
        while (!currentDate.isAfter(request.getEndDate())) {
            if (request.getDaysOfWeek().contains(currentDate.getDayOfWeek().getValue())) {
                BusSlot slot = new BusSlot();
                slot.setBus(bus);
                slot.setRoute(route);
                slot.setOwner(route.getOwner());
                slot.setSlotDate(currentDate);
                slot.setDepartureTime(request.getDepartTime());
                slot.setArrivalTime(request.getDepartTime().plusMinutes(request.getDurationMinutes()));
                slot.setPrice(request.getPrice());
                slot.setTotalSeats(bus.getTotalSeats());
                slot.setAvailableSeats(bus.getTotalSeats());
                slot.setStatus(BusSlotStatus.SCHEDULED);
                slot = busSlotRepository.save(slot);

                generatedSlots.add(BusManagementDTO.SlotSummary.builder()
                    .id(slot.getId())
                    .ownerCode(slot.getOwner().getEmail())
                    .routeCode(slot.getRoute().getId().toString())
                    .departTime(slot.getSlotDate() + " " + slot.getDepartureTime())
                    .busPlate(slot.getBus().getLicensePlate())
                    .sold(0)
                    .total(slot.getTotalSeats())
                    .status(slot.getStatus().name())
                    .price(slot.getPrice())
                    .slotDate(slot.getSlotDate())
                    .departureTime(slot.getDepartureTime())
                    .arrivalTime(slot.getArrivalTime())
                    .build());
            }
            currentDate = currentDate.plusDays(1);
        }

        return generatedSlots;
    }

    public BusManagementDTO.SlotDetail getSlotDetail(Integer id) {
        BusSlot slot = busSlotRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Slot not found"));

        List<BusManagementDTO.SeatInfo> seats = slot.getSeats().stream()
            .map(seat -> BusManagementDTO.SeatInfo.builder()
                .id(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .isBooked(seat.getIsBooked())
                .price(seat.getPrice())
                .seatType(seat.getSeatType() != null ? seat.getSeatType().name() : null)
                .build())
            .collect(Collectors.toList());

        List<BusManagementDTO.BookingInfo> bookings = slot.getBusBookings().stream()
            .map(booking -> BusManagementDTO.BookingInfo.builder()
                .id(booking.getId())
                .bookingReference(booking.getBookingReference())
                .customerName(booking.getCustomer().getFullName())
                .numPassengers(booking.getNumPassengers())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus().name())
                .bookingDate(booking.getBookingDate())
                .build())
            .collect(Collectors.toList());

        return BusManagementDTO.SlotDetail.builder()
            .id(slot.getId())
            .ownerCode(slot.getOwner().getEmail())
            .routeCode(slot.getRoute().getId().toString())
            .busPlate(slot.getBus().getLicensePlate())
            .slotDate(slot.getSlotDate())
            .departureTime(slot.getDepartureTime())
            .arrivalTime(slot.getArrivalTime())
            .price(slot.getPrice())
            .totalSeats(slot.getTotalSeats())
            .availableSeats(slot.getAvailableSeats())
            .status(slot.getStatus().name())
            .delayReason(slot.getDelayReason() != null ? slot.getDelayReason().name() : null)
            .currentLocation(slot.getCurrentLocation())
            .driverContactInfo(slot.getDriverContactInfo())
            .seats(seats)
            .bookings(bookings)
            .build();
    }

    // ==================== MODERATION TAB ====================
    public List<BusManagementDTO.ModerationItem> getModerationQueue(String type, String status) {
        List<BusManagementDTO.ModerationItem> items = new ArrayList<>();
        
        // For now, return empty list since Route doesn't have status
        // In a real system, you might have a separate moderation table
        return items;
    }

    public void approveModeration(Integer id) {
        // Route không có status field, bỏ qua
    }

    public void rejectModeration(Integer id) {
        // Route không có status field, bỏ qua
    }

    public Map<String, Object> getModerationDiff(Integer id) {
        // Return empty diff since Route doesn't have status
        Map<String, Object> diff = new HashMap<>();
        diff.put("id", id);
        diff.put("type", "ROUTE");
        diff.put("changes", new ArrayList<>());
        return diff;
    }
}
