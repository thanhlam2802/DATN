package backend.backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResultDto {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
}
