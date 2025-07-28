package backend.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequestDto {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String newPassword;

}
