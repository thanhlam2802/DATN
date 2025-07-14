package backend.backend.service.busService;

import backend.backend.dto.BusDTO.CreateBusCategoryDTO;
import backend.backend.dto.BusDTO.UpdateBusCategoryDTO;
import backend.backend.entity.BusCategory;

import java.util.List;

public interface BusCategoryService {

    BusCategory createBusCategory(CreateBusCategoryDTO dto);
    BusCategory updateBusCategory(Integer id, UpdateBusCategoryDTO dto);
    void deleteBusCategory(Integer id);
    BusCategory getBusCategoryById(Integer id);
    List<BusCategory> getAllBusCategories();

}
