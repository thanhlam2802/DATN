package backend.backend.dto.BusDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusCategoryResponse {
    private Integer id;
    private String name;
    private Integer ownerId; // ✅ THÊM: ID doanh nghiệp sở hữu category

    // Constructor để chuyển đổi từ BusCategory entity sang BusCategoryResponse DTO
    public BusCategoryResponse(backend.backend.entity.BusCategory busCategory) {
        this.id = busCategory.getId();
        this.name = busCategory.getName();
        
        // ✅ THÊM: Map owner information (handle null safely)
        if (busCategory.getOwner() != null) {
            this.ownerId = busCategory.getOwner().getId();
        }
    }
}
