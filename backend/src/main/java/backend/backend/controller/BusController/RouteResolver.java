package backend.backend.controller.BusController;

import backend.backend.dto.BusDTO.CreateRouteDTO;
import backend.backend.dto.BusDTO.UpdateRouteDTO;
import backend.backend.entity.Route;
import backend.backend.implement.busImplement.RouteServiceImpl;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class RouteResolver {

    private final RouteServiceImpl routeService;

    @QueryMapping
    public List<Route> findAllRoutes(){
        return routeService.findAllRoute();
    }

    @MutationMapping
    public Route createRoute(@Argument CreateRouteDTO input){
        return routeService.createRoute(input);
    }

    @MutationMapping
    public Route updateRoute(@Argument UpdateRouteDTO input) {
        return routeService.updateRoute(input);
    }

    @MutationMapping
    public boolean deleteRoute(@Argument Integer id) {
         routeService.deleteRoute(id);
         return true;
    }

    @QueryMapping
    public Optional<Route> findByOriginAndDestination (@Argument String origin,@Argument String destination) {
        return routeService.findByOriginAndDestination(origin, destination);
    }

    @QueryMapping
    public Optional<Route> findRouteById(@Argument Integer id) {
        return routeService.findRouteById(id);
    }


}
