package backend.backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.backend.entity.User;
import backend.backend.entity.Voucher;

public interface VoucherDAO  extends JpaRepository<Voucher, Integer> {

}
