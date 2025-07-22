package backend.backend.implement.busImplement;

import backend.backend.dao.Bus.RouteDAO;
import backend.backend.dto.BusDTO.CreateRouteRequest;
import backend.backend.dto.BusDTO.UpdateRouteRequest;
import backend.backend.entity.Route;
import backend.backend.service.busService.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final RouteDAO routeDAO;

    @Override
    public List<Route> findAllRoute() {
        return routeDAO.findAll();
    }

    @Override
    public Optional<Route> findRouteById(Integer id) {
        return routeDAO.findById(id);
    }

    @Override
    @Transactional
    public Route createRoute(CreateRouteRequest routeDTO) {
        Route route = new Route();
        route.setDestination(routeDTO.destination());
        route.setDistanceKm(routeDTO.distanceKm());
        route.setEstimatedDurationMinutes(routeDTO.estimatedDurationMinutes());
        route.setOrigin(routeDTO.origin());

        return routeDAO.save(route);
    }

    @Override
    public Route updateRoute(Integer id, UpdateRouteRequest routeDTO) {
        // Tìm Route hiện có theo ID
        Route existingRoute = routeDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Route with ID " + id + " not found."));

        if (routeDTO.origin() != null) {
            existingRoute.setOrigin(routeDTO.origin());
        }
        if (routeDTO.destination() != null) {
            existingRoute.setDestination(routeDTO.destination());
        }
        if (routeDTO.distanceKm() != null) {
            existingRoute.setDistanceKm(routeDTO.distanceKm());
        }
        if (routeDTO.estimatedDurationMinutes() != null) {
            existingRoute.setEstimatedDurationMinutes(routeDTO.estimatedDurationMinutes());
        }

        return routeDAO.save(existingRoute);
    }

    @Override
    public void deleteRoute(Integer id) {
        Optional<Route> existingRoute = routeDAO.findById(id);
        if (existingRoute.isPresent()) {
            routeDAO.delete(existingRoute.get());
        } else {
            throw new RuntimeException("Route with ID " + id + " not found.");
        }
    }

    @Override
    public Optional<Route> findByOriginAndDestination( String origin, String destination) {
        return routeDAO.findByOriginAndDestination(origin, destination);
    }
}
