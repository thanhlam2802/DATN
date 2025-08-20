package backend.backend.dto.BusDTO;

import lombok.Data;

@Data
public class CustomerSummaryDto {
    private Integer id;
    private String fullName;
    private String email;
    private String phone;
}
