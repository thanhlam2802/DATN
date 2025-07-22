package backend.backend.controller.BusController;

import backend.backend.dto.BusDTO.BusResponse;
import backend.backend.dto.BusDTO.CreateBusRequest;
import backend.backend.dto.BusDTO.UpdateBusRequest;
// import backend.backend.entity.Bus; // Không cần import Bus entity nếu chỉ làm việc với DTO
// import backend.backend.implement.busImplement.BusServiceImpl; // Không cần import implement class trực tiếp
import backend.backend.service.busService.BusService; // Đảm bảo import đúng service interface
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Giữ CrossOrigin nếu bạn muốn cho phép tất cả các origin
public class BusResolver {

    private final BusService busService; // Tiêm BusService vào

    @QueryMapping
    public List<BusResponse> findAllBuses() {
        return busService.findAllBuses();
    }


    @QueryMapping
    public BusResponse findBusById(@Argument Integer id) {
        // Tùy chọn: Thêm EntityNotFoundException nếu muốn trả về lỗi GraphQL nếu không tìm thấy
        return busService.findBusById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Không tìm thấy xe bus với ID: " + id));
    }

    @QueryMapping
    public List<BusResponse> findBusesByOwnerId(@Argument Integer ownerId) {
        return busService.findBusesByOwnerId(ownerId);
    }

    // THÊM CÁC PHƯƠNG THỨC MỚI ĐỂ ĐỒNG BỘ VỚI BUSSERVICE
    @QueryMapping
    public List<BusResponse> findBusesByCategoryId(@Argument Integer categoryId) {
        return busService.findBusesByCategoryId(categoryId);
    }

    @QueryMapping
    public List<BusResponse> searchBuses(@Argument String name, @Argument String licensePlate) {
        return busService.searchBuses(name, licensePlate);
    }

    @MutationMapping
    public BusResponse createBus(@Argument CreateBusRequest input) {
        return busService.createBus(input);
    }

    @MutationMapping
    public BusResponse updateBus(@Argument Integer id, @Argument UpdateBusRequest input) {
        return busService.updateBus(id, input);
    }

    @MutationMapping
    public Boolean deleteBus(@Argument Integer id) {
        busService.deleteBus(id);
        return true; // Trả về true nếu xóa thành công, exception sẽ được xử lý nếu không tìm thấy
    }

}
