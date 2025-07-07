package backend.backend.service;



import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    // Inject bean Cloudinary đã được cấu hình
    @Autowired
    private Cloudinary cloudinary;

    /**
     * Tải một tệp lên Cloudinary.
     *
     * @param file   Đối tượng MultipartFile nhận từ request của người dùng.
     * @param folder Thư mục trên Cloudinary để lưu trữ tệp.
     * @return Một Map chứa thông tin về tệp đã tải lên (URL, public_id, v.v.).
     * @throws IOException Nếu có lỗi trong quá trình tải lên.
     */
    public Map upload(MultipartFile file, String folder) throws IOException {
        // Tải lên tệp và trả về kết quả
        return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "auto" // Tự động xác định loại tệp (image, video, raw)
        ));
    }

    /**
     * Xóa một tệp khỏi Cloudinary dựa trên public_id của nó.
     *
     * @param publicId public_id của tệp cần xóa.
     * @return Một Map chứa kết quả của thao tác xóa.
     * @throws IOException Nếu có lỗi trong quá trình xóa.
     */
    public Map delete(String publicId) throws IOException {
        // Xóa tệp và trả về kết quả
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    /**
     * Lưu ý về "Read" và "Update":
     * 1. Read (Đọc): Tương tự như Node.js, sau khi tải lên, bạn sẽ nhận được một
     * `secure_url`.
     * Bạn lưu URL này cùng với `public_id` vào cơ sở dữ liệu và sử dụng nó để hiển
     * thị.
     * 2. Update (Cập nhật): Để cập nhật một tệp, bạn có thể gọi lại hàm `upload` và
     * truyền vào `public_id` của tệp cũ
     * trong các tùy chọn. Cloudinary sẽ tự động ghi đè lên tệp cũ.
     * Ví dụ:
     * cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
     * "public_id", "id_cua_tep_cu",
     * "overwrite", true
     * ));
     */
}
