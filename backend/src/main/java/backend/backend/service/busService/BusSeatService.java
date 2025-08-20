package backend.backend.service.busService;

import backend.backend.dto.BusDTO.*;

import java.util.List;

public interface BusSeatService {
    List<BusSeatResponse> getBusSeats(Integer busSlotId);
    BusSeatResponse getBusSeatById(Integer id);
    List<BusSeatResponse> getAvailableSeats(Integer busSlotId);
    List<BusSeatResponse> getBookedSeats(Integer busSlotId);
    BusSeatResponse getSeatByNumber(Integer busSlotId, String seatNumber);
    BusSlotResponse getBusSlotWithSeats(Integer id);

    // === MUTATIONS ===
    BusSeatResponse createBusSeat(CreateBusSeatInput input);
    BusSeatResponse updateBusSeat(UpdateBusSeatInput input);
    Boolean deleteBusSeat(Integer id);

    // === BOOKING OPERATIONS ===
    BusSeatResponse bookSeat(BookSeatInput input);
    BusSeatResponse releaseSeat(ReleaseSeatInput input);
    List<BusSeatResponse> bookMultipleSeats(List<Integer> seatIds, Integer userId);
    List<BusSeatResponse> releaseMultipleSeats(List<Integer> seatIds, Integer userId);
}