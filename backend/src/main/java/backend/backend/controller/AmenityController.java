package backend.backend.controller;

import backend.backend.dto.Hotel.AmenityDto;
import backend.backend.entity.ApiResponse;
import backend.backend.service.AmenityService;
import backend.backend.utils.ResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/amenities")
@CrossOrigin(origins = "*")
public class AmenityController {

    @Autowired
    private AmenityService amenityService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AmenityDto>>> getAllAmenities() {
        List<AmenityDto> amenityDtos = amenityService.getAllAmenities();
        return ResponseFactory.success(amenityDtos, "Lấy danh sách tiện ích thành công.");
    }
}