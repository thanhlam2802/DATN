package backend.backend.controller.BusController;

import backend.backend.dto.BusDTO.CreateBusCategoryRequest;
import backend.backend.dto.BusDTO.UpdateBusCategoryRequest;
import backend.backend.entity.BusCategory;
import backend.backend.service.busService.BusCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
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
}