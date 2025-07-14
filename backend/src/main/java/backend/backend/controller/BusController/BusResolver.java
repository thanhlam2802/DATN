package backend.backend.controller.BusController;

import backend.backend.dto.BusDTO.CreateBusDTO;
import backend.backend.dto.BusDTO.UpdateBusDTO;
import backend.backend.entity.Bus;
import backend.backend.implement.busImplement.BusServiceImpl;
import backend.backend.service.busService.BusService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BusResolver {

    private final BusService busService; // Tiêm BusService vào


    @QueryMapping
    public List<Bus> findAllBuses() {
        return busService.findAllBuses();
    }

    @QueryMapping
    public Bus findBusById(@Argument Integer id) {
        // Tùy chọn: Thêm EntityNotFoundException nếu muốn trả về lỗi GraphQL nếu không tìm thấy
        return busService.findBusById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Không tìm thấy xe bus với ID: " + id));
    }

    @QueryMapping
    public List<Bus> findBusByOwner_Id(@Argument Integer ownerId) {
        return busService.findBusesByOwner_Id(ownerId);
    }

    @MutationMapping
    public Bus createBus(@Argument CreateBusDTO input) {
        return busService.createBus(input);
    }

    @MutationMapping
    public Bus updateBus(@Argument Integer id, @Argument UpdateBusDTO input) {
        return busService.updateBus(id, input);
    }

    @MutationMapping
    public Boolean deleteBus(@Argument Integer id) {
        busService.deleteBus(id);
        return true; // Trả về true nếu xóa thành công, exception sẽ được xử lý nếu không tìm thấy
    }


}