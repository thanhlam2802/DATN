package backend.backend.controller;

import backend.backend.dto.BusManagementDTO;
import backend.backend.entity.*;
import backend.backend.entity.enumBus.BusSlotStatus;
import backend.backend.entity.enumBus.RouteStatus;
import backend.backend.service.BusManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BusManagementController.class)
@ActiveProfiles("test")
class BusManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BusManagementService busManagementService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Route testRoute;
    private Bus testBus;
    private BusSlot testSlot;
    private Location originLocation;
    private Location destinationLocation;

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
    }

    // ==================== OVERVIEW TAB TESTS ====================

    @Test
    void getOverviewKPIs_ShouldReturnKPIs() throws Exception {
        // Given
        Map<String, Object> kpis = new HashMap<>();
        kpis.put("bookingsToday", 120);
        kpis.put("occupancyRate", 73.5);
        kpis.put("cancellations", 5);
        kpis.put("activeProviders", 12);

        when(busManagementService.getOverviewKPIs()).thenReturn(kpis);

        // When & Then
        mockMvc.perform(get("/api/admin/bus-management/overview/kpis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingsToday").value(120))
                .andExpect(jsonPath("$.occupancyRate").value(73.5))
                .andExpect(jsonPath("$.cancellations").value(5))
                .andExpect(jsonPath("$.activeProviders").value(12));
    }

    @Test
    void getDataQualityAlerts_ShouldReturnAlerts() throws Exception {
        // Given
        List<BusManagementDTO.DataQualityAlert> alerts = Arrays.asList(
                BusManagementDTO.DataQualityAlert.builder()
                        .type("PRICE_OVERLAP")
                        .ownerName("Test Provider")
                        .description("Giá trùng hiệu lực trên Saigon-Da Nang")
                        .suggestion("Điều chỉnh khoảng ngày / hợp nhất pricebook")
                        .at("2025-01-10T10:30:00")
                        .build()
        );

        when(busManagementService.getDataQualityAlerts(anyString())).thenReturn(alerts);

        // When & Then
        mockMvc.perform(get("/api/admin/bus-management/overview/alerts")
                        .param("query", "price"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("PRICE_OVERLAP"))
                .andExpect(jsonPath("$[0].ownerName").value("Test Provider"));
    }

    // ==================== PROVIDERS TAB TESTS ====================

    @Test
    void getProviders_ShouldReturnProviders() throws Exception {
        // Given
        List<BusManagementDTO.ProviderSummary> providers = Arrays.asList(
                BusManagementDTO.ProviderSummary.builder()
                        .id(1)
                        .code("test@provider.com")
                        .name("Test Provider")
                        .status("ACTIVE")
                        .routesCount(5)
                        .next7dSlots(20)
                        .build()
        );

        when(busManagementService.getProviders(anyString(), anyString())).thenReturn(providers);

        // When & Then
        mockMvc.perform(get("/api/admin/bus-management/providers")
                        .param("query", "test")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].code").value("test@provider.com"))
                .andExpect(jsonPath("$[0].name").value("Test Provider"));
    }

    @Test
    void createProvider_ShouldReturnCreatedProvider() throws Exception {
        // Given
        BusManagementDTO.CreateProviderRequest request = new BusManagementDTO.CreateProviderRequest();
        request.setCode("new@provider.com");
        request.setName("New Provider");
        request.setStatus("ACTIVE");

        BusManagementDTO.ProviderSummary createdProvider = BusManagementDTO.ProviderSummary.builder()
                .id(2)
                .code("new@provider.com")
                .name("New Provider")
                .status("ACTIVE")
                .routesCount(0)
                .next7dSlots(0)
                .build();

        when(busManagementService.createProvider(any(BusManagementDTO.CreateProviderRequest.class)))
                .thenReturn(createdProvider);

        // When & Then
        mockMvc.perform(post("/api/admin/bus-management/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.code").value("new@provider.com"));
    }

    @Test
    void banProvider_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/admin/bus-management/providers/1/ban"))
                .andExpect(status().isOk());
    }

    // ==================== ROUTES TAB TESTS ====================

    @Test
    void getRoutes_ShouldReturnRoutes() throws Exception {
        // Given
        List<BusManagementDTO.RouteSummary> routes = Arrays.asList(
                BusManagementDTO.RouteSummary.builder()
                        .id(1)
                        .ownerCode("test@provider.com")
                        .routeCode("test@provider.com-Saigon-Da Nang-1")
                        .origin("Saigon")
                        .destination("Da Nang")
                        .status("APPROVED")
                        .futureSlots(10)
                        .distanceKm(850.0)
                        .estimatedDurationMinutes(900)
                        .build()
        );

        when(busManagementService.getRoutes(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(routes);

        // When & Then
        mockMvc.perform(get("/api/admin/bus-management/routes")
                        .param("owner", "test")
                        .param("origin", "saigon")
                        .param("destination", "danang")
                        .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].origin").value("Saigon"))
                .andExpect(jsonPath("$[0].destination").value("Da Nang"));
    }

    @Test
    void createRoute_ShouldReturnCreatedRoute() throws Exception {
        // Given
        BusManagementDTO.CreateRouteRequest request = new BusManagementDTO.CreateRouteRequest();
        request.setOwnerCode("test@provider.com");
        request.setRouteCode("TEST-SGN-DAD-01");
        request.setOrigin("Saigon");
        request.setDestination("Da Nang");
        request.setStatus("DRAFT");
        request.setDistanceKm(850.0);
        request.setEstimatedDurationMinutes(900);

        BusManagementDTO.RouteSummary createdRoute = BusManagementDTO.RouteSummary.builder()
                .id(2)
                .ownerCode("test@provider.com")
                .routeCode("test@provider.com-Saigon-Da Nang-2")
                .origin("Saigon")
                .destination("Da Nang")
                .status("DRAFT")
                .futureSlots(0)
                .distanceKm(850.0)
                .estimatedDurationMinutes(900)
                .build();

        when(busManagementService.createRoute(any(BusManagementDTO.CreateRouteRequest.class)))
                .thenReturn(createdRoute);

        // When & Then
        mockMvc.perform(post("/api/admin/bus-management/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void submitRouteReview_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/admin/bus-management/routes/1/submit-review"))
                .andExpect(status().isOk());
    }

    @Test
    void approveRoute_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/admin/bus-management/routes/1/approve"))
                .andExpect(status().isOk());
    }

    @Test
    void rejectRoute_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/admin/bus-management/routes/1/reject"))
                .andExpect(status().isOk());
    }

    // ==================== SLOTS TAB TESTS ====================

    @Test
    void getSlots_ShouldReturnSlots() throws Exception {
        // Given
        List<BusManagementDTO.SlotSummary> slots = Arrays.asList(
                BusManagementDTO.SlotSummary.builder()
                        .id(1)
                        .ownerCode("test@provider.com")
                        .routeCode("1")
                        .departTime("2025-01-11 22:00")
                        .busPlate("51F-123.45")
                        .sold(12)
                        .total(40)
                        .status("SCHEDULED")
                        .price(new BigDecimal("250000"))
                        .slotDate(LocalDate.now().plusDays(1))
                        .departureTime(LocalTime.of(22, 0))
                        .arrivalTime(LocalTime.of(13, 0))
                        .build()
        );

        when(busManagementService.getSlots(anyString(), anyString(), anyString()))
                .thenReturn(slots);

        // When & Then
        mockMvc.perform(get("/api/admin/bus-management/slots")
                        .param("owner", "test")
                        .param("route", "1")
                        .param("date", "2025-01-11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].busPlate").value("51F-123.45"))
                .andExpect(jsonPath("$[0].sold").value(12))
                .andExpect(jsonPath("$[0].total").value(40));
    }

    @Test
    void bulkGenerateSlots_ShouldReturnGeneratedSlots() throws Exception {
        // Given
        BusManagementDTO.BulkGenerateRequest request = new BusManagementDTO.BulkGenerateRequest();
        request.setOwnerCode("test@provider.com");
        request.setRouteCode("1");
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(7));
        request.setDaysOfWeek(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        request.setDepartTime(LocalTime.of(22, 0));
        request.setDurationMinutes(900);
        request.setPrice(new BigDecimal("250000"));
        request.setBusId(1);

        List<BusManagementDTO.SlotSummary> generatedSlots = Arrays.asList(
                BusManagementDTO.SlotSummary.builder()
                        .id(1)
                        .ownerCode("test@provider.com")
                        .routeCode("1")
                        .departTime("2025-01-11 22:00")
                        .busPlate("51F-123.45")
                        .sold(0)
                        .total(40)
                        .status("SCHEDULED")
                        .price(new BigDecimal("250000"))
                        .slotDate(LocalDate.now().plusDays(1))
                        .departureTime(LocalTime.of(22, 0))
                        .arrivalTime(LocalTime.of(13, 0))
                        .build()
        );

        when(busManagementService.bulkGenerateSlots(any(BusManagementDTO.BulkGenerateRequest.class)))
                .thenReturn(generatedSlots);

        // When & Then
        mockMvc.perform(post("/api/admin/bus-management/slots/bulk-generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].sold").value(0));
    }

    @Test
    void getSlotDetail_ShouldReturnSlotDetail() throws Exception {
        // Given
        BusManagementDTO.SlotDetail slotDetail = BusManagementDTO.SlotDetail.builder()
                .id(1)
                .ownerCode("test@provider.com")
                .routeCode("1")
                .busPlate("51F-123.45")
                .slotDate(LocalDate.now().plusDays(1))
                .departureTime(LocalTime.of(22, 0))
                .arrivalTime(LocalTime.of(13, 0))
                .price(new BigDecimal("250000"))
                .totalSeats(40)
                .availableSeats(28)
                .status("SCHEDULED")
                .seats(new ArrayList<>())
                .bookings(new ArrayList<>())
                .build();

        when(busManagementService.getSlotDetail(1)).thenReturn(slotDetail);

        // When & Then
        mockMvc.perform(get("/api/admin/bus-management/slots/1/detail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.busPlate").value("51F-123.45"))
                .andExpect(jsonPath("$.totalSeats").value(40))
                .andExpect(jsonPath("$.availableSeats").value(28));
    }

    // ==================== MODERATION TAB TESTS ====================

    @Test
    void getModerationQueue_ShouldReturnModerationItems() throws Exception {
        // Given
        List<BusManagementDTO.ModerationItem> items = Arrays.asList(
                BusManagementDTO.ModerationItem.builder()
                        .id(1)
                        .type("ROUTE")
                        .ownerCode("test@provider.com")
                        .submittedBy("Test Provider")
                        .submittedAt("2025-01-10T10:30:00")
                        .status("PENDING_REVIEW")
                        .content("Saigon → Da Nang")
                        .build()
        );

        when(busManagementService.getModerationQueue(anyString(), anyString()))
                .thenReturn(items);

        // When & Then
        mockMvc.perform(get("/api/admin/bus-management/moderation")
                        .param("type", "ROUTE")
                        .param("status", "PENDING_REVIEW"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].type").value("ROUTE"))
                .andExpect(jsonPath("$[0].status").value("PENDING_REVIEW"));
    }

    @Test
    void approveModeration_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/admin/bus-management/moderation/1/approve"))
                .andExpect(status().isOk());
    }

    @Test
    void rejectModeration_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/admin/bus-management/moderation/1/reject"))
                .andExpect(status().isOk());
    }

    @Test
    void getModerationDiff_ShouldReturnDiff() throws Exception {
        // Given
        Map<String, Object> diff = new HashMap<>();
        diff.put("id", 1);
        diff.put("origin", "Saigon");
        diff.put("destination", "Da Nang");
        diff.put("distance", 850.0);
        diff.put("duration", 900);
        diff.put("status", "PENDING_REVIEW");
        diff.put("owner", "test@provider.com");

        when(busManagementService.getModerationDiff(1)).thenReturn(diff);

        // When & Then
        mockMvc.perform(get("/api/admin/bus-management/moderation/1/diff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.origin").value("Saigon"))
                .andExpect(jsonPath("$.destination").value("Da Nang"));
    }
}
