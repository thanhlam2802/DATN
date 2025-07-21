package backend.backend.service.implement;

import backend.backend.dao.CustomerDAO;
import backend.backend.dto.CustomerDto;
import backend.backend.entity.Customer;
import backend.backend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
} 