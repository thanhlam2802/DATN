package backend.backend.integration;

import backend.backend.dto.BusManagementDTO;
import backend.backend.entity.*;
import backend.backend.entity.enumBus.BusSlotStatus;
import backend.backend.entity.enumBus.RouteStatus;
import backend.backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BusManagementIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private BusSlotRepository busSlotRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private BusCategoryRepository busCategoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private User testUser;
    private Route testRoute;
    private Bus testBus;
    private Location originLocation;
    private Location destinationLocation;
    private BusCategory testCategory;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Create test data
        testUser = new User();
        testUser.setName("Integration Test Provider");
        testUser.setEmail("integration@test.com");
        testUser.setPasswordHash("test_password_hash");
        testUser = userRepository.save(testUser);

        originLocation = new Location();
        originLocation.setName("Test Origin");
        originLocation = locationRepository.save(originLocation);

        destinationLocation = new Location();
        destinationLocation.setName("Test Destination");
        destinationLocation = locationRepository.save(destinationLocation);

        testCategory = new BusCategory();
        testCategory.setName("Test Category");
        testCategory.setOwner(testUser);
        testCategory = busCategoryRepository.save(testCategory);

        testRoute = new Route();
        testRoute.setOwner(testUser);
        testRoute.setOriginLocation(originLocation);
        testRoute.setDestinationLocation(destinationLocation);
        testRoute.setStatus(RouteStatus.DRAFT);
        testRoute.setDistanceKm(100.0);
        testRoute.setEstimatedDurationMinutes(120);
        testRoute = routeRepository.save(testRoute);

        testBus = new Bus();
        testBus.setName("Integration Test Bus");
        testBus.setLicensePlate("TEST-123.45");
        testBus.setTotalSeats(30);
        testBus.setCategory(testCategory);
        testBus.setOwner(testUser);
        testBus = busRepository.save(testBus);
    }

    @Test
    void getOverviewKPIs_ShouldReturnKPIs() throws Exception {
        // Given - Create some test slots
        BusSlot slot = new BusSlot();
        slot.setBus(testBus);
        slot.setRoute(testRoute);
        slot.setOwner(testUser);
        slot.setSlotDate(LocalDate.now().plusDays(1));
        slot.setDepartureTime(LocalTime.of(10, 0));
        slot.setArrivalTime(LocalTime.of(12, 0));
        slot.setPrice(new BigDecimal("100000"));
        slot.setTotalSeats(30);
        slot.setAvailableSeats(25);
        slot.setStatus(BusSlotStatus.SCHEDULED);
        busSlotRepository.save(slot);

        // When & Then
        mockMvc.perform(get("/api/admin/bus-management/overview/kpis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingsToday").exists())
                .andExpect(jsonPath("$.occupancyRate").exists())
                .andExpect(jsonPath("$.cancellations").exists())
                .andExpect(jsonPath("$.activeProviders").exists());
    }

    @Test
    void getProviders_ShouldReturnProviders() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/bus-management/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testUser.getId()))
                .andExpect(jsonPath("$[0].code").value("integration@test.com"))
                .andExpect(jsonPath("$[0].name").value("Integration Test Provider"));
    }

    @Test
    void createProvider_ShouldCreateAndReturnProvider() throws Exception {
        // Given
        BusManagementDTO.CreateProviderRequest request = new BusManagementDTO.CreateProviderRequest();
        request.setCode("new@integration.com");
        request.setName("New Integration Provider");
        request.setStatus("ACTIVE");

        // When & Then
        mockMvc.perform(post("/api/admin/bus-management/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("new@integration.com"))
                .andExpect(jsonPath("$.name").value("New Integration Provider"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getRoutes_ShouldReturnRoutes() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/bus-management/routes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testRoute.getId()))
                .andExpect(jsonPath("$[0].ownerCode").value("integration@test.com"))
                .andExpect(jsonPath("$[0].origin").value("Test Origin"))
                .andExpect(jsonPath("$[0].destination").value("Test Destination"));
    }

    @Test
    void createRoute_ShouldCreateAndReturnRoute() throws Exception {
        // Given
        BusManagementDTO.CreateRouteRequest request = new BusManagementDTO.CreateRouteRequest();
        request.setOwnerCode("integration@test.com");
        request.setRouteCode("INTEGRATION-TEST-01");
        request.setOrigin("Test Origin");
        request.setDestination("Test Destination");
        request.setStatus("DRAFT");
        request.setDistanceKm(150.0);
        request.setEstimatedDurationMinutes(180);

        // When & Then
        mockMvc.perform(post("/api/admin/bus-management/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerCode").value("integration@test.com"))
                .andExpect(jsonPath("$.origin").value("Test Origin"))
                .andExpect(jsonPath("$.destination").value("Test Destination"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void submitRouteReview_ShouldUpdateRouteStatus() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/admin/bus-management/routes/" + testRoute.getId() + "/submit-review"))
                .andExpect(status().isOk());

        // Verify the route status was updated
        Route updatedRoute = routeRepository.findById(testRoute.getId()).orElse(null);
        assert updatedRoute != null;
        assert updatedRoute.getStatus() == RouteStatus.PENDING_REVIEW;
    }

    @Test
    void approveRoute_ShouldUpdateRouteStatus() throws Exception {
        // Given - Set route to pending review first
        testRoute.setStatus(RouteStatus.PENDING_REVIEW);
        routeRepository.save(testRoute);

        // When & Then
        mockMvc.perform(post("/api/admin/bus-management/routes/" + testRoute.getId() + "/approve"))
                .andExpect(status().isOk());

        // Verify the route status was updated
        Route updatedRoute = routeRepository.findById(testRoute.getId()).orElse(null);
        assert updatedRoute != null;
        assert updatedRoute.getStatus() == RouteStatus.APPROVED;
    }

    @Test
    void getSlots_ShouldReturnSlots() throws Exception {
        // Given - Create a test slot
        BusSlot slot = new BusSlot();
        slot.setBus(testBus);
        slot.setRoute(testRoute);
        slot.setOwner(testUser);
        slot.setSlotDate(LocalDate.now().plusDays(1));
        slot.setDepartureTime(LocalTime.of(10, 0));
        slot.setArrivalTime(LocalTime.of(12, 0));
        slot.setPrice(new BigDecimal("100000"));
        slot.setTotalSeats(30);
        slot.setAvailableSeats(25);
        slot.setStatus(BusSlotStatus.SCHEDULED);
        busSlotRepository.save(slot);

        // When & Then
        mockMvc.perform(get("/api/admin/bus-management/slots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(slot.getId()))
                .andExpect(jsonPath("$[0].ownerCode").value("integration@test.com"))
                .andExpect(jsonPath("$[0].busPlate").value("TEST-123.45"))
                .andExpect(jsonPath("$[0].sold").value(5)) // 30 - 25 = 5
                .andExpect(jsonPath("$[0].total").value(30));
    }

    @Test
    void bulkGenerateSlots_ShouldGenerateSlots() throws Exception {
        // Given
        BusManagementDTO.BulkGenerateRequest request = new BusManagementDTO.BulkGenerateRequest();
        request.setOwnerCode("integration@test.com");
        request.setRouteCode(testRoute.getId().toString());
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(3));
        request.setDaysOfWeek(java.util.Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        request.setDepartTime(LocalTime.of(10, 0));
        request.setDurationMinutes(120);
        request.setPrice(new BigDecimal("100000"));
        request.setBusId(testBus.getId());

        // When & Then
        mockMvc.perform(post("/api/admin/bus-management/slots/bulk-generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(3))); // 3 days
    }

    @Test
    void getSlotDetail_ShouldReturnSlotDetail() throws Exception {
        // Given - Create a test slot
        BusSlot slot = new BusSlot();
        slot.setBus(testBus);
        slot.setRoute(testRoute);
        slot.setOwner(testUser);
        slot.setSlotDate(LocalDate.now().plusDays(1));
        slot.setDepartureTime(LocalTime.of(10, 0));
        slot.setArrivalTime(LocalTime.of(12, 0));
        slot.setPrice(new BigDecimal("100000"));
        slot.setTotalSeats(30);
        slot.setAvailableSeats(25);
        slot.setStatus(BusSlotStatus.SCHEDULED);
        slot.setSeats(new java.util.ArrayList<>());
        slot.setBusBookings(new java.util.ArrayList<>());
        slot = busSlotRepository.save(slot);

        // When & Then
        mockMvc.perform(get("/api/admin/bus-management/slots/" + slot.getId() + "/detail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(slot.getId()))
                .andExpect(jsonPath("$.ownerCode").value("integration@test.com"))
                .andExpect(jsonPath("$.busPlate").value("TEST-123.45"))
                .andExpect(jsonPath("$.totalSeats").value(30))
                .andExpect(jsonPath("$.availableSeats").value(25));
    }

    @Test
    void getModerationQueue_ShouldReturnModerationItems() throws Exception {
        // Given - Set route to pending review
        testRoute.setStatus(RouteStatus.PENDING_REVIEW);
        routeRepository.save(testRoute);

        // When & Then
        mockMvc.perform(get("/api/admin/bus-management/moderation")
                        .param("type", "ROUTE")
                        .param("status", "PENDING_REVIEW"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testRoute.getId()))
                .andExpect(jsonPath("$[0].type").value("ROUTE"))
                .andExpect(jsonPath("$[0].ownerCode").value("integration@test.com"))
                .andExpect(jsonPath("$[0].status").value("PENDING_REVIEW"));
    }

    @Test
    void getModerationDiff_ShouldReturnDiff() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/bus-management/moderation/" + testRoute.getId() + "/diff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testRoute.getId()))
                .andExpect(jsonPath("$.origin").value("Test Origin"))
                .andExpect(jsonPath("$.destination").value("Test Destination"))
                .andExpect(jsonPath("$.distance").value(100.0))
                .andExpect(jsonPath("$.duration").value(120))
                .andExpect(jsonPath("$.owner").value("integration@test.com"));
    }

    @Test
    void banProvider_ShouldDeleteUserBuses() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/admin/bus-management/providers/" + testUser.getId() + "/ban"))
                .andExpect(status().isOk());

        // Verify the user's buses were deleted
        java.util.List<Bus> userBuses = busRepository.findByOwner(testUser);
        assert userBuses.isEmpty();
    }
}
