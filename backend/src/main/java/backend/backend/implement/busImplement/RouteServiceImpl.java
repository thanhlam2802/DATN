package backend.backend.implement.busImplement;

import backend.backend.dao.Bus.BusSlotDAO;
import backend.backend.dao.Bus.LocationDAO;
import backend.backend.dao.Bus.RouteBusCategoryPriceDAO;
import backend.backend.dao.Bus.RouteDAO;
import backend.backend.dao.UserDAO;
import backend.backend.dto.BusDTO.CreateLocationInput;
import backend.backend.dto.BusDTO.CreateRouteRequest;
import backend.backend.dto.BusDTO.RouteResponse; // Đảm bảo đã import
import backend.backend.dto.BusDTO.RouteCard;
import backend.backend.dto.BusDTO.UpdateRouteRequest;
import backend.backend.entity.Location; // Đảm bảo đã import
import backend.backend.entity.Route;
import backend.backend.entity.User;
import backend.backend.service.busService.RouteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
    
    // ✅ THÊM MỚI: Lấy popular routes cho trang chủ - MOCK DATA ĐƠN GIẢN
    @Override
    @Transactional(readOnly = true)
    public List<RouteCard> getPopularRoutes(Integer limit) {
        // Tạo mock data cho demo nhanh
        List<RouteCard> mockRoutes = Arrays.asList(
            new RouteCard(1, "Vũng Tàu", "Hồ Chí Minh", "https://nld.mediacdn.vn/291774122806476800/2024/8/16/tp-65-1723817004792851519414.jpg", 
                         new java.math.BigDecimal("150000")),
            new RouteCard(2, "Hà Nội", "Hải Phòng", "https://static.vinwonders.com/production/2025/04/hai-phong-topbanner.jpg", 
                         new java.math.BigDecimal("200000")),
            new RouteCard(3, "Đà Nẵng", "Huế", "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1a/Ng%E1%BB%8D_M%C3%B4n_Hu%E1%BA%BF_-_NKS.jpg/960px-Ng%E1%BB%8D_M%C3%B4n_Hu%E1%BA%BF_-_NKS.jpg", 
                         new java.math.BigDecimal("120000")),
            new RouteCard(4, "Cần Thơ", "Hồ Chí Minh", "https://ik.imagekit.io/tvlk/blog/2021/11/dia-diem-du-lich-can-tho-cover.jpg?tr=q-70,c-at_max,w-500,h-250,dpr-2", 
                         new java.math.BigDecimal("180000")),
            new RouteCard(5, "Nha Trang", "Đà Lạt", "https://cdn.nhandan.vn/images/6e407d305cea747ceadbf81d9ed5d5614c5275b6497477e68beb293bf8c03a5fa0c142aa2a9708a43dc1b6d808bd3be2f30df6e68b0f816001601be460586338c2381094306107c33e4c998a37d06158/1-da-lat-mong-manh-man-suong-4611-737.jpg", 
                         new java.math.BigDecimal("160000")),
            new RouteCard(6, "Hà Nội", "Sapa", "https://thesinhtour.com/wp-content/uploads/2021/12/du-lich-sapa-1.jpg", 
                         new java.math.BigDecimal("250000")),
            new RouteCard(8, "Hồ Chí Minh", "Phan Thiết", "https://www.homecredit.vn/upload/01_du_lich_phan_thiet_giup_ban_len_day_cot_tinh_than_hieu_qua_8be4ef09f6.jpg", 
                         new java.math.BigDecimal("140000"))
        );
        
        int actualLimit = limit != null ? Math.min(limit, mockRoutes.size()) : Math.min(10, mockRoutes.size());
        return mockRoutes.subList(0, actualLimit);
    }
    
}

