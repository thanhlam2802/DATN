package backend.backend.service;

import backend.backend.dto.Hotel.ProvinceDto;
import java.util.List;

public interface ProvinceService {
    List<ProvinceDto> getAllProvinces();

    ProvinceDto createProvince(ProvinceDto dto);

    ProvinceDto updateProvince(Integer id, ProvinceDto dto);

    void deleteProvince(Integer id);
}