package backend.backend.implement;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

// THAY ĐỔI: Import DTO mới
import backend.backend.dto.TourListAdminDTO; 
import backend.backend.dto.VendorSummaryDTO;
import backend.backend.entity.ApplicationStatus;
import backend.backend.entity.SupplierApplication;
import backend.backend.entity.Tour;
import backend.backend.entity.TourStatus;
import backend.backend.entity.User;
import backend.backend.dao.SupplierApplicationDAO; // Sửa thành Repository
import backend.backend.dao.TourDAO; // Sửa thành Repository
import backend.backend.service.TourManagementService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;


@Service
public class TourManagementServiceImpl implements TourManagementService {

    // Sửa: Đổi tên biến để nhất quán với Spring Data JPA
    @Autowired
    private TourDAO tourRepository;

    @Autowired
    private SupplierApplicationDAO supplierApplicationRepository;
    @Override
    @Transactional(readOnly = true)
    public List<VendorSummaryDTO> getVendorSummary() {
        List<SupplierApplication> approvedApplications =
            supplierApplicationRepository.findByStatusAndServiceType(
                ApplicationStatus.APPROVED, 
                "TOUR_SUPPLIER"
            );

        return approvedApplications.stream().map(application -> {
            User vendorUser = application.getUser();
            long tourCount = tourRepository.countByOwner(vendorUser);

            return new VendorSummaryDTO(
                vendorUser.getId(),
                application.getBusinessName(),
                tourCount
            );
        }).collect(Collectors.toList());
    }


    /**
     * THAY ĐỔI: Phương thức này giờ trả về TourListAdminDTO
     */
    @Override
    @Transactional
    public TourListAdminDTO updateTourStatus(Long id, TourStatus status) {
        Tour tour = tourRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tour với ID: " + id));
        
        tour.setStatus(status);
        Tour updatedTour = tourRepository.save(tour);

      
        return convertToTourListDto(updatedTour);
    }

    /**
     * THAY ĐỔI: Phương thức này giờ trả về List<TourListAdminDTO>
     */
    @Override
    @Transactional(readOnly = true)
    public List<TourListAdminDTO> findToursByCriteria(String search, TourStatus status, Long vendorId, String destination) {
        Specification<Tour> spec = (root, query, criteriaBuilder) -> {
            Predicate p = criteriaBuilder.conjunction();
            if (StringUtils.hasText(search)) {
                p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("name"), "%" + search + "%"));
            }
            if (status != null) {
                p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("status"), status));
            }
            if (vendorId != null) {
                p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("owner").get("id"), vendorId));
            }
            if (StringUtils.hasText(destination)) {
                p = criteriaBuilder.and(p, criteriaBuilder.like(root.get("destination"), "%" + destination + "%"));
            }
            return p;
        };

        List<Tour> tours = tourRepository.findAll(spec);

        // Chuyển đổi kết quả sang DTO list mới
        return tours.stream()
            .map(this::convertToTourListDto) // Gọi hàm helper mới
            .collect(Collectors.toList());
    }
    private TourListAdminDTO convertToTourListDto(Tour tour) {
        String vendorName = supplierApplicationRepository
                .findByUserAndStatusAndServiceType(
                    tour.getOwner(), 
                    ApplicationStatus.APPROVED, 
                    "TOUR_SUPPLIER"
                )
                .map(SupplierApplication::getBusinessName)
                .orElse(tour.getOwner().getName());

        return TourListAdminDTO.builder()
                .id(tour.getId())
                .name(tour.getName())
                .vendorName(vendorName)
                .destination(tour.getDestination())
                .status(tour.getStatus())
                .build();
    }

}