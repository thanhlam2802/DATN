package backend.backend.service;

import backend.backend.dto.Hotel.ProvinceDto;
import java.util.List;

public interface ProvinceService {
    List<ProvinceDto> getAllProvinces();
}