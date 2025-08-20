package backend.backend.implement.busImplement;

import backend.backend.dao.Bus.BusSlotDAO;
import backend.backend.dao.Bus.LocationDAO;
import backend.backend.dao.Bus.RouteBusCategoryPriceDAO;
import backend.backend.dao.Bus.RouteDAO;
import backend.backend.dao.UserDAO;
import backend.backend.dto.BusDTO.CreateLocationInput;
import backend.backend.dto.BusDTO.CreateRouteRequest;
import backend.backend.dto.BusDTO.RouteResponse; // Đảm bảo đã import
import backend.backend.dto.BusDTO.UpdateRouteRequest;
import backend.backend.entity.Location; // Đảm bảo đã import
import backend.backend.entity.Route;
import backend.backend.entity.User;
import backend.backend.service.busService.RouteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // THÊM: Import Collectors cho Stream API

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {


        private final RouteDAO routeDAO;
        private final LocationDAO locationDAO; // Đã inject LocationDAO
        private final BusSlotDAO busSlotDAO;
        private final RouteBusCategoryPriceDAO  routeBusCategoryPriceDAO;
        private final UserDAO userDAO; // ✅ THÊM MỚI: Để validate owner

    // Các phương thức trả về Route entity nguyên bản (cho GraphQL Resolver)
    @Override
    @Transactional(readOnly = true) // Đảm bảo phương thức là transactional để fetch eager Location
    public List<Route> findAllRoute() {
        return routeDAO.findAllWithLocations(); // Sử dụng phương thức DAO đã viết để fetch eager Location
    }

    @Override
    @Transactional(readOnly = true) // Đảm bảo phương thức là transactional để fetch eager Location
    public Optional<Route> findRouteById(Integer id) {
        return routeDAO.findByIdWithLocations(id); // Sử dụng phương thức DAO đã viết để fetch eager Location
    }

    @Override
    @Transactional
    public Route createRoute(CreateRouteRequest routeDTO) {
        Route route = new Route();

        // Sử dụng phương thức helper để tìm hoặc tạo Location
        Location originLocation = findOrCreateLocation(routeDTO.originLocationDetails());
        Location destinationLocation = findOrCreateLocation(routeDTO.destinationLocationDetails());

        // ✅ THÊM MỚI: Set owner cho Route
        User owner = userDAO.findById(routeDTO.ownerId())
                .orElseThrow(() -> new EntityNotFoundException("Owner with ID " + routeDTO.ownerId() + " not found."));

        route.setOriginLocation(originLocation);
        route.setDestinationLocation(destinationLocation);
        route.setOwner(owner);
        route.setDistanceKm(routeDTO.distanceKm());
        route.setEstimatedDurationMinutes(routeDTO.estimatedDurationMinutes());

        return routeDAO.save(route);
    }

    @Override
    @Transactional
    public Route updateRoute(Integer id, UpdateRouteRequest routeDTO) {
        // Sử dụng findByIdWithLocations để đảm bảo Location được tải eager
        Route existingRoute = routeDAO.findByIdWithLocations(id)
                .orElseThrow(() -> new EntityNotFoundException("Route with ID " + id + " not found."));

        if (routeDTO.originLocationDetails() != null) {
            Location newOriginLocation = findOrCreateLocation(routeDTO.originLocationDetails());
            existingRoute.setOriginLocation(newOriginLocation);
        }
        if (routeDTO.destinationLocationDetails() != null) {
            Location newDestinationLocation = findOrCreateLocation(routeDTO.destinationLocationDetails());
            existingRoute.setDestinationLocation(newDestinationLocation);
        }

        if (routeDTO.distanceKm() != null) {
            existingRoute.setDistanceKm(routeDTO.distanceKm());
        }
        if (routeDTO.estimatedDurationMinutes() != null) {
            existingRoute.setEstimatedDurationMinutes(routeDTO.estimatedDurationMinutes());
        }

        return routeDAO.save(existingRoute);
    }

    // Phương thức helper để tìm kiếm Location theo tên, hoặc tạo mới nếu chưa tồn tại
    @Transactional // Đảm bảo phương thức này cũng chạy trong transaction
    private Location findOrCreateLocation(CreateLocationInput locationInput) {
        Optional<Location> existingLocation = locationDAO.findByName(locationInput.name());

        if (existingLocation.isPresent()) {
            return existingLocation.get();
        } else {
            Location newLocation = new Location();
            newLocation.setName(locationInput.name());
            newLocation.setProvinceCity(locationInput.provinceCity());
            newLocation.setDistrict(locationInput.district());
            newLocation.setAddressDetails(locationInput.addressDetails());
            // Các trường createdAt/updatedAt sẽ được xử lý bởi @PrePersist trong Location entity
            return locationDAO.save(newLocation);
        }
    }

    @Override
    @Transactional
    public void deleteRoute(Integer id) {
        if (!routeDAO.existsById(id)) {
            throw new EntityNotFoundException("Không tìm thấy Route với ID: " + id);
        }
        routeBusCategoryPriceDAO.deleteByRoute_Id(id);
        busSlotDAO.deleteByRouteId(id);
        routeDAO.deleteById(id);
    }

    // --- CÁC PHƯƠNG THỨC TRẢ VỀ ROUTERESPONSE DTO ---
    // Phương thức này hiện tại sẽ trả về RouteResponse với Location object lồng ghép
    // nếu bạn vẫn muốn giữ RouteResponse cho các mục đích khác ngoài GraphQL hoặc cho REST API.
    @Override
    @Transactional(readOnly = true) // Đảm bảo transactional cho việc ánh xạ DTO
    public List<RouteResponse> findAllRouteDetails() {
        return routeDAO.findAllWithLocations().stream()
                .map(RouteResponse::new) // Sử dụng constructor của RouteResponse để ánh xạ
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true) // Đảm bảo transactional cho việc ánh xạ DTO
    public Optional<RouteResponse> findRouteDetailsById(Integer id) {
        return routeDAO.findByIdWithLocations(id)
                .map(RouteResponse::new); // Sử dụng constructor của RouteResponse để ánh xạ
    }
    
    // ✅ THÊM MỚI: Lấy routes theo ownerId
    @Override
    @Transactional(readOnly = true)
    public List<Route> getRoutesByOwnerId(Integer ownerId) {
        return routeDAO.findByOwnerId(ownerId);
    }
}

