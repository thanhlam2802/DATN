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
        try {
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

                FlightDto flightDto = this.toFlightDto(flight);
                result.add(flightDto);
            }
           

            return result;

        } catch (Exception e) {
          
            throw e;
        }
    }


    @Transactional
    @Override
    public FlightDto getFlightDetail(Integer flightId) {
        String requestId = UUID.randomUUID().toString();
      
        try {
            Flight f = flightDAO.findById(flightId).orElse(null);
            if (f == null) {
               
                return null;
            }
            FlightDto dto = toFlightDto(f);
            
            return dto;
        } catch (Exception e) {
          
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightDto> getAllFlights() {
        String requestId = UUID.randomUUID().toString();
       
        try {
            List<FlightDto> list = flightDAO.findAll()
                    .stream()
                    .map(this::toFlightDto)
                    .collect(Collectors.toList());
           
            return list;
        } catch (Exception e) {
            
            throw e;
        }
    }

    @Override
    @Transactional
    public int getAvailableSeats(Integer flightId) {
        String requestId = UUID.randomUUID().toString();
        
        try {
            Optional<Flight> opt = flightDAO.findById(flightId);
            if (opt.isEmpty()) {
               
                return 0;
            }
            Flight flight = opt.get();
            int sold = flightBookingDAO.countSoldSeatsByFlightId(flightId);
            int total = flight.getFlightSlots().size();
            int available = total - sold;
       
            return available;
        } catch (Exception e) {
           
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Integer> getAvailableSeatsDetail(Integer flightId) {
        String requestId = UUID.randomUUID().toString();
       
        try {
            Map<String, Integer> result = new HashMap<>();
            result.put("total",             flightSlotDAO.countAvailableSlotsByFlightId(flightId));
            result.put("economy",           flightSlotDAO.countAvailableEconomySlotsByFlightId(flightId));
            result.put("business",          flightSlotDAO.countAvailableBusinessSlotsByFlightId(flightId));
            result.put("economyWindow",     flightSlotDAO.countAvailableEconomyWindowSlotsByFlightId(flightId));
            result.put("economyAisle",      flightSlotDAO.countAvailableEconomyAisleSlotsByFlightId(flightId));
            result.put("businessWindow",    flightSlotDAO.countAvailableBusinessWindowSlotsByFlightId(flightId));
            result.put("businessAisle",     flightSlotDAO.countAvailableBusinessAisleSlotsByFlightId(flightId));

            return result;
        } catch (Exception e) {
           
            throw e;
        }
    }
    private FlightDto toFlightDto(Flight flight) {
       
        List<FlightImageDto> images = Optional.ofNullable(flight.getFlightImages())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toFlightImageDto)
                .collect(Collectors.toList());
       

        List<FlightSlotDto> slots = Optional.ofNullable(flight.getFlightSlots())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toFlightSlotDto)
                .collect(Collectors.toList());
       

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
       
        return dto;
    }

    private AirportDto toAirportDto(Airport airport) {
        
        AirportDto dto = AirportDto.builder()
                .id(airport.getId())
                .name(airport.getName())
                .build();
  
        return dto;
    }

    private AirlineDto toAirlineDto(Airline airline) {
        AirlineDto dto = AirlineDto.builder()
                .id(airline.getId())
                .name(airline.getName())
                .build();
       
        return dto;
    }

    private FlightCategoryDto toFlightCategoryDto(FlightCategory category) {
        
        FlightCategoryDto dto = FlightCategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
      
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FlightSlotDto> findFirstAvailableSlot(FindAvailableSlotRequestDto request) {
        String requestId = UUID.randomUUID().toString();
      
        try {
            Optional<FlightSlot> slot = flightSlotDAO.findFirstAvailableSlotByCriteria(
                    request.getFlightId(),
                    request.getIsAisle(),
                    request.getIsWindow(),
                    request.getIsBusiness()
            );
            
            Optional<FlightSlotDto> result = slot.map(this::toFlightSlotDto);
          
            return result;
        } catch (Exception e) {
       
            throw e;
        }
    }

}
