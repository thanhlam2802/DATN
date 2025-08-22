package backend.backend.implement;

import backend.backend.dao.Hotel.ProvinceDAO;
import backend.backend.dto.Hotel.ProvinceDto;
import backend.backend.entity.Province;
import backend.backend.service.ProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProvinceServiceImpl implements ProvinceService {

    @Autowired
    private ProvinceDAO provinceDAO;

    @Override
    @Transactional(readOnly = true)
    public List<ProvinceDto> getAllProvinces() {
        return provinceDAO.findAll().stream()
                .map(ProvinceDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProvinceDto createProvince(ProvinceDto dto) {
        Province p = new Province();
        p.setName(dto.getName());
        p.setImageUrl(dto.getImageUrl());
        Province saved = provinceDAO.save(p);
        return ProvinceDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public ProvinceDto updateProvince(Integer id, ProvinceDto dto) {
        Province p = provinceDAO.findById(id).orElseThrow(() -> new IllegalArgumentException("Province not found: " + id));
        if (dto.getName() != null) p.setName(dto.getName());
        if (dto.getImageUrl() != null) p.setImageUrl(dto.getImageUrl());
        Province saved = provinceDAO.save(p);
        return ProvinceDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public void deleteProvince(Integer id) {
        if (!provinceDAO.existsById(id)) {
            throw new IllegalArgumentException("Province not found: " + id);
        }
        provinceDAO.deleteById(id);
    }
}