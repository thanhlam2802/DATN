package backend.backend.service.implement;

import backend.backend.dao.*;
import backend.backend.dto.*;
import backend.backend.dto.auth.UserRoleEnum;
import backend.backend.entity.*;
import backend.backend.service.AdminFlightService;
import backend.backend.service.FlightService;
import backend.backend.service.ImageStorageService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

@Slf4j
@Service
public class AdminFlightServiceImpl implements AdminFlightService {

    @Autowired private FlightDAO flightDAO;
    @Autowired private UserDAO userDAO;
    @Autowired private FlightBookingDAO flightBookingDAO;
    @Autowired private AirportDAO airportDAO;
    @Autowired private FlightSlotDAO flightSlotDAO;
    @Autowired private AirlineDAO airlineDAO;
    @Autowired private FlightCategoryDAO flightCategoryDAO;
    @Autowired private ImageStorageService imageStorageService;
    @Autowired private ImageDAO imageDAO;
    @Autowired private FlightImageDAO flightImageDAO;
    @Autowired private FlightService flightService;
    @Override
    @Transactional
    public List<FlightDto> getFlights(int page, int size, String filter) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_FLIGHTS_REQUEST        - RequestId: {}, page: {}, size: {}, filter: {}", requestId, page, size, filter);
        try {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Integer ownerId = currentUser != null ? currentUser.getId() : null;
            List<FlightDto> dtos = flightDAO.findByOwnerId(ownerId).stream()
                    .map(this::toFlightListDto)
                    .collect(Collectors.toList());
            log.info("GET_FLIGHTS_SUCCESS        - RequestId: {}, count: {}", requestId, dtos.size());
            return dtos;
        } catch (Exception e) {
            log.error("GET_FLIGHTS_FAILED         - RequestId: {}, error: {}", requestId, e.getMessage(), e);
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
            User u =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            f.setOwner(u);

            Flight saved = flightDAO.save(f);
            log.info("CREATE_FLIGHT_DETAILS_SUC  - RequestId: {}, flightId: {}", requestId, saved.getId());

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
    public List<FlightSlotDto> getSeatsBooked(Integer flightId) {
        String requestId = UUID.randomUUID().toString();
        log.info("""
                GET_SEATS_REQUEST   - RequestId: {}, flightId: {}""", requestId, flightId);
        try {
            List<FlightSlot> ls= flightDAO.findslotByBooked(flightId);
            if (ls.size() == 0) {
                log.warn("GET_SEATS_NOT_FOUND       - RequestId: {}, flightId: {}", requestId, flightId);
                return Collections.emptyList();
            }
            List<FlightSlotDto> res = new ArrayList<>();
            for (FlightSlot fs : ls) {
                FlightSlotDto dto = toFlightSlotDto(fs);
                res.add(dto);
                log.info("GET_SEATS_SUCCESS         - RequestId: {}, flightId: {}, dto: {}", requestId, flightId, dto);
            }
            return res;
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
                    log.info("UPDATED_SEAT              - RequestId: {}, slotId: {}", requestId, s.getId());
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
    @Transactional
    @Override
    public List<FlightOrderReservationDto> getFlightBookings(String filter) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_BOOKINGS_ADMIN_REQ    - RequestId: {}, filter: {}", requestId, filter);
        try {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Integer ownerId = currentUser != null ? currentUser.getId() : null;
            log.info("GET_BOOKINGS_ADMIN_REQ    - ownerId: {}", ownerId);
            List<FlightBooking> bookings = flightBookingDAO.findByOwnerId(ownerId);
            var result = bookings.stream()
                    .map(this::toReservationDto)
                    .collect(Collectors.toList());
            log.info("GET_BOOKINGS_ADMIN_SUC    - RequestId: {}, count: {}", requestId, result.size());
            return result;
        } catch (Exception e) {
            log.error("GET_BOOKINGS_ADMIN_FAIL   - RequestId: {}, error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    @Override
    public List<FlightOrderReservationDto> getFlightBookings(String filter, int page, int size) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_BOOKINGS_ADMIN_REQ    - RequestId: {}, filter: {}, page: {}, size: {}", requestId, filter, page, size);
        try {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Integer ownerId = currentUser != null ? currentUser.getId() : null;
            var pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by("id").descending());
            var bookingsPage = flightBookingDAO.findByOwnerId(ownerId, pageable);
            var result = bookingsPage.getContent().stream()
                    .map(this::toReservationDto)
                    .collect(Collectors.toList());
            log.info("GET_BOOKINGS_ADMIN_SUC    - RequestId: {}, count: {}, total: {}", requestId, result.size(), bookingsPage.getTotalElements());
            return result;
        } catch (Exception e) {
            log.error("GET_BOOKINGS_ADMIN_FAIL   - RequestId: {}, error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public FlightOrderReservationDto getFlightBookingDetail(Integer bookingId) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_BOOKING_ADMIN_REQ     - RequestId: {}, bookingId: {}", requestId, bookingId);
        try {
            var b = flightBookingDAO.findById(bookingId).orElse(null);
            if (b == null) {
                log.warn("GET_BOOKING_ADMIN_NOT_FOUND- RequestId: {}, bookingId: {}", requestId, bookingId);
                return null;
            }
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Integer ownerId = currentUser != null ? currentUser.getId() : null;
            Flight f = Optional.ofNullable(b.getFlightSlot()).map(FlightSlot::getFlight).orElse(null);
            if (f == null || f.getOwner() == null || !Objects.equals(f.getOwner().getId(), ownerId)) {
                log.warn("GET_BOOKING_ADMIN_FORBIDDEN- RequestId: {}, bookingId: {}", requestId, bookingId);
                return null;
            }
            var dto = toReservationDto(b);
            log.info("GET_BOOKING_ADMIN_SUC     - RequestId: {}, bookingId: {}", requestId, bookingId);
            return dto;
        } catch (Exception e) {
            log.error("GET_BOOKING_ADMIN_FAIL    - RequestId: {}, bookingId: {}, error: {}", requestId, bookingId, e.getMessage(), e);
            throw e;
        }
    }

    private FlightOrderReservationDto toReservationDto(FlightBooking b) {
        Flight f = b.getFlightSlot().getFlight();
        FlightOrderReservationDto dto = new FlightOrderReservationDto();
        // reuse existing mapping helpers
        dto.setBooking(toBookingDetailDto(b, f));
        dto.setFlightSlot(toFlightSlotDto(b.getFlightSlot()));
        // order, customer may require other services; set minimal if available
        if (b.getOrder() != null) {
            OrderDto od = new OrderDto();
            od.setId(b.getOrder().getId());
            dto.setOrder(od);
        }
        if (b.getCustomer() != null) {
            Customer c = b.getCustomer();
            CustomerDto cd = CustomerDto.builder()
                    .id(c.getId())
                    .fullName(c.getFullName())
                    .gender(c.getGender())
                    .dob(c.getDob())
                    .passport(c.getPassport())
                    .email(c.getEmail())
                    .phone(c.getPhone())
                    .build();
            dto.setCustomer(cd);
        }
        return dto;
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
    public void deleteFlightBooking(Integer bookingId) {
        String requestId = UUID.randomUUID().toString();
        log.info("DELETE_BOOKING_ADMIN_REQ  - RequestId: {}, bookingId: {}", requestId, bookingId);
        try {
            flightBookingDAO.deleteById(bookingId);
            log.info("DELETE_BOOKING_ADMIN_SUC  - RequestId: {}, bookingId: {}", requestId, bookingId);
        } catch (Exception e) {
            log.error("DELETE_BOOKING_ADMIN_FAIL - RequestId: {}, bookingId: {}, error: {}", requestId, bookingId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public FlightOrderReservationDto updateFlightBooking(Integer bookingId, FlightOrderReservationDto dto) {
        String requestId = UUID.randomUUID().toString();
        log.info("UPDATE_BOOKING_DETAILS_REQ - RequestId: {}, bookingId: {}", requestId, bookingId);
        try {
            FlightBooking booking = flightBookingDAO.findById(bookingId).orElseThrow();
            // ownership check
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Integer ownerId = currentUser != null ? currentUser.getId() : null;
            Flight flight = booking.getFlightSlot().getFlight();
            if (flight == null || flight.getOwner() == null || !Objects.equals(flight.getOwner().getId(), ownerId)) {
                throw new RuntimeException("Forbidden");
            }

            // Update customer
            if (dto.getCustomer() != null && booking.getCustomer() != null) {
                Customer c = booking.getCustomer();
                CustomerDto cd = dto.getCustomer();
                c.setFullName(cd.getFullName());
                c.setGender(cd.getGender());
                c.setDob(cd.getDob());
                c.setPassport(cd.getPassport());
                c.setEmail(cd.getEmail());
                c.setPhone(cd.getPhone());
            }

            // Update flight slot basic fields (price, seatNumber, aisle/window/business, luggage)
            if (dto.getFlightSlot() != null) {
                FlightSlot s = booking.getFlightSlot();
                FlightSlotDto sd = dto.getFlightSlot();
                if (sd.getPrice() != null) s.setPrice(sd.getPrice());
                if (sd.getSeatNumber() != null) s.setSeatNumber(sd.getSeatNumber());
                if (sd.getIsAisle() != null) s.setIsAisle(sd.getIsAisle());
                if (sd.getIsWindow() != null) s.setIsWindow(sd.getIsWindow());
                if (sd.getIsBusiness() != null) s.setIsBusiness(sd.getIsBusiness());
                if (sd.getCarryOnLuggage() != null) s.setCarryOnLuggage(sd.getCarryOnLuggage());
            }

            // Optionally update totalPrice if provided in booking detail
            if (dto.getBooking() != null && dto.getBooking().getTotalPrice() != null) {
                booking.setTotalPrice(java.math.BigDecimal.valueOf(dto.getBooking().getTotalPrice()));
            }

            // persist via owning repositories already injected
            // JPA will flush changes on transaction commit
            log.info("UPDATE_BOOKING_DETAILS_SUC - RequestId: {}, bookingId: {}", requestId, bookingId);
            return toReservationDto(booking);
        } catch (Exception e) {
            log.error("UPDATE_BOOKING_DETAILS_FAIL - RequestId: {}, bookingId: {}, error: {}", requestId, bookingId, e.getMessage(), e);
            throw e;
        }
    }
    @Transactional
    @Override
    public void updateGroupSeat(Integer flightId, FlightSeatGroupDto dto) {
        String requestId = UUID.randomUUID().toString();
        log.info("UPDATE_GROUP_SEAT_REQ       - requestId: {}, flightId: {}, dto: {}", requestId, flightId, dto);

        try {
            Map<String, Integer> seatDetails = flightService.getAvailableSeatsDetail(flightId);
            int ecoWindow   = seatDetails.getOrDefault("economyWindow", 0);
            int ecoAisle    = seatDetails.getOrDefault("economyAisle", 0);
            int bizWindow   = seatDetails.getOrDefault("businessWindow", 0);
            int bizAisle    = seatDetails.getOrDefault("businessAisle", 0);

            Flight fl = flightDAO.findById(flightId).orElse(null);
            if (fl == null) {
                log.warn("UPDATE_GROUP_SEAT_FLIGHT_NOT_FOUND - requestId: {}, flightId: {}", requestId, flightId);
                return;
            }

            boolean isBusiness = dto.getIsBusiness();
            boolean isWindow = dto.getIsWindow();
            int desiredCount = dto.getCount();

            if (isBusiness) {
                if (isWindow) {
                    if (bizWindow < desiredCount) {
                        int toCreate = desiredCount - bizWindow;
                        log.info("CREATE_SLOT_BIZ_WINDOW     - requestId: {}, flightId: {}, creating: {}", requestId, flightId, toCreate);
                        createBackupSlots(toCreate, fl, dto);
                    } else if (bizWindow > desiredCount) {
                        int toDelete = bizWindow - desiredCount;
                        log.info("DELETE_SLOT_BIZ_WINDOW     - requestId: {}, flightId: {}, deleting: {}", requestId, flightId, toDelete);
                        deletebyGroup(toDelete, fl, dto);
                    } else {
                        log.info("NO_CHANGE_BIZ_WINDOW       - requestId: {}, flightId: {}, count unchanged", requestId, flightId);
                    }
                } else {
                    if (bizAisle < desiredCount) {
                        int toCreate = desiredCount - bizAisle;
                        log.info("CREATE_SLOT_BIZ_AISLE      - requestId: {}, flightId: {}, creating: {}", requestId, flightId, toCreate);
                        createBackupSlots(toCreate, fl, dto);
                    } else if (bizAisle > desiredCount) {
                        int toDelete = bizAisle - desiredCount;
                        log.info("DELETE_SLOT_BIZ_AISLE      - requestId: {}, flightId: {}, deleting: {}", requestId, flightId, toDelete);
                        deletebyGroup(toDelete, fl, dto);
                    } else {
                        log.info("NO_CHANGE_BIZ_AISLE        - requestId: {}, flightId: {}, count unchanged", requestId, flightId);
                    }
                }
            } else {
                if (isWindow) {
                    if (ecoWindow < desiredCount) {
                        int toCreate = desiredCount - ecoWindow;
                        log.info("CREATE_SLOT_ECO_WINDOW     - requestId: {}, flightId: {}, creating: {}", requestId, flightId, toCreate);
                        createBackupSlots(toCreate, fl, dto);
                    } else if (ecoWindow > desiredCount) {
                        int toDelete = ecoWindow - desiredCount;
                        log.info("DELETE_SLOT_ECO_WINDOW     - requestId: {}, flightId: {}, deleting: {}", requestId, flightId, toDelete);
                        deletebyGroup(toDelete, fl, dto);
                    } else {
                        log.info("NO_CHANGE_ECO_WINDOW       - requestId: {}, flightId: {}, count unchanged", requestId, flightId);
                    }
                } else {
                    if (ecoAisle < desiredCount) {
                        int toCreate = desiredCount - ecoAisle;
                        log.info("CREATE_SLOT_ECO_AISLE      - requestId: {}, flightId: {}, creating: {}", requestId, flightId, toCreate);
                        createBackupSlots(toCreate, fl, dto);
                    } else if (ecoAisle > desiredCount) {
                        int toDelete = ecoAisle - desiredCount;
                        log.info("DELETE_SLOT_ECO_AISLE      - requestId: {}, flightId: {}, deleting: {}", requestId, flightId, toDelete);
                        deletebyGroup(toDelete, fl, dto);
                    } else {
                        log.info("NO_CHANGE_ECO_AISLE        - requestId: {}, flightId: {}, count unchanged", requestId, flightId);
                    }
                }
            }

            flightSlotDAO.updateFlightSlotsByConditions(
                    dto.getPrice(),
                    dto.getCarryOnLuggage(),
                    !isWindow,
                    isWindow,
                    isBusiness,
                    flightId
            );

            log.info("UPDATE_SLOT_CONDITIONS_DONE - requestId: {}, flightId: {}", requestId, flightId);

        } catch (Exception e) {
            log.error("UPDATE_GROUP_SEAT_FAILED   - requestId: {}, flightId: {}, error: {}", requestId, flightId, e.getMessage(), e);
            throw e;
        }
    }
    private void createBackupSlots(int count, Flight flight, FlightSeatGroupDto dto) {
        String requestId = UUID.randomUUID().toString();
        log.info("CREATE_BACKUP_SLOTS_START   - requestId: {}, flightId: {}, creating: {}, isBusiness: {}, isWindow: {}",
                requestId, flight.getId(), count, dto.getIsBusiness(), dto.getIsWindow());

        try {
            for (int i = 0; i < count; i++) {
                FindAvailableSlotRequestDto dtoReq = new FindAvailableSlotRequestDto();
                dtoReq.setFlightId(flight.getId());
                dtoReq.setIsAisle(!dto.getIsWindow());
                dtoReq.setIsBusiness(dto.getIsBusiness());
                dtoReq.setIsWindow(dto.getIsWindow());

                FlightSlot slot = new FlightSlot();
                slot.setFlight(flight);
                slot.setPrice(dto.getPrice());
                slot.setIsAisle(!dto.getIsWindow());
                slot.setIsBusiness(dto.getIsBusiness());
                slot.setIsWindow(dto.getIsWindow());
                slot.setCarryOnLuggage(dto.getCarryOnLuggage());

                int bk = 1;
                String code;
                do {
                    code = "backup" + bk;
                    bk++;
                } while (flightSlotDAO.existsBySeatNumber(code));

                slot.setSeatNumber(code);
                flightSlotDAO.save(slot);
                log.info("CREATE_BACKUP_SLOT_SUCCESS - requestId: {}, seatNumber: {}", requestId, code);
            }

            log.info("CREATE_BACKUP_SLOTS_DONE    - requestId: {}, flightId: {}", requestId, flight.getId());
        } catch (Exception e) {
            log.error("CREATE_BACKUP_SLOTS_FAILED  - requestId: {}, flightId: {}, error: {}", requestId, flight.getId(), e.getMessage(), e);
            throw e;
        }
    }
    private void deletebyGroup(int count, Flight flight, FlightSeatGroupDto dto) {
        String requestId = UUID.randomUUID().toString();
        log.info("DELETE_SLOT_GROUP_START     - requestId: {}, flightId: {}, deleting: {}, isBusiness: {}, isWindow: {}",
                requestId, flight.getId(), count, dto.getIsBusiness(), dto.getIsWindow());

        try {
            List<FlightSlot> ls = flightDAO.findUnbookedSlotByConditions(
                    flight.getId(), !dto.getIsWindow(), dto.getIsWindow(), dto.getIsBusiness()
            );

            if (ls.size() < count) {
                log.warn("DELETE_SLOT_GROUP_INSUFFICIENT - requestId: {}, flightId: {}, available: {}, required: {}",
                        requestId, flight.getId(), ls.size(), count);
                return;
            }

            for (int i = 0; i < count; i++) {
                String seat = ls.get(i).getSeatNumber();
                flightSlotDAO.delete(ls.get(i));
                log.info("DELETE_SLOT_SUCCESS         - requestId: {}, seatNumber: {}", requestId, seat);
            }

            log.info("DELETE_SLOT_GROUP_DONE      - requestId: {}, flightId: {}", requestId, flight.getId());
        } catch (Exception e) {
            log.error("DELETE_SLOT_GROUP_FAILED    - requestId: {}, flightId: {}, error: {}", requestId, flight.getId(), e.getMessage(), e);
            throw e;
        }
    }


    @Override
    public List<FlightStatisticsDto> getFlightStatistics(String type, String value) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_STATS_REQUEST - RequestId: {}, type: {}, value: {}", requestId, type, value);
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

    // ===== Airlines CRUD =====
    @Override
    public List<AirlineDto> getAirlines() {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_AIRLINES_REQUEST      - RequestId: {}", requestId);
        try {
            var dtos = airlineDAO.findAll().stream().map(this::toAirlineDto).collect(Collectors.toList());
            log.info("GET_AIRLINES_SUCCESS      - RequestId: {}, count: {}", requestId, dtos.size());
            return dtos;
        } catch (Exception e) {
            log.error("GET_AIRLINES_FAILED       - RequestId: {}, error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AirlineDto createAirline(AirlineDto airlineDto) {
        String requestId = UUID.randomUUID().toString();
        log.info("CREATE_AIRLINE_REQUEST    - RequestId: {}, payload: {}", requestId, airlineDto);
        try {
            Airline a = new Airline(); a.setName(airlineDto.getName());
            Airline saved = airlineDAO.save(a);
            AirlineDto dto = toAirlineDto(saved);
            log.info("CREATE_AIRLINE_SUCCESS    - RequestId: {}, airlineId: {}", requestId, saved.getId());
            return dto;
        } catch (Exception e) {
            log.error("CREATE_AIRLINE_FAILED     - RequestId: {}, error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AirlineDto updateAirline(Integer airlineId, AirlineDto airlineDto) {
        String requestId = UUID.randomUUID().toString();
        log.info("UPDATE_AIRLINE_REQUEST    - RequestId: {}, airlineId: {}, payload: {}", requestId, airlineId, airlineDto);
        try {
            Airline a = airlineDAO.findById(airlineId).orElse(null);
            if (a == null) {
                log.warn("UPDATE_AIRLINE_NOT_FOUND  - RequestId: {}, airlineId: {}", requestId, airlineId);
                return null;
            }
            a.setName(airlineDto.getName());
            Airline saved = airlineDAO.save(a);
            AirlineDto dto = toAirlineDto(saved);
            log.info("UPDATE_AIRLINE_SUCCESS    - RequestId: {}, airlineId: {}", requestId, airlineId);
            return dto;
        } catch (Exception e) {
            log.error("UPDATE_AIRLINE_FAILED     - RequestId: {}, airlineId: {}, error: {}", requestId, airlineId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteAirline(Integer airlineId) {
        String requestId = UUID.randomUUID().toString();
        log.info("DELETE_AIRLINE_REQUEST    - RequestId: {}, airlineId: {}", requestId, airlineId);
        try {
            airlineDAO.deleteById(airlineId);
            log.info("DELETE_AIRLINE_SUCCESS    - RequestId: {}, airlineId: {}", requestId, airlineId);
        } catch (Exception e) {
            log.error("DELETE_AIRLINE_FAILED     - RequestId: {}, airlineId: {}, error: {}", requestId, airlineId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public List<ImageDto> updateFlightImages(Integer flightId, List<MultipartFile> files, List<Integer> keepImageIds) {
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
                    log.info("UPLOADED_IMAGE - flightId: {}, imageId: {}", flightId, savedImg.getId());
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

    @Override
    @Transactional
    public List<ImageDto> uploadFlightImages(Integer flightId, List<MultipartFile> files) {
        String requestId = UUID.randomUUID().toString();
        log.info("UPLOAD_FLIGHT_IMAGES_REQUEST - RequestId: {}, flightId: {}, fileCount: {}", requestId, flightId, files.size());
        try {
            Flight flight = flightDAO.findById(flightId).orElseThrow();
            List<ImageDto> uploadedImages = new ArrayList<>();
            
            if (files != null) {
                for (MultipartFile imgFile : files) {
                    if (imgFile.isEmpty()) continue;
                    try {
                        Map<String,String> up = imageStorageService.uploadImage(imgFile);
                        Image img = new Image();
                        img.setUrl(up.get("url"));
                        img.setAltText(flight.getName());
                        img.setUploadedAt(LocalDateTime.now());
                        img.setPublicId(up.get("publicId"));
                        Image savedImg = imageDAO.save(img);
                        FlightImage fi = new FlightImage();
                        fi.setFlight(flight);
                        fi.setImage(savedImg);
                        flightImageDAO.save(fi);
                        
                        ImageDto dto = new ImageDto();
                        dto.setId(savedImg.getId());
                        dto.setImageUrl(savedImg.getUrl());
                        uploadedImages.add(dto);
                        
                        log.info("UPLOADED_IMAGE - RequestId: {}, flightId: {}, imageId: {}", requestId, flightId, savedImg.getId());
                    } catch (Exception ex) {
                        log.error("UPLOAD_IMAGE_FAILED - RequestId: {}, flightId: {}, error: {}", requestId, flightId, ex.getMessage(), ex);
                    }
                }
            }
            
            log.info("UPLOAD_FLIGHT_IMAGES_SUCCESS - RequestId: {}, flightId: {}, uploadedCount: {}", requestId, flightId, uploadedImages.size());
            return uploadedImages;
        } catch (Exception e) {
            log.error("UPLOAD_FLIGHT_IMAGES_FAILED - RequestId: {}, flightId: {}, error: {}", requestId, flightId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteFlightImage(Integer flightId, Integer imageId) {
        String requestId = UUID.randomUUID().toString();
        log.info("DELETE_FLIGHT_IMAGE_REQUEST - RequestId: {}, flightId: {}, imageId: {}", requestId, flightId, imageId);
        try {
            // Tìm FlightImage theo flightId và imageId
            Optional<FlightImage> flightImageOpt = flightImageDAO.findById(imageId);
            if (flightImageOpt.isEmpty()) {
                log.warn("DELETE_FLIGHT_IMAGE_NOT_FOUND - RequestId: {}, flightId: {}, imageId: {}", requestId, flightId, imageId);
                throw new RuntimeException("Flight image not found");
            }
            FlightImage flightImage = flightImageOpt.get();
            // Xóa ảnh khỏi cloud storage
            try {
                imageStorageService.deleteImage(flightImage.getImage().getPublicId());
            } catch (Exception ex) {
                log.error("DELETE_IMAGE_FROM_CLOUD_FAILED - RequestId: {}, flightId: {}, imageId: {}, error: {}", requestId, flightId, imageId, ex.getMessage(), ex);
            }
            // Xóa liên kết FlightImage
            flightImageDAO.delete(flightImage);
            // Xóa Image
            imageDAO.delete(flightImage.getImage());
            log.info("DELETE_FLIGHT_IMAGE_SUCCESS - RequestId: {}, flightId: {}, imageId: {}", requestId, flightId, imageId);
        } catch (Exception e) {
            log.error("DELETE_FLIGHT_IMAGE_FAILED - RequestId: {}, flightId: {}, imageId: {}, error: {}", requestId, flightId, imageId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public List<ImageDto> addFlightImages(Integer flightId, List<MultipartFile> files) {
        String requestId = UUID.randomUUID().toString();
        log.info("ADD_FLIGHT_IMAGES_REQUEST - RequestId: {}, flightId: {}, fileCount: {}", requestId, flightId, files.size());
        try {
            Flight flight = flightDAO.findById(flightId).orElseThrow();
            List<ImageDto> uploadedImages = new ArrayList<>();
            
            if (files != null) {
                for (MultipartFile imgFile : files) {
                    if (imgFile.isEmpty()) continue;
                    try {
                        Map<String,String> up = imageStorageService.uploadImage(imgFile);
                        Image img = new Image();
                        img.setUrl(up.get("url"));
                        img.setAltText(flight.getName());
                        img.setUploadedAt(LocalDateTime.now());
                        img.setPublicId(up.get("publicId"));
                        Image savedImg = imageDAO.save(img);
                        FlightImage fi = new FlightImage();
                        fi.setFlight(flight);
                        fi.setImage(savedImg);
                        flightImageDAO.save(fi);
                        
                        ImageDto dto = new ImageDto();
                        dto.setId(savedImg.getId());
                        dto.setImageUrl(savedImg.getUrl());
                        uploadedImages.add(dto);
                        
                        log.info("ADDED_IMAGE - RequestId: {}, flightId: {}, imageId: {}", requestId, flightId, savedImg.getId());
                    } catch (Exception ex) {
                        log.error("ADD_IMAGE_FAILED - RequestId: {}, flightId: {}, error: {}", requestId, flightId, ex.getMessage(), ex);
                    }
                }
            }
            
            log.info("ADD_FLIGHT_IMAGES_SUCCESS - RequestId: {}, flightId: {}, uploadedCount: {}", requestId, flightId, uploadedImages.size());
            return uploadedImages;
        } catch (Exception e) {
            log.error("ADD_FLIGHT_IMAGES_FAILED - RequestId: {}, flightId: {}, error: {}", requestId, flightId, e.getMessage(), e);
            throw e;
        }
    }

    // ===== Super Admin Dashboard =====
    @Override
    public List<FlightAdminSummaryDto> getFlightAdminSummaries() {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_FLIGHT_ADMIN_SUMMARIES_REQUEST - RequestId: {}", requestId);
        try {
            List<User> flightAdmins = userDAO.findByRole(UserRoleEnum.FLIGHT_SUPPLIER);
            List<FlightAdminSummaryDto> summaries = new ArrayList<>();
            
            for (User admin : flightAdmins) {
                Integer adminId = admin.getId();
                int totalFlights = flightDAO.countByOwnerId(adminId);
                Double totalRevenue = flightBookingDAO.sumRevenueByOwnerId(adminId);
                if (totalRevenue == null) totalRevenue = 0.0;
                Long totalBookings = flightBookingDAO.countByOwnerId(adminId);
                Double avgOccupancy = flightDAO.getAverageOccupancyRateByOwnerId(adminId);
                if (avgOccupancy == null) avgOccupancy = 0.0;
                
                FlightAdminSummaryDto summary = FlightAdminSummaryDto.builder()
                        .adminId(adminId)
                        .adminName(admin.getName())
                        .adminEmail(admin.getEmail())
                        .totalFlights(totalFlights)
                        .totalRevenue(totalRevenue)
                        .totalBookings(totalBookings)
                        .averageOccupancyRate(avgOccupancy)
                        .build();
                
                summaries.add(summary);
            }
            
            log.info("GET_FLIGHT_ADMIN_SUMMARIES_SUCCESS - RequestId: {}, count: {}", requestId, summaries.size());
            return summaries;
        } catch (Exception e) {
            log.error("GET_FLIGHT_ADMIN_SUMMARIES_FAILED - RequestId: {}, error: {}", requestId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public FlightAdminDetailDto getFlightAdminDetail(Integer adminId) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_FLIGHT_ADMIN_DETAIL_REQUEST - RequestId: {}, adminId: {}", requestId, adminId);
        try {
            User admin = userDAO.findById(adminId).orElse(null);
            if (admin == null) {
                log.warn("GET_FLIGHT_ADMIN_DETAIL_NOT_FOUND - RequestId: {}, adminId: {}", requestId, adminId);
                return null;
            }
            int totalFlights = flightDAO.countByOwnerId(adminId);
            Double totalRevenue = flightBookingDAO.sumRevenueByOwnerId(adminId);
            if (totalRevenue == null) totalRevenue = 0.0;
            Long totalBookings = flightBookingDAO.countByOwnerId(adminId);
            Double avgOccupancy = flightDAO.getAverageOccupancyRateByOwnerId(adminId);
            if (avgOccupancy == null) avgOccupancy = 0.0;
            List<Flight> recentFlights = flightDAO.findRecentFlightsByOwnerId(adminId, 10);
            List<FlightAdminDetailDto.FlightSummaryDto> flightSummaries = recentFlights.stream()
                    .map(flight -> {
                        int totalSeats = flightSlotDAO.countByFlightId(flight.getId());
                        int bookedSeats = flightBookingDAO.countSoldSeatsByFlightId(flight.getId());
                        Double revenue = flightBookingDAO.sumRevenueByFlightId(flight.getId());
                        if (revenue == null) revenue = 0.0;
                        
                        return FlightAdminDetailDto.FlightSummaryDto.builder()
                                .flightId(flight.getId())
                                .flightNumber(flight.getFlightNumber())
                                .departureAirport(flight.getDepartureAirport() != null ? flight.getDepartureAirport().getName() : "")
                                .arrivalAirport(flight.getArrivalAirport() != null ? flight.getArrivalAirport().getName() : "")
                                .departureTime(flight.getDepartureTime() != null ? flight.getDepartureTime().toString() : "")
                                .totalSeats(totalSeats)
                                .bookedSeats(bookedSeats)
                                .revenue(revenue)
                                .build();
                    })
                    .collect(Collectors.toList());
            
            FlightAdminDetailDto detail = FlightAdminDetailDto.builder()
                    .adminId(adminId)
                    .adminName(admin.getName())
                    .adminEmail(admin.getEmail())
                    .totalFlights(totalFlights)
                    .totalRevenue(totalRevenue)
                    .totalBookings(totalBookings)
                    .averageOccupancyRate(avgOccupancy)
                    .recentFlights(flightSummaries)
                    .build();
            
            log.info("GET_FLIGHT_ADMIN_DETAIL_SUCCESS - RequestId: {}, adminId: {}", requestId, adminId);
            return detail;
        } catch (Exception e) {
            log.error("GET_FLIGHT_ADMIN_DETAIL_FAILED - RequestId: {}, adminId: {}, error: {}", requestId, adminId, e.getMessage(), e);
            throw e;
        }
    }
    @Transactional
    @Override
    public Page<FlightDto> getFlightsByAdminId(Integer adminId, int page, int size, String filter) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_FLIGHTS_BY_ADMIN_REQUEST - RequestId: {}, adminId: {}, page: {}, size: {}", requestId, adminId, page, size);
        try {
            var pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by("id").descending());
            var flightsPage = flightDAO.findByOwnerIdWithPagination(adminId, pageable);
            var dtos = flightsPage.getContent().stream()
                    .map(this::toFlightListDto)
                    .collect(Collectors.toList());
            
            var result = new org.springframework.data.domain.PageImpl<>(dtos, pageable, flightsPage.getTotalElements());
            log.info("GET_FLIGHTS_BY_ADMIN_SUCCESS - RequestId: {}, adminId: {}, count: {}, total: {}", requestId, adminId, dtos.size(), flightsPage.getTotalElements());
            return result;
        } catch (Exception e) {
            log.error("GET_FLIGHTS_BY_ADMIN_FAILED - RequestId: {}, adminId: {}, error: {}", requestId, adminId, e.getMessage(), e);
            throw e;
        }
    }

    // mapping helpers with debug logging

    private FlightDto toFlightDto(Flight flight) {
        // Full detail mapping (used for detail views where necessary)
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

        return FlightDto.builder()
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
    }

    private FlightDto toFlightListDto(Flight flight) {
        // Lightweight mapping for list: avoid loading images/slots to improve performance
        return FlightDto.builder()
                .id(flight.getId())
                .flightNumber(flight.getFlightNumber())
                .name(flight.getName())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .category(flight.getCategory() != null ? toFlightCategoryDto(flight.getCategory()) : null)
                .ownerId(flight.getOwner() != null ? flight.getOwner().getId() : null)
                .departureAirport(flight.getDepartureAirport() != null ? toAirportDto(flight.getDepartureAirport()) : null)
                .arrivalAirport(flight.getArrivalAirport() != null ? toAirportDto(flight.getArrivalAirport()) : null)
                .airline(flight.getAirline() != null ? toAirlineDto(flight.getAirline()) : null)
                .build();
    }
    private FlightImageDto toFlightImageDto(FlightImage fi) {
        Image img = fi.getImage();
        FlightImageDto dto = FlightImageDto.builder()
                .id(fi.getId())
                .imageId(img != null ? img.getId() : null)
                .flightId(fi.getFlight() != null ? fi.getFlight().getId() : null)
                .imageUrl(img != null ? img.getUrl() : null)
                .altText(img != null ? img.getAltText() : null)
                .uploadedAt(img != null ? img.getUploadedAt() : null)
                .build();
        return dto;
    }
    private FlightSlotDto toFlightSlotDto(FlightSlot s) {
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
        return dto;
    }

    private FlightBookingDetailDto toBookingDetailDto(FlightBooking b, Flight f) {
        FlightBookingDetailDto dto = FlightBookingDetailDto.builder()
                .bookingId(b.getId())
                .createdAt(b.getBookingDate())
                .flight(toFlightDto(f))
                .totalPrice(b.getFlightSlot().getPrice().doubleValue())
                .status("BOOKED")
                .build();
        return dto;
    }

    private AirportDto toAirportDto(Airport a) {
        AirportDto dto = AirportDto.builder()
                .id(a.getId())
                .name(a.getName())
                .build();
        return dto;
    }

    private AirlineDto toAirlineDto(Airline a) {
        AirlineDto dto = AirlineDto.builder()
                .id(a.getId())
                .name(a.getName())
                .build();
        return dto;
    }

    private FlightCategoryDto toFlightCategoryDto(FlightCategory c) {
        FlightCategoryDto dto = FlightCategoryDto.builder()
                .id(c.getId())
                .name(c.getName())
                .build();
        return dto;
    }

    @Override
    public MonthlyFlightStatisticsDto getMonthlyFlightStatistics(Integer year, Integer month) {
        String requestId = UUID.randomUUID().toString();
        log.info("[THỐNG_KÊ_THÁNG][BẮT_ĐẦU] requestId={}, năm={}, tháng={}", requestId, year, month);

        try {
            // Tính tháng trước
            int previousMonth = (month == 1) ? 12 : month - 1;
            int previousYear = (month == 1) ? year - 1 : year;
            log.info("[THỐNG_KÊ_THÁNG][{}] Tháng trước được tính là: năm={}, tháng={}", requestId, previousYear, previousMonth);

            // Thống kê tháng hiện tại
            log.info("[THỐNG_KÊ_THÁNG][{}] Bắt đầu lấy dữ liệu thống kê tháng hiện tại...", requestId);
            Long totalFlightsCurrentMonth = flightDAO.countFlightsByMonth(year, month);
            log.info("[THỐNG_KÊ_THÁNG][{}] Tổng số chuyến bay tháng {}/{}: {}", requestId, month, year, totalFlightsCurrentMonth);

            Long totalBookingsCurrentMonth = flightBookingDAO.countBookingsByMonth(year, month);
            log.info("[THỐNG_KÊ_THÁNG][{}] Tổng số lượt đặt vé tháng {}/{}: {}", requestId, month, year, totalBookingsCurrentMonth);

            Double totalRevenueCurrentMonth = flightBookingDAO.sumRevenueByMonth(year, month);
            if (totalRevenueCurrentMonth == null) {
                totalRevenueCurrentMonth = 0.0;
                log.warn("[THỐNG_KÊ_THÁNG][{}] Doanh thu tháng {}/{} không có dữ liệu, gán mặc định = 0.0", requestId, month, year);
            } else {
                log.info("[THỐNG_KÊ_THÁNG][{}] Doanh thu tháng {}/{}: {}", requestId, month, year, totalRevenueCurrentMonth);
            }

            Double averageOccupancyRateCurrentMonth = calculateAverageOccupancyRate(year, month);
            if (averageOccupancyRateCurrentMonth == null) {
                averageOccupancyRateCurrentMonth = 0.0;
                log.warn("[THỐNG_KÊ_THÁNG][{}] Tỷ lệ lấp đầy tháng {}/{} không có dữ liệu, gán mặc định = 0.0", requestId, month, year);
            } else {
                log.info("[THỐNG_KÊ_THÁNG][{}] Tỷ lệ lấp đầy trung bình tháng {}/{}: {}%", requestId, month, year, averageOccupancyRateCurrentMonth);
            }

            // Thống kê tháng trước
            log.info("[THỐNG_KÊ_THÁNG][{}] Bắt đầu lấy dữ liệu thống kê tháng trước...", requestId);
            Long totalFlightsPreviousMonth = flightDAO.countFlightsByMonth(previousYear, previousMonth);
            log.info("[THỐNG_KÊ_THÁNG][{}] Tổng số chuyến bay tháng {}/{}: {}", requestId, previousMonth, previousYear, totalFlightsPreviousMonth);

            Long totalBookingsPreviousMonth = flightBookingDAO.countBookingsByMonth(previousYear, previousMonth);
            log.info("[THỐNG_KÊ_THÁNG][{}] Tổng số lượt đặt vé tháng {}/{}: {}", requestId, previousMonth, previousYear, totalBookingsPreviousMonth);

            Double totalRevenuePreviousMonth = flightBookingDAO.sumRevenueByMonth(previousYear, previousMonth);
            if (totalRevenuePreviousMonth == null) {
                totalRevenuePreviousMonth = 0.0;
                log.warn("[THỐNG_KÊ_THÁNG][{}] Doanh thu tháng {}/{} không có dữ liệu, gán mặc định = 0.0", requestId, previousMonth, previousYear);
            } else {
                log.info("[THỐNG_KÊ_THÁNG][{}] Doanh thu tháng {}/{}: {}", requestId, previousMonth, previousYear, totalRevenuePreviousMonth);
            }

            Double averageOccupancyRatePreviousMonth = calculateAverageOccupancyRate(previousYear, previousMonth);
            if (averageOccupancyRatePreviousMonth == null) {
                averageOccupancyRatePreviousMonth = 0.0;
                log.warn("[THỐNG_KÊ_THÁNG][{}] Tỷ lệ lấp đầy tháng {}/{} không có dữ liệu, gán mặc định = 0.0", requestId, previousMonth, previousYear);
            } else {
                log.info("[THỐNG_KÊ_THÁNG][{}] Tỷ lệ lấp đầy trung bình tháng {}/{}: {}%", requestId, previousMonth, previousYear, averageOccupancyRatePreviousMonth);
            }

            // Tính phần trăm thay đổi
            log.info("[THỐNG_KÊ_THÁNG][{}] Đang tính phần trăm thay đổi giữa hai tháng...", requestId);
            Double flightsChangePercentage = calculatePercentageChange(totalFlightsPreviousMonth, totalFlightsCurrentMonth);
            log.info("[THỐNG_KÊ_THÁNG][{}] Thay đổi số chuyến bay: {}%", requestId, flightsChangePercentage);

            Double bookingsChangePercentage = calculatePercentageChange(totalBookingsPreviousMonth, totalBookingsCurrentMonth);
            log.info("[THỐNG_KÊ_THÁNG][{}] Thay đổi lượt đặt vé: {}%", requestId, bookingsChangePercentage);

            Double revenueChangePercentage = calculatePercentageChange(totalRevenuePreviousMonth, totalRevenueCurrentMonth);
            log.info("[THỐNG_KÊ_THÁNG][{}] Thay đổi doanh thu: {}%", requestId, revenueChangePercentage);

            Double occupancyChangePercentage = calculatePercentageChange(averageOccupancyRatePreviousMonth, averageOccupancyRateCurrentMonth);
            log.info("[THỐNG_KÊ_THÁNG][{}] Thay đổi tỷ lệ lấp đầy: {}%", requestId, occupancyChangePercentage);

            // Build DTO kết quả
            MonthlyFlightStatisticsDto result = MonthlyFlightStatisticsDto.builder()
                    .year(year)
                    .month(month)
                    .totalFlightsCurrentMonth(totalFlightsCurrentMonth)
                    .totalBookingsCurrentMonth(totalBookingsCurrentMonth)
                    .totalRevenueCurrentMonth(totalRevenueCurrentMonth)
                    .averageOccupancyRateCurrentMonth(averageOccupancyRateCurrentMonth)
                    .totalFlightsPreviousMonth(totalFlightsPreviousMonth)
                    .totalBookingsPreviousMonth(totalBookingsPreviousMonth)
                    .totalRevenuePreviousMonth(totalRevenuePreviousMonth)
                    .averageOccupancyRatePreviousMonth(averageOccupancyRatePreviousMonth)
                    .flightsChangePercentage(flightsChangePercentage)
                    .bookingsChangePercentage(bookingsChangePercentage)
                    .revenueChangePercentage(revenueChangePercentage)
                    .occupancyChangePercentage(occupancyChangePercentage)
                    .build();

            log.info("[THỐNG_KÊ_THÁNG][THÀNH_CÔNG] requestId={}, kết_quả={}", requestId, result);
            return result;

        } catch (Exception e) {
            log.error("[THỐNG_KÊ_THÁNG][LỖI] requestId={}, năm={}, tháng={}, lỗi={}, chi_tiết={}",
                    requestId, year, month, e.getMessage(), e);
            throw e;
        }
    }

    private Double calculateAverageOccupancyRate(Integer year, Integer month) {
        try {
            Double avg = flightDAO.getAverageOccupancyRateByMonth(year, month);
            return avg != null ? avg : 0.0;
            
        } catch (Exception e) {
            log.error("CALCULATE_OCCUPANCY_RATE_FAILED - year: {}, month: {}, error: {}", year, month, e.getMessage(), e);
            return 0.0;
        }
    }
    
    private Double calculatePercentageChange(Double previousValue, Double currentValue) {
        if (previousValue == null || previousValue == 0) {
            return currentValue != null && currentValue > 0 ? 100.0 : 0.0;
        }
        return ((currentValue - previousValue) / previousValue) * 100.0;
    }
    
    private Double calculatePercentageChange(Long previousValue, Long currentValue) {
        if (previousValue == null || previousValue == 0) {
            return currentValue != null && currentValue > 0 ? 100.0 : 0.0;
        }
        return ((currentValue - previousValue) * 100.0) / previousValue;
    }

    @Override
    public List<BookingByDestinationDto> getBookingsByDestination(Integer year, Integer month) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_BOOKINGS_BY_DESTINATION_REQUEST - RequestId: {}, year: {}, month: {}", requestId, year, month);
        
        try {
            // Sử dụng JPA query tối ưu thay vì findAll().stream()
            List<Object[]> results = flightBookingDAO.getBookingsByDestinationOptimized(year, month);
            
            // Chuyển đổi kết quả thành DTO
            List<BookingByDestinationDto> result = results.stream()
                    .map(row -> BookingByDestinationDto.builder()
                            .destination((String) row[0])
                            .bookingCount(((Number) row[1]).longValue())
                            .build())
                    .collect(Collectors.toList());

            log.info("GET_BOOKINGS_BY_DESTINATION_SUCCESS - RequestId: {}, year: {}, month: {}, count: {}", 
                    requestId, year, month, result.size());
            return result;

        } catch (Exception e) {
            log.error("GET_BOOKINGS_BY_DESTINATION_FAILED - RequestId: {}, year: {}, month: {}, error: {}", 
                    requestId, year, month, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<RevenueBySeatClassDto> getRevenueBySeatClass(Integer year, Integer month) {
        String requestId = UUID.randomUUID().toString();
        log.info("GET_REVENUE_BY_SEAT_CLASS_REQUEST - RequestId: {}, year: {}, month: {}", requestId, year, month);
        
        try {
            // Sử dụng JPA query tối ưu thay vì findAll().stream()
            List<Object[]> results = flightBookingDAO.getRevenueBySeatClassOptimized(year, month);
            
            // Chuyển đổi kết quả thành DTO
            List<RevenueBySeatClassDto> result = results.stream()
                    .map(row -> RevenueBySeatClassDto.builder()
                            .seatClass((String) row[0])
                            .revenue(((Number) row[1]).doubleValue())
                            .build())
                    .collect(Collectors.toList());

            log.info("GET_REVENUE_BY_SEAT_CLASS_SUCCESS - RequestId: {}, year: {}, month: {}, count: {}", 
                    requestId, year, month, result.size());
            return result;

        } catch (Exception e) {
            log.error("GET_REVENUE_BY_SEAT_CLASS_FAILED - RequestId: {}, year: {}, month: {}, error: {}", 
                    requestId, year, month, e.getMessage(), e);
            throw e;
        }
    }
}
