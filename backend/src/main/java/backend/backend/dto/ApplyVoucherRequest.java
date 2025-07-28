package backend.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplyVoucherRequest {

    @NotBlank(message = "Voucher code cannot be blank")
    private String voucherCode;
}