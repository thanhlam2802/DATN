package backend.backend.controller.BusController;

import backend.backend.dto.BusDTO.CreateBusCategoryRequest;
import backend.backend.dto.BusDTO.UpdateBusCategoryRequest;
import backend.backend.entity.BusCategory;
import backend.backend.service.busService.BusCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BusCategoryResolver {

    private final BusCategoryService busCategoryService;


    // --- Query Mappings ---
    @QueryMapping
    public BusCategory getBusCategoryById(@Argument Integer id) {
        return busCategoryService.getBusCategoryById(id);
    }

    @QueryMapping
    public List<BusCategory> getAllBusCategories() {
        return busCategoryService.getAllBusCategories();
    }

    // ✅ RESTORED: BusCategory thuộc về owner
    @QueryMapping
    public List<BusCategory> getBusCategoriesByOwnerId(@Argument Integer ownerId) {
        return busCategoryService.getBusCategoriesByOwnerId(ownerId);
    }

    // --- Mutation Mappings ---
    @MutationMapping
    public BusCategory createBusCategory(@Argument CreateBusCategoryRequest input) {
        return busCategoryService.createBusCategory(input);
    }

    @MutationMapping
    public BusCategory updateBusCategory(@Argument Integer id, @Argument UpdateBusCategoryRequest input) {
        return busCategoryService.updateBusCategory(id, input);
    }

    @MutationMapping
    public Boolean deleteBusCategory(@Argument Integer id) {
        busCategoryService.deleteBusCategory(id);
        return true;
    }

    // ✅ UNIFIED: Schema mapping hỗ trợ cả BusCategory entity và BusCategoryResponse DTO
    @SchemaMapping(typeName = "BusCategory", field = "ownerId")
    public Integer ownerId(Object source) {
        if (source instanceof BusCategory) {
            BusCategory busCategory = (BusCategory) source;
            return busCategory.getOwner() != null ? busCategory.getOwner().getId() : null;
        } else if (source instanceof backend.backend.dto.BusDTO.BusCategoryResponse) {
            backend.backend.dto.BusDTO.BusCategoryResponse categoryResponse = (backend.backend.dto.BusDTO.BusCategoryResponse) source;
            return categoryResponse.getOwnerId();
        }
        return null; // Fallback nếu không match type nào
    }
}