package backend.backend.controller.BusController;

import backend.backend.dto.BusDTO.CreateImageInput;

import backend.backend.entity.Image;
import backend.backend.implement.ImageServiceImpl;
import backend.backend.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ImageMutationResolver {

    private final ImageService imageService;

    // Annotation @MutationMapping chỉ định phương thức này xử lý một mutation GraphQL
    // Tên của mutation trong schema là "createImage"
    @MutationMapping
    public Image createImage(@Argument CreateImageInput input) {
        // Gọi service để tạo và lưu bản ghi ảnh vào DB
        return imageService.createImage(input);
    }

    // Nếu bạn có các mutation khác như updateImage, deleteImage, bạn sẽ định nghĩa chúng ở đây:
    // @MutationMapping
    // public Image updateImage(@Argument Integer id, @Argument UpdateImageInput input) { ... }

    // @MutationMapping
    // public Boolean deleteImage(@Argument Integer id) { ... }
}