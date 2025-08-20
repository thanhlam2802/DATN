package backend.backend.dao.Bus;

import backend.backend.entity.Bus;
import backend.backend.entity.BusRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BusDAO extends JpaRepository<Bus, Integer> {

    // Phương thức gốc, có thể cần JOIN FETCH nếu BusRoute cũng là lazy
    // Nếu BusRoute là LAZY và bạn cần nó, bạn có thể thêm JOIN FETCH ở đây
    List<Bus> findBusByBusRoutes(List<BusRoute> busRoutes);

    // --- CÁC PHƯƠNG THỨC ĐƯỢC CHỈNH SỬA ĐỂ BAO GỒM JOIN FETCH ---

    // Phương thức findById: Đảm bảo tải đầy đủ chi tiết khi tìm theo ID
    @Query("SELECT b FROM Bus b LEFT JOIN FETCH b.busImages LEFT JOIN FETCH b.category LEFT JOIN FETCH b.owner WHERE b.id = :id")
    Optional<Bus> findById(@Param("id") Integer id); // <-- Sửa đổi phương thức findById gốc

    // Phương thức findAll: Đảm bảo tải đầy đủ chi tiết khi lấy tất cả
    @Query("SELECT b FROM Bus b LEFT JOIN FETCH b.busImages LEFT JOIN FETCH b.category LEFT JOIN FETCH b.owner")
    List<Bus> findAll(); // <-- Sửa đổi phương thức findAll gốc

    // Phương thức findBusByOwner_Id: Đảm bảo tải đầy đủ chi tiết khi tìm theo Owner ID
    // Dòng này có thể bị trùng lặp với findByOwnerId, hãy giữ một cái và dùng nó
    @Query("SELECT b FROM Bus b LEFT JOIN FETCH b.busImages LEFT JOIN FETCH b.category LEFT JOIN FETCH b.owner WHERE b.owner.id = :ownerId")
    List<Bus> findBusByOwner_Id(@Param("ownerId") Integer ownerId); // <-- Sửa đổi phương thức này

    // Phương thức findByOwnerId: Đảm bảo tải đầy đủ chi tiết khi tìm theo Owner ID
    // Nếu bạn muốn dùng tên này, hãy đảm bảo nó là duy nhất và nhất quán
    @Query("SELECT b FROM Bus b LEFT JOIN FETCH b.busImages LEFT JOIN FETCH b.category LEFT JOIN FETCH b.owner WHERE b.owner.id = :ownerId")
    List<Bus> findByOwnerId(@Param("ownerId") Integer ownerId); // <-- Sửa đổi phương thức này

    // 2. Tìm kiếm Bus theo ID của danh mục (BusCategory)
    @Query("SELECT b FROM Bus b LEFT JOIN FETCH b.busImages LEFT JOIN FETCH b.category LEFT JOIN FETCH b.owner WHERE b.category.id = :categoryId")
    List<Bus> findByCategoryId(@Param("categoryId") Integer categoryId); // <-- Sửa đổi phương thức này

    // 3. Tìm kiếm Bus theo tên xe (không phân biệt chữ hoa/thường, chứa một phần)
    @Query("SELECT b FROM Bus b LEFT JOIN FETCH b.busImages LEFT JOIN FETCH b.category LEFT JOIN FETCH b.owner WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Bus> findByNameContainingIgnoreCase(@Param("name") String name); // <-- Sửa đổi phương thức này

    // 4. Tìm kiếm Bus theo biển số xe (không phân biệt chữ hoa/thường, chứa một phần)
    @Query("SELECT b FROM Bus b LEFT JOIN FETCH b.busImages LEFT JOIN FETCH b.category LEFT JOIN FETCH b.owner WHERE LOWER(b.licensePlate) LIKE LOWER(CONCAT('%', :licensePlate, '%'))")
    List<Bus> findByLicensePlateContainingIgnoreCase(@Param("licensePlate") String licensePlate); // <-- Sửa đổi phương thức này

    // 📊 STATISTICS METHODS
    @Query("SELECT COUNT(b) FROM Bus b WHERE b.owner.id = :ownerId")
    Long countByOwnerId(@Param("ownerId") Integer ownerId);

    // 📊 BUS STATUS STATISTICS METHODS
    @Query("SELECT COUNT(b) FROM Bus b WHERE b.owner.id = :ownerId AND EXISTS (SELECT 1 FROM BusSlot bs WHERE bs.bus.id = b.id)")
    Long countActiveBusesByOwnerId(@Param("ownerId") Integer ownerId);

    @Query("SELECT COUNT(b) FROM Bus b WHERE b.owner.id = :ownerId AND NOT EXISTS (SELECT 1 FROM BusSlot bs WHERE bs.bus.id = b.id)")
    Long countInactiveBusesByOwnerId(@Param("ownerId") Integer ownerId);

}
