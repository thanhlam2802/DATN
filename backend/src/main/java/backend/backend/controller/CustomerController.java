package backend.backend.controller;

import backend.backend.dto.CustomerDto;
import backend.backend.entity.ApiResponse;
import backend.backend.service.CustomerService;
import backend.backend.utils.ResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@CrossOrigin(origins = {
        "https://poly-java-6-fb151.web.app",
        "https://www.travela.io.vn",
        "http://localhost:5173"
})
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerDto>>> getAllCustomers() {
        List<CustomerDto> customers = customerService.getAllCustomers();
        return ResponseFactory.success(customers, "Lấy danh sách khách hàng thành công.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDto>> getCustomerById(@PathVariable Integer id) {
        CustomerDto customer = customerService.getCustomerDtoById(id);
        if (customer == null) {
            return ResponseFactory.error(org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng với id: " + id);
        }
        return ResponseFactory.success(customer, "Lấy thông tin khách hàng thành công.");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerDto>> createCustomer(@RequestBody CustomerDto customerDto) {
        CustomerDto created = customerService.createCustomer(customerDto);
        return ResponseFactory.created(created, "Tạo khách hàng thành công.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerDto>> updateCustomer(@PathVariable Integer id, @RequestBody CustomerDto customerDto) {
        CustomerDto updated = customerService.updateCustomer(id, customerDto);
        return ResponseFactory.success(updated, "Cập nhật khách hàng thành công.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Integer id) {
        customerService.deleteCustomer(id);
        return ResponseFactory.success(null, "Xóa khách hàng thành công.");
    }
} 