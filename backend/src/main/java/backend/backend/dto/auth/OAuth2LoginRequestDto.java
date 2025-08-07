package backend.backend.dto.auth;

import lombok.Data;

@Data
public class OAuth2LoginRequestDto {
    private String email;
    private String name;
    private AuthProvider authProvider;
}
