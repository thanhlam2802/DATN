package backend.backend.service;

import backend.backend.dto.BusManagementDTO;
import backend.backend.entity.*;
import backend.backend.entity.enumBus.BusSlotStatus;
import backend.backend.entity.enumBus.RouteStatus;
import backend.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusManagementServiceTest {

    @Mock
    private BusRepository busRepository;

    @Mock
    private BusSlotRepository busSlotRepository;

    @Mock
    private BusBookingRepository busBookingRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private BusCategoryRepository busCategoryRepository;

    @InjectMocks
    private BusManagementService busManagementService;

    private User testUser;
    private Route testRoute;
    private Bus testBus;
    private BusSlot testSlot;
    private Location originLocation;
    private Location destinationLocation;
    private BusBooking testBooking;

    @BeforeEach
    void setUp() {
        // Setup test data
        testUser = new User();
        testUser.setId(1);
        testUser.setName("Test Provider");
        testUser.setEmail("test@provider.com");

        originLocation = new Location();
        originLocation.setId(1);
        originLocation.setName("Saigon");

        destinationLocation = new Location();
        destinationLocation.setId(2);
        destinationLocation.setName("Da Nang");

        testRoute = new Route();
        testRoute.setId(1);
        testRoute.setOwner(testUser);
        testRoute.setOriginLocation(originLocation);
        testRoute.setDestinationLocation(destinationLocation);
        testRoute.setStatus(RouteStatus.APPROVED);
        testRoute.setDistanceKm(850.0);
        testRoute.setEstimatedDurationMinutes(900);
        testRoute.setRouteBusCategoryPrices(new ArrayList<>());
        testRoute.setCreatedAt(Instant.now());
        testRoute.setUpdatedAt(Instant.now());

        testBus = new Bus();
        testBus.setId(1);
        testBus.setName("Test Bus");
        testBus.setLicensePlate("51F-123.45");
        testBus.setTotalSeats(40);
        testBus.setOwner(testUser);

        testSlot = new BusSlot();
        testSlot.setId(1);
        testSlot.setBus(testBus);
        testSlot.setRoute(testRoute);
        testSlot.setOwner(testUser);
        testSlot.setSlotDate(LocalDate.now().plusDays(1));
        testSlot.setDepartureTime(LocalTime.of(22, 0));
        testSlot.setArrivalTime(LocalTime.of(13, 0));
        testSlot.setPrice(new BigDecimal("250000"));
        testSlot.setTotalSeats(40);
        testSlot.setAvailableSeats(28);
        testSlot.setStatus(BusSlotStatus.SCHEDULED);
        testSlot.setSeats(new ArrayList<>());
        testSlot.setBusBookings(new ArrayList<>());

        testBooking = new BusBooking();
        testBooking.setId(1);
        testBooking.setBusSlot(testSlot);
        testBooking.setBookingDate(LocalDateTime.now());
    }

    // ==================== OVERVIEW TAB TESTS ====================

    @Test
    void getOverviewKPIs_ShouldReturnCorrectKPIs() {
        // Given
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);
        
        when(busBookingRepository.countByBookingDateBetween(any(), any())).thenReturn(120L);
        when(busSlotRepository.findBySlotDateBetween(today, nextWeek))
                .thenReturn(Arrays.asList(testSlot));
        when(userRepository.countByBusesIsNotEmpty()).thenReturn(12L);

        // When
        Map<String, Object> kpis = busManagementService.getOverviewKPIs();

        // Then
        assertEquals(120L, kpis.get("bookingsToday"));
        assertEquals(12L, kpis.get("activeProviders"));
        assertNotNull(kpis.get("occupancyRate"));
        assertNotNull(kpis.get("cancellations"));
    }

    @Test
    void getDataQualityAlerts_ShouldReturnAlerts() {
        // Given
        when(routeRepository.findAll()).thenReturn(Arrays.asList(testRoute));

        // When
        List<BusManagementDTO.DataQualityAlert> alerts = busManagementService.getDataQualityAlerts("test");

        // Then
        assertNotNull(alerts);
        // Since no price overlaps in test data, alerts should be empty
        assertTrue(alerts.isEmpty());
    }

    // ==================== PROVIDERS TAB TESTS ====================

    @Test
    void getProviders_ShouldReturnProviders() {
        // Given
        when(userRepository.findByBusesIsNotEmpty()).thenReturn(Arrays.asList(testUser));
        when(busRepository.findByOwner(testUser)).thenReturn(Arrays.asList(testBus));
        when(routeRepository.findByOwner(testUser)).thenReturn(Arrays.asList(testRoute));
        when(busSlotRepository.countByOwnerAndSlotDateBetween(any(), any(), any())).thenReturn(20L);

        // When
        List<BusManagementDTO.ProviderSummary> providers = busManagementService.getProviders("test", "ACTIVE");

        // Then
        assertNotNull(providers);
        assertEquals(1, providers.size());
        assertEquals("test@provider.com", providers.get(0).getCode());
        assertEquals("Test Provider", providers.get(0).getName());
        assertEquals("ACTIVE", providers.get(0).getStatus());
        assertEquals(1, providers.get(0).getRoutesCount());
        assertEquals(20, providers.get(0).getNext7dSlots());
    }

    @Test
    void createProvider_ShouldCreateAndReturnProvider() {
        // Given
        BusManagementDTO.CreateProviderRequest request = new BusManagementDTO.CreateProviderRequest();
        request.setCode("new@provider.com");
        request.setName("New Provider");
        request.setStatus("ACTIVE");

        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        BusManagementDTO.ProviderSummary provider = busManagementService.createProvider(request);

        // Then
        assertNotNull(provider);
        assertEquals("test@provider.com", provider.getCode());
        assertEquals("Test Provider", provider.getName());
        assertEquals("ACTIVE", provider.getStatus());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void banProvider_ShouldDeleteUserBuses() {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(busRepository.findByOwner(testUser)).thenReturn(Arrays.asList(testBus));

        // When
        busManagementService.banProvider(1);

        // Then
        verify(busRepository).deleteAll(Arrays.asList(testBus));
    }

    // ==================== ROUTES TAB TESTS ====================

    @Test
    void getRoutes_ShouldReturnRoutes() {
        // Given
        when(routeRepository.findAll()).thenReturn(Arrays.asList(testRoute));
        when(busSlotRepository.countByRouteAndSlotDateAfter(any(), any())).thenReturn(10L);

        // When
        List<BusManagementDTO.RouteSummary> routes = busManagementService.getRoutes(null, null, null, null);

        // Then
        assertNotNull(routes);
        assertEquals(1, routes.size());
        assertEquals("test@provider.com", routes.get(0).getOwnerCode());
        assertEquals("Saigon", routes.get(0).getOrigin());
        assertEquals("Da Nang", routes.get(0).getDestination());
        assertEquals("APPROVED", routes.get(0).getStatus());
        assertEquals(10, routes.get(0).getFutureSlots());
    }

    @Test
    void createRoute_ShouldCreateAndReturnRoute() {
        // Given
        BusManagementDTO.CreateRouteRequest request = new BusManagementDTO.CreateRouteRequest();
        request.setOwnerCode("test@provider.com");
        request.setRouteCode("TEST-SGN-DAD-01");
        request.setOrigin("Saigon");
        request.setDestination("Da Nang");
        request.setStatus("DRAFT");
        request.setDistanceKm(850.0);
        request.setEstimatedDurationMinutes(900);

        when(userRepository.findByEmail("test@provider.com")).thenReturn(Optional.of(testUser));
        when(locationRepository.findByName("Saigon")).thenReturn(Optional.of(originLocation));
        when(locationRepository.findByName("Da Nang")).thenReturn(Optional.of(destinationLocation));
        when(routeRepository.save(any(Route.class))).thenReturn(testRoute);

        // When
        BusManagementDTO.RouteSummary route = busManagementService.createRoute(request);

        // Then
        assertNotNull(route);
        assertEquals("test@provider.com", route.getOwnerCode());
        assertEquals("Saigon", route.getOrigin());
        assertEquals("Da Nang", route.getDestination());
        verify(routeRepository).save(any(Route.class));
    }

    @Test
    void submitRouteReview_ShouldUpdateRouteStatus() {
        // Given
        when(routeRepository.findById(1)).thenReturn(Optional.of(testRoute));
        when(routeRepository.save(any(Route.class))).thenReturn(testRoute);

        // When
        busManagementService.submitRouteReview(1);

        // Then
        verify(routeRepository).save(argThat(route -> route.getStatus() == RouteStatus.PENDING_REVIEW));
    }

    @Test
    void approveRoute_ShouldUpdateRouteStatus() {
        // Given
        when(routeRepository.findById(1)).thenReturn(Optional.of(testRoute));
        when(routeRepository.save(any(Route.class))).thenReturn(testRoute);

        // When
        busManagementService.approveRoute(1);

        // Then
        verify(routeRepository).save(argThat(route -> route.getStatus() == RouteStatus.APPROVED));
    }

    @Test
    void rejectRoute_ShouldUpdateRouteStatus() {
        // Given
        when(routeRepository.findById(1)).thenReturn(Optional.of(testRoute));
        when(routeRepository.save(any(Route.class))).thenReturn(testRoute);

        // When
        busManagementService.rejectRoute(1);

        // Then
        verify(routeRepository).save(argThat(route -> route.getStatus() == RouteStatus.REJECTED));
    }

    // ==================== SLOTS TAB TESTS ====================

    @Test
    void getSlots_ShouldReturnSlots() {
        // Given
        when(busSlotRepository.findAll()).thenReturn(Arrays.asList(testSlot));

        // When
        List<BusManagementDTO.SlotSummary> slots = busManagementService.getSlots(null, null, null);

        // Then
        assertNotNull(slots);
        assertEquals(1, slots.size());
        assertEquals("test@provider.com", slots.get(0).getOwnerCode());
        assertEquals("51F-123.45", slots.get(0).getBusPlate());
        assertEquals(12, slots.get(0).getSold()); // 40 - 28 = 12
        assertEquals(40, slots.get(0).getTotal());
    }

    @Test
    void bulkGenerateSlots_ShouldGenerateSlots() {
        // Given
        BusManagementDTO.BulkGenerateRequest request = new BusManagementDTO.BulkGenerateRequest();
        request.setOwnerCode("test@provider.com");
        request.setRouteCode("1");
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(3));
        request.setDaysOfWeek(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        request.setDepartTime(LocalTime.of(22, 0));
        request.setDurationMinutes(900);
        request.setPrice(new BigDecimal("250000"));
        request.setBusId(1);

        when(routeRepository.findById(1)).thenReturn(Optional.of(testRoute));
        when(busRepository.findById(1)).thenReturn(Optional.of(testBus));
        when(busSlotRepository.save(any(BusSlot.class))).thenReturn(testSlot);

        // When
        List<BusManagementDTO.SlotSummary> generatedSlots = busManagementService.bulkGenerateSlots(request);

        // Then
        assertNotNull(generatedSlots);
        // Should generate slots for 3 days
        assertEquals(3, generatedSlots.size());
        verify(busSlotRepository, times(3)).save(any(BusSlot.class));
    }

    @Test
    void getSlotDetail_ShouldReturnSlotDetail() {
        // Given
        when(busSlotRepository.findById(1)).thenReturn(Optional.of(testSlot));

        // When
        BusManagementDTO.SlotDetail slotDetail = busManagementService.getSlotDetail(1);

        // Then
        assertNotNull(slotDetail);
        assertEquals("test@provider.com", slotDetail.getOwnerCode());
        assertEquals("51F-123.45", slotDetail.getBusPlate());
        assertEquals(40, slotDetail.getTotalSeats());
        assertEquals(28, slotDetail.getAvailableSeats());
        assertEquals("SCHEDULED", slotDetail.getStatus());
    }

    // ==================== MODERATION TAB TESTS ====================

    @Test
    void getModerationQueue_ShouldReturnModerationItems() {
        // Given
        testRoute.setStatus(RouteStatus.PENDING_REVIEW);
        when(routeRepository.findByStatus(RouteStatus.PENDING_REVIEW))
                .thenReturn(Arrays.asList(testRoute));

        // When
        List<BusManagementDTO.ModerationItem> items = busManagementService.getModerationQueue("ROUTE", "PENDING_REVIEW");

        // Then
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("ROUTE", items.get(0).getType());
        assertEquals("test@provider.com", items.get(0).getOwnerCode());
        assertEquals("PENDING_REVIEW", items.get(0).getStatus());
    }

    @Test
    void approveModeration_ShouldUpdateRouteStatus() {
        // Given
        when(routeRepository.findById(1)).thenReturn(Optional.of(testRoute));
        when(routeRepository.save(any(Route.class))).thenReturn(testRoute);

        // When
        busManagementService.approveModeration(1);

        // Then
        verify(routeRepository).save(argThat(route -> route.getStatus() == RouteStatus.APPROVED));
    }

    @Test
    void rejectModeration_ShouldUpdateRouteStatus() {
        // Given
        when(routeRepository.findById(1)).thenReturn(Optional.of(testRoute));
        when(routeRepository.save(any(Route.class))).thenReturn(testRoute);

        // When
        busManagementService.rejectModeration(1);

        // Then
        verify(routeRepository).save(argThat(route -> route.getStatus() == RouteStatus.REJECTED));
    }

    @Test
    void getModerationDiff_ShouldReturnDiff() {
        // Given
        when(routeRepository.findById(1)).thenReturn(Optional.of(testRoute));

        // When
        Map<String, Object> diff = busManagementService.getModerationDiff(1);

        // Then
        assertNotNull(diff);
        assertEquals(1, diff.get("id"));
        assertEquals("Saigon", diff.get("origin"));
        assertEquals("Da Nang", diff.get("destination"));
        assertEquals(850.0, diff.get("distance"));
        assertEquals(900, diff.get("duration"));
        assertEquals("APPROVED", diff.get("status"));
        assertEquals("test@provider.com", diff.get("owner"));
    }

    // ==================== ERROR HANDLING TESTS ====================

    @Test
    void createProvider_WithInvalidData_ShouldThrowException() {
        // Given
        BusManagementDTO.CreateProviderRequest request = new BusManagementDTO.CreateProviderRequest();
        request.setCode("invalid");
        request.setName("");
        request.setStatus("INVALID");

        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Invalid data"));

        // When & Then
        assertThrows(RuntimeException.class, () -> busManagementService.createProvider(request));
    }

    @Test
    void getSlotDetail_WithInvalidId_ShouldThrowException() {
        // Given
        when(busSlotRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> busManagementService.getSlotDetail(999));
    }

    @Test
    void unbanProvider_ShouldThrowException() {
        // When & Then
        assertThrows(RuntimeException.class, () -> busManagementService.unbanProvider(1));
    }
}
