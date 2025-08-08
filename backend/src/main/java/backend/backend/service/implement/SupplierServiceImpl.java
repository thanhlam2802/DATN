package backend.backend.service.implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import backend.backend.dao.SupplierApplicationDAO;
import backend.backend.dao.UserDAO;
import backend.backend.dto.MailResultDto;
import backend.backend.dto.SupplierApplicationDetailDto;
import backend.backend.dto.SupplierApplicationRequest;
import backend.backend.dto.auth.UserRoleEnum; // Import UserRoleEnum
import backend.backend.entity.ApplicationStatus;
import backend.backend.entity.Role;
import backend.backend.entity.SupplierApplication;
import backend.backend.entity.User;
import backend.backend.entity.UserRole;
import backend.backend.entity.UserRoleId;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.repository.RoleRepository;
import backend.backend.repository.UserRoleRepository;
import backend.backend.service.SupplierService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp triển khai của SupplierService, chứa logic nghiệp vụ cụ thể.
 */
@Service
public class SupplierServiceImpl implements SupplierService {
	@Autowired
    private  SupplierApplicationDAO applicationDAO;
	@Autowired
    private UserDAO userDAO;
	@Autowired
    private RoleRepository roleRepository; 
	@Autowired
    private  UserRoleRepository userRoleRepository;
	@Autowired
    private StreamBridge streamBridge;
   
   
	 @Override
	    @Transactional
	    public SupplierApplication createApplication(SupplierApplicationRequest request, User user) {
	        SupplierApplication application = new SupplierApplication();
	        application.setUser(user);
	        application.setServiceType(request.getServiceType());
	        application.setBusinessName(request.getBusinessName());
	        application.setTaxId(request.getTaxId());
	        application.setAddress(request.getAddress());
	        application.setContactPerson(request.getContactPerson());
	        application.setBusinessPhone(request.getBusinessPhone());
	        application.setStatus(ApplicationStatus.PENDING_REVIEW);

	    
	        return applicationDAO.save(application);
	    }

    
	 @Override
	 @Transactional
	 public void approveApplication(Integer applicationId, String newRoleName) {
	     UserRoleEnum roleEnum;
	     try {
	         roleEnum = UserRoleEnum.valueOf(newRoleName.toUpperCase());
	     } catch (IllegalArgumentException e) {
	         throw new IllegalArgumentException("Invalid role name provided: " + newRoleName);
	     }

	     SupplierApplication application = applicationDAO.findById(applicationId)
	             .orElseThrow(() -> new ResourceNotFoundException("SupplierApplication not found with id: " + applicationId));

	     Role newRole = roleRepository.findByName(roleEnum)
	             .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + newRoleName));

	     User user = application.getUser();
	     boolean isSuperAdmin = user.getUserRoles().stream()
	                                .anyMatch(userRole -> userRole.getRole().getName() == UserRoleEnum.SUPER_ADMIN);

	     if (isSuperAdmin) {
	         throw new IllegalStateException("Cannot change the role of a SUPER_ADMIN.");
	     }

	     user.getUserRoles().stream()
	         .filter(userRole -> userRole.getRole().getName() == UserRoleEnum.USER)
	         .findFirst()
	         .ifPresent(userRoleToDelete -> {
	             user.getUserRoles().remove(userRoleToDelete);
	             userRoleRepository.delete(userRoleToDelete);
	         });

	     UserRole newUserRole = new UserRole();

	
	  UserRoleId userRoleId = new UserRoleId(user.getId(), newRole.getId());
	  newUserRole.setId(userRoleId);

	  newUserRole.setUser(user);
	  newUserRole.setRole(newRole);
	     
	     userRoleRepository.save(newUserRole);

	     application.setStatus(ApplicationStatus.APPROVED);
	     application.setReviewedAt(LocalDateTime.now());
	     applicationDAO.save(application);

	     // ✅ Gửi mail thông báo phê duyệt
	     SupplierApplicationDetailDto dto = mapToDto(application);
	     sendResultMailKafka(user, dto, "accepted");
	 }


	 @Override
	 @Transactional
	 public void rejectApplication(Integer applicationId) {
	     SupplierApplication application = applicationDAO.findById(applicationId)
	             .orElseThrow(() -> new ResourceNotFoundException("SupplierApplication not found with id: " + applicationId));

	     application.setStatus(ApplicationStatus.REJECTED);
	     application.setReviewedAt(LocalDateTime.now());
	     applicationDAO.save(application);

	     // ✅ Gửi mail thông báo từ chối
	     SupplierApplicationDetailDto dto = mapToDto(application);
	     sendResultMailKafka(application.getUser(), dto, "rejected");
	 }

    
    @Override
    public long countPendingApplications() {

    	long count = applicationDAO.countByStatus(ApplicationStatus.PENDING_REVIEW);
		return count; 
    	
    }
    
    @Override
    @Transactional(readOnly = true) // Rất quan trọng để tránh lỗi Lazy Loading
    public List<SupplierApplicationDetailDto> getPendingApplications() {
        // Lấy danh sách đơn đăng ký từ database
        // Dùng JOIN FETCH để lấy luôn thông tin User, tránh lỗi N+1
        List<SupplierApplication> applications = applicationDAO.findByStatus(ApplicationStatus.PENDING_REVIEW);

        // Chuyển đổi danh sách Entity sang danh sách DTO mới
        return applications.stream().map(app -> {
            SupplierApplicationDetailDto dto = new SupplierApplicationDetailDto();
            
            // Map thông tin cơ bản của đơn
            dto.setId(app.getId());
            dto.setStatus(app.getStatus());
            
            // Map thông tin người dùng (nếu có)
            if (app.getUser() != null) {
                dto.setUserName(app.getUser().getName());
                dto.setUserEmail(app.getUser().getEmail());
            }

            // == PHẦN QUAN TRỌNG: Map thông tin công ty từ đơn đăng ký ==
            dto.setServiceType(app.getServiceType());
            dto.setBusinessName(app.getBusinessName());
            dto.setTaxId(app.getTaxId());
            dto.setAddress(app.getAddress());
            dto.setContactPerson(app.getContactPerson());
            dto.setBusinessPhone(app.getBusinessPhone());
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    private void sendResultMailKafka(User user, SupplierApplicationDetailDto appDetail, String decision) {
        MailResultDto dto = MailResultDto.builder()
            .recipient( appDetail.getUserEmail())
            .applicantName( appDetail.getUserName())
            .businessName(appDetail.getBusinessName()) 
            .decision(decision)
            .contactEmail("support@example.com")
            .hotline("0123 456 789")
            .address("123 Đường ABC, TP. XYZ")
            .dashboardLink("https://example.com/dashboard")
            .build();

        streamBridge.send("resultMail-out-0", dto);
    }
    private SupplierApplicationDetailDto mapToDto(SupplierApplication app) {
        SupplierApplicationDetailDto dto = new SupplierApplicationDetailDto();

        dto.setId(app.getId());
        dto.setStatus(app.getStatus());

        if (app.getUser() != null) {
            dto.setUserName(app.getUser().getName());
            dto.setUserEmail(app.getUser().getEmail());
        }

        dto.setServiceType(app.getServiceType());
        dto.setBusinessName(app.getBusinessName());
        dto.setTaxId(app.getTaxId());
        dto.setAddress(app.getAddress());
        dto.setContactPerson(app.getContactPerson());
        dto.setBusinessPhone(app.getBusinessPhone());

        return dto;
    }



}
