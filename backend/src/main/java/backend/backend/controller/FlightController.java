package backend.backend.controller;

import backend.backend.dto.*;
import backend.backend.entity.*;
import backend.backend.service.CustomerService;
import backend.backend.service.FlightService;
import backend.backend.dao.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/flights")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @Autowired
    private AirlineDAO airlineDAO;

    @Autowired
    private AirportDAO airportDAO;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private FlightCategoryDAO flightCategoryDAO;

    @PostMapping("/search")
    public ResponseEntity<List<FlightDto>> searchFlights(@RequestBody  FlightSearchRequestDto request) {
        String requestId = UUID.randomUUID().toString();
        log.info("SEARCH_FLIGHTS_REQUEST - RequestId: {}, Params: {}", requestId, request);
        try {
            List<FlightDto> result = flightService.searchFlights(request);
            log.info("SEARCH_FLIGHTS_SUCCESS - RequestId: {}, Count: {}", requestId, result.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("SEARCH_FLIGHTS_FAILED - RequestId: {}, Error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{flightId}")
    public ResponseEntity<FlightDto> getFlightDetail(@PathVariable Integer flightId) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_FLIGHT_DETAIL_REQUEST - RequestId: {}, FlightId: {}", requestId, flightId);
        try {
            FlightDto dto = flightService.getFlightDetail(flightId);
            log.info("GET_FLIGHT_DETAIL_SUCCESS - RequestId: {}, FlightId: {}", requestId, flightId);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("GET_FLIGHT_DETAIL_FAILED - RequestId: {}, FlightId: {}, Error: {}", requestId, flightId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{flightId}/available-seats")
    public ResponseEntity<Map<String, Integer>> getAvailableSeats(@PathVariable Integer flightId) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_AVAILABLE_SEATS_REQUEST - RequestId: {}, FlightId: {}", requestId, flightId);
        try {
            Map<String, Integer> result = flightService.getAvailableSeatsDetail(flightId);
            log.info("GET_AVAILABLE_SEATS_SUCCESS - RequestId: {}, FlightId: {}, Seats: {}", requestId, flightId, result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("GET_AVAILABLE_SEATS_FAILED - RequestId: {}, FlightId: {}, Error: {}", requestId, flightId, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/find-available-slot")
    public ResponseEntity<FlightSlotDto> findFirstAvailableSlot(@RequestBody FindAvailableSlotRequestDto request) {
        String requestId = UUID.randomUUID().toString();
        log.info("FIND_FIRST_AVAILABLE_SLOT_REQUEST - RequestId: {}, request: {}", requestId, request);
        try {
            Optional<FlightSlotDto> result = flightService.findFirstAvailableSlot(request);
            if (result.isPresent()) {
                log.info("FIND_FIRST_AVAILABLE_SLOT_SUCCESS - RequestId: {}, found slot: {}", requestId, result.get().getId());
                return ResponseEntity.ok(result.get());
            } else {
                log.warn("FIND_FIRST_AVAILABLE_SLOT_NOT_FOUND - RequestId: {}", requestId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("FIND_FIRST_AVAILABLE_SLOT_FAILED - RequestId: {}, error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/airlines")
    public ResponseEntity<List<AirlineDto>> getAllAirlines() {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_ALL_AIRLINES_REQUEST - RequestId: {}", requestId);
        try {
            List<AirlineDto> dtos = airlineDAO.findAllByOrderByName().stream()
                    .map(a -> AirlineDto.builder().id(a.getId()).name(a.getName()).build())
                    .collect(Collectors.toList());
            log.info("GET_ALL_AIRLINES_SUCCESS - RequestId: {}, Count: {}", requestId, dtos.size());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            log.error("GET_ALL_AIRLINES_FAILED - RequestId: {}, Error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/airports")
    public ResponseEntity<List<AirportDto>> getAllAirports() {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_ALL_AIRPORTS_REQUEST - RequestId: {}", requestId);
        try {
            List<AirportDto> dtos = airportDAO.findAll().stream()
                    .map(a -> AirportDto.builder().id(a.getId()).name(a.getName()).build())
                    .collect(Collectors.toList());
            log.info("GET_ALL_AIRPORTS_SUCCESS - RequestId: {}, Count: {}", requestId, dtos.size());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            log.error("GET_ALL_AIRPORTS_FAILED - RequestId: {}, Error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<List<FlightCategoryDto>> getAllFlightCategories() {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_ALL_CATEGORIES_REQUEST - RequestId: {}", requestId);
        try {
            List<FlightCategoryDto> dtos = flightCategoryDAO.findAllByOrderByName().stream()
                    .map(c -> FlightCategoryDto.builder().id(c.getId()).name(c.getName()).build())
                    .collect(Collectors.toList());
            log.info("GET_ALL_CATEGORIES_SUCCESS - RequestId: {}, Count: {}", requestId, dtos.size());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            log.error("GET_ALL_CATEGORIES_FAILED - RequestId: {}, Error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }
    @PutMapping("/update-customer/{id}")
    public ResponseEntity<CustomerDto> updateCustomer( @PathVariable Integer id,@RequestBody CustomerDto customerDto) {
        String requestId = UUID.randomUUID().toString();
        log.info("UPDATE_CUSTOMER_REQUEST – requestId={}, id={}, payload={}",requestId, id, customerDto);
        try {
            CustomerDto updated = customerService.updateCustomer(id, customerDto);
            log.info("UPDATE_CUSTOMER_SUCCESS – requestId={}, id={}",requestId, updated.getId());

            return ResponseEntity.ok(updated);

        } catch (EntityNotFoundException ex) {
            log.warn("UPDATE_CUSTOMER_NOT_FOUND – requestId={}, id={}", requestId, id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("UPDATE_CUSTOMER_FAILED – requestId={}, id={}, error={}",requestId, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
