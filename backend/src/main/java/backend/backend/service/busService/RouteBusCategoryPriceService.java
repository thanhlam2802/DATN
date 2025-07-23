package backend.backend.service.busService;

import backend.backend.dto.BusDTO.CreateRouteBusCategoryPriceRequest;
import backend.backend.dto.BusDTO.RouteBusCategoryPriceResponse;
import backend.backend.dto.BusDTO.UpdateRouteBusCategoryPriceRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RouteBusCategoryPriceService {
    RouteBusCategoryPriceResponse createRouteBusCategoryPrice(CreateRouteBusCategoryPriceRequest request);
    RouteBusCategoryPriceResponse updateRouteBusCategoryPrice(Integer id, UpdateRouteBusCategoryPriceRequest request);
    void deleteRouteBusCategoryPrice(Integer id);
    Optional<RouteBusCategoryPriceResponse> findRouteBusCategoryPriceById(Integer id);
    List<RouteBusCategoryPriceResponse> findAllRouteBusCategoryPrices();
    Optional<RouteBusCategoryPriceResponse> findActiveRouteBusCategoryPrice(Integer routeId, Integer busCategoryId, LocalDate date);
}
