package backend.backend.service.busService;

import backend.backend.dto.BusDTO.CreateRouteRequest;
import backend.backend.dto.BusDTO.RouteResponse;
import backend.backend.dto.BusDTO.UpdateRouteRequest;
import backend.backend.entity.Route;

import java.util.List;
import java.util.Optional;

public interface RouteService {


    List<Route> findAllRoute(); // Sẽ trả về Route entity với Location được fetch eager
    Optional<Route> findRouteById(Integer id); // Sẽ trả về Route entity với Location được fetch eager
    Route createRoute(CreateRouteRequest routeDTO);
    Route updateRoute(Integer id, UpdateRouteRequest routeDTO);
    void deleteRoute(Integer id);

    List<RouteResponse> findAllRouteDetails(); // THÊM: Phương thức mới trả về List<RouteResponse>
    Optional<RouteResponse> findRouteDetailsById(Integer id); // THÊM: Phương thức mới trả về Optional<RouteResponse>

}
