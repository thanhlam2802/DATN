package backend.backend.dto.BusDTO;

public class BusCategoryResponse {
    private Integer id;
    private String name;

    // Constructor để chuyển đổi từ BusCategory entity sang BusCategoryResponse DTO
    public BusCategoryResponse(backend.backend.entity.BusCategory busCategory) {
        this.id = busCategory.getId();
        this.name = busCategory.getName();
    }
}
