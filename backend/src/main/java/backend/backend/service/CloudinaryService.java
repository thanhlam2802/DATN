package backend.backend.service;





import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Service để xử lý các hoạt động liên quan đến Cloudinary.
 * Bao gồm tải lên, xóa và cập nhật với tính năng tối ưu hóa dung lượng.
 */
@Service
public class CloudinaryService {


    @Autowired
    private Cloudinary cloudinary;

    /**
     * Tải một tệp lên Cloudinary và tự động giảm dung lượng.
     *
     * @param file   Đối tượng MultipartFile nhận từ request của người dùng.
     * @param folder Thư mục trên Cloudinary để lưu trữ tệp.
     * @return Một Map chứa thông tin về tệp đã tải lên (URL, public_id, v.v.).
     * @throws IOException Nếu có lỗi trong quá trình tải lên.
     */
    public Map upload(MultipartFile file, String folder) throws IOException {
        // Tải lên tệp và trả về kết quả.
        // Thêm các tham số 'quality' và 'fetch_format' để tối ưu hóa.
        return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "auto", // Tự động xác định loại tệp (image, video, raw)
                "quality", "auto", // Tự động điều chỉnh chất lượng để cân bằng giữa chất lượng và dung lượng
                "fetch_format", "auto" // Tự động chọn định dạng tệp tối ưu nhất (ví dụ: WebP)
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
     * Cập nhật một tệp trên Cloudinary bằng cách tải lên một tệp mới để ghi đè.
     * Quá trình này cũng sẽ áp dụng các biến đổi để giảm dung lượng.
     *
     * @param publicId public_id của tệp cần cập nhật.
     * @param file     Đối tượng MultipartFile mới.
     * @return Một Map chứa thông tin về tệp đã cập nhật.
     * @throws IOException Nếu có lỗi trong quá trình cập nhật.
     */
    public Map update(String publicId, MultipartFile file) throws IOException {
        // Tải lên tệp mới để ghi đè lên tệp cũ có cùng public_id
        return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "public_id", publicId,
                "overwrite", true,
                "resource_type", "auto",
                "quality", "auto",
                "fetch_format", "auto"));
    }

    /**
     * Lưu ý về "Read" (Đọc):
     * Sau khi tải lên thành công, bạn sẽ nhận được một `secure_url`.
     * Bạn lưu URL này cùng với `public_id` vào cơ sở dữ liệu và sử dụng nó để hiển
     * thị.
     * URL này đã trỏ đến phiên bản ảnh đã được tối ưu hóa.
     */
}