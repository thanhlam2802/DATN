package backend.backend.implement;



import backend.backend.dto.VoucherDTO;
import backend.backend.entity.Voucher;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.dao.VoucherDAO;
import backend.backend.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

	 @Autowired VoucherDAO voucherRepository;
    private final ModelMapper modelMapper;

    @Override
    public VoucherDTO createVoucher(VoucherDTO voucherDTO) {
        // Chuyển DTO thành Entity
        Voucher voucher = modelMapper.map(voucherDTO, Voucher.class);
        
        // Logic nghiệp vụ bổ sung (nếu cần)
        // Ví dụ: kiểm tra mã code đã tồn tại chưa
        if(voucherRepository.findByCode(voucher.getCode()).isPresent()) {
            throw new IllegalArgumentException("Mã voucher đã tồn tại.");
        }

     
        Voucher savedVoucher = voucherRepository.save(voucher);

        return modelMapper.map(savedVoucher, VoucherDTO.class);
    }

    @Override
    public VoucherDTO updateVoucher(Integer id, VoucherDTO voucherDTO) {
        // Tìm voucher hiện có
        Voucher existingVoucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy voucher với id: " + id));

        // Cập nhật các trường từ DTO
        modelMapper.map(voucherDTO, existingVoucher);
        existingVoucher.setId(id); 
        existingVoucher.setUpdatedAt(LocalDateTime.now());

        // Lưu lại
        Voucher updatedVoucher = voucherRepository.save(existingVoucher);
        return modelMapper.map(updatedVoucher, VoucherDTO.class);
    }

    @Override
    public void deleteVoucher(Integer id) {
        if (!voucherRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy voucher với id: " + id);
        }
        voucherRepository.deleteById(id);
    }

    @Override
    public VoucherDTO getVoucherById(Integer id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy voucher với id: " + id));
        return modelMapper.map(voucher, VoucherDTO.class);
    }

    @Override
    public VoucherDTO getVoucherByCode(String code) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy voucher với mã: " + code));
        return modelMapper.map(voucher, VoucherDTO.class);
    }

    @Override
    public List<VoucherDTO> getAllVouchers() {
        List<Voucher> vouchers = voucherRepository.findAll();
        return vouchers.stream()
                .map(voucher -> modelMapper.map(voucher, VoucherDTO.class))
                .collect(Collectors.toList());
    }
}