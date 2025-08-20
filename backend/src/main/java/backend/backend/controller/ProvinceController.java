package backend.backend.controller;

import backend.backend.dto.Hotel.ProvinceDto;
import backend.backend.entity.ApiResponse;
import backend.backend.entity.Province;
import backend.backend.service.ProvinceService;
import backend.backend.utils.ResponseFactory;
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProvinceDto>>> getAllProvinces() {
        List<ProvinceDto> provinces = provinceService.getAllProvinces();
        return ResponseFactory.success(provinces, "Lấy danh sách tỉnh thành công");
    }
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Province>>> getAll() {
        List<Province> provinces = provinceService.getAll();
        return ResponseFactory.success(provinces, "Lấy danh sách tỉnh thành công");
    }
    
    
}