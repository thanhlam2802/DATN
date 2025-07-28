package backend.backend.controller.BusController;

import backend.backend.dto.BusDTO.CreateRouteRequest;
import backend.backend.dto.BusDTO.UpdateRouteRequest;
import backend.backend.entity.Route;
import backend.backend.service.busService.RouteService; // Import interface RouteService
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RouteResolver {

    private final RouteService routeService; // Sử dụng interface RouteService

    @QueryMapping
    public List<Route> findAllRoutes(){ // Trả về List<Route> entity
        return routeService.findAllRoute(); // Gọi phương thức trả về Route entity
    }

    @MutationMapping
    public Route createRoute(@Argument CreateRouteRequest input){ // Trả về Route entity
        return routeService.createRoute(input); // Trả về Route entity trực tiếp
    }

    @MutationMapping
    public Route updateRoute(@Argument Integer id, @Argument UpdateRouteRequest input) { // Trả về Route entity
        return routeService.updateRoute(id, input); // Trả về Route entity trực tiếp
    }

    @MutationMapping
    public boolean deleteRoute(@Argument Integer id) {
        routeService.deleteRoute(id);
        return true;
    }

    @QueryMapping
    public Optional<Route> findRouteById(@Argument Integer id) { // Trả về Optional<Route> entity
        return routeService.findRouteById(id); // Gọi phương thức trả về Route entity
    }
}