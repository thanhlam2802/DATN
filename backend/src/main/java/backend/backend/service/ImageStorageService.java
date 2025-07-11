package backend.backend.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

/**
 * Interface định nghĩa các chức năng cho việc lưu trữ và quản lý hình ảnh.
 */
public interface ImageStorageService {

    /**
     * Tải một file ảnh lên dịch vụ lưu trữ.
     *
     * @param file Đối tượng MultipartFile từ request.
     * @return Một Map chứa "url" và "publicId" của ảnh đã tải lên.
     * @throws IOException
     */
    Map<String, String> uploadImage(MultipartFile file) throws IOException;

    /**
     * Xóa một ảnh khỏi dịch vụ lưu trữ dựa trên mã định danh của nó.
     *
     * @param publicId mã định danh (ví dụ: public_id của Cloudinary) của ảnh cần xóa.
     * @throws IOException
     */
     void deleteImage(String publicId) throws IOException;
    void deleteImage2(String url);
}