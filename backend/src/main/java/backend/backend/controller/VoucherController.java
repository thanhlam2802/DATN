package backend.backend.controller;

import backend.backend.dto.VoucherDTO;
import backend.backend.service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    // API tạo mới voucher
    @PostMapping
    public ResponseEntity<VoucherDTO> createVoucher(@Valid @RequestBody VoucherDTO voucherDTO) {
        VoucherDTO createdVoucher = voucherService.createVoucher(voucherDTO);
        return new ResponseEntity<>(createdVoucher, HttpStatus.CREATED);
    }

    // API lấy danh sách tất cả voucher
    @GetMapping
    public ResponseEntity<List<VoucherDTO>> getAllVouchers() {
        List<VoucherDTO> vouchers = voucherService.getAllVouchers();
        return ResponseEntity.ok(vouchers);
    }

    // API lấy voucher theo ID
    @GetMapping("/{id}")
    public ResponseEntity<VoucherDTO> getVoucherById(@PathVariable Integer id) {
        VoucherDTO voucher = voucherService.getVoucherById(id);
        return ResponseEntity.ok(voucher);
    }

    // API lấy voucher theo CODE
    @GetMapping("/code/{code}")
    public ResponseEntity<VoucherDTO> getVoucherByCode(@PathVariable String code) {
        VoucherDTO voucher = voucherService.getVoucherByCode(code);
        return ResponseEntity.ok(voucher);
    }

    // API cập nhật voucher
    @PutMapping("/{id}")
    public ResponseEntity<VoucherDTO> updateVoucher(@PathVariable Integer id, @Valid @RequestBody VoucherDTO voucherDTO) {
        VoucherDTO updatedVoucher = voucherService.updateVoucher(id, voucherDTO);
        return ResponseEntity.ok(updatedVoucher);
    }

    // API xóa voucher
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable Integer id) {
        voucherService.deleteVoucher(id);
        return ResponseEntity.noContent().build();
    }
}