package backend.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Size(min = 10, max = 20)
    @Pattern(regexp = "^[0-9]*$")
    private String phone;

    @NotBlank
    private String name;

    @NotBlank
    private String password;
}
