package backend.backend.service.busService;

import backend.backend.dao.Bus.BusDAO;
import backend.backend.dao.Bus.RouteDAO;
import backend.backend.dto.BusDTO.CreateRouteDTO;
import backend.backend.dto.BusDTO.UpdateRouteDTO;
import backend.backend.entity.Route;

import java.util.List;
import java.util.Optional;

public interface RouteService {

    List<Route> findAllRoute();
    Optional<Route> findRouteById(Integer id);
    Route createRoute(CreateRouteDTO routeDTO);
    Route updateRoute(UpdateRouteDTO routeDTO);
    void deleteRoute(Integer id);
    Optional<Route> findByOriginAndDestination (String origin, String destination);
}
