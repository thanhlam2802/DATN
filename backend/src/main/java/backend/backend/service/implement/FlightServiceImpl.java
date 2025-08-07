package backend.backend.service.implement;

import backend.backend.dao.*;
import backend.backend.dto.*;
import backend.backend.entity.*;
import backend.backend.service.FlightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FlightServiceImpl implements FlightService {

    @Autowired
    private FlightDAO flightDAO;

    @Autowired
    private FlightBookingDAO flightBookingDAO;

    @Autowired
    private FlightSlotDAO flightSlotDAO;

    @Autowired
    private AirlineDAO airlineDAO;

    @Override
    @Transactional(readOnly = true)
    public List<FlightDto> searchFlights(FlightSearchRequestDto request) {
        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        log.info("[SEARCH_FLIGHTS_REQUEST] RequestId: {}, Input Params:", requestId);
        log.info("  - DepartureAirportId: {}", request.getDepartureAirportId());
        log.info("  - ArrivalAirportId:   {}", request.getArrivalAirportId());
        log.info("  - DepartureDate:      {}", request.getDepartureDate());
        log.info("  - AirlineId:          {}", request.getAirlineId());
        log.info("  - TimeWindow:         {}", request.getTimeWindow());
        log.info("  - MinPrice:           {}", request.getMinPrice());
        log.info("  - MaxPrice:           {}", request.getMaxPrice());
        log.info("  - getCategoryId:           {}", request.getCategoryId());
        try {
            List<Flight> flights = flightDAO.findAllUpcomingFlights();
            log.info("[{}] Tổng chuyến bay ban đầu: {}", requestId, flights.size());
            Integer startHour= null;
            Integer endHour =null;
            if (request.getTimeWindow()!=null) {
                if (request.getTimeWindow().equalsIgnoreCase("morning")){
                    startHour= 5;
                    endHour =12;
                }else if (request.getTimeWindow().equalsIgnoreCase("afternoon")){
                    startHour= 12;
                    endHour =20;
                }else if (request.getTimeWindow().equalsIgnoreCase("night")){
                    startHour= 20;
                    endHour =5;
                }
            }
            List<Flight>  ls =flightDAO.searchFlights(
                    request.getDepartureAirportId(),
                    request.getArrivalAirportId(),
                    request.getAirlineId(),
                    request.getCategoryId(),
                    request.getDepartureDate(),
                    startHour,endHour,
                    request.getMinPrice(),
                    request.getMaxPrice()
            );
            List<FlightDto> result = new ArrayList<>();
            for (Flight flight : ls) {
                log.info("[FORR]  id flight: {}",flight.getId());
                FlightDto flightDto = this.toFlightDto(flight);
                result.add(flightDto);
            }
            log.info("[SEARCH_FLIGHTS_SUCCESS] RequestId: {}, Tổng kết quả: {}", requestId, result.size());

            return result;

        } catch (Exception e) {
            log.info("[SEARCH_FLIGHTS_FAILED] RequestId: {}, Exception: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }


    @Transactional
    @Override
    public FlightDto getFlightDetail(Integer flightId) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_FLIGHT_DETAIL_REQUEST  - RequestId: {}, flightId: {}", requestId, flightId);
        try {
            Flight f = flightDAO.findById(flightId).orElse(null);
            if (f == null) {
                log.warn("GET_FLIGHT_DETAIL_NOT_FOUND- RequestId: {}, flightId: {}", requestId, flightId);
                return null;
            }
            FlightDto dto = toFlightDto(f);
            log.info("GET_FLIGHT_DETAIL_SUCCESS  - RequestId: {}, flightId: {}", requestId, flightId);
            return dto;
        } catch (Exception e) {
            log.error("GET_FLIGHT_DETAIL_FAILED   - RequestId: {}, flightId: {}, error: {}", requestId, flightId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightDto> getAllFlights() {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_ALL_FLIGHTS_REQUEST   - RequestId: {}", requestId);
        try {
            List<FlightDto> list = flightDAO.findAll()
                    .stream()
                    .map(this::toFlightDto)
                    .collect(Collectors.toList());
            log.info("GET_ALL_FLIGHTS_SUCCESS   - RequestId: {}, count: {}", requestId, list.size());
            return list;
        } catch (Exception e) {
            log.error("GET_ALL_FLIGHTS_FAILED    - RequestId: {}, error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public int getAvailableSeats(Integer flightId) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_AVAILABLE_SEATS_REQ   - RequestId: {}, flightId: {}", requestId, flightId);
        try {
            Optional<Flight> opt = flightDAO.findById(flightId);
            if (opt.isEmpty()) {
                log.warn("GET_AVAILABLE_SEATS_NOT_FOUND - RequestId: {}, flightId: {}", requestId, flightId);
                return 0;
            }
            Flight flight = opt.get();
            int sold = flightBookingDAO.countSoldSeatsByFlightId(flightId);
            int total = flight.getFlightSlots().size();
            int available = total - sold;
            log.info("GET_AVAILABLE_SEATS_SUCCESS - RequestId: {}, flightId: {}, available: {}", requestId, flightId, available);
            return available;
        } catch (Exception e) {
            log.error("GET_AVAILABLE_SEATS_FAILED    - RequestId: {}, flightId: {}, error: {}", requestId, flightId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Integer> getAvailableSeatsDetail(Integer flightId) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_SEATS_DETAIL_REQUEST  - RequestId: {}, flightId: {}", requestId, flightId);
        try {
            Map<String, Integer> result = new HashMap<>();
            result.put("total",             flightSlotDAO.countAvailableSlotsByFlightId(flightId));
            result.put("economy",           flightSlotDAO.countAvailableEconomySlotsByFlightId(flightId));
            result.put("business",          flightSlotDAO.countAvailableBusinessSlotsByFlightId(flightId));
            result.put("economyWindow",     flightSlotDAO.countAvailableEconomyWindowSlotsByFlightId(flightId));
            result.put("economyAisle",      flightSlotDAO.countAvailableEconomyAisleSlotsByFlightId(flightId));
            result.put("businessWindow",    flightSlotDAO.countAvailableBusinessWindowSlotsByFlightId(flightId));
            result.put("businessAisle",     flightSlotDAO.countAvailableBusinessAisleSlotsByFlightId(flightId));
            log.info("GET_SEATS_DETAIL_SUCCESS  - RequestId: {}, flightId: {}, details: {}", requestId, flightId, result);
            return result;
        } catch (Exception e) {
            log.error("GET_SEATS_DETAIL_FAILED   - RequestId: {}, flightId: {}, error: {}", requestId, flightId, e.getMessage(), e);
            throw e;
        }
    }
    private FlightDto toFlightDto(Flight flight) {
        log.debug("MAPPING_FLIGHT_TO_DTO       - flightId: {}", flight.getId());
        List<FlightImageDto> images = Optional.ofNullable(flight.getFlightImages())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toFlightImageDto)
                .collect(Collectors.toList());
        log.debug("MAPPED_FLIGHT_IMAGES        - flightId: {}, imageCount: {}", flight.getId(), images.size());

        List<FlightSlotDto> slots = Optional.ofNullable(flight.getFlightSlots())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toFlightSlotDto)
                .collect(Collectors.toList());
        log.debug("MAPPED_FLIGHT_SLOTS         - flightId: {}, slotCount: {}", flight.getId(), slots.size());

        Double minPrice = slots.stream()
                .map(FlightSlotDto::getPrice)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .min().orElse(Double.NaN);
        Double maxPrice = slots.stream()
                .map(FlightSlotDto::getPrice)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .max().orElse(Double.NaN);
        log.debug("CALCULATED_PRICE_RANGE      - flightId: {}, minPrice: {}, maxPrice: {}",
                flight.getId(), minPrice, maxPrice);

        FlightDto dto = FlightDto.builder()
                .id(flight.getId())
                .flightNumber(flight.getFlightNumber())
                .name(flight.getName())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .createdAt(flight.getCreatedAt())
                .updatedAt(flight.getUpdatedAt())

                .category(flight.getCategory() != null ? toFlightCategoryDto(flight.getCategory()) : null)
                .ownerId(flight.getOwner() != null ? flight.getOwner().getId() : null)
                .departureAirport(flight.getDepartureAirport() != null ? toAirportDto(flight.getDepartureAirport()) : null)
                .arrivalAirport(flight.getArrivalAirport() != null ? toAirportDto(flight.getArrivalAirport()) : null)
                .airline(flight.getAirline() != null ? toAirlineDto(flight.getAirline()) : null)
                .images(images)
                .flightSlots(slots)
                .minPrice(Double.isNaN(minPrice) ? null : minPrice)
                .maxPrice(Double.isNaN(maxPrice) ? null : maxPrice)
                .totalAvailableSeats(flightDAO.countByBookingId(flight.getId()))
                .build();
        log.debug("MAPPING_FLIGHT_TO_DTO_DONE  - flightId: {}", flight.getId());
        return dto;
    }

    private FlightSlotDto toFlightSlotDto(FlightSlot slot) {
        log.debug("MAPPING_SLOT_TO_DTO         - slotId: {}, flightId: {}",
                slot.getId(), slot.getFlight() != null ? slot.getFlight().getId() : null);
        FlightSlotDto dto = FlightSlotDto.builder()
                .id(slot.getId())
                .flightId(slot.getFlight() != null ? slot.getFlight().getId() : null)
                .seatNumber(slot.getSeatNumber())
                .price(slot.getPrice())
                .isBusiness(slot.getIsBusiness())
                .isWindow(slot.getIsWindow())
                .isAisle(slot.getIsAisle())
                .carryOnLuggage(slot.getCarryOnLuggage())
                .build();
        log.debug("MAPPING_SLOT_TO_DTO_DONE    - slotId: {}", slot.getId());
        return dto;
    }

    private FlightImageDto toFlightImageDto(FlightImage fi) {
        Image img = fi.getImage();
        log.debug("MAPPING_IMAGE_TO_DTO        - flightImageId: {}, imageId: {}",
                fi.getId(), img != null ? img.getId() : null);
        FlightImageDto dto = FlightImageDto.builder()
                .imageId(img != null ? img.getId() : null)
                .flightId(fi.getFlight() != null ? fi.getFlight().getId() : null)
                .imageUrl(img != null ? img.getUrl() : null)
                .altText(img != null ? img.getAltText() : null)
                .uploadedAt(img != null ? img.getUploadedAt() : null)
                .build();
        log.debug("MAPPING_IMAGE_TO_DTO_DONE   - flightImageId: {}", fi.getId());
        return dto;
    }

    private AirportDto toAirportDto(Airport airport) {
        log.debug("MAPPING_AIRPORT_TO_DTO      - airportId: {}", airport.getId());
        AirportDto dto = AirportDto.builder()
                .id(airport.getId())
                .name(airport.getName())
                .build();
        log.debug("MAPPING_AIRPORT_TO_DTO_DONE - airportId: {}", airport.getId());
        return dto;
    }

    private AirlineDto toAirlineDto(Airline airline) {
        log.debug("MAPPING_AIRLINE_TO_DTO      - airlineId: {}", airline.getId());
        AirlineDto dto = AirlineDto.builder()
                .id(airline.getId())
                .name(airline.getName())
                .build();
        log.debug("MAPPING_AIRLINE_TO_DTO_DONE - airlineId: {}", airline.getId());
        return dto;
    }

    private FlightCategoryDto toFlightCategoryDto(FlightCategory category) {
        log.debug("MAPPING_CATEGORY_TO_DTO     - categoryId: {}", category.getId());
        FlightCategoryDto dto = FlightCategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
        log.debug("MAPPING_CATEGORY_TO_DTO_DONE- categoryId: {}", category.getId());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FlightSlotDto> findFirstAvailableSlot(FindAvailableSlotRequestDto request) {
        String requestId = UUID.randomUUID().toString();
        log.info("FIND_FIRST_AVAILABLE_SLOT_REQUEST - RequestId: {}, request: {}", requestId, request);
        try {
            Optional<FlightSlot> slot = flightSlotDAO.findFirstAvailableSlotByCriteria(
                    request.getFlightId(),
                    request.getIsAisle(),
                    request.getIsWindow(),
                    request.getIsBusiness()
            );
            
            Optional<FlightSlotDto> result = slot.map(this::toFlightSlotDto);
            log.info("FIND_FIRST_AVAILABLE_SLOT_SUCCESS - RequestId: {}, found: {}", requestId, result.isPresent());
            return result;
        } catch (Exception e) {
            log.error("FIND_FIRST_AVAILABLE_SLOT_FAILED - RequestId: {}, error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

}
