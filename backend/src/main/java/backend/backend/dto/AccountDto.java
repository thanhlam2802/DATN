package backend.backend.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AccountDto {
	 private Long id;
    private String name;
    private String gender;
    private Date birthday;
    private List<String> roles;
}