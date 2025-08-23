package backend.backend.controller;

import backend.backend.dto.Hotel.AmenityDto;
import backend.backend.entity.ApiResponse;
import backend.backend.service.AmenityService;
import backend.backend.utils.ResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/amenities")
@CrossOrigin(origins = {
        "https://poly-java-6-fb151.web.app",
        "https://www.travela.io.vn",
        "http://localhost:5173"
})
public class AmenityController {

    @Autowired
    private AmenityService amenityService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AmenityDto>>> getAllAmenities() {
        List<AmenityDto> amenityDtos = amenityService.getAllAmenities();
        return ResponseFactory.success(amenityDtos, "Lấy danh sách tiện ích thành công.");
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AmenityDto>> createAmenity(@RequestBody AmenityDto amenityDto) {
        AmenityDto createdAmenity = amenityService.createAmenity(amenityDto);
        return ResponseFactory.success(createdAmenity, "Tạo tiện ích thành công.");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AmenityDto>> updateAmenity(@PathVariable Integer id, @RequestBody AmenityDto amenityDto) {
        amenityDto.setId(id);
        AmenityDto updatedAmenity = amenityService.updateAmenity(amenityDto);
        return ResponseFactory.success(updatedAmenity, "Cập nhật tiện ích thành công.");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAmenity(@PathVariable Integer id) {
        amenityService.deleteAmenity(id);
        return ResponseFactory.success(null, "Xóa tiện ích thành công.");
    }
}