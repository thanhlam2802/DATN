package backend.backend.controller.BusController;

import backend.backend.dto.BusDTO.CreateRouteBusCategoryPriceRequest;
import backend.backend.dto.BusDTO.CreateRouteBusCategoryPriceInput; // ADD: Import the correct input type
import backend.backend.dto.BusDTO.RouteBusCategoryPriceResponse;
import backend.backend.dto.BusDTO.UpdateRouteBusCategoryPriceRequest;
import backend.backend.service.busService.RouteBusCategoryPriceService;
import graphql.GraphQLException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDate;
import java.util.List;

@Controller
@CrossOrigin (origins = "*")
@RequiredArgsConstructor
public class RouteBusCategoryPriceResolver {

    private final RouteBusCategoryPriceService routeBusCategoryPriceService;

    @QueryMapping
    public List<RouteBusCategoryPriceResponse> findAllRouteBusCategoryPrices() {
        return routeBusCategoryPriceService.findAllRouteBusCategoryPrices();
    }

    @QueryMapping
    public RouteBusCategoryPriceResponse findRouteBusCategoryPriceById(@Argument Integer id) {
        return routeBusCategoryPriceService.findRouteBusCategoryPriceById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy quy tắc giá với ID: " + id));
    }

    @QueryMapping
    public RouteBusCategoryPriceResponse findActiveRouteBusCategoryPrice(
            @Argument Integer routeId,
            @Argument Integer busCategoryId,
            @Argument String date) { // Nhận String
        LocalDate parsedDate = LocalDate.parse(date);
        return routeBusCategoryPriceService.findActiveRouteBusCategoryPrice(routeId, busCategoryId, parsedDate)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy quy tắc giá hiệu lực cho tuyến đường " + routeId + " và loại xe " + busCategoryId + " vào ngày " + date));
    }

    @MutationMapping
    public RouteBusCategoryPriceResponse createRouteBusCategoryPrice(@Argument CreateRouteBusCategoryPriceInput input) {
        try {
            // Convert CreateRouteBusCategoryPriceInput to CreateRouteBusCategoryPriceRequest
            CreateRouteBusCategoryPriceRequest request = CreateRouteBusCategoryPriceRequest.builder()
                    .routeId(input.routeId())
                    .busCategoryId(input.busCategoryId())
                    .basePrice(input.basePrice())
                    .promotionPrice(input.promotionPrice())
                    .validFrom(LocalDate.parse(input.validFrom()))  // Convert String to LocalDate
                    .validTo(LocalDate.parse(input.validTo()))      // Convert String to LocalDate
                    .notes(input.notes())
                    .build();
            
            return routeBusCategoryPriceService.createRouteBusCategoryPrice(request);
        } catch (IllegalArgumentException e) {
            throw new GraphQLException(e.getMessage());
        }
    }

    @MutationMapping
    public RouteBusCategoryPriceResponse updateRouteBusCategoryPrice(@Argument Integer id, @Argument UpdateRouteBusCategoryPriceRequest input) {
        try {
            return routeBusCategoryPriceService.updateRouteBusCategoryPrice(id, input);
        } catch (IllegalArgumentException e) {
            throw new GraphQLException(e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new GraphQLException("Không tìm thấy quy tắc giá để cập nhật: " + e.getMessage());
        }
    }

    @MutationMapping
    public Boolean deleteRouteBusCategoryPrice(@Argument Integer id) {
        try {
            routeBusCategoryPriceService.deleteRouteBusCategoryPrice(id);
            return true;
        } catch (EntityNotFoundException e) {
            throw new GraphQLException("Không tìm thấy quy tắc giá để xóa: " + e.getMessage());
        }
    }

}
