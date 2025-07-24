package backend.backend.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import backend.backend.entity.Voucher;
import backend.backend.entity.VoucherStatus;

public interface VoucherDAO  extends JpaRepository<Voucher, Integer> {

	/**
     * Tìm kiếm voucher bằng mã code.
     * Rất quan trọng cho chức năng áp dụng voucher của người dùng.
     *
     * @param code Mã voucher cần tìm.
     * @return Optional chứa voucher nếu tìm thấy.
     */
    Optional<Voucher> findByCode(String code);
    /**
     * Tìm tất cả các voucher đang hoạt động và còn hiệu lực trong một ngày nhất định.
     * Dùng để gợi ý các voucher phù hợp cho người dùng.
     *
     * @param status Trạng thái của voucher (ví dụ: ACTIVE).
     * @param startDate Ngày hiện tại, để so sánh với ngày bắt đầu của voucher.
     * @param expiryDate Ngày hiện tại, để so sánh với ngày kết thúc của voucher.
     * @return Danh sách các voucher hợp lệ.
     */
    List<Voucher> findAllByStatusAndStartDateLessThanEqualAndExpiryDateGreaterThanEqual(VoucherStatus status, LocalDate startDate, LocalDate expiryDate);


}
