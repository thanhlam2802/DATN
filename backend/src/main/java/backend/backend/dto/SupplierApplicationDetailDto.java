package backend.backend.dto;

import backend.backend.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierApplicationDetailDto {
    // Thông tin cơ bản của đơn
    private Long id;
    private ApplicationStatus status;

    // Thông tin người nộp đơn
    private String userName;
    private String userEmail;

    // Thông tin công ty (lấy từ đơn đăng ký)
    private String serviceType;
    private String businessName;
    private String taxId;
    private String address;
    private String contactPerson;
    private String businessPhone;
}