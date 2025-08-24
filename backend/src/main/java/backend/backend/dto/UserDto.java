package backend.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import backend.backend.dto.auth.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Integer id;
    private String fullName;
    private String email;
    private String phone;
    private String role; 
    
    private String avatar; 
  
   
}