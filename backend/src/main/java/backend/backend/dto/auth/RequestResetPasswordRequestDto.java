package backend.backend.dto.auth;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class RequestResetPasswordRequestDto {
    @Email
    private String email;
}
