package backend.backend.implement.busImplement;

import backend.backend.dao.Bus.BusAmenityDAO;
import backend.backend.dao.Bus.BusCategoryDAO;
import backend.backend.dao.Bus.BusDAO;
import backend.backend.dao.Bus.BusImageDAO;
import backend.backend.dao.ImageDAO;
import backend.backend.dto.BusDTO.*;
import backend.backend.entity.*;
import backend.backend.repository.UserRepository;
import backend.backend.service.busService.BusService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset; // Thêm import cho ZoneOffset
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Log4j2
public class BusServiceImpl implements BusService {
    private final BusDAO busDAO;
    private final BusCategoryDAO busCategoryDAO;
    private final UserRepository userRepository;
    private final ImageDAO imageRepository;
    private final BusImageDAO busImageRepository;
    private final BusAmenityDAO busAmenityDAO; // <-- BỔ SUNG: Inject BusAmenityDAO

//    // --- Helper Methods ---
//    private User getCurrentAuthenticatedUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
//            throw new IllegalStateException("Người dùng chưa được xác thực hoặc không tìm thấy thông tin.");
//        }
//        String username = authentication.getName();
//        return userRepository.findByName(username)
//                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng: " + username));
//    }

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
                image.getUploadedAt() != null ? image.getUploadedAt().atOffset(ZoneOffset.ofHours(7)) : null,
                image.getPublicId()
        );
    }

    private BusImageResponse convertToBusImageResponse(BusImage busImage) {
        return new BusImageResponse(
                busImage.getBus() != null ? busImage.getBus().getId() : null, // Thêm kiểm tra null
                busImage.getImage() != null ? busImage.getImage().getId() : null, // Thêm kiểm tra null
                busImage.getImage() != null ? convertToImageResponse(busImage.getImage()) : null // Thêm kiểm tra null
        );
    }

    private BusAmenityResponse convertToBusAmenityResponse(BusAmenity busAmenity) {
        return new BusAmenityResponse(busAmenity);
    }

    private BusResponse convertToBusResponse(Bus bus) {
        List<BusImageResponse> busImageResponses = new ArrayList<>();
        if (bus.getBusImages() != null && !bus.getBusImages().isEmpty()) {
            busImageResponses = bus.getBusImages().stream()
                    .map(this::convertToBusImageResponse)
                    .collect(toList());
        }

        // BỔ SUNG: Chuyển đổi Set<BusAmenity> sang List<BusAmenityResponse>
        List<BusAmenityResponse> busAmenityResponses = new ArrayList<>();
        if (bus.getAmenities() != null && !bus.getAmenities().isEmpty()) {
            busAmenityResponses = bus.getAmenities().stream()
                    .map(this::convertToBusAmenityResponse)
                    .collect(toList());
        }

        return new BusResponse(
                bus.getId(),
                bus.getName(),
                bus.getLicensePlate(),
                bus.getTotalSeats(),
                bus.getCategory() != null ? bus.getCategory().getId() : null,
                bus.getCategory() != null ? bus.getCategory().getName() : null,
                bus.getOwner() != null ? bus.getOwner().getId() : null,
                bus.getOwner() != null ? bus.getOwner().getName() : null,
                busImageResponses,
                bus.getCreatedAt(),
                bus.getUpdatedAt(),
                busAmenityResponses // <-- BỔ SUNG: Truyền danh sách tiện ích
        );
    }

    // --- Service Methods ---

    @Override
    @Transactional
    public BusResponse createBus(CreateBusRequest dto) {
        try {
            // Chuyển đổi String ID sang Integer
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

            // BỔ SUNG: XỬ LÝ TIỆN ÍCH KHI TẠO XE (THEO TÊN)
            if (dto.amenityNames() != null && !dto.amenityNames().isEmpty()) {
                Set<BusAmenity> amenities = new HashSet<>();
                for (String amenityName : dto.amenityNames()) {
                    // Tìm kiếm tiện ích theo tên, nếu không có thì tạo mới
                    BusAmenity amenity = busAmenityDAO.findByName(amenityName.trim())
                            .orElseGet(() -> {
                                log.info("Tạo tiện ích xe buýt mới: {}", amenityName);
                                BusAmenity newAmenity = new BusAmenity();
                                newAmenity.setName(amenityName.trim());
                                // Bạn có thể thêm mô tả mặc định hoặc để trống
                                return busAmenityDAO.save(newAmenity);
                            });
                    amenities.add(amenity);
                }
                newBus.setAmenities(amenities);
            }

            Bus savedBus = busDAO.save(newBus);

            // Xử lý liên kết ảnh
            if (dto.imageIds() != null && !dto.imageIds().isEmpty()) {
                List<Integer> imageIdsInt = dto.imageIds().stream()
                        .map(Integer::parseInt)
                        .collect(toList());

                log.info("Attempting to link images with IDs: {} to Bus ID: {}", imageIdsInt, savedBus.getId());

                List<Image> images = imageRepository.findAllById(imageIdsInt);
                if (images.size() != imageIdsInt.size()) {
                    List<Integer> foundIds = images.stream().map(Image::getId).collect(toList());
                    List<Integer> missingIds = imageIdsInt.stream()
                            .filter(id -> !foundIds.contains(id))
                            .collect(toList());
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
                }).collect(toList());
                busImageRepository.saveAll(busImages);

                savedBus.setBusImages(new ArrayList<>(busImages));
            }

            log.info("Bus Service Implement -> Tạo mới xe bởi: {}", owner.getName());
            return convertToBusResponse(savedBus);
        } catch (DataIntegrityViolationException e) {
            log.error("Lỗi vi phạm ràng buộc khi tạo Bus. Chi tiết lỗi từ DB: {}", e.getMessage(), e);
            Throwable rootCause = e.getRootCause();
            String errorMessage = "Dữ liệu bị trùng hoặc vi phạm ràng buộc không xác định.";
            if (rootCause != null) {
                String rootCauseMessage = rootCause.getMessage();
                log.error("Root cause message: {}", rootCauseMessage);
                if (rootCauseMessage.contains("duplicate key") || rootCauseMessage.contains("Cannot insert duplicate key")) {
                    errorMessage = "Biển số xe hoặc tên xe đã tồn tại. Vui lòng chọn giá trị khác.";
                    if (rootCauseMessage.contains("license_plate")) {
                        errorMessage = "Biển số xe đã tồn tại. Vui lòng chọn biển số khác.";
                    } else if (rootCauseMessage.contains("name")) {
                        errorMessage = "Tên xe đã tồn tại. Vui lòng chọn tên khác.";
                    } else if (rootCauseMessage.contains("PK__bus_imag") || rootCauseMessage.contains("UQ__bus_imag")) {
                        errorMessage = "Ảnh đã được liên kết với xe bus này hoặc một liên kết ảnh khác bị trùng lặp.";
                    }
                } else if (rootCauseMessage.contains("FOREIGN KEY constraint")) {
                    errorMessage = "ID danh mục, ID chủ sở hữu, hoặc ID ảnh không hợp lệ.";
                    if (rootCauseMessage.contains("FK__buses__owner_id")) {
                        errorMessage = "ID chủ sở hữu không tồn tại. Vui lòng kiểm tra lại.";
                    } else if (rootCauseMessage.contains("FK__buses__category_")) {
                        errorMessage = "ID danh mục xe không tồn tại. Vui lòng kiểm tra lại.";
                    } else if (rootCauseMessage.contains("FK__bus_image__image_id")) {
                        errorMessage = "ID ảnh không tồn tại. Vui lòng kiểm tra lại ID ảnh.";
                    }
                }
            }
            throw new IllegalArgumentException(errorMessage);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID không hợp lệ. Vui lòng đảm bảo ID là số.", e);
        } catch (Exception e) {
            log.error("Lỗi không xác định khi tạo Bus: {}", e.getMessage(), e);
            throw new RuntimeException("Đã xảy ra lỗi không mong muốn khi tạo xe bus.", e);
        }
    }

    @Override
    @Transactional
    public BusResponse updateBus(Integer busId, UpdateBusRequest updateBusRequest) {
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
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chủ sở hữu với ID: " + newOwnerIdInt));
            existingBus.setOwner(owner);
        });

        if (updateBusRequest.imageIds() != null) {
            List<BusImage> currentBusImages = existingBus.getBusImages();
            HashSet<Integer> currentImageIds = currentBusImages.stream()
                    .map(bi -> bi.getImage().getId())
                    .collect(Collectors.toCollection(HashSet::new));

            List<Integer> newImageIds = updateBusRequest.imageIds().stream()
                    .map(Integer::parseInt)
                    .toList();

            List<BusImage> imagesToRemove = currentBusImages.stream()
                    .filter(bi -> !newImageIds.contains(bi.getImage().getId()))
                    .collect(toList());
            if (!imagesToRemove.isEmpty()) {
                busImageRepository.deleteAll(imagesToRemove);
                existingBus.getBusImages().removeAll(imagesToRemove);
            }

            List<Integer> imageIdsToAdd = newImageIds.stream()
                    .filter(id -> !currentImageIds.contains(id))
                    .collect(toList());

            if (!imageIdsToAdd.isEmpty()) {
                List<Image> images = imageRepository.findAllById(imageIdsToAdd);
                if (images.size() != imageIdsToAdd.size()) {
                    List<Integer> foundIds = images.stream().map(Image::getId).collect(toList());
                    List<Integer> missingIds = imageIdsToAdd.stream()
                            .filter(id -> !foundIds.contains(id))
                            .collect(toList());
                    log.warn("Một số ID ảnh không tìm thấy khi cập nhật Bus ID {}: {}", busId, missingIds);
                    throw new EntityNotFoundException("Một số ảnh không tìm thấy cho các ID được cung cấp: " + missingIds);
                }
                List<BusImage> newBusImages = images.stream().map(image -> {
                    BusImage busImage = new BusImage();
                    busImage.setId(new BusImageId(existingBus.getId(), image.getId()));
                    busImage.setBus(existingBus);
                    busImage.setImage(image);
                    return busImage;
                }).collect(toList());
                busImageRepository.saveAll(newBusImages);
                existingBus.getBusImages().addAll(newBusImages);
            }
        }

        // BỔ SUNG: XỬ LÝ TIỆN ÍCH KHI CẬP NHẬT XE (THEO TÊN)
        if (updateBusRequest.amenityNames() != null) {
            Set<BusAmenity> updatedAmenities = new HashSet<>();
            for (String amenityName : updateBusRequest.amenityNames()) {
                BusAmenity amenity = busAmenityDAO.findByName(amenityName.trim())
                        .orElseGet(() -> {
                            log.info("Tạo tiện ích xe buýt mới khi cập nhật: {}", amenityName);
                            BusAmenity newAmenity = new BusAmenity();
                            newAmenity.setName(amenityName.trim());
                            return busAmenityDAO.save(newAmenity);
                        });
                updatedAmenities.add(amenity);
            }
            existingBus.setAmenities(updatedAmenities);
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
        return busDAO.findById(busId).map(this::convertToBusResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> findAllBuses() {
        return busDAO.findAll().stream()
                .map(this::convertToBusResponse)
                .collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> findBusesByOwnerId(Integer ownerId) {
        return busDAO.findByOwnerId(ownerId).stream()
                .map(this::convertToBusResponse)
                .collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> findBusesByCategoryId(Integer categoryId) {
        return busDAO.findByCategoryId(categoryId).stream()
                .map(this::convertToBusResponse)
                .collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> searchBuses(String name, String licensePlate) {
        List<Bus> buses;
        if (name != null && !name.isEmpty()) {
            buses = busDAO.findByNameContainingIgnoreCase(name);
        } else if (licensePlate != null && !licensePlate.isEmpty()) {
            buses = busDAO.findByLicensePlateContainingIgnoreCase(licensePlate);
        } else {
            buses = busDAO.findAll();
        }
        return buses.stream()
                .map(this::convertToBusResponse)
                .collect(toList());
    }
}
