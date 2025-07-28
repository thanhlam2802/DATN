package backend.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyAccountRequestDto {
    @NotBlank
    private String email;

    @NotBlank
    private String code;
}
