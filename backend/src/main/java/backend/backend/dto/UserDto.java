package backend.backend.dto;

import lombok.Data;

@Data
public class UserDto {
    private Integer id;
    private String fullName;
    private String email;
    private String phone;
    private String role; 
}