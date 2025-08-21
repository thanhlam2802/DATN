package backend.backend.service;

import backend.backend.dto.BusDTO.CreateImageInput;
import backend.backend.entity.Image;

public interface ImageService {
    Image createImage(CreateImageInput input);
}
