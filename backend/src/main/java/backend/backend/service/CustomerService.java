package backend.backend.service;

import backend.backend.dto.CustomerDto;
import java.util.List;
 
public interface CustomerService {
    CustomerDto getCustomerDtoById(Integer id);
    CustomerDto createCustomer(CustomerDto customerDto);
    CustomerDto updateCustomer(Integer id, CustomerDto customerDto);
    void deleteCustomer(Integer id);
    List<CustomerDto> getAllCustomers();
} 