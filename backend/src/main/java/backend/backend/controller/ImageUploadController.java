package backend.backend.controller;

import backend.backend.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ImageUploadController {

    private static final Logger logger = LoggerFactory.getLogger(ImageUploadController.class);

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        
        logger.info("Received image upload request - File name: {}, Size: {} bytes", 
                   file.getOriginalFilename(), file.getSize());
        
        try {
            if (file.isEmpty()) {
                logger.warn("Empty file received");
                response.put("error", "File is empty");
                return ResponseEntity.badRequest().body(response);
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                logger.warn("Invalid file type: {}", contentType);
                response.put("error", "Only image files are allowed");
                return ResponseEntity.badRequest().body(response);
            }

            logger.info("Uploading to Cloudinary folder: hotel-description");
            Map uploadResult = cloudinaryService.upload(file, "hotel-description");
            
            String url = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");
            
            logger.info("Upload successful - URL: {}, Public ID: {}", url, publicId);
            
            if (url == null) {
                logger.error("No URL returned from Cloudinary");
                response.put("error", "Failed to get URL from Cloudinary");
                return ResponseEntity.internalServerError().body(response);
            }

            response.put("url", url);
            response.put("publicId", publicId);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            logger.error("IO Exception during upload: {}", e.getMessage(), e);
            response.put("error", "Upload failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        } catch (Exception e) {
            logger.error("Unexpected error during upload: {}", e.getMessage(), e);
            response.put("error", "Unexpected error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
} 