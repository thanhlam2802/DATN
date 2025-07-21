package backend.backend.dto.auth;

import lombok.Data;

@Data
public class VerifyOtpRequestDto {
    private String code;
}
