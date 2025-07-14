package backend.backend.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AccountDto {
    private String name;
    private String gender;
    private Date birthday;
}