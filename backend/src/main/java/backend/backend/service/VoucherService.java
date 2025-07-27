package backend.backend.service;

import backend.backend.dto.VoucherDTO;

import java.math.BigDecimal;
import java.util.List;

public interface VoucherService {
    VoucherDTO createVoucher(VoucherDTO voucherDTO);
    VoucherDTO updateVoucher(Integer id, VoucherDTO voucherDTO);
    void deleteVoucher(Integer id);
    VoucherDTO getVoucherById(Integer id);
    VoucherDTO getVoucherByCode(String code);
    List<VoucherDTO> getAllVouchers();
    
    List<VoucherDTO> getSuggestedVouchers(BigDecimal orderAmount);
    
}