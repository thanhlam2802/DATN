package backend.backend.utils;



import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Arrays;
import java.util.UUID;

/**
 * Lớp tiện ích để xử lý các hoạt động liên quan đến tải lên và quản lý file.
 */
public class FileUploadUtil {

    /**
     * Lưu một file duy nhất vào thư mục được chỉ định.
     *
     * @param uploadDir     Thư mục để lưu file (ví dụ: "user-photos/").
     * @param fileName      Tên file sẽ được lưu.
     * @param multipartFile Đối tượng file được tải lên từ request.
     * @throws IOException Nếu có lỗi trong quá trình tạo thư mục hoặc sao chép file.
     */
    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        // Tạo thư mục nếu nó chưa tồn tại.
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Sử dụng try-with-resources để đảm bảo inputStream được đóng lại sau khi dùng.
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            // Sao chép nội dung file vào đường dẫn đích, ghi đè nếu file đã tồn tại.
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            // Ném ra một exception rõ ràng hơn.
            throw new IOException("Không thể lưu file: " + fileName, ioe);
        }
    }

    /**
     * Lưu một danh sách nhiều file.
     *
     * @param uploadDir Thư mục để lưu các file.
     * @param files     Mảng các đối tượng MultipartFile cần lưu.
     * @throws IOException Nếu có lỗi xảy ra với bất kỳ file nào.
     */
    public static void saveFiles(String uploadDir, MultipartFile[] files) throws IOException {
        // Chuyển đổi mảng thành một Stream và xử lý từng file.
        Arrays.stream(files).forEach(file -> {
            // Tạo tên file duy nhất để tránh ghi đè lên nhau.
            String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());
            try {
                saveFile(uploadDir, uniqueFileName, file);
            } catch (IOException e) {
               
                throw new RuntimeException("Không thể lưu file: " + file.getOriginalFilename(), e);
            }
        });
    }

    /**
     * Tạo ra một tên file duy nhất bằng cách thêm UUID vào trước tên file gốc.
     * Điều này giúp ngăn chặn việc các file có cùng tên ghi đè lên nhau.
     *
     * @param originalFileName Tên file gốc.
     * @return Tên file duy nhất (ví dụ: "uuid-ten-file.jpg").
     */
    public static String generateUniqueFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isEmpty()) {
            return UUID.randomUUID().toString();
        }
        return UUID.randomUUID() + "-" + originalFileName;
    }

    /**
     * Xóa sạch tất cả các file trong một thư mục.
     * Hữu ích khi cần dọn dẹp thư mục tạm.
     *
     * @param dirPath Đường dẫn của thư mục cần xóa.
     */
    public static void cleanDir(String dirPath) {
        Path dir = Paths.get(dirPath);
        try {
            Files.list(dir).forEach(file -> {
                if (!Files.isDirectory(file)) {
                    try {
                        Files.delete(file);
                    } catch (IOException ex) {
                        System.err.println("Không thể xóa file: " + file);
                    }
                }
            });
        } catch (IOException e) {
            System.err.println("Không thể liệt kê thư mục: " + dir);
        }
    }

    /**
     * Xóa một file cụ thể.
     *
     * @param filePath Đường dẫn đầy đủ tới file cần xóa.
     */
    public static void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }
}