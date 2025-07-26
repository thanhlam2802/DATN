package backend.backend.implement;



import backend.backend.dto.VoucherDTO;
import java.util.Comparator;
import backend.backend.entity.Voucher;
import backend.backend.entity.VoucherStatus;
import backend.backend.entity.VoucherType;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.dao.VoucherDAO;
import backend.backend.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    /**
     * Lấy danh sách các voucher có thể áp dụng và có lợi nhất cho một đơn hàng.
     * @param orderAmount Tổng giá trị đơn hàng hiện tại.
     * @return Danh sách các VoucherDTO đã được sắp xếp.
     */
    public List<VoucherDTO> getSuggestedVouchers(BigDecimal orderAmount) {
        LocalDate today = LocalDate.now();

       
        List<Voucher> activeVouchers = voucherRepository.findAllByStatusAndStartDateLessThanEqualAndExpiryDateGreaterThanEqual(
                VoucherStatus.ACTIVE, today, today);
       
        return activeVouchers.stream()
            .filter(voucher -> {
                boolean isEligible = true;
                if (voucher.getConditionMinAmount() != null && orderAmount.compareTo(voucher.getConditionMinAmount()) < 0) {
                    isEligible = false;
                }
              
                if (voucher.getUsageLimit() != null && voucher.getUsageCount() >= voucher.getUsageLimit()) {
                    isEligible = false;
                }
                return isEligible;
            })

            .sorted(Comparator.comparing((Voucher voucher) -> calculateEffectiveDiscount(voucher, orderAmount)).reversed())
            .map(this::convertToDto)
            .collect(Collectors.toList());
	    }
	    /**
	     * Hàm helper để tính toán số tiền giảm giá thực tế của một voucher.
	     */
	    private BigDecimal calculateEffectiveDiscount(Voucher voucher, BigDecimal orderAmount) {
	        if (voucher.getType() == VoucherType.FIXED_AMOUNT) {
	            return voucher.getDiscountAmount();
	        }
	        if (voucher.getType() == VoucherType.PERCENTAGE) {
	            BigDecimal discount = orderAmount.multiply(BigDecimal.valueOf(voucher.getDiscountPercentage() / 100.0));
	            if (voucher.getMaxDiscountAmount() != null && discount.compareTo(voucher.getMaxDiscountAmount()) > 0) {
	                return voucher.getMaxDiscountAmount();
	            }
	            return discount;
	        }
	        return BigDecimal.ZERO;
	    }

	    private VoucherDTO convertToDto(Voucher voucher) {
	        if (voucher == null) {
	            return null;
	        }

	        VoucherDTO dto = new VoucherDTO();
	        dto.setId(voucher.getId());
	        dto.setCode(voucher.getCode());
	        dto.setName(voucher.getName());
	        dto.setDescription(voucher.getDescription());
	        dto.setStatus(voucher.getStatus());
	        dto.setType(voucher.getType());
	        dto.setDiscountAmount(voucher.getDiscountAmount());
	        dto.setDiscountPercentage(voucher.getDiscountPercentage());
	        dto.setMaxDiscountAmount(voucher.getMaxDiscountAmount());
	        dto.setConditionMinAmount(voucher.getConditionMinAmount());
	        dto.setStartDate(voucher.getStartDate());
	        dto.setExpiryDate(voucher.getExpiryDate());
	        dto.setUsageLimit(voucher.getUsageLimit());
	        dto.setUsageCount(voucher.getUsageCount());
	        
	        return dto;
	    }

}