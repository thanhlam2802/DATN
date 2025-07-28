package backend.backend.service.implement;

import backend.backend.dao.CustomerDAO;
import backend.backend.dto.CustomerDto;
import backend.backend.entity.Customer;
import backend.backend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerDAO customerDAO;

    @Override
    public CustomerDto getCustomerDtoById(Integer id) {
        Customer customer = customerDAO.findById(id).orElse(null);
        if (customer == null) return null;
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setFullName(customer.getFullName());
        dto.setPhone(customer.getPhone());
        dto.setEmail(customer.getEmail());
        dto.setGender(customer.getGender());
        dto.setDob(customer.getDob());
        dto.setPassport(customer.getPassport());
        return dto;
    }

    @Override
    public CustomerDto createCustomer(CustomerDto customerDto) {
        Customer customer = new Customer();
        customer.setFullName(customerDto.getFullName());
        customer.setPhone(customerDto.getPhone());
        customer.setEmail(customerDto.getEmail());
        customer.setGender(customerDto.getGender());
        customer.setDob(customerDto.getDob());
        customer.setPassport(customerDto.getPassport());
        Customer saved = customerDAO.save(customer);
        return toDto(saved);
    }

    @Override
    public CustomerDto updateCustomer(Integer id, CustomerDto customerDto) {
        Customer customer = customerDAO.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setFullName(customerDto.getFullName());
        customer.setPhone(customerDto.getPhone());
        customer.setEmail(customerDto.getEmail());
        customer.setGender(customerDto.getGender());
        customer.setDob(customerDto.getDob());
        customer.setPassport(customerDto.getPassport());
        Customer saved = customerDAO.save(customer);
        return toDto(saved);
    }

    @Override
    public void deleteCustomer(Integer id) {
        customerDAO.deleteById(id);
    }

    @Override
    public List<CustomerDto> getAllCustomers() {
        List<Customer> customers = customerDAO.findAll();
        return customers.stream().map(this::toDto).toList();
    }

    private CustomerDto toDto(Customer customer) {
        if (customer == null) return null;
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setFullName(customer.getFullName());
        dto.setPhone(customer.getPhone());
        dto.setEmail(customer.getEmail());
        dto.setGender(customer.getGender());
        dto.setDob(customer.getDob());
        dto.setPassport(customer.getPassport());
        return dto;
    }
} 