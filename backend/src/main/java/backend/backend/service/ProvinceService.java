package backend.backend.service;

import backend.backend.dto.Hotel.ProvinceDto;
import backend.backend.entity.Province;

import java.util.List;

public interface ProvinceService {
    List<ProvinceDto> getAllProvinces();
    
   List<Province> getAll();
    
}