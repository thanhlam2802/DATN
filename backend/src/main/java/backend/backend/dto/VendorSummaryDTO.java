package backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorSummaryDTO {
    private Integer vendorId;
    private String vendorName;
    private long tourCount;
}