package backend.backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyAccountResponseDto {
    private String email;
    private boolean isVerified;
}
