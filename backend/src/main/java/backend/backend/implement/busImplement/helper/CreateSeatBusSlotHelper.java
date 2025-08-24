package backend.backend.implement.busImplement.helper;

import backend.backend.entity.Bus;
import backend.backend.entity.BusSeat;
import backend.backend.entity.BusSlot;
import backend.backend.entity.enumBus.BusSeatType;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class CreateSeatBusSlotHelper {

    /**
     * Tạo danh sách ghế cho một BusSlot
     */
    public List<BusSeat> createSeatsForBusSlot(BusSlot busSlot, Integer totalSeats) {
        List<BusSeat> seats = new ArrayList<>();

        Bus bus = busSlot.getBus();
        String busName = bus.getName().toLowerCase();
        BusSeatType seatType = determineSeatType(busName);

        log.info("Tạo {} ghế cho xe '{}' với loại ghế: {}",
                totalSeats, bus.getName(), seatType);

        for (int i = 1; i <= totalSeats; i++) {
            BusSeat seat = new BusSeat();

            // Liên kết với BusSlot
            seat.setBusSlot(busSlot);

            // Trạng thái mặc định
            seat.setIsBooked(false);

            // Giá ghế
            seat.setPrice(busSlot.getPrice());

            // Loại ghế
            seat.setSeatType(seatType);

            // Tạo mã ghế theo loại xe và layout dãy
            seat.setSeatNumber(generateSeatNumber(i, seatType, totalSeats));

            seats.add(seat);
        }

        log.info("Đã tạo {} ghế với layout: {}", totalSeats, seatType);
        return seats;
    }

    /**
     * Xác định loại xe dựa trên tên xe
     */
    private BusSeatType determineSeatType(String busName) {
        if (busName.contains("giường đôi") || busName.contains("double room")) {
            return BusSeatType.BED_DOUBLE_ROOM;
        } else if (busName.contains("giường") || busName.contains("bed")) {
            return BusSeatType.BED_SINGLE_ROOM;
        } else if (busName.contains("shuttle") || busName.contains("trung")) {
            return BusSeatType.SHUTTLE_STANDARD;
        } else {
            // Mặc định là xe giường phòng đơn nếu không xác định được
            return BusSeatType.BED_SINGLE_ROOM;
        }
    }

    /**
     * Tạo mã ghế theo loại xe với layout dãy cụ thể
     */
    private String generateSeatNumber(int seatIndex, BusSeatType seatType, int totalSeats) {
        switch (seatType) {
            case BED_SINGLE_ROOM:
                return generateBedSingleSeatNumber(seatIndex, totalSeats);
            case BED_DOUBLE_ROOM:
                return generateBedDoubleSeatNumber(seatIndex, totalSeats);
            case SHUTTLE_STANDARD:
                return generateShuttleSeatNumber(seatIndex, totalSeats);
            default:
                return String.valueOf(seatIndex);
        }
    }

    /**
     * Mã ghế cho xe giường phòng đơn - 3 dãy ABC
     * Layout: 3 dãy, mỗi dãy có số ghế bằng nhau
     * Ví dụ: 45 ghế = 3 dãy × 15 ghế/dãy
     */
    private String generateBedSingleSeatNumber(int seatIndex, int totalSeats) {
        int seatsPerRow = (int) Math.ceil((double) totalSeats / 3); // Chia đều cho 3 dãy, làm tròn lên
        int row = (seatIndex - 1) / seatsPerRow;
        int col = (seatIndex - 1) % seatsPerRow;

        // Đảm bảo không vượt quá 3 dãy (A, B, C)
        if (row >= 3) {
            row = 2; // Dãy cuối cùng (C)
        }

        char rowChar = (char) ('A' + row); // A, B, C
        return "" + rowChar + (col + 1);
    }

    /**
     * Mã ghế cho xe giường phòng đôi - 2 dãy AB
     * Layout: 2 dãy, mỗi dãy có số ghế bằng nhau
     * Ví dụ: 30 ghế = 2 dãy × 15 ghế/dãy
     */
    private String generateBedDoubleSeatNumber(int seatIndex, int totalSeats) {
        int seatsPerRow = (int) Math.ceil((double) totalSeats / 2); // Chia đều cho 2 dãy, làm tròn lên
        int row = (seatIndex - 1) / seatsPerRow;
        int col = (seatIndex - 1) % seatsPerRow;

        // Đảm bảo không vượt quá 2 dãy (A, B)
        if (row >= 2) {
            row = 1; // Dãy cuối cùng (B)
        }

        char rowChar = (char) ('A' + row); // A, B
        return "" + rowChar + (col + 1);
    }

    /**
     * Mã ghế cho xe shuttle tiêu chuẩn - 1 dãy A
     * Layout: 1 dãy, tất cả ghế trong dãy A
     * Ví dụ: 16 ghế = 1 dãy × 16 ghế
     */
    private String generateShuttleSeatNumber(int seatIndex, int totalSeats) {
        // Tất cả ghế đều thuộc dãy A
        return "A" + seatIndex;
    }


}