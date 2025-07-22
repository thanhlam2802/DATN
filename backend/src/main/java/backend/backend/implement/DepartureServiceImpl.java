package backend.backend.implement;

import backend.backend.dao.DepartureDAO;
import backend.backend.dao.TourDAO;
import backend.backend.dto.DepartureDto;
import backend.backend.entity.Departure;
import backend.backend.entity.Tour;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.service.DepartureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartureServiceImpl implements DepartureService {

    @Autowired
    DepartureDAO departureRepository;
    @Autowired
    TourDAO tourRepository;

    @Override
    public List<DepartureDto> getDeparturesByTourId(Long tourId) {
        List<Departure> departures = departureRepository.findByTourId(tourId);
        return departures.stream()
                .map(DepartureDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public DepartureDto createDeparture(Long tourId, DepartureDto departureDto) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Tour"));

        Departure newDeparture = new Departure();
        newDeparture.setDepartureDate(LocalDate.parse(departureDto.getDepartureDate()));
        newDeparture.setAdultPrice(departureDto.getAdultPrice());
        newDeparture.setChildPrice(departureDto.getChildPrice());
        newDeparture.setDiscount(departureDto.getDiscount());
        newDeparture.setSeatCount(departureDto.getSeatCount());
        newDeparture.setBookedSeats(0);
        newDeparture.setTour(tour);

        Departure savedDeparture = departureRepository.save(newDeparture);
        return DepartureDto.fromEntity(savedDeparture);
    }

    /**
     * Triển khai chức năng Sửa ngày khởi hành
     */
    @Override
    public DepartureDto updateDeparture(Long departureId, DepartureDto departureDto) {
        Departure existingDeparture = departureRepository.findById(departureId)
                .orElseThrow(() -> new ResourceNotFoundException("Departure"));

        existingDeparture.setDepartureDate(LocalDate.parse(departureDto.getDepartureDate()));
        existingDeparture.setAdultPrice(departureDto.getAdultPrice());
        existingDeparture.setChildPrice(departureDto.getChildPrice());
        existingDeparture.setDiscount(departureDto.getDiscount());
        existingDeparture.setSeatCount(departureDto.getSeatCount());

        Departure updatedDeparture = departureRepository.save(existingDeparture);
        return DepartureDto.fromEntity(updatedDeparture);
    }

    /**
     * Triển khai chức năng Xóa ngày khởi hành
     */
    @Override
    public void deleteDeparture(Long departureId) {
        Departure departureToDelete = departureRepository.findById(departureId)
                .orElseThrow(() -> new ResourceNotFoundException("Departure"));

        if (departureToDelete.getBookedSeats() != null && departureToDelete.getBookedSeats() > 0) {
            throw new IllegalStateException("Không thể xóa ngày khởi hành đã có người đặt chỗ.");
        }

        departureRepository.delete(departureToDelete);
    }

    /**
     * MỚI: Triển khai chức năng tạo hàng loạt ngày khởi hành.
     */
    @Override
    public List<DepartureDto> createRecurringDepartures(Long tourId, DepartureDto templateDto, int intervalDays,
            int count) {
        // 1. Tìm tour tương ứng để gán ngày khởi hành
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found with id: " + tourId));

        List<Departure> departuresToSave = new ArrayList<>();
        // 2. Lấy ngày bắt đầu từ DTO mẫu
        LocalDate startDate = LocalDate.parse(templateDto.getDepartureDate());

        // 3. Vòng lặp để tạo ra 'count' ngày khởi hành
        for (int i = 0; i < count; i++) {
            Departure newDeparture = new Departure();

            // Sao chép thông tin từ DTO mẫu
            newDeparture.setAdultPrice(templateDto.getAdultPrice());
            newDeparture.setChildPrice(templateDto.getChildPrice());
            newDeparture.setDiscount(templateDto.getDiscount());
            newDeparture.setSeatCount(templateDto.getSeatCount());
            newDeparture.setBookedSeats(0);
            newDeparture.setTour(tour);

            // Tạo ngày khởi hành liên tiếp
            LocalDate departureDate = startDate.plusDays(i);
            newDeparture.setDepartureDate(departureDate);

            departuresToSave.add(newDeparture);
        }

        // 6. Lưu tất cả các ngày khởi hành đã tạo vào cơ sở dữ liệu
        List<Departure> savedDepartures = departureRepository.saveAll(departuresToSave);

        // 7. Chuyển đổi các thực thể đã lưu thành DTO và trả về
        return savedDepartures.stream()
                .map(DepartureDto::fromEntity)
                .collect(Collectors.toList());
    }
}
