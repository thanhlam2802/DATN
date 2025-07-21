package backend.backend.service;

import backend.backend.dto.CustomerDto;
 
public interface CustomerService {
    CustomerDto getCustomerDtoById(Integer id);
} 