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
import java.util.*;
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

    // --- Các hàm getAll, getById, create, update không thay đổi ---
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
        mapDtoToBasicInfo(dto, tourToUpdate);
        tourToUpdate.setTags(processTags(dto.getTags()));
        
        tourToUpdate.getTourSchedules().clear();
        tourScheduleRepository.flush();
        processSchedules(dto.getSchedules(), tourToUpdate);
        
        tourToUpdate.getDepartures().clear();
        departureRepository.flush();
        processDepartures(dto.getDepartures(), tourToUpdate);
        
        updateImages(dto.getImageUrls(), newImages, tourToUpdate);
        
        Tour updatedTour = tourRepository.save(tourToUpdate);
        return getTourById(updatedTour.getId());
    }


    // --- THAY THẾ TOÀN BỘ HÀM deleteTour BẰNG PHIÊN BẢN MỚI NÀY ---
    @Override
    @Transactional
    public void deleteTour(Long id) {
        // Đầu tiên, kiểm tra xem tour có tồn tại không
        if (!tourRepository.existsById(id)) {
            throw new EntityNotFoundException("Không tìm thấy tour với ID: " + id);
        }

        // 1. Kiểm tra trực tiếp DB xem tour này có booking nào không.
        if (departureRepository.existsByTourIdAndBookedSeatsGreaterThan(id, 0)) {
            throw new IllegalStateException("Không thể xóa tour này vì đã có khách hàng đặt tour.");
        }

        // 2. Kiểm tra trực tiếp DB xem tour này có ngày khởi hành nào không.
        if (departureRepository.existsByTourId(id)) {
            throw new IllegalStateException("Không thể xóa tour này vì đã có ngày khởi hành được thiết lập.");
        }

        // Nếu tất cả các kiểm tra đều qua, lúc này mới lấy tour ra để xử lý xóa
        Tour tour = tourRepository.findById(id).get(); 

        // Xóa ảnh trên Cloudinary
        if (tour.getTourImages() != null) {
            for (TourImage tourImage : tour.getTourImages()) {
                try {
                    if (tourImage.getImage() != null && tourImage.getImage().getPublicId() != null) {
                        imageStorageService.deleteImage(tourImage.getImage().getPublicId());
                    }
                } catch (IOException e) {
                    System.err.println("Lỗi xóa ảnh từ dịch vụ lưu trữ: " + e.getMessage());
                }
            }
        }
        
        // Cuối cùng, xóa tour
        tourRepository.delete(tour);
    }

    // --- Các hàm Helper không thay đổi ---
    private void mapDtoToBasicInfo(TourRequestDTO dto, Tour tour) {
        System.out.println("--- [DEBUG] Dữ liệu DTO nhận được để map: " + dto.toString());
        tour.setName(dto.getName());
        tour.setDescription(dto.getDescription());
        tour.setPrice(dto.getPrice());
        tour.setDurationDays(dto.getDurationDays());
        tour.setDeparturePoint(dto.getDeparturePoint());
        tour.setDestination(dto.getDestination());
        tour.setStatus(dto.getStatus());
    }

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
        List<TourSchedule> schedules = new ArrayList<>();
        for (ScheduleRequestDTO scheduleDto : scheduleDtos) {
            TourSchedule schedule = new TourSchedule();
            schedule.setTour(tour);
            schedule.setDayNumber(scheduleDto.getDayNumber());
            schedule.setTitle(scheduleDto.getTitle());
            schedule.setScheduleDate(null); // Hoặc một giá trị mặc định nếu cần

            if (scheduleDto.getActivities() != null) {
                List<TourItineraryActivity> activities = new ArrayList<>();
                for (ActivityRequestDTO activityDto : scheduleDto.getActivities()) {
                    TourItineraryActivity activity = new TourItineraryActivity();
                    activity.setTourSchedule(schedule);
               
                    activity.setActivityTime(activityDto.getTime());
                    activity.setActivityTitle(activityDto.getActivityTitle());
                    activity.setDescription(activityDto.getDescription());
                    activity.setIcon(activityDto.getIcon());
                    activities.add(activity);
                }
                schedule.setActivities(activities);
            }
            schedules.add(schedule);
        }
        tour.getTourSchedules().addAll(schedules);
    }

    private void processDepartures(List<DepartureRequestDTO> departureDtos, Tour tour) {
        if (departureDtos == null) return;
        List<Departure> departures = new ArrayList<>();
        for (DepartureRequestDTO departureDto : departureDtos) {
            Departure departure = new Departure();
            departure.setTour(tour);
            departure.setDepartureDate(departureDto.getDepartureDate());
            departure.setAdultPrice(departureDto.getAdultPrice());
            departure.setChildPrice(departureDto.getChildPrice());
            departure.setDiscount(departureDto.getDiscount());
            departure.setSeatCount(departureDto.getSeatCount());
            departures.add(departure);
        }
        tour.getDepartures().addAll(departures);
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
}