package backend.backend.service.busService;

import backend.backend.dto.BusDTO.CreateRouteRequest;
import backend.backend.dto.BusDTO.UpdateRouteRequest;
import backend.backend.entity.Route;

import java.util.List;
import java.util.Optional;

public interface RouteService {

    List<Route> findAllRoute();
    Optional<Route> findRouteById(Integer id);
    Route createRoute(CreateRouteRequest routeDTO);
    Route updateRoute(Integer id, UpdateRouteRequest routeDTO);
    void deleteRoute(Integer id);
    Optional<Route> findByOriginAndDestination (String origin, String destination);

}
