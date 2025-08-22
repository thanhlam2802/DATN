package backend.backend.implement;

import backend.backend.dto.BusDTO.CreateImageInput;
import backend.backend.entity.Image;
import backend.backend.dao.ImageDAO;

import backend.backend.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageDAO imageDAO;

    @Autowired
    public ImageServiceImpl(ImageDAO imageDAO) {
        this.imageDAO = imageDAO;
    }

    @Override
    public Image createImage(CreateImageInput input) {
        if (input.url() == null || input.publicId() == null) {
            throw new IllegalArgumentException("URL and PublicID cannot be null");
        }
        
        Image image = new Image();
        image.setUrl(input.url());
        image.setPublicId(input.publicId());
        image.setAltText(input.altText());
        
        // The save method from JpaRepository will return the saved entity,
        // which now includes the generated ID.
        return imageDAO.save(image);
    }


}