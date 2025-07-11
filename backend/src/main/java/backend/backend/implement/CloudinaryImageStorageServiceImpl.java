package backend.backend.implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import backend.backend.service.CloudinaryService;
import backend.backend.service.ImageStorageService;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryImageStorageServiceImpl implements ImageStorageService {

    @Autowired
    private CloudinaryService cloudinaryService;


    @Override
    public Map<String, String> uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Không thể tải lên một tệp trống.");
        }

        // Gọi đến CloudinaryService để tải file lên thư mục "tours"
        Map uploadResult = cloudinaryService.upload(file, "tours");

        String url = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        if (url == null || publicId == null) {
            throw new IOException("Lỗi khi tải tệp lên Cloudinary, không nhận được URL hoặc Public ID.");
        }

        return Map.of("url", url, "publicId", publicId);
    }

    @Override
    public void deleteImage2(String url) {
        if (url == null || url.trim().isEmpty()) return;
        // Tách publicId từ url Cloudinary
        // Ví dụ: https://res.cloudinary.com/demo/image/upload/v1234567890/tours/abc123.jpg
        // publicId là phần sau /upload/ và trước .jpg
        try {
            String[] parts = url.split("/upload/");
            if (parts.length < 2) return;
            String publicIdWithExt = parts[1];
            String publicId = publicIdWithExt.replaceAll("\\.[a-zA-Z0-9]+$", "");
            cloudinaryService.delete(publicId);
        } catch (Exception e) {
            // log lỗi nếu cần
        }
    }

    @Override
       public void deleteImage(String publicId) throws IOException {
        if (publicId == null || publicId.trim().isEmpty()) {
            return;
        }
        cloudinaryService.delete(publicId);
    }
}