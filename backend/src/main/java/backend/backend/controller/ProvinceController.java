package backend.backend.controller;

import backend.backend.dto.Hotel.ProvinceDto;
import backend.backend.entity.ApiResponse;
import backend.backend.service.ProvinceService;
import backend.backend.service.CloudinaryService;
import backend.backend.utils.ResponseFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/provinces")
@CrossOrigin(origins = {
      "https://poly-java-6-fb151.web.app",
      "https://www.travela.io.vn",
      "http://localhost:5173"
  })
public class ProvinceController {

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProvinceDto>>> getAllProvinces() {
        List<ProvinceDto> provinces = provinceService.getAllProvinces();
        return ResponseFactory.success(provinces, "Lấy danh sách tỉnh thành công");
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ProvinceDto>> createProvince(@RequestBody ProvinceDto dto) {
        ProvinceDto created = provinceService.createProvince(dto);
        return ResponseFactory.created(created, "Tạo khu vực thành công.");
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ProvinceDto>> createProvinceMultipart(
            @RequestPart("name") String name,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "imageUrl", required = false) String imageUrl
    ) {
        ProvinceDto dto = new ProvinceDto();
        dto.setName(name);
        String resolvedUrl = imageUrl;
        try {
            if (image != null && !image.isEmpty()) {
                Map<String, Object> uploadResult = cloudinaryService.upload(image, "provinces");
                resolvedUrl = (String) uploadResult.get("secure_url");
            }
        } catch (Exception e) {
            return ResponseFactory.error(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Upload ảnh thất bại: " + e.getMessage());
        }
        dto.setImageUrl(resolvedUrl);
        ProvinceDto created = provinceService.createProvince(dto);
        return ResponseFactory.created(created, "Tạo khu vực thành công.");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ProvinceDto>> updateProvince(@PathVariable Integer id, @RequestBody ProvinceDto dto) {
        ProvinceDto updated = provinceService.updateProvince(id, dto);
        return ResponseFactory.success(updated, "Cập nhật khu vực thành công.");
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ProvinceDto>> updateProvinceMultipart(
            @PathVariable Integer id,
            @RequestPart(value = "name", required = false) String name,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "imageUrl", required = false) String imageUrl
    ) {
        ProvinceDto dto = new ProvinceDto();
        dto.setName(name);
        String resolvedUrl = imageUrl;
        try {
            if (image != null && !image.isEmpty()) {
                Map<String, Object> uploadResult = cloudinaryService.upload(image, "provinces");
                resolvedUrl = (String) uploadResult.get("secure_url");
            }
        } catch (Exception e) {
            return ResponseFactory.error(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Upload ảnh thất bại: " + e.getMessage());
        }
        dto.setImageUrl(resolvedUrl);
        ProvinceDto updated = provinceService.updateProvince(id, dto);
        return ResponseFactory.success(updated, "Cập nhật khu vực thành công.");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteProvince(@PathVariable Integer id) {
        provinceService.deleteProvince(id);
        return ResponseFactory.success(null, "Xóa khu vực thành công.");
    }
}