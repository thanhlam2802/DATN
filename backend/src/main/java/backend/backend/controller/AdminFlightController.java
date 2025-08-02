package backend.backend.controller;

import backend.backend.dto.*;
import backend.backend.service.AdminFlightService;
import backend.backend.service.ImageStorageService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminFlightController {

    @Autowired
    private AdminFlightService adminFlightService;

    @Autowired
    private ImageStorageService imageStorageService;

    @GetMapping("/flights")
    public ResponseEntity<List<FlightDto>> getFlights(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String filter) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_FLIGHTS_REQUEST - RequestId: {}, page: {}, size: {}, filter: {}", requestId, page, size, filter);
        try {
            List<FlightDto> list = adminFlightService.getFlights(page, size, filter);
            log.info("GET_FLIGHTS_SUCCESS - RequestId: {}, Count: {}", requestId, list.size());
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            log.error("GET_FLIGHTS_FAILED - RequestId: {}, Error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/flights/{flightId}")
    public ResponseEntity<FlightDto> getFlightDetail(@PathVariable Integer flightId) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_FLIGHT_DETAIL_REQUEST - RequestId: {}, flightId: {}", requestId, flightId);
        try {
            FlightDto dto = adminFlightService.getFlightDetail(flightId);
            log.info("GET_FLIGHT_DETAIL_SUCCESS - RequestId: {}, flightId: {}", requestId, flightId);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("GET_FLIGHT_DETAIL_FAILED - RequestId: {}, flightId: {}, Error: {}", requestId, flightId, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/flights")
    public ResponseEntity<FlightDto> createFlight(@Valid @RequestBody CreateFlightRequestDto requestDto) {
        String requestId = UUID.randomUUID().toString();
        log.info("CREATE_FLIGHT_REQUEST - RequestId: {}, Payload: {}", requestId, requestDto);
        try {
            FlightDto created = adminFlightService.createFlightWithDetails(requestDto);
            log.info("CREATE_FLIGHT_SUCCESS - RequestId: {}, FlightId: {}", requestId, created.getId());
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            log.error("CREATE_FLIGHT_FAILED - RequestId: {}, Error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/flights/{flightId}/images")
    public ResponseEntity<List<ImageDto>> uploadFlightImages(
            @PathVariable Integer flightId,
            @RequestParam("files") List<MultipartFile> files) {
        String requestId = UUID.randomUUID().toString();
        log.info("UPLOAD_FLIGHT_IMAGES_REQUEST - RequestId: {}, flightId: {}, fileCount: {}", requestId, flightId, files.size());
        try {
            List<ImageDto> uploadedImages = adminFlightService.uploadFlightImages(flightId, files);
            log.info("UPLOAD_FLIGHT_IMAGES_SUCCESS - RequestId: {}, flightId: {}, uploadedCount: {}", requestId, flightId, uploadedImages.size());
            return ResponseEntity.ok(uploadedImages);
        } catch (Exception e) {
            log.error("UPLOAD_FLIGHT_IMAGES_FAILED - RequestId: {}, flightId: {}, Error: {}", requestId, flightId, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/flights/{flightId}")
    public ResponseEntity<FlightDto> updateFlight(
            @PathVariable Integer flightId,
            @Valid @RequestBody FlightDto flightDto) {
        String requestId = UUID.randomUUID().toString();
        log.info("UPDATE_FLIGHT_REQUEST - RequestId: {}, flightId: {}, Payload: {}", requestId, flightId, flightDto);
        try {
            FlightDto updated = adminFlightService.updateFlight(flightId, flightDto);
            log.info("UPDATE_FLIGHT_SUCCESS - RequestId: {}, flightId: {}", requestId, flightId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("UPDATE_FLIGHT_FAILED - RequestId: {}, flightId: {}, Error: {}", requestId, flightId, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/flights/{flightId}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Integer flightId) {
        String requestId = UUID.randomUUID().toString();
        log.info("DELETE_FLIGHT_REQUEST - RequestId: {}, flightId: {}", requestId, flightId);
        try {
            adminFlightService.deleteFlight(flightId);
            log.info("DELETE_FLIGHT_SUCCESS - RequestId: {}, flightId: {}", requestId, flightId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("DELETE_FLIGHT_FAILED - RequestId: {}, flightId: {}, Error: {}", requestId, flightId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/flights/{flightId}/seats")
    public ResponseEntity<List<FlightSlotDto>> getSeats(@PathVariable Integer flightId) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_SEATS_REQUEST - RequestId: {}, flightId: {}", requestId, flightId);
        try {
            List<FlightSlotDto> seats = adminFlightService.getSeats(flightId);
            log.info("GET_SEATS_SUCCESS - RequestId: {}, flightId: {}, Count: {}", requestId, flightId, seats.size());
            return ResponseEntity.ok(seats);
        } catch (Exception e) {
            log.error("GET_SEATS_FAILED - RequestId: {}, flightId: {}, Error: {}", requestId, flightId, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/flights/{flightId}/seats")
    public ResponseEntity<List<FlightSlotDto>> updateSeats(
            @PathVariable String flightId,
            @Valid @RequestBody List<FlightSlotDto> seats) {
        String requestId = UUID.randomUUID().toString();
        log.info("UPDATE_SEATS_REQUEST - RequestId: {}, flightId: {}, PayloadCount: {}", requestId, flightId, seats.size());
        try {
            List<FlightSlotDto> updated = adminFlightService.updateSeats(Integer.parseInt(flightId), seats);
            log.info("UPDATE_SEATS_SUCCESS - RequestId: {}, flightId: {}, UpdatedCount: {}", requestId, flightId, updated.size());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("UPDATE_SEATS_FAILED - RequestId: {}, flightId: {}, Error: {}", requestId, flightId, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/flights/seats/{slotId}")
    public ResponseEntity<FlightSlotDto> updateSeat(
            @PathVariable Integer slotId,
            @Valid @RequestBody FlightSlotDto slotDto) {
        String requestId = UUID.randomUUID().toString();
        log.info("UPDATE_SEAT_REQUEST - RequestId: {},  slotId: {}, Payload: {}", requestId, slotId, slotDto);
        try {
            FlightSlotDto updated = adminFlightService.updateSeat(slotId, slotDto);
            log.info("UPDATE_SEAT_SUCCESS - RequestId: {}, slotId: {}", requestId, slotId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("UPDATE_SEAT_FAILED - RequestId: {}, slotId: {}, Error: {}", requestId, slotId, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/flights/{flightId}/seats/{slotId}")
    public ResponseEntity<Void> deleteSeat(
            @PathVariable Integer flightId,
            @PathVariable Integer slotId) {
        String requestId = UUID.randomUUID().toString();
        log.info("DELETE_SEAT_REQUEST - RequestId: {}, flightId: {}, slotId: {}", requestId, flightId, slotId);
        try {
            adminFlightService.deleteSeat(slotId);
            log.info("DELETE_SEAT_SUCCESS - RequestId: {}, flightId: {}, slotId: {}", requestId, flightId, slotId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("DELETE_SEAT_FAILED - RequestId: {}, flightId: {}, slotId: {}, Error: {}", requestId, flightId, slotId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/flight-bookings")
    public ResponseEntity<List<FlightBookingDetailDto>> getFlightBookings(
            @RequestParam(required = false) String filter) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_BOOKINGS_REQUEST - RequestId: {}, filter: {}", requestId, filter);
        try {
            List<FlightBookingDetailDto> list = adminFlightService.getFlightBookings(filter);
            log.info("GET_BOOKINGS_SUCCESS - RequestId: {}, Count: {}", requestId, list.size());
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            log.error("GET_BOOKINGS_FAILED - RequestId: {}, Error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/flight-bookings/{bookingId}")
    public ResponseEntity<FlightBookingDetailDto> getFlightBookingDetail(@PathVariable Integer bookingId) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_BOOKING_DETAIL_REQUEST - RequestId: {}, bookingId: {}", requestId, bookingId);
        try {
            FlightBookingDetailDto detail = adminFlightService.getFlightBookingDetail(bookingId);
            log.info("GET_BOOKING_DETAIL_SUCCESS - RequestId: {}, bookingId: {}", requestId, bookingId);
            return ResponseEntity.ok(detail);
        } catch (Exception e) {
            log.error("GET_BOOKING_DETAIL_FAILED - RequestId: {}, bookingId: {}, Error: {}", requestId, bookingId, e.getMessage(), e);
            throw e;
        }
    }
    @GetMapping("/flight-booked/{fligtId}")
    public ResponseEntity<List<FlightSlotDto>> flightBooked(@PathVariable Integer fligtId) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_BOOKING_DETAIL_REQUEST - RequestId: {}, bookingId: {}", requestId, fligtId);
        try {
            List<FlightSlotDto>  detail = adminFlightService.getSeatsBooked(fligtId);
            log.info("GET_BOOKING_DETAIL_SUCCESS - RequestId: {}, bookingId: {}", requestId, fligtId);
            return ResponseEntity.ok(detail);
        } catch (Exception e) {
            log.error("GET_BOOKING_DETAIL_FAILED - RequestId: {}, bookingId: {}, Error: {}", requestId, fligtId, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/flight-bookings/{bookingId}")
    public ResponseEntity<FlightBookingDetailDto> updateFlightBookingStatus(
            @PathVariable Integer bookingId,
            @RequestParam String status) {
        String requestId = UUID.randomUUID().toString();
        log.info("UPDATE_BOOKING_STATUS_REQUEST - RequestId: {}, bookingId: {}, status: {}", requestId, bookingId, status);
        try {
            FlightBookingDetailDto updated = adminFlightService.updateFlightBookingStatus(bookingId, status);
            log.info("UPDATE_BOOKING_STATUS_SUCCESS - RequestId: {}, bookingId: {}, status: {}", requestId, bookingId, status);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("UPDATE_BOOKING_STATUS_FAILED - RequestId: {}, bookingId: {}, Error: {}", requestId, bookingId, e.getMessage(), e);
            throw e;
        }
    }
    @PutMapping("/updateGroupSeat/{flightId}")
    public ResponseEntity<Void> updateGroupSeat(
            @PathVariable Integer flightId,
            @RequestBody FlightSeatGroupDto dto) {
        String requestId = UUID.randomUUID().toString();
        log.info("UPDATE_BOOKING_STATUS_REQUEST - RequestId: {}, bookingId: {}, status: {}", requestId, flightId, dto);
        try {
            adminFlightService.updateGroupSeat(flightId,dto);
            log.info("UPDATE_BOOKING_STATUS_SUCCESS - RequestId: {}, bookingId: {}, status: {}", requestId, flightId, dto);
        } catch (Exception e) {
            log.error("UPDATE_BOOKING_STATUS_FAILED - RequestId: {}, bookingId: {}, Error: {}", requestId, flightId, e.getMessage(), e);
            throw e;
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/flights/statistics")
    public ResponseEntity<List<FlightStatisticsDto>> getFlightStatistics(
            @RequestParam String type,
            @RequestParam String value) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_STATISTICS_REQUEST - RequestId: {}, type: {}, value: {}", requestId, type, value);
        try {
            List<FlightStatisticsDto> stats = adminFlightService.getFlightStatistics(type, value);
            log.info("GET_STATISTICS_SUCCESS - RequestId: {}, type: {}, ResultCount: {}", requestId, type, stats.size());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("GET_STATISTICS_FAILED - RequestId: {}, type: {}, Error: {}", requestId, type, e.getMessage(), e);
            throw e;
        }
    }

        @GetMapping("/flights/monthly-statistics")
    public ResponseEntity<MonthlyFlightStatisticsDto> getMonthlyFlightStatistics(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_MONTHLY_STATISTICS_REQUEST - RequestId: {}, year: {}, month: {}", requestId, year, month);
        try {
            MonthlyFlightStatisticsDto stats = adminFlightService.getMonthlyFlightStatistics(year, month);
            log.info("GET_MONTHLY_STATISTICS_SUCCESS - RequestId: {}, year: {}, month: {}", requestId, year, month);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("GET_MONTHLY_STATISTICS_FAILED - RequestId: {}, year: {}, month: {}, Error: {}",
                     requestId, year, month, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/flights/bookings-by-destination")
    public ResponseEntity<List<BookingByDestinationDto>> getBookingsByDestination(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_BOOKINGS_BY_DESTINATION_REQUEST - RequestId: {}, year: {}, month: {}", requestId, year, month);
        try {
            List<BookingByDestinationDto> stats = adminFlightService.getBookingsByDestination(year, month);
            log.info("GET_BOOKINGS_BY_DESTINATION_SUCCESS - RequestId: {}, year: {}, month: {}", requestId, year, month);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("GET_BOOKINGS_BY_DESTINATION_FAILED - RequestId: {}, year: {}, month: {}, Error: {}",
                     requestId, year, month, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/flights/revenue-by-seat-class")
    public ResponseEntity<List<RevenueBySeatClassDto>> getRevenueBySeatClass(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_REVENUE_BY_SEAT_CLASS_REQUEST - RequestId: {}, year: {}, month: {}", requestId, year, month);
        try {
            List<RevenueBySeatClassDto> stats = adminFlightService.getRevenueBySeatClass(year, month);
            log.info("GET_REVENUE_BY_SEAT_CLASS_SUCCESS - RequestId: {}, year: {}, month: {}", requestId, year, month);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("GET_REVENUE_BY_SEAT_CLASS_FAILED - RequestId: {}, year: {}, month: {}, Error: {}",
                     requestId, year, month, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/airports")
    public ResponseEntity<List<AirportDto>> getAirports() {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_AIRPORTS_REQUEST - RequestId: {}", requestId);
        try {
            List<AirportDto> list = adminFlightService.getAirports();
            log.info("GET_AIRPORTS_SUCCESS - RequestId: {}, Count: {}", requestId, list.size());
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            log.error("GET_AIRPORTS_FAILED - RequestId: {}, Error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/airports")
    public ResponseEntity<AirportDto> createAirport(@Valid @RequestBody AirportDto airportDto) {
        String requestId = UUID.randomUUID().toString();
        log.info("CREATE_AIRPORT_REQUEST - RequestId: {}, Payload: {}", requestId, airportDto);
        try {
            AirportDto created = adminFlightService.createAirport(airportDto);
            log.info("CREATE_AIRPORT_SUCCESS - RequestId: {}, AirportId: {}", requestId, created.getId());
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            log.error("CREATE_AIRPORT_FAILED - RequestId: {}, Error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/airports/{airportId}")
    public ResponseEntity<AirportDto> updateAirport(
            @PathVariable Integer airportId,
            @Valid @RequestBody AirportDto airportDto) {
        String requestId = UUID.randomUUID().toString();
        log.info("UPDATE_AIRPORT_REQUEST - RequestId: {}, airportId: {}, Payload: {}", requestId, airportId, airportDto);
        try {
            AirportDto updated = adminFlightService.updateAirport(airportId, airportDto);
            log.info("UPDATE_AIRPORT_SUCCESS - RequestId: {}, airportId: {}", requestId, airportId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("UPDATE_AIRPORT_FAILED - RequestId: {}, airportId: {}, Error: {}", requestId, airportId, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/airports/{airportId}")
    public ResponseEntity<Void> deleteAirport(@PathVariable Integer airportId) {
        String requestId = UUID.randomUUID().toString();
        log.info("DELETE_AIRPORT_REQUEST - RequestId: {}, airportId: {}", requestId, airportId);
        try {
            adminFlightService.deleteAirport(airportId);
            log.info("DELETE_AIRPORT_SUCCESS - RequestId: {}, airportId: {}", requestId, airportId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("DELETE_AIRPORT_FAILED - RequestId: {}, airportId: {}, Error: {}", requestId, airportId, e.getMessage(), e);
            throw e;
        }
    }

//    @PutMapping("/flights/{flightId}/images")
//    public ResponseEntity<List<ImageDto>> updateFlightImages(
//            @PathVariable Integer flightId,
//            @RequestParam(value = "files", required = false) List<MultipartFile> files,
//            @RequestParam(value = "keepImageIds", required = false) List<Integer> keepImageIds) {
//        String requestId = UUID.randomUUID().toString();
//        log.info("UPDATE_FLIGHT_IMAGES_REQUEST - RequestId: {}, flightId: {}, keepImageIds: {}, files: {}", requestId, flightId, keepImageIds, files != null ? files.size() : 0);
//        try {
//            List<ImageDto> updated = adminFlightService.updateFlightImages(flightId, files, keepImageIds);
//            log.info("UPDATE_FLIGHT_IMAGES_SUCCESS - RequestId: {}, flightId: {}, updatedCount: {}", requestId, flightId, updated.size());
//            return ResponseEntity.ok(updated);
//        } catch (Exception e) {
//            log.error("UPDATE_FLIGHT_IMAGES_FAILED - RequestId: {}, flightId: {}, Error: {}", requestId, flightId, e.getMessage(), e);
//            throw e;
//        }
//    }

    @DeleteMapping("/flights/{flightId}/images/{imageId}")
    public ResponseEntity<Void> deleteFlightImage(
            @PathVariable Integer flightId,
            @PathVariable Integer imageId) {
        String requestId = UUID.randomUUID().toString();
        log.info("DELETE_FLIGHT_IMAGE_REQUEST - RequestId: {}, flightId: {}, imageId: {}", requestId, flightId, imageId);
        try {
            adminFlightService.deleteFlightImage(flightId, imageId);
            log.info("DELETE_FLIGHT_IMAGE_SUCCESS - RequestId: {}, flightId: {}, imageId: {}", requestId, flightId, imageId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("DELETE_FLIGHT_IMAGE_FAILED - RequestId: {}, flightId: {}, imageId: {}, Error: {}", requestId, flightId, imageId, e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/flights/{flightId}/imagesAdd")
    public ResponseEntity<List<ImageDto>> addFlightImages(
            @PathVariable Integer flightId,
            @RequestParam("files") List<MultipartFile> files) {
        String requestId = UUID.randomUUID().toString();
        log.info("ADD_FLIGHT_IMAGES_REQUEST - RequestId: {}, flightId: {}, fileCount: {}", requestId, flightId, files.size());
        try {
            List<ImageDto> uploadedImages = adminFlightService.addFlightImages(flightId, files);
            log.info("ADD_FLIGHT_IMAGES_SUCCESS - RequestId: {}, flightId: {}, uploadedCount: {}", requestId, flightId, uploadedImages.size());
            return ResponseEntity.ok(uploadedImages);
        } catch (Exception e) {
            log.error("ADD_FLIGHT_IMAGES_FAILED - RequestId: {}, flightId: {}, Error: {}", requestId, flightId, e.getMessage(), e);
            throw e;
        }
    }
}
