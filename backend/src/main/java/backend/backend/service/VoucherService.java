package backend.backend.service;

import backend.backend.dto.VoucherDTO;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VoucherService {
    VoucherDTO createVoucher(VoucherDTO voucherDTO);
    VoucherDTO updateVoucher(Integer id, VoucherDTO voucherDTO);
    void deleteVoucher(Integer id);
    VoucherDTO getVoucherById(Integer id);
    VoucherDTO getVoucherByCode(String code);
    Page<VoucherDTO>  getAllVouchers(Pageable pageable, String query);
    
    List<VoucherDTO> getSuggestedVouchers(BigDecimal orderAmount);
    
}