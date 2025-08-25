package backend.backend.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
public class UserUpdateRequestDto {
    @Size(min = 2, max = 200, message = "Tên phải từ 2 đến 200 ký tự")
    private String name;

    @Email(message = "Email không hợp lệ")
    private String email;

    private String phone;
    private String address;
    private String gender;
    private Date birthday;
    private String status;
    private List<String> roles;
}
