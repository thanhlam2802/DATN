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
	public List<Province> getAll() {
		
		return provinceDAO.findAll();
	}
}