package backend.backend.implement.busImplement;

import backend.backend.dao.Bus.BusCategoryDAO;
import backend.backend.dao.Bus.BusDAO;
import backend.backend.dao.Bus.BusImageDAO;
import backend.backend.dao.ImageDAO;
import backend.backend.dto.BusDTO.BusImageResponse;
import backend.backend.dto.BusDTO.BusResponse;
import backend.backend.dto.BusDTO.CreateBusRequest;
import backend.backend.dto.BusDTO.ImageResponse;
import backend.backend.dto.BusDTO.UpdateBusRequest;
import backend.backend.entity.Bus;
import backend.backend.entity.BusCategory;
import backend.backend.entity.BusImage;
import backend.backend.entity.Image;
import backend.backend.entity.User;
import backend.backend.entity.BusImageId;
import backend.backend.repository.UserRepository;
import backend.backend.service.busService.BusService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset; // Thêm import cho ZoneOffset
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class BusServiceImpl implements BusService {

    private final BusDAO busDAO;
    private final BusCategoryDAO busCategoryDAO;
    private final UserRepository userRepository;
    private final ImageDAO imageRepository;
    private final BusImageDAO busImageRepository;

    // --- Helper Methods ---
    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("Người dùng chưa được xác thực hoặc không tìm thấy thông tin.");
        }
        String username = authentication.getName();
        return userRepository.findByName(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng: " + username));
    }

    private String safeTrim(String raw, String errorMessage) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
        return raw.trim();
    }

    // --- Convert Entity to DTO Methods ---
    private ImageResponse convertToImageResponse(Image image) {
        return new ImageResponse(
                image.getId(),
                image.getUrl(),
                image.getAltText(),
                // Chuyển đổi LocalDateTime từ Entity sang OffsetDateTime cho DTO
                image.getUploadedAt() != null ? image.getUploadedAt().atOffset(ZoneOffset.ofHours(7)) : null,
                image.getPublicId()
        );
    }

    private BusImageResponse convertToBusImageResponse(BusImage busImage) {
        return new BusImageResponse(
                busImage.getBus().getId(),
                busImage.getImage().getId(),
                convertToImageResponse(busImage.getImage()) // Chuyển đổi Image Entity thành ImageResponse DTO
        );
    }

    private BusResponse convertToBusResponse(Bus bus) {
        List<BusImageResponse> busImageResponses = new ArrayList<>();
        // Đảm bảo rằng collection busImages được truy cập và chuyển đổi
        // Nếu bus.getBusImages() là null (hiếm khi xảy ra với JPA collection),
        // hoặc là một PersistentBag rỗng, nó sẽ được xử lý đúng.
        if (bus.getBusImages() != null && !bus.getBusImages().isEmpty()) { // <-- Kiểm tra thêm isEmpty()
            busImageResponses = bus.getBusImages().stream()
                    .map(this::convertToBusImageResponse)
                    .collect(Collectors.toList());
        }
        // Nếu bus.getBusImages() là null hoặc rỗng, busImageResponses sẽ là ArrayList rỗng, không phải null.

        return new BusResponse(
                bus.getId(),
                bus.getName(),
                bus.getLicensePlate(),
                bus.getTotalSeats(),
                bus.getCategory() != null ? bus.getCategory().getId() : null,
                bus.getCategory() != null ? bus.getCategory().getName() : null,
                bus.getOwner() != null ? bus.getOwner().getId() : null,
                bus.getOwner() != null ? bus.getOwner().getName() : null,
                busImageResponses, // <-- Truyền List rỗng nếu không có ảnh, KHÔNG PHẢI NULL
                bus.getCreatedAt(),
                bus.getUpdatedAt()
        );
    }

    // --- Service Methods ---

    @Override
    @Transactional
    public BusResponse createBus(CreateBusRequest dto) {
        Integer categoryIdInt = Integer.parseInt(dto.categoryId());
        BusCategory category = busCategoryDAO.findById(categoryIdInt)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục xe với ID: " + dto.categoryId()));

        Integer ownerIdInt = Integer.parseInt(dto.ownerId());
        User owner = userRepository.findById(ownerIdInt)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chủ sở hữu với ID: " + dto.ownerId()));

        String name = safeTrim(dto.name(), "Tên xe không được để trống.");
        String licensePlate = safeTrim(dto.licensePlate(), "Biển số xe không được để trống.");

        Bus newBus = new Bus();
        newBus.setName(name);
        newBus.setLicensePlate(licensePlate);
        newBus.setTotalSeats(dto.totalSeats());
        newBus.setCategory(category);
        newBus.setOwner(owner);
        newBus.setCreatedAt(OffsetDateTime.now());
        newBus.setUpdatedAt(OffsetDateTime.now());

        Bus savedBus = busDAO.save(newBus);

        if (dto.imageIds() != null && !dto.imageIds().isEmpty()) {
            List<Integer> imageIdsInt = dto.imageIds().stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            log.info("Attempting to link images with IDs: {} to Bus ID: {}", imageIdsInt, savedBus.getId());

            List<Image> images = imageRepository.findAllById(imageIdsInt);
            if (images.size() != imageIdsInt.size()) {
                List<Integer> foundIds = images.stream().map(Image::getId).collect(Collectors.toList());
                List<Integer> missingIds = imageIdsInt.stream()
                        .filter(id -> !foundIds.contains(id))
                        .collect(Collectors.toList());
                log.warn("Một số ID ảnh không tìm thấy khi tạo Bus: {}", missingIds);
                throw new EntityNotFoundException("Một số ảnh không tìm thấy cho các ID được cung cấp: " + missingIds);
            }

            for (Image image : images) {
                if (image.getUrl() == null || image.getPublicId() == null) {
                    log.error("Image with ID {} is incomplete (URL or Public ID is null)." +
                            " Transaction will be rolled back.", image.getId());
                    throw new IllegalArgumentException("Dữ liệu ảnh không đầy đủ cho ID: "
                            + image.getId() + ". Thiếu URL hoặc Public ID.");
                }
            }

            List<BusImage> busImages = images.stream().map(image -> {
                BusImage busImage = new BusImage();
                busImage.setId(new BusImageId(savedBus.getId(), image.getId()));
                busImage.setBus(savedBus);
                busImage.setImage(image);
                return busImage;
            }).collect(Collectors.toList());
            busImageRepository.saveAll(busImages);

            savedBus.setBusImages(new ArrayList<>(busImages));
        }

        log.info("Bus Service Implement -> Tạo mới xe bởi: {}", owner.getName());
        return convertToBusResponse(savedBus);
    }

    @Override
    @Transactional
    public BusResponse updateBus(Integer busId, UpdateBusRequest updateBusRequest) {
        // Sử dụng findById đã có JOIN FETCH trong BusDAO
        Bus existingBus = busDAO.findById(busId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy xe bus với ID: " + busId));

        Optional.ofNullable(updateBusRequest.name())
                .map(name -> safeTrim(name, "Tên xe không được để trống."))
                .ifPresent(existingBus::setName);
        Optional.ofNullable(updateBusRequest.licensePlate())
                .map(licensePlate -> safeTrim(licensePlate, "Biển số xe không được để trống."))
                .ifPresent(existingBus::setLicensePlate);
        Optional.ofNullable(updateBusRequest.totalSeats())
                .ifPresent(existingBus::setTotalSeats);

        Optional.ofNullable(updateBusRequest.categoryId()).ifPresent(newCategoryIdString -> {
            Integer newCategoryIdInt = Integer.parseInt(newCategoryIdString);
            BusCategory category = busCategoryDAO.findById(newCategoryIdInt)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục xe với ID: " + newCategoryIdString));
            existingBus.setCategory(category);
        });

        Optional.ofNullable(updateBusRequest.ownerId()).ifPresent(newOwnerIdString -> {
            Integer newOwnerIdInt = Integer.parseInt(newOwnerIdString);
            User owner = userRepository.findById(newOwnerIdInt)
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chủ sở hữu với ID: " + newOwnerIdString));
            existingBus.setOwner(owner);
        });

        if (updateBusRequest.imageIds() != null) {
            List<BusImage> currentBusImages = existingBus.getBusImages(); // Đã được tải do JOIN FETCH
            HashSet<Integer> currentImageIds = currentBusImages.stream()
                    .map(bi -> bi.getImage().getId())
                    .collect(Collectors.toCollection(HashSet::new));

            List<Integer> newImageIds = updateBusRequest.imageIds().stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            List<BusImage> imagesToRemove = currentBusImages.stream()
                    .filter(bi -> !newImageIds.contains(bi.getImage().getId()))
                    .collect(Collectors.toList());
            if (!imagesToRemove.isEmpty()) {
                busImageRepository.deleteAll(imagesToRemove);
                existingBus.getBusImages().removeAll(imagesToRemove);
            }

            List<Integer> imageIdsToAdd = newImageIds.stream()
                    .filter(id -> !currentImageIds.contains(id))
                    .collect(Collectors.toList());

            if (!imageIdsToAdd.isEmpty()) {
                List<Image> images = imageRepository.findAllById(imageIdsToAdd);
                if (images.size() != imageIdsToAdd.size()) {
                    List<Integer> foundIds = images.stream().map(Image::getId).collect(Collectors.toList());
                    List<Integer> missingIds = imageIdsToAdd.stream()
                            .filter(id -> !foundIds.contains(id))
                            .collect(Collectors.toList());
                    log.warn("Một số ID ảnh không tìm thấy khi cập nhật Bus ID {}: {}", busId, missingIds);
                    throw new EntityNotFoundException("Một số ảnh không tìm thấy cho các ID được cung cấp: " + missingIds);
                }
                List<BusImage> newBusImages = images.stream().map(image -> {
                    BusImage busImage = new BusImage();
                    busImage.setId(new BusImageId(existingBus.getId(), image.getId()));
                    busImage.setBus(existingBus);
                    busImage.setImage(image);
                    return busImage;
                }).collect(Collectors.toList());
                busImageRepository.saveAll(newBusImages);
                existingBus.getBusImages().addAll(newBusImages);
            }
        }

        existingBus.setUpdatedAt(OffsetDateTime.now());
        Bus savedBus = busDAO.save(existingBus);

        return convertToBusResponse(savedBus);
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
    @Transactional(readOnly = true)
    public Optional<BusResponse> findBusById(Integer busId) {
        // Sử dụng findById đã có JOIN FETCH trong BusDAO
        return busDAO.findById(busId).map(this::convertToBusResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> findAllBuses() {
        // Sử dụng findAll đã có JOIN FETCH trong BusDAO
        return busDAO.findAll().stream()
                .map(this::convertToBusResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> findBusesByOwnerId(Integer ownerId) {
        // Sử dụng findByOwnerId đã có JOIN FETCH trong BusDAO
        return busDAO.findByOwnerId(ownerId).stream()
                .map(this::convertToBusResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> findBusesByCategoryId(Integer categoryId) {
        // Sử dụng findByCategoryId đã có JOIN FETCH trong BusDAO
        return busDAO.findByCategoryId(categoryId).stream()
                .map(this::convertToBusResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> searchBuses(String name, String licensePlate) {
        List<Bus> buses;
        if (name != null && !name.isEmpty()) {
            // Sử dụng findByNameContainingIgnoreCase đã có JOIN FETCH
            buses = busDAO.findByNameContainingIgnoreCase(name);
        } else if (licensePlate != null && !licensePlate.isEmpty()) {
            // Sử dụng findByLicensePlateContainingIgnoreCase đã có JOIN FETCH
            buses = busDAO.findByLicensePlateContainingIgnoreCase(licensePlate);
        } else {
            // Sử dụng findAll đã có JOIN FETCH
            buses = busDAO.findAll();
        }
        return buses.stream()
                .map(this::convertToBusResponse)
                .collect(Collectors.toList());
    }


}
