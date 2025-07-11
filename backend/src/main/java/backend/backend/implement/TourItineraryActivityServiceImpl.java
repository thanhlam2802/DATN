package backend.backend.implement;



import backend.backend.dao.TourItineraryActivityDAO;
import backend.backend.entity.TourItineraryActivity;

import backend.backend.service.TourItineraryActivityService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TourItineraryActivityServiceImpl implements TourItineraryActivityService {
	 @Autowired
     TourItineraryActivityDAO tourItineraryActivityRepository;

    @Override
    public TourItineraryActivity createTourItineraryActivity(TourItineraryActivity tourItineraryActivity) {
        return tourItineraryActivityRepository.save(tourItineraryActivity);
    }

    @Override
    public TourItineraryActivity updateTourItineraryActivity(Long id, TourItineraryActivity tourItineraryActivityDetails) {
        TourItineraryActivity existingActivity = tourItineraryActivityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hoạt động của lịch trình với id: " + id));

        existingActivity.setActivityTime(tourItineraryActivityDetails.getActivityTime());
        existingActivity.setActivityTitle(tourItineraryActivityDetails.getActivityTitle());
        existingActivity.setDescription(tourItineraryActivityDetails.getDescription());
        existingActivity.setIcon(tourItineraryActivityDetails.getIcon());


        return tourItineraryActivityRepository.save(existingActivity);
    }

    @Override
    public void deleteTourItineraryActivity(Long id) {
        if (!tourItineraryActivityRepository.existsById(id)) {
            throw new EntityNotFoundException("Không tìm thấy hoạt động của lịch trình với id: " + id + " để xóa.");
        }
        tourItineraryActivityRepository.deleteById(id);
    }

    @Override
    public List<TourItineraryActivity> getAllTourItineraryActivities() {
        return tourItineraryActivityRepository.findAll();
    }

    @Override
    public Optional<TourItineraryActivity> getTourItineraryActivityById(Long id) {
        return tourItineraryActivityRepository.findById(id);
    }
}