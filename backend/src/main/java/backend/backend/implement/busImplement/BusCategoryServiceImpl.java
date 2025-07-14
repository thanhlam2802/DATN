package backend.backend.implement.busImplement;

import backend.backend.dao.Bus.BusCategoryDAO;
import backend.backend.dto.BusDTO.CreateBusCategoryDTO;
import backend.backend.dto.BusDTO.UpdateBusCategoryDTO;
import backend.backend.entity.BusCategory;
import backend.backend.service.busService.BusCategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BusCategoryServiceImpl implements BusCategoryService {

    private final BusCategoryDAO busCategoryRepository;



    @Override
    @Transactional
    public BusCategory createBusCategory(CreateBusCategoryDTO dto) {
        String rawName = dto.name();
        String trimmedName = rawName != null ? rawName.trim() : null;

        if (trimmedName == null || trimmedName.isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống.");
        }

        if (busCategoryRepository.existsByName(trimmedName)) {
            throw new IllegalArgumentException("Tên danh mục '" + trimmedName + "' đã tồn tại.");
        }

        BusCategory busCategory = new BusCategory();
        busCategory.setName(trimmedName);

        return busCategoryRepository.save(busCategory);
    }

    @Override
    @Transactional
    public BusCategory updateBusCategory(Integer id, UpdateBusCategoryDTO dto) {
        BusCategory existingCategory = busCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục xe với ID: " + id));

        Optional.ofNullable(dto.name())
                .map(String::trim)
                .filter(newName -> !newName.isEmpty())
                .ifPresent(newName -> {

                        // Kiểm tra xem tên mới có trùng với danh mục khác không (trừ chính nó)
                        if (busCategoryRepository.findByName(newName)
                                .filter(category -> !category.getId().equals(id))
                                .isPresent()) {
                            throw new IllegalArgumentException("Tên danh mục đã tồn tại, vui lòng chọn tên khác.");
                        }
                        existingCategory.setName(newName);

                });
        return busCategoryRepository.save(existingCategory);
    }

    @Override
    @Transactional
    public void deleteBusCategory(Integer id) {
        if (!busCategoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Không tìm thấy danh mục xe với ID: " + id);
        }
        // Lưu ý: Nếu có các xe Bus đang liên kết với danh mục này,
        // bạn cần xử lý mối quan hệ (ví dụ: gán null, xóa cascade, hoặc cấm xóa)
        // Hiện tại, JPA sẽ ném lỗi nếu có ràng buộc khóa ngoại và bạn cố gắng xóa.
        busCategoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BusCategory getBusCategoryById(Integer id) {
        return busCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục xe với ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusCategory> getAllBusCategories() {
        return busCategoryRepository.findAll();
    }
}
