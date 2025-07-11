package backend.backend.service.implement;

import backend.backend.dao.*;
import backend.backend.dto.*;
import backend.backend.entity.*;
import backend.backend.service.AdminFlightService;
import backend.backend.service.ImageStorageService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminFlightServiceImpl implements AdminFlightService {

    @Autowired private FlightDAO flightDAO;
    @Autowired private FlightBookingDAO flightBookingDAO;
    @Autowired private AirportDAO airportDAO;
    @Autowired private FlightSlotDAO flightSlotDAO;
    @Autowired private AirlineDAO airlineDAO;
    @Autowired private FlightCategoryDAO flightCategoryDAO;
    @Autowired private ImageStorageService imageStorageService;
    @Autowired private ImageDAO imageDAO;
    @Autowired private FlightImageDAO flightImageDAO;

    @Override
    @Transactional
    public List<FlightDto> getFlights(int page, int size, String filter) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_FLIGHTS_REQUEST        - RequestId: {}, page: {}, size: {}, filter: {}", requestId, page, size, filter);
        try {
            List<FlightDto> dtos = flightDAO.findAll().stream()
                    .map(this::toFlightDto).collect(Collectors.toList());
            log.info("GET_FLIGHTS_SUCCESS        - RequestId: {}, count: {}", requestId, dtos.size());
            return dtos;
        } catch (Exception e) {
            log.error("GET_FLIGHTS_FAILED         - RequestId: {}, error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

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
    public FlightDto createFlight(FlightDto flightDto) {
        String requestId = UUID.randomUUID().toString();
        log.info("CREATE_FLIGHT_REQUEST      - RequestId: {}, payload: {}", requestId, flightDto);
        try {
            Flight f = new Flight();
            f.setFlightNumber(flightDto.getFlightNumber());
            f.setName(flightDto.getName());
            f.setDepartureTime(flightDto.getDepartureTime());
            f.setArrivalTime(flightDto.getArrivalTime());
            f.setCreatedAt(LocalDateTime.now());
            f.setUpdatedAt(LocalDateTime.now());
            if (flightDto.getAirline() != null) {
                Airline a = airlineDAO.findById(flightDto.getAirline().getId()).orElse(null);
                f.setAirline(a);
            }
            if (flightDto.getCategory() != null) {
                FlightCategory c = flightCategoryDAO.findById(flightDto.getCategory().getId()).orElse(null);
                f.setCategory(c);
            }
            Flight saved = flightDAO.save(f);
            FlightDto dto = toFlightDto(saved);
            log.info("CREATE_FLIGHT_SUCCESS      - RequestId: {}, flightId: {}", requestId, saved.getId());
            return dto;
        } catch (Exception e) {
            log.error("CREATE_FLIGHT_FAILED       - RequestId: {}, error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }
    @Transactional
    @Override
    public FlightDto updateFlight(Integer flightId, FlightDto flightDto) {
        String requestId = UUID.randomUUID().toString();
        log.info("UPDATE_FLIGHT_REQUEST      - RequestId: {}, flightId: {}, payload: {}", requestId, flightId, flightDto);
        try {
            Flight f = flightDAO.findById(flightId).orElse(null);
            if (f == null) {
                log.warn("UPDATE_FLIGHT_NOT_FOUND    - RequestId: {}, flightId: {}", requestId, flightId);
                return null;
            }
            f.setFlightNumber(flightDto.getFlightNumber());
            f.setName(flightDto.getName());
            f.setDepartureTime(flightDto.getDepartureTime());
            f.setArrivalTime(flightDto.getArrivalTime());
            f.setUpdatedAt(LocalDateTime.now());
            if (flightDto.getAirline() != null) {
                Airline a = airlineDAO.findById(flightDto.getAirline().getId()).orElse(null);
                f.setAirline(a);
            }
            Flight saved = flightDAO.save(f);
            FlightDto dto = toFlightDto(saved);
            log.info("UPDATE_FLIGHT_SUCCESS      - RequestId: {}, flightId: {}", requestId, flightId);
            return dto;
        } catch (Exception e) {
            log.error("UPDATE_FLIGHT_FAILED       - RequestId: {}, flightId: {}, error: {}", requestId, flightId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteFlight(Integer flightId) {
        String requestId = UUID.randomUUID().toString();
        log.info("DELETE_FLIGHT_REQUEST      - RequestId: {}, flightId: {}", requestId, flightId);
        try {
            flightDAO.deleteById(flightId);
            log.info("DELETE_FLIGHT_SUCCESS      - RequestId: {}, flightId: {}", requestId, flightId);
        } catch (Exception e) {
            log.error("DELETE_FLIGHT_FAILED       - RequestId: {}, flightId: {}, error: {}", requestId, flightId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public FlightDto createFlightWithDetails(CreateFlightRequestDto req) {
        String requestId = UUID.randomUUID().toString();
        log.info("CREATE_FLIGHT_DETAILS_REQ  - RequestId: {}, payload: {}", requestId, req);
        try {
            Flight f = new Flight();
            f.setFlightNumber(req.getFlightNumber());
            f.setName(req.getName());
            f.setDepartureTime(req.getDepartureTime());
            f.setArrivalTime(req.getArrivalTime());
            f.setCreatedAt(LocalDateTime.now());
            f.setUpdatedAt(LocalDateTime.now());
            if (req.getAirlineId() != null) {
                Airline a = new Airline(); a.setId(req.getAirlineId());
                f.setAirline(a);
            }
            if (req.getCategoryId() != null) {
                FlightCategory c = new FlightCategory(); c.setId(req.getCategoryId());
                f.setCategory(c);
            }
            if (req.getDepartureAirportId() != null) {
                Airport da = new Airport(); da.setId(req.getDepartureAirportId());
                f.setDepartureAirport(da);
            }
            if (req.getArrivalAirportId() != null) {
                Airport aa = new Airport(); aa.setId(req.getArrivalAirportId());
                f.setArrivalAirport(aa);
            }
            // giả định owner
            User u = new User(); u.setId(5);
            f.setOwner(u);

            Flight saved = flightDAO.save(f);
            log.info("CREATE_FLIGHT_DETAILS_SUC  - RequestId: {}, flightId: {}", requestId, saved.getId());

            if (req.getImages() != null) {
                for (MultipartFile imgFile : req.getImages()) {
                    if (imgFile.isEmpty()) continue;
                    try {
                        Map<String,String> up = imageStorageService.uploadImage(imgFile);
                        Image img = new Image();
                        img.setUrl(up.get("url"));
                        img.setAltText(saved.getName());
                        img.setUploadedAt(LocalDateTime.now());
                        Image savedImg = imageDAO.save(img);
                        FlightImage fi = new FlightImage();
                        fi.setFlight(saved);
                        fi.setImage(savedImg);
                        flightImageDAO.save(fi);
                        log.debug("UPLOADED_IMAGE            - RequestId: {}, imageId: {}", requestId, savedImg.getId());
                    } catch (Exception ex) {
                        log.error("UPLOAD_IMAGE_FAILED       - RequestId: {}, error: {}", requestId, ex.getMessage(), ex);
                    }
                }
            }

            if (req.getTicketInfo() != null) {
                var t = req.getTicketInfo();
                for (int i = 0; i < t.getEconomy(); i++) {
                    FlightSlot s = new FlightSlot();
                    s.setFlight(saved);
                    s.setIsBusiness(false);
                    s.setSeatNumber("E" + (i+1));
                    s.setIsWindow(i < t.getEconomyWindow());
                    s.setIsAisle(i >= t.getEconomyWindow());
                    s.setPrice(BigDecimal.valueOf(t.getEconomyPrice()));
                    s.setCarryOnLuggage(Optional.ofNullable(t.getEconomyLuggage()).orElse(7));
                    flightSlotDAO.save(s);
                }
                for (int i = 0; i < t.getBusiness(); i++) {
                    FlightSlot s = new FlightSlot();
                    s.setFlight(saved);
                    s.setIsBusiness(true);
                    s.setSeatNumber("B" + (i+1));
                    s.setIsWindow(i < t.getBusinessWindow());
                    s.setIsAisle(i >= t.getBusinessWindow());
                    s.setPrice(BigDecimal.valueOf(t.getBusinessPrice()));
                    s.setCarryOnLuggage(Optional.ofNullable(t.getBusinessLuggage()).orElse(10));
                    flightSlotDAO.save(s);
                }
                log.info("CREATED_SLOTS              - RequestId: {}, flightId: {}, economy: {}, business: {}",
                        requestId, saved.getId(), t.getEconomy(), t.getBusiness());
            }

            FlightDto dto = toFlightDto(saved);
            log.info("CREATE_FLIGHT_DETAILS_DONE - RequestId: {}, flightId: {}", requestId, saved.getId());
            return dto;
        } catch (Exception e) {
            log.error("CREATE_FLIGHT_DETAILS_FAIL - RequestId: {}, error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<FlightSlotDto> getSeats(Integer flightId) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_SEATS_REQUEST         - RequestId: {}, flightId: {}", requestId, flightId);
        try {
            Flight f = flightDAO.findById(flightId).orElse(null);
            if (f == null) {
                log.warn("GET_SEATS_NOT_FOUND       - RequestId: {}, flightId: {}", requestId, flightId);
                return Collections.emptyList();
            }
            var dtos = f.getFlightSlots().stream().map(this::toFlightSlotDto).collect(Collectors.toList());
            log.info("GET_SEATS_SUCCESS         - RequestId: {}, flightId: {}, count: {}", requestId, flightId, dtos.size());
            return dtos;
        } catch (Exception e) {
            log.error("GET_SEATS_FAILED          - RequestId: {}, flightId: {}, error: {}", requestId, flightId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<FlightSlotDto> updateSeats(Integer flightId, List<FlightSlotDto> seats) {
        String requestId = UUID.randomUUID().toString();
        log.info("UPDATE_SEATS_REQUEST      - RequestId: {}, flightId: {}, payloadSize: {}", requestId, flightId, seats.size());
        try {
            for (var sDto : seats) {
                flightSlotDAO.findById(sDto.getId()).ifPresent(s -> {
                    s.setIsBusiness(sDto.getIsBusiness());
                    s.setPrice(sDto.getPrice());
                    s.setIsWindow(sDto.getIsWindow());
                    s.setIsAisle(sDto.getIsAisle());
                    s.setSeatNumber(sDto.getSeatNumber());
                    flightSlotDAO.save(s);
                    log.debug("UPDATED_SEAT              - RequestId: {}, slotId: {}", requestId, s.getId());
                });
            }
            var updated = flightDAO.findById(flightId).orElseThrow().getFlightSlots()
                    .stream().map(this::toFlightSlotDto).collect(Collectors.toList());
            log.info("UPDATE_SEATS_SUCCESS      - RequestId: {}, flightId: {}, count: {}", requestId, flightId, updated.size());
            return updated;
        } catch (Exception e) {
            log.error("UPDATE_SEATS_FAILED       - RequestId: {}, flightId: {}, error: {}", requestId, flightId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public FlightSlotDto updateSeat(Integer slotId, FlightSlotDto slotDto) {
        String requestId = UUID.randomUUID().toString();
        log.info("UPDATE_SEAT_REQUEST - RequestId: {}, slotId: {}, payload: {}", requestId, slotId, slotDto);
        try {
            FlightSlot slot = flightSlotDAO.findById(slotId).orElse(null);
            if (slot == null) {
                log.warn("UPDATE_SEAT_NOT_FOUND - RequestId: {}, slotId: {}", requestId, slotId);
                return null;
            }
            slot.setIsBusiness(slotDto.getIsBusiness());
            slot.setPrice(slotDto.getPrice());
            slot.setIsWindow(slotDto.getIsWindow());
            slot.setIsAisle(slotDto.getIsAisle());
            slot.setSeatNumber(slotDto.getSeatNumber());
            slot.setCarryOnLuggage(slotDto.getCarryOnLuggage());
            FlightSlot saved = flightSlotDAO.save(slot);
            log.info("UPDATE_SEAT_SUCCESS - RequestId: {}, slotId: {}", requestId, slotId);
            return toFlightSlotDto(saved);
        } catch (Exception e) {
            log.error("UPDATE_SEAT_FAILED - RequestId: {}, slotId: {}, error: {}", requestId, slotId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteSeat(Integer slotId) {
        String requestId = UUID.randomUUID().toString();
        log.info("DELETE_SEAT_REQUEST - RequestId: {}, slotId: {}", requestId, slotId);
        try {
            flightSlotDAO.deleteById(slotId);
            log.info("DELETE_SEAT_SUCCESS - RequestId: {}, slotId: {}", requestId, slotId);
        } catch (Exception e) {
            log.error("DELETE_SEAT_FAILED - RequestId: {}, slotId: {}, error: {}", requestId, slotId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<FlightBookingDetailDto> getFlightBookings(String filter) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_BOOKINGS_ADMIN_REQ    - RequestId: {}, filter: {}", requestId, filter);
        try {
            var dtos = flightBookingDAO.findAll().stream()
                    .map(b -> toBookingDetailDto(b, b.getFlightSlot().getFlight()))
                    .collect(Collectors.toList());
            log.info("GET_BOOKINGS_ADMIN_SUC    - RequestId: {}, count: {}", requestId, dtos.size());
            return dtos;
        } catch (Exception e) {
            log.error("GET_BOOKINGS_ADMIN_FAIL   - RequestId: {}, error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public FlightBookingDetailDto getFlightBookingDetail(Integer bookingId) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_BOOKING_ADMIN_REQ     - RequestId: {}, bookingId: {}", requestId, bookingId);
        try {
            var b = flightBookingDAO.findById(bookingId).orElse(null);
            if (b == null) {
                log.warn("GET_BOOKING_ADMIN_NOT_FOUND- RequestId: {}, bookingId: {}", requestId, bookingId);
                return null;
            }
            var dto = toBookingDetailDto(b, b.getFlightSlot().getFlight());
            log.info("GET_BOOKING_ADMIN_SUC     - RequestId: {}, bookingId: {}", requestId, bookingId);
            return dto;
        } catch (Exception e) {
            log.error("GET_BOOKING_ADMIN_FAIL    - RequestId: {}, bookingId: {}, error: {}", requestId, bookingId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public FlightBookingDetailDto updateFlightBookingStatus(Integer bookingId, String status) {
        String requestId = UUID.randomUUID().toString();
        log.info("UPDATE_BOOKING_ADMIN_REQ  - RequestId: {}, bookingId: {}, status: {}", requestId, bookingId, status);
        try {
            var b = flightBookingDAO.findById(bookingId).orElse(null);
            if (b == null) {
                log.warn("UPDATE_BOOKING_ADMIN_NOT_FOUND- RequestId: {}, bookingId: {}", requestId, bookingId);
                return null;
            }
            // giả lập đổi trạng thái
            log.info("UPDATE_BOOKING_ADMIN_SUC  - RequestId: {}, bookingId: {}, status: {}", requestId, bookingId, status);
            return toBookingDetailDto(b, b.getFlightSlot().getFlight());
        } catch (Exception e) {
            log.error("UPDATE_BOOKING_ADMIN_FAIL - RequestId: {}, bookingId: {}, error: {}", requestId, bookingId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<FlightStatisticsDto> getFlightStatistics(String type, String value) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_STATS_REQUEST         - RequestId: {}, type: {}, value: {}", requestId, type, value);
        try {
            var stats = flightDAO.findAll().stream().map(f -> {
                FlightStatisticsDto dto = new FlightStatisticsDto();
                dto.setFlightId(f.getId());
                dto.setFlightCode(f.getFlightNumber());
                dto.setDate(LocalDate.now());
                int total = Optional.ofNullable(f.getFlightSlots()).map(List::size).orElse(0);
                int sold = flightBookingDAO.countSoldSeatsByFlightId(f.getId());
                double rev = flightBookingDAO.sumRevenueByFlightId(f.getId());
                dto.setTotalTickets(total);
                dto.setSoldTickets(sold);
                dto.setRevenue(rev);
                dto.setOccupancyRate(total>0? sold*100.0/total : 0);
                return dto;
            }).collect(Collectors.toList());
            log.info("GET_STATS_SUCCESS         - RequestId: {}, count: {}", requestId, stats.size());
            return stats;
        } catch (Exception e) {
            log.error("GET_STATS_FAILED          - RequestId: {}, error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<AirportDto> getAirports() {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_AIRPORTS_REQUEST      - RequestId: {}", requestId);
        try {
            var dtos = airportDAO.findAll().stream().map(this::toAirportDto).collect(Collectors.toList());
            log.info("GET_AIRPORTS_SUCCESS      - RequestId: {}, count: {}", requestId, dtos.size());
            return dtos;
        } catch (Exception e) {
            log.error("GET_AIRPORTS_FAILED       - RequestId: {}, error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AirportDto createAirport(AirportDto airportDto) {
        String requestId = UUID.randomUUID().toString();
        log.info("CREATE_AIRPORT_REQUEST    - RequestId: {}, payload: {}", requestId, airportDto);
        try {
            Airport a = new Airport(); a.setName(airportDto.getName());
            Airport saved = airportDAO.save(a);
            AirportDto dto = toAirportDto(saved);
            log.info("CREATE_AIRPORT_SUCCESS    - RequestId: {}, airportId: {}", requestId, saved.getId());
            return dto;
        } catch (Exception e) {
            log.error("CREATE_AIRPORT_FAILED     - RequestId: {}, error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AirportDto updateAirport(Integer airportId, AirportDto airportDto) {
        String requestId = UUID.randomUUID().toString();
        log.info("UPDATE_AIRPORT_REQUEST    - RequestId: {}, airportId: {}, payload: {}", requestId, airportId, airportDto);
        try {
            Airport a = airportDAO.findById(airportId).orElse(null);
            if (a == null) {
                log.warn("UPDATE_AIRPORT_NOT_FOUND  - RequestId: {}, airportId: {}", requestId, airportId);
                return null;
            }
            a.setName(airportDto.getName());
            Airport saved = airportDAO.save(a);
            AirportDto dto = toAirportDto(saved);
            log.info("UPDATE_AIRPORT_SUCCESS    - RequestId: {}, airportId: {}", requestId, airportId);
            return dto;
        } catch (Exception e) {
            log.error("UPDATE_AIRPORT_FAILED     - RequestId: {}, airportId: {}, error: {}", requestId, airportId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteAirport(Integer airportId) {
        String requestId = UUID.randomUUID().toString();
        log.info("DELETE_AIRPORT_REQUEST    - RequestId: {}, airportId: {}", requestId, airportId);
        try {
            airportDAO.deleteById(airportId);
            log.info("DELETE_AIRPORT_SUCCESS    - RequestId: {}, airportId: {}", requestId, airportId);
        } catch (Exception e) {
            log.error("DELETE_AIRPORT_FAILED     - RequestId: {}, airportId: {}, error: {}", requestId, airportId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public List<ImageDto> updateFlightImages(Integer flightId, List<MultipartFile> files, List<Integer> keepImageIds) {
        String requestId = UUID.randomUUID().toString();
        Flight flight = flightDAO.findById(flightId).orElseThrow();
        // Lấy danh sách FlightImage hiện tại
        List<FlightImage> currentImages = flightImageDAO.findByFlightId(flightId);
        // Xác định ảnh cần xóa
        List<FlightImage> toDelete = currentImages.stream()
                .filter(fi -> keepImageIds == null || !keepImageIds.contains(fi.getImage().getId()))
                .collect(Collectors.toList());
        // Xóa liên kết và ảnh
        for (FlightImage fi : toDelete) {
            try {
                imageStorageService.deleteImage2(fi.getImage().getUrl());
                flightImageDAO.delete(fi);
                imageDAO.delete(fi.getImage());
            } catch (Exception ex) {
                log.error("DELETE_IMAGE_FAILED - flightId: {}, imageId: {}, error: {}", flightId, fi.getImage().getId(), ex.getMessage(), ex);
            }
        }
        // Thêm ảnh mới
        if (files != null) {
            for (MultipartFile imgFile : files) {
                if (imgFile.isEmpty()) continue;
                try {
                    Map<String,String> up = imageStorageService.uploadImage(imgFile);
                    Image img = new Image();
                    img.setUrl(up.get("url"));
                    img.setAltText(flight.getName());
                    img.setUploadedAt(LocalDateTime.now());
                    Image savedImg = imageDAO.save(img);
                    FlightImage fi = new FlightImage();
                    fi.setFlight(flight);
                    fi.setImage(savedImg);
                    flightImageDAO.save(fi);
                    log.debug("UPLOADED_IMAGE - flightId: {}, imageId: {}", flightId, savedImg.getId());
                } catch (Exception ex) {
                    log.error("UPLOAD_IMAGE_FAILED - flightId: {}, error: {}", flightId, ex.getMessage(), ex);
                }
            }
        }
        // Trả về danh sách ảnh mới nhất
        List<FlightImage> updatedImages = flightImageDAO.findByFlightId(flightId);
        return updatedImages.stream().map(fi -> {
            ImageDto dto = new ImageDto();
            dto.setId(fi.getImage().getId());
            dto.setImageUrl(fi.getImage().getUrl());
            return dto;
        }).toList();
    }

    // mapping helpers with debug logging

    private FlightDto toFlightDto(Flight f) {
        log.debug("MAPPING_FLIGHT_TO_DTO      - flightId: {}", f.getId());
        FlightDto dto = FlightDto.builder()
                .id(f.getId())
                .flightNumber(f.getFlightNumber())
                .name(f.getName())
                .departureTime(f.getDepartureTime())
                .arrivalTime(f.getArrivalTime())
                .createdAt(f.getCreatedAt())
                .updatedAt(f.getUpdatedAt())
                .categoryId(Optional.ofNullable(f.getCategory()).map(FlightCategory::getId).orElse(null))
                .category(Optional.ofNullable(f.getCategory()).map(this::toFlightCategoryDto).orElse(null))
                .ownerId(Optional.ofNullable(f.getOwner()).map(User::getId).orElse(null))
                .departureAirport(Optional.ofNullable(f.getDepartureAirport()).map(this::toAirportDto).orElse(null))
                .arrivalAirport(Optional.ofNullable(f.getArrivalAirport()).map(this::toAirportDto).orElse(null))
                .airline(Optional.ofNullable(f.getAirline()).map(this::toAirlineDto).orElse(null))
                .build();
        log.debug("MAPPING_FLIGHT_TO_DTO_DONE - flightId: {}", f.getId());
        return dto;
    }

    private FlightSlotDto toFlightSlotDto(FlightSlot s) {
        log.debug("MAPPING_SLOT_TO_DTO        - slotId: {}", s.getId());
        FlightSlotDto dto = FlightSlotDto.builder()
                .id(s.getId())
                .flightId(Optional.ofNullable(s.getFlight()).map(Flight::getId).orElse(null))
                .seatNumber(s.getSeatNumber())
                .price(s.getPrice())
                .isBusiness(s.getIsBusiness())
                .isWindow(s.getIsWindow())
                .isAisle(s.getIsAisle())
                .carryOnLuggage(s.getCarryOnLuggage())
                .build();
        log.debug("MAPPING_SLOT_TO_DTO_DONE   - slotId: {}", s.getId());
        return dto;
    }

    private FlightBookingDetailDto toBookingDetailDto(FlightBooking b, Flight f) {
        log.debug("MAPPING_BOOKING_TO_DTO     - bookingId: {}", b.getId());
        FlightBookingDetailDto dto = FlightBookingDetailDto.builder()
                .bookingId(b.getId())
                .createdAt(b.getBookingDate())
                .flight(toFlightDto(f))
                .totalPrice(b.getFlightSlot().getPrice().doubleValue())
                .status("BOOKED")
                .build();
        log.debug("MAPPING_BOOKING_TO_DTO_DONE- bookingId: {}", b.getId());
        return dto;
    }

    private AirportDto toAirportDto(Airport a) {
        log.debug("MAPPING_AIRPORT_TO_DTO     - airportId: {}", a.getId());
        AirportDto dto = AirportDto.builder()
                .id(a.getId())
                .name(a.getName())
                .build();
        log.debug("MAPPING_AIRPORT_TO_DTO_DONE- airportId: {}", a.getId());
        return dto;
    }

    private AirlineDto toAirlineDto(Airline a) {
        log.debug("MAPPING_AIRLINE_TO_DTO     - airlineId: {}", a.getId());
        AirlineDto dto = AirlineDto.builder()
                .id(a.getId())
                .name(a.getName())
                .build();
        log.debug("MAPPING_AIRLINE_TO_DTO_DONE- airlineId: {}", a.getId());
        return dto;
    }

    private FlightCategoryDto toFlightCategoryDto(FlightCategory c) {
        log.debug("MAPPING_CATEGORY_TO_DTO    - categoryId: {}", c.getId());
        FlightCategoryDto dto = FlightCategoryDto.builder()
                .id(c.getId())
                .name(c.getName())
                .build();
        log.debug("MAPPING_CATEGORY_TO_DTO_DONE- categoryId: {}", c.getId());
        return dto;
    }
}
