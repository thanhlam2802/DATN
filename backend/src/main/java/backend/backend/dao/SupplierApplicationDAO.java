
package backend.backend.dao;

import backend.backend.entity.ApplicationStatus;
import backend.backend.entity.SupplierApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface DAO sử dụng Spring Data JPA để tương tác với cơ sở dữ liệu
 * cho thực thể SupplierApplication.
 *
 * JpaRepository đã cung cấp sẵn các phương thức CRUD cơ bản (save, findById, findAll, ...).
 */
@Repository
public interface SupplierApplicationDAO extends JpaRepository<SupplierApplication, Integer> {

    /**
     * Spring Data JPA sẽ tự động tạo ra phương thức truy vấn dựa trên tên của nó.
     * Phương thức này sẽ tìm tất cả các đơn đăng ký có một trạng thái nhất định
     * và sắp xếp chúng theo ngày tạo (cũ nhất trước).
     *
     * @param status Trạng thái cần tìm (ví dụ: PENDING_REVIEW).
     * @return một danh sách các SupplierApplication.
     */
    List<SupplierApplication> findByStatusOrderByCreatedAtAsc(ApplicationStatus status);

    long countByStatus(ApplicationStatus status);

	List<SupplierApplication> findByStatus(ApplicationStatus status);

}
