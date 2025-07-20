package backend.backend.dto.auth;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {
    @Email
    private String email;

    @NotBlank
    private String name;

    @NotBlank
    private String password;

    @NotNull
    private UserRoleEnum role;
}
