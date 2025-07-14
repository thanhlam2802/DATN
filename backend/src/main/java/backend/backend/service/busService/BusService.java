package backend.backend.service.busService;

import backend.backend.dto.BusDTO.CreateBusDTO;
import backend.backend.dto.BusDTO.UpdateBusDTO;
import backend.backend.entity.Bus;

import java.util.List;
import java.util.Optional;

public interface BusService {

    List<Bus> findAlBuses();
    Optional<Bus> findBusById(Integer id);
    Bus createBus(CreateBusDTO busDTO);
    Bus updateBus(Integer busId,UpdateBusDTO busDTO);
    void deleteBus(Integer id);

}
