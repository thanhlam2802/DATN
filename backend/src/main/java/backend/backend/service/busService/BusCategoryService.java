package backend.backend.service.busService;

import backend.backend.dto.BusDTO.CreateBusCategoryRequest;
import backend.backend.dto.BusDTO.UpdateBusCategoryRequest;
import backend.backend.entity.BusCategory;

import java.util.List;

public interface BusCategoryService {

    BusCategory createBusCategory(CreateBusCategoryRequest dto);
    BusCategory updateBusCategory(Integer id, UpdateBusCategoryRequest dto);
    void deleteBusCategory(Integer id);
    BusCategory getBusCategoryById(Integer id);
    List<BusCategory> getAllBusCategories();

    // ❌ REMOVED: BusCategory là global cho tất cả nhà xe
    // Chỉ cần getAllBusCategories() là đủ

}
