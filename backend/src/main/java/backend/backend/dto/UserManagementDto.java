package backend.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class UserManagementDto {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String gender;
    private Date birthday;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isVerified;
    private String status;
    private List<String> roles;
    private Integer totalBookings;
    private Long totalSpent;
}
