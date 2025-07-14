package backend.backend.implement.busImplement;

import backend.backend.dao.Bus.BusCategoryDAO;
import backend.backend.dao.Bus.BusDAO;
import backend.backend.dao.UserDAO;
import backend.backend.dto.BusDTO.CreateBusDTO;
import backend.backend.dto.BusDTO.UpdateBusDTO;
import backend.backend.entity.Bus;
import backend.backend.entity.BusCategory;
import backend.backend.entity.User;
import backend.backend.repository.UserRepository;
import backend.backend.service.busService.BusService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class BusServiceImpl implements BusService {

    private final BusDAO busDAO;
    private final BusCategoryDAO busCategoryRepository;
    private final UserRepository userRepository;


    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("Người dùng chưa được xác thực hoặc không tìm thấy thông tin.");
        }

        String username = authentication.getName();
        return userRepository.findByName(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng: " + username));
    }


    @Override
    @Transactional
    public Bus createBus(CreateBusDTO dto) {
        // 1. Lấy thông tin danh mục xe
        BusCategory category = busCategoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục xe với ID: " + dto.categoryId()));

        // 2. Lấy người dùng hiện tại
        User owner = getCurrentAuthenticatedUser();

        // 3. Tiền xử lý dữ liệu đầu vào (trim & validate)
        String name = safeTrim(dto.name(), "Tên xe không được để trống.");
        String origin = safeTrim(dto.origin(), "Điểm đi không được để trống.");
        String destination = safeTrim(dto.destination(), "Điểm đến không được để trống.");

        // 4. Tạo mới xe
        Bus newBus = new Bus();
        newBus.setName(name);
        newBus.setOrigin(origin);
        newBus.setDestination(destination);
        newBus.setDepartureTime(dto.departureTime());
        newBus.setArrivalTime(dto.arrivalTime());
        newBus.setCategory(category);
        newBus.setOwner(owner);

        log.info("Bus Service Implement -> Tạo mới xe bởi: {}", owner.getName());

        return busDAO.save(newBus);
    }

    // Hàm hỗ trợ xử lý trim & kiểm tra chuỗi rỗng/null
    private String safeTrim(String raw, String errorMessage) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
        return raw.trim();
    }

    @Override
    @Transactional
    public Bus updateBus(Integer busId, UpdateBusDTO updateBusDTO) {
        Bus existingBus = busDAO.findById(busId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy xe bus với ID: " + busId));

        Optional.ofNullable(updateBusDTO.name())
                .map(name -> safeTrim(name, "Tên xe không được để trống."))
                .ifPresent(existingBus::setName);
        // updated_at sẽ được Hibernate tự động cập nhật nếu dùng @PreUpdate

        // Cập nhật các trường nếu DTO cung cấp giá trị mới
        Optional.ofNullable(updateBusDTO.name()).ifPresent(existingBus::setName);
        Optional.ofNullable(updateBusDTO.origin()).ifPresent(existingBus::setOrigin);
        Optional.ofNullable(updateBusDTO.destination()).ifPresent(existingBus::setDestination);
        Optional.ofNullable(updateBusDTO.departureTime()).ifPresent(existingBus::setDepartureTime);
        Optional.ofNullable(updateBusDTO.arrivalTime()).ifPresent(existingBus::setArrivalTime);

        // Cập nhật BusCategory nếu có categoryId mới
        Optional.ofNullable(updateBusDTO.categoryId()).ifPresent(newCategoryId -> {
            BusCategory category = busCategoryRepository.findById(newCategoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục xe với ID: " + newCategoryId));
            existingBus.setCategory(category);
        });

        // Cập nhật Owner (User) nếu có ownerId mới
        Optional.ofNullable(updateBusDTO.ownerId()).ifPresent(newOwnerId -> {
            User owner = userRepository.findById(newOwnerId)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chủ sở hữu với ID: " + newOwnerId));
            existingBus.setOwner(owner);
        });


        return busDAO.save(existingBus);
    }

    @Override
    @Transactional
    public void deleteBus(Integer busId) {
        if (!busDAO.existsById(busId)) {
            throw new EntityNotFoundException("Không tìm thấy xe bus với ID: " + busId);
        }
        busDAO.deleteById(busId);
    }

    @Override
    public List<Bus> findBusesByOwner_Id(Integer id) {

        busDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chủ sở hữu với ID: " + id));

        return busDAO.findBusByOwner_Id(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Bus> findBusById(Integer busId) {
        return busDAO.findById(busId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bus> findAllBuses() {
        return busDAO.findAll();
    }

}
