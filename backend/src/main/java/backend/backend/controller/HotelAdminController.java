package backend.backend.controller;

import backend.backend.dto.Hotel.HotelDetailDto;
import backend.backend.entity.ApiResponse;
import backend.backend.service.HotelService;
import backend.backend.utils.ResponseFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/hotels")
@CrossOrigin(origins = "*")
public class HotelAdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(HotelAdminController.class);
    
    @Autowired
    private HotelService hotelService;

    @PostMapping
    public ResponseEntity<ApiResponse<HotelDetailDto>> createHotel(MultipartHttpServletRequest request) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String hotelJson = request.getParameter("hotel");
            HotelDetailDto hotelDto = mapper.readValue(hotelJson, HotelDetailDto.class);
            List<MultipartFile> images = request.getFiles("images");
            Map<String, List<MultipartFile>> roomImagesMap = new java.util.HashMap<>();
            java.util.Iterator<String> fileNames = request.getFileNames();
            while (fileNames.hasNext()) {
                String name = fileNames.next();
                if (name.toLowerCase().startsWith("roomimages_")) {
                    String key = name.toLowerCase();
                    List<MultipartFile> files = request.getFiles(name);
                    roomImagesMap.put(key, files);
                }
            }
            HotelDetailDto created = hotelService.createHotel(hotelDto, images, roomImagesMap);
            return ResponseFactory.success(created, "Tạo khách sạn thành công");
        } catch (Exception e) {
            logger.error("[CREATE] Error creating hotel: {}", e.getMessage(), e);
            return ResponseFactory.error(org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Lỗi parse JSON: " + e.getMessage(), null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HotelDetailDto>> updateHotel(
            @PathVariable Integer id,
            MultipartHttpServletRequest request) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String hotelJson = request.getParameter("hotel");
            HotelDetailDto hotelDto = mapper.readValue(hotelJson, HotelDetailDto.class);
            List<MultipartFile> images = request.getFiles("images");
            List<String> deleteImageUrls = request.getParameterValues("deleteImageUrls") != null
                    ? java.util.Arrays.asList(request.getParameterValues("deleteImageUrls"))
                    : null;
            Map<String, List<MultipartFile>> roomImagesMap = new java.util.HashMap<>();
            Map<String, List<String>> deleteRoomImageUrlsMap = new java.util.HashMap<>();
            java.util.Iterator<String> fileNames = request.getFileNames();
            while (fileNames.hasNext()) {
                String name = fileNames.next();
                if (name.toLowerCase().startsWith("roomimages_")) {
                    String key = name.toLowerCase();
                    List<MultipartFile> files = request.getFiles(name);
                    roomImagesMap.put(key, files);
                }
            }
            java.util.Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String name = paramNames.nextElement();
                if (name.startsWith("deleteRoomImageUrls_")) {
                    String key = name.substring("deleteRoomImageUrls_".length());
                    String[] arr = request.getParameterValues(name);
                    if (arr != null && arr.length > 0) {
                        List<String> urls = new java.util.ArrayList<>();
                        for (String s : arr) {
                            if (s.startsWith("[")) {
                                urls.addAll(java.util.Arrays.asList(mapper.readValue(s, String[].class)));
                            } else {
                                urls.add(s);
                            }
                        }
                        deleteRoomImageUrlsMap.put(key, urls);
                    }
                }
            }
            HotelDetailDto updated = hotelService.updateHotel(id, hotelDto, images, deleteImageUrls, roomImagesMap,
                    deleteRoomImageUrlsMap);
            return ResponseFactory.success(updated, "Cập nhật khách sạn thành công");
        } catch (Exception e) {
            logger.error("[UPDATE] Error updating hotel ID {}: {}", id, e.getMessage(), e);
            return ResponseFactory.error(org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Lỗi parse JSON: " + e.getMessage(), null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHotel(@PathVariable Integer id) {
        hotelService.deleteHotel(id);
        return ResponseFactory.success(null, "Xóa khách sạn thành công");
    }
}