package backend.backend.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import backend.backend.entity.Voucher;

public interface VoucherDAO  extends JpaRepository<Voucher, Integer> {

	/**
     * Tìm kiếm voucher bằng mã code.
     * Rất quan trọng cho chức năng áp dụng voucher của người dùng.
     *
     * @param code Mã voucher cần tìm.
     * @return Optional chứa voucher nếu tìm thấy.
     */
    Optional<Voucher> findByCode(String code);
}
