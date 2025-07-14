package backend.backend.implement.busImplement;

import backend.backend.dao.Bus.BusDAO;
import backend.backend.dto.BusDTO.CreateBusDTO;
import backend.backend.dto.BusDTO.UpdateBusDTO;
import backend.backend.entity.Bus;
import backend.backend.service.busService.BusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BusServiceImpl implements BusService {

    private final BusDAO busDAO;

    @Override
    public List<Bus> findAlBuses() {
        return busDAO.findAll();
    }

    @Override
    public Optional<Bus> findBusById(Integer id) {
        return busDAO.findById(id);
    }

    @Override
    public Bus createBus(CreateBusDTO busDTO) {

    }

    @Override
    public Bus updateBus(UpdateBusDTO busDTO) {
        return null;
    }

    @Override
    public void deleteBus(Integer id) {

    }


}
