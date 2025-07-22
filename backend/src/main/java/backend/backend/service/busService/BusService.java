package backend.backend.service.busService;

import backend.backend.dto.BusDTO.BusResponse;
import backend.backend.dto.BusDTO.BusSlotResponse;
import backend.backend.dto.BusDTO.CreateBusRequest;
import backend.backend.dto.BusDTO.UpdateBusRequest;
import backend.backend.entity.Bus;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface BusService {
    BusResponse createBus(CreateBusRequest dto);
    BusResponse updateBus(Integer busId, UpdateBusRequest updateBusRequest);
    void deleteBus(Integer busId);
    Optional<BusResponse> findBusById(Integer busId);
    List<BusResponse> findAllBuses();
    // Đã đổi tên từ findBusesByOwner_Id sang findBusesByOwnerId để nhất quán với schema và triển khai
    List<BusResponse> findBusesByOwnerId(Integer ownerId); // <-- Đã cập nhật
    List<BusResponse> findBusesByCategoryId(Integer categoryId); // <-- Thêm vào để đồng bộ với ServiceImpl
    List<BusResponse> searchBuses(String name, String licensePlate); // <-- Thêm vào để đồng bộ với ServiceImpl
}
