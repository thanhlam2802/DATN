package backend.backend.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Date;

@Data
public class UserCreateRequestDto {
    @NotBlank(message = "Tên không được để trống")
    @Size(min = 2, max = 200, message = "Tên phải từ 2 đến 200 ký tự")
    private String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    private String phone;
    private String address;
    private String gender;
    private Date birthday;
    
    private String role;
    
    private java.util.List<String> roles;
}
