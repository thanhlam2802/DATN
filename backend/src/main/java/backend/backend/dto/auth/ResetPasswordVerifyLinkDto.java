package backend.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordVerifyLinkDto {
    @NotBlank
    private String resetToken;
    @NotBlank
    private String otpCode;
}
