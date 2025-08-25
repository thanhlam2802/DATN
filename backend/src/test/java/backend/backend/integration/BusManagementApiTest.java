package backend.backend.integration;

import backend.backend.BackendApplication;
import backend.backend.entity.*;
import backend.backend.entity.enumBus.BusSlotStatus;
import backend.backend.entity.enumBus.RouteStatus;
import backend.backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = BackendApplication.class)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class BusManagementApiTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private BusSlotRepository busSlotRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private User testUser;
    private Route testRoute;
    private Bus testBus;
    private BusSlot testSlot;
    private Location originLocation;
    private Location destinationLocation;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();

        // Setup test data
        testUser = new User();
        testUser.setName("Test Provider");
        testUser.setEmail("test@provider.com");
        testUser.setPasswordHash("password123");
        testUser = userRepository.save(testUser);

        originLocation = new Location();
        originLocation.setName("Saigon");
        originLocation = locationRepository.save(originLocation);

        destinationLocation = new Location();
        destinationLocation.setName("Da Nang");
        destinationLocation = locationRepository.save(destinationLocation);

        testRoute = new Route();
        testRoute.setOwner(testUser);
        testRoute.setOriginLocation(originLocation);
        testRoute.setDestinationLocation(destinationLocation);
        testRoute.setStatus(RouteStatus.APPROVED);
        testRoute.setDistanceKm(850.0);
        testRoute.setEstimatedDurationMinutes(900);
        testRoute.setCreatedAt(Instant.now());
        testRoute.setUpdatedAt(Instant.now());
        testRoute = routeRepository.save(testRoute);

        testBus = new Bus();
        testBus.setName("Test Bus");
        testBus.setLicensePlate("51F-123.45");
        testBus.setTotalSeats(40);
        testBus.setOwner(testUser);
        testBus = busRepository.save(testBus);

        testSlot = new BusSlot();
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
        testSlot = busSlotRepository.save(testSlot);
    }

    @Test
    void testGetOverviewKPIs() throws Exception {
        mockMvc.perform(get("/api/bus-management/overview/kpis"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bookingsToday").exists())
                .andExpect(jsonPath("$.occupancyRate").exists())
                .andExpect(jsonPath("$.cancellations").exists())
                .andExpect(jsonPath("$.activeProviders").exists());
    }

    @Test
    void testGetProviders() throws Exception {
        mockMvc.perform(get("/api/bus-management/providers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testUser.getId()))
                .andExpect(jsonPath("$[0].code").value("test@provider.com"))
                .andExpect(jsonPath("$[0].name").value("Test Provider"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    void testGetRoutes() throws Exception {
        mockMvc.perform(get("/api/bus-management/routes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testRoute.getId()))
                .andExpect(jsonPath("$[0].ownerCode").value("test@provider.com"))
                .andExpect(jsonPath("$[0].origin").value("Saigon"))
                .andExpect(jsonPath("$[0].destination").value("Da Nang"))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }

    @Test
    void testGetSlots() throws Exception {
        mockMvc.perform(get("/api/bus-management/slots"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testSlot.getId()))
                .andExpect(jsonPath("$[0].ownerCode").value("test@provider.com"))
                .andExpect(jsonPath("$[0].busPlate").value("51F-123.45"))
                .andExpect(jsonPath("$[0].status").value("SCHEDULED"));
    }

    @Test
    void testGetModerationQueue() throws Exception {
        // Set route to pending review
        testRoute.setStatus(RouteStatus.PENDING_REVIEW);
        routeRepository.save(testRoute);

        mockMvc.perform(get("/api/bus-management/moderation/queue"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testCreateProvider() throws Exception {
        String createProviderRequest = """
                {
                    "code": "new@provider.com",
                    "name": "New Provider"
                }
                """;

        mockMvc.perform(post("/api/bus-management/providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createProviderRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("new@provider.com"))
                .andExpect(jsonPath("$.name").value("New Provider"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void testCreateRoute() throws Exception {
        String createRouteRequest = """
                {
                    "ownerCode": "test@provider.com",
                    "origin": "Saigon",
                    "destination": "Hanoi",
                    "distanceKm": 1200.0,
                    "estimatedDurationMinutes": 1440,
                    "status": "DRAFT"
                }
                """;

        mockMvc.perform(post("/api/bus-management/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRouteRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ownerCode").value("test@provider.com"))
                .andExpect(jsonPath("$.origin").value("Saigon"))
                .andExpect(jsonPath("$.destination").value("Hanoi"));
    }

    @Test
    void testApproveRoute() throws Exception {
        // Set route to pending review first
        testRoute.setStatus(RouteStatus.PENDING_REVIEW);
        routeRepository.save(testRoute);

        mockMvc.perform(put("/api/bus-management/routes/{id}/approve", testRoute.getId()))
                .andExpect(status().isOk());

        // Verify route is now approved
        mockMvc.perform(get("/api/bus-management/routes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }

    @Test
    void testBanProvider() throws Exception {
        mockMvc.perform(put("/api/bus-management/providers/{id}/ban", testUser.getId()))
                .andExpect(status().isOk());

        // Verify provider is banned (no buses)
        mockMvc.perform(get("/api/bus-management/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
