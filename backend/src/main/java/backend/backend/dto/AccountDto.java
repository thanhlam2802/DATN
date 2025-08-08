package backend.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class AccountDto {
    @NotBlank
    private String name;

    private GenderEnum gender;

    @NotNull
    private Date birthday;
}