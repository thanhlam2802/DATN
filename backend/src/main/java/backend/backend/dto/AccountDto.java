package backend.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AccountDto {
	 private Long id;
    @NotBlank
    private String name;

    private GenderEnum gender;

    @NotNull
    private Date birthday;
    private List<String> roles;
}