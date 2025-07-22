package backend.backend.implement;

import backend.backend.dao.*;
import backend.backend.dto.*;
import backend.backend.entity.*;
import backend.backend.service.ImageStorageService;
import backend.backend.service.TourAdminService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TourAdminServiceImpl implements TourAdminService {

    @Autowired private TourDAO tourRepository;
    @Autowired private TagDAO tagRepository;
    @Autowired private TourScheduleDAO tourScheduleRepository;
    @Autowired private TourItineraryActivityDAO tourItineraryActivityRepository;
    @Autowired private DepartureDAO departureRepository;
    @Autowired private ImageDAO imageRepository;
    @Autowired private TourImageDAO tourImageRepository;
    @Autowired private ImageStorageService imageStorageService;

    @Override
    @Transactional(readOnly = true)
    public List<TourDetailAdminDTO> getAllToursForAdmin() {
        return tourRepository.findAll().stream()
                .map(TourDetailAdminDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TourDetailAdminDTO getTourById(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tour với ID: " + id));
        return new TourDetailAdminDTO(tour);
    }

    @Override
    @Transactional
    public TourDetailAdminDTO createTour(TourRequestDTO dto, List<MultipartFile> images) {
        Tour tour = new Tour();
        mapDtoToBasicInfo(dto, tour);
        tour.setTags(processTags(dto.getTags()));
        Tour savedTour = tourRepository.save(tour);
        // Khi tạo mới, vẫn dùng process bình thường
        processSchedules(dto.getSchedules(), savedTour);
        processDepartures(dto.getDepartures(), savedTour);
        processAndSaveImages(images, savedTour);
        return getTourById(savedTour.getId());
    }

    @Override
    @Transactional
    public TourDetailAdminDTO updateTour(Long id, TourRequestDTO dto, List<MultipartFile> newImages) {
        Tour tourToUpdate = tourRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tour với ID: " + id));

        // 1. Cập nhật thông tin cơ bản
        mapDtoToBasicInfo(dto, tourToUpdate);
        tourToUpdate.setTags(processTags(dto.getTags()));

        // 2. Đồng bộ hóa các mục con một cách thông minh
        synchronizeSchedules(tourToUpdate, dto.getSchedules());
        synchronizeDepartures(tourToUpdate, dto.getDepartures());

        // 3. Cập nhật hình ảnh
        updateImages(dto.getImageUrls(), newImages, tourToUpdate);

        // 4. Lưu lại tất cả các thay đổi
        Tour updatedTour = tourRepository.save(tourToUpdate);
        return getTourById(updatedTour.getId());
    }

    @Override
    @Transactional
    public void deleteTour(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tour với ID: " + id));

        // Kiểm tra xem có booking nào không
        boolean hasBookings = tour.getDepartures().stream()
                .anyMatch(departure -> departure.getBookedSeats() > 0);
        if (hasBookings) {
            throw new IllegalStateException("Không thể xóa tour này vì đã có khách hàng đặt tour.");
        }

        // Xóa ảnh trên Cloudinary
        if (tour.getTourImages() != null) {
            // Tạo một bản sao của list để tránh lỗi khi vừa duyệt vừa xóa
            List<TourImage> imagesToRemove = new ArrayList<>(tour.getTourImages());
            for (TourImage tourImage : imagesToRemove) {
                try {
                    if (tourImage.getImage() != null && tourImage.getImage().getPublicId() != null) {
                        imageStorageService.deleteImage(tourImage.getImage().getPublicId());
                    }
                } catch (IOException e) {
                    System.err.println("Lỗi xóa ảnh từ dịch vụ lưu trữ: " + e.getMessage());
                }
            }
        }

        // Xóa tour. Các collection con (schedules, departures, images) sẽ được xóa theo
        // nếu bạn đã cấu hình `cascade = CascadeType.ALL, orphanRemoval = true` trong Entity Tour.
        tourRepository.delete(tour);
    }
    
    // --- CÁC HÀM HELPER ĐỒNG BỘ HÓA ---

    private void synchronizeSchedules(Tour tour, List<ScheduleRequestDTO> scheduleDtos) {
        if (scheduleDtos == null) scheduleDtos = Collections.emptyList();

        Map<Integer, ScheduleRequestDTO> dtoMap = scheduleDtos.stream()
                .collect(Collectors.toMap(ScheduleRequestDTO::getDayNumber, Function.identity(), (d1, d2) -> d1));
        Map<Integer, TourSchedule> existingMap = tour.getTourSchedules().stream()
                .collect(Collectors.toMap(TourSchedule::getDayNumber, Function.identity()));

        // Xóa
        List<TourSchedule> toRemove = existingMap.values().stream()
                .filter(existing -> !dtoMap.containsKey(existing.getDayNumber()))
                .collect(Collectors.toList());
        tour.getTourSchedules().removeAll(toRemove);
        tourScheduleRepository.deleteAll(toRemove);

        // Cập nhật và Thêm mới
        dtoMap.forEach((dayNumber, dto) -> {
            TourSchedule schedule = existingMap.getOrDefault(dayNumber, new TourSchedule());
            mapDtoToSchedule(dto, schedule); // Map thông tin
            if (schedule.getId() == null) { // Nếu là schedule mới
                schedule.setTour(tour);
                tour.getTourSchedules().add(schedule);
            }
        });
    }

    private void synchronizeDepartures(Tour tour, List<DepartureRequestDTO> departureDtos) {
        if (departureDtos == null) departureDtos = Collections.emptyList();

        Map<LocalDate, DepartureRequestDTO> dtoMap = departureDtos.stream()
                .collect(Collectors.toMap(
                    DepartureRequestDTO::getDepartureDate, 
                    Function.identity(), 
                    (d1, d2) -> d1 
                ));
        
        Map<LocalDate, Departure> existingMap = tour.getDepartures().stream()
                .collect(Collectors.toMap(
                    Departure::getDepartureDate, 
                    Function.identity(),
                    (d1, d2) -> d1 
                ));

        // Xóa
        List<Departure> toRemove = existingMap.values().stream()
                .filter(existing -> !dtoMap.containsKey(existing.getDepartureDate()))
                .collect(Collectors.toList());
        
        for (Departure departure : toRemove) {
            if (departure.getBookedSeats() > 0) {
                throw new IllegalStateException("Không thể xóa ngày khởi hành " + departure.getDepartureDate() + " vì đã có khách đặt.");
            }
        }
        tour.getDepartures().removeAll(toRemove);
        departureRepository.deleteAll(toRemove);

        // Cập nhật và Thêm mới
        dtoMap.forEach((date, dto) -> {
            Departure departure = existingMap.getOrDefault(date, new Departure());
            mapDtoToDeparture(dto, departure); // Map thông tin
            if (departure.getId() == null) { // Nếu là departure mới
                departure.setTour(tour);
                tour.getDepartures().add(departure);
            }
        });
    }

    // --- CÁC HÀM HELPER MAP DTO -> ENTITY ---

    private void mapDtoToDeparture(DepartureRequestDTO dto, Departure entity) {
        entity.setDepartureDate(dto.getDepartureDate());
        entity.setAdultPrice(dto.getAdultPrice());
        entity.setChildPrice(dto.getChildPrice());
        entity.setDiscount(dto.getDiscount());
        entity.setSeatCount(dto.getSeatCount());
    }

    private void mapDtoToSchedule(ScheduleRequestDTO dto, TourSchedule entity) {
        entity.setDayNumber(dto.getDayNumber());
        entity.setTitle(dto.getTitle());
        if (entity.getActivities() == null) {
            entity.setActivities(new ArrayList<>());
        }
        entity.getActivities().clear(); // Đơn giản hóa: luôn tạo lại activity
        if (dto.getActivities() != null) {
            dto.getActivities().forEach(activityDto -> {
                TourItineraryActivity newActivity = new TourItineraryActivity();
                mapDtoToActivity(activityDto, newActivity);
                newActivity.setTourSchedule(entity);
                entity.getActivities().add(newActivity);
            });
        }
    }

    private void mapDtoToActivity(ActivityRequestDTO dto, TourItineraryActivity entity) {
        entity.setActivityTime(dto.getTime());
        entity.setActivityTitle(dto.getActivityTitle());
        entity.setDescription(dto.getDescription());
        entity.setIcon(dto.getIcon());
    }

    private void mapDtoToBasicInfo(TourRequestDTO dto, Tour tour) {
        tour.setName(dto.getName());
        tour.setDescription(dto.getDescription());
        tour.setPrice(dto.getPrice());
        tour.setDurationDays(dto.getDurationDays());
        tour.setDeparturePoint(dto.getDeparturePoint());
        tour.setDestination(dto.getDestination());
        tour.setStatus(dto.getStatus());
    }

    // --- CÁC HÀM HELPER CŨ (GIỮ NGUYÊN) ---

    private Set<Tag> processTags(List<TourRequestDTO.TagDTO> tagDtos) {
        if (tagDtos == null || tagDtos.isEmpty()) return new HashSet<>();
        Set<Tag> tags = new HashSet<>();
        for (TourRequestDTO.TagDTO tagDto : tagDtos) {
            if (tagDto.getId() != null) {
                tagRepository.findById(tagDto.getId()).ifPresent(tags::add);
            } else if (StringUtils.hasText(tagDto.getName())) {
                Tag tag = tagRepository.findByName(tagDto.getName())
                        .orElseGet(() -> tagRepository.save(new Tag(tagDto.getName())));
                tags.add(tag);
            }
        }
        return tags;
    }

    private void processSchedules(List<ScheduleRequestDTO> scheduleDtos, Tour tour) {
        if (scheduleDtos == null) return;
        for (ScheduleRequestDTO dto : scheduleDtos) {
            TourSchedule schedule = new TourSchedule();
            mapDtoToSchedule(dto, schedule);
            schedule.setTour(tour);
            tour.getTourSchedules().add(schedule);
        }
    }

    private void processDepartures(List<DepartureRequestDTO> departureDtos, Tour tour) {
        if (departureDtos == null) return;
        for (DepartureRequestDTO dto : departureDtos) {
            Departure departure = new Departure();
            mapDtoToDeparture(dto, departure);
            departure.setTour(tour);
            tour.getDepartures().add(departure);
        }
    }

    private void processAndSaveImages(List<MultipartFile> imageFiles, Tour tour) {
        if (imageFiles == null || imageFiles.isEmpty()) return;
        for (MultipartFile imageFile : imageFiles) {
            try {
                Map<String, String> uploadResult = imageStorageService.uploadImage(imageFile);
                Image image = new Image();
                image.setUrl(uploadResult.get("url"));
                image.setPublicId(uploadResult.get("publicId"));
                Image savedImage = imageRepository.save(image);

                TourImage tourImage = new TourImage();
                tourImage.setTour(tour);
                tourImage.setImage(savedImage);
                tourImage.setId(new TourImageId(tour.getId(), savedImage.getId()));
                tourImageRepository.save(tourImage);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi khi tải ảnh lên: " + e.getMessage(), e);
            }
        }
    }
    
    private void updateImages(List<String> existingImageUrls, List<MultipartFile> newImages, Tour tour) {
        if (tour.getTourImages() != null) {
            Set<String> existingUrlsSet = new HashSet<>(existingImageUrls == null ? Collections.emptyList() : existingImageUrls);
            List<TourImage> imagesToDelete = tour.getTourImages().stream()
                    .filter(tourImage -> !existingUrlsSet.contains(tourImage.getImage().getUrl()))
                    .collect(Collectors.toList());

            for (TourImage tourImage : imagesToDelete) {
                try {
                    imageStorageService.deleteImage(tourImage.getImage().getPublicId());
                } catch (IOException e) {
                    System.err.println("Lỗi xóa ảnh cũ trên cloud: " + e.getMessage());
                }
            }
            tour.getTourImages().removeAll(imagesToDelete);
        }
        processAndSaveImages(newImages, tour);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TourDetailAdminDTO> getToursByUserId(Integer userId) {
        // Giả sử Tour entity có trường `private User owner;`
        // return tourRepository.findByOwnerId(userId).stream()
        //         .map(TourDetailAdminDTO::new) 
        //         .collect(Collectors.toList());
        return new ArrayList<>(); // Tạm thời trả về list rỗng
    }
}