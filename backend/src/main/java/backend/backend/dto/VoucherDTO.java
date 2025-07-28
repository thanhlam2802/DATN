package backend.backend.dto;

import backend.backend.entity.VoucherStatus;
import backend.backend.entity.VoucherType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VoucherDTO {
    private Integer id;

    @NotBlank(message = "Mã voucher không được để trống")
    @Size(max = 50, message = "Mã voucher không được vượt quá 50 ký tự")
    private String code;

    @NotBlank(message = "Tên voucher không được để trống")
    @Size(max = 255, message = "Tên voucher không được vượt quá 255 ký tự")
    private String name;

    private String description;

    @NotNull(message = "Trạng thái không được để trống")
    private VoucherStatus status;

    @NotNull(message = "Loại voucher không được để trống")
    private VoucherType type;

    private BigDecimal discountAmount;

    @Min(value = 0, message = "Phần trăm giảm giá phải lớn hơn hoặc bằng 0")
    @Max(value = 100, message = "Phần trăm giảm giá phải nhỏ hơn hoặc bằng 100")
    private Integer discountPercentage;

    private BigDecimal maxDiscountAmount;

    @DecimalMin(value = "0.0", inclusive = false, message = "Điều kiện giá trị đơn hàng phải lớn hơn 0")
    private BigDecimal conditionMinAmount;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @FutureOrPresent(message = "Ngày bắt đầu phải là ngày hiện tại hoặc tương lai")
    private LocalDate startDate;

    @NotNull(message = "Ngày hết hạn không được để trống")
    @Future(message = "Ngày hết hạn phải là một ngày trong tương lai")
    private LocalDate expiryDate;

    @Min(value = 1, message = "Giới hạn sử dụng phải lớn hơn 0")
    private Integer usageLimit;

    private Integer userUsageLimit = 1;
    
    private Integer usageCount = 0;
}