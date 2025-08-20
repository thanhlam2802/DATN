package backend.backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierApplicationRequest {
	@NotBlank(message = "Service type is required.")
    private String serviceType; 
    @NotBlank(message = "Business name is required.")
    @Size(min = 2, max = 100, message = "Business name must be between 2 and 100 characters.")
    private String businessName;

    @NotBlank(message = "Tax ID is required.")
    @Size(min = 5, max = 20, message = "Tax ID must be between 5 and 20 characters.")
    private String taxId;

    @NotBlank(message = "Business address is required.")
    @Size(max = 255, message = "Address cannot be longer than 255 characters.")
    private String address;

    @NotBlank(message = "Contact person is required.")
    @Size(min = 2, max = 100, message = "Contact person name must be between 2 and 100 characters.")
    private String contactPerson;

    @NotBlank(message = "Business phone number is required.")
    @Size(min = 9, max = 15, message = "Phone number must be between 9 and 15 digits.")
    private String businessPhone;

    
}
