package backend.backend.controller.BusController;

import backend.backend.dto.BusDTO.*;
import backend.backend.implement.busImplement.BusSeatServiceImpl;
import backend.backend.service.busService.BusSeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BusSeatResolver {



        private final BusSeatService busSeatService;

        // === QUERIES ===

        @QueryMapping
        public List<BusSeatResponse> getBusSeats(@Argument Integer busSlotId) {
            return busSeatService.getBusSeats(busSlotId);
        }

        @QueryMapping
        public BusSeatResponse getBusSeat(@Argument Integer id) {
            return busSeatService.getBusSeatById(id);
        }

        @QueryMapping
        public List<BusSeatResponse> getAvailableSeats(@Argument Integer busSlotId) {
            return busSeatService.getAvailableSeats(busSlotId);
        }

        @QueryMapping
        public List<BusSeatResponse> getBookedSeats(@Argument Integer busSlotId) {
            return busSeatService.getBookedSeats(busSlotId);
        }

        @QueryMapping
        public BusSeatResponse getSeatByNumber(@Argument Integer busSlotId, @Argument String seatNumber) {
            return busSeatService.getSeatByNumber(busSlotId, seatNumber);
        }

        @QueryMapping
        public BusSlotResponse getBusSlotWithSeats(@Argument Integer id) {
            return busSeatService.getBusSlotWithSeats(id);
        }

        // === MUTATIONS ===

        @MutationMapping
        public BusSeatResponse createBusSeat(@Argument CreateBusSeatInput input) {
            return busSeatService.createBusSeat(input);
        }

        @MutationMapping
        public BusSeatResponse updateBusSeat(@Argument UpdateBusSeatInput input) {
            return busSeatService.updateBusSeat(input);
        }

        @MutationMapping
        public Boolean deleteBusSeat(@Argument Integer id) {
            return busSeatService.deleteBusSeat(id);
        }

        @MutationMapping
        public BusSeatResponse bookSeat(@Argument BookSeatInput input) {
            return busSeatService.bookSeat(input);
        }

        @MutationMapping
        public BusSeatResponse releaseSeat(@Argument ReleaseSeatInput input) {
            return busSeatService.releaseSeat(input);
        }

        @MutationMapping
        public List<BusSeatResponse> bookMultipleSeats(@Argument List<Integer> seatIds, @Argument Integer userId) {
            return busSeatService.bookMultipleSeats(seatIds, userId);
        }

        @MutationMapping
        public List<BusSeatResponse> releaseMultipleSeats(@Argument List<Integer> seatIds, @Argument Integer userId) {
            return busSeatService.releaseMultipleSeats(seatIds, userId);
        }
    }

