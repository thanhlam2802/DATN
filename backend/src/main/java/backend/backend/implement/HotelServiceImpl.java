package backend.backend.implement;

import backend.backend.dao.ReviewDAO;
import backend.backend.dao.Hotel.HotelDAO;
import backend.backend.dao.Hotel.HotelRoomVariantDAO;
import backend.backend.dao.Hotel.HotelImageDAO;
import backend.backend.dao.ImageDAO;
import backend.backend.dao.Hotel.HotelRoomDAO;
import backend.backend.dao.Hotel.AmenityDAO;
import backend.backend.dto.*;
import backend.backend.dto.Hotel.HotelDetailDto;
import backend.backend.dto.Hotel.HotelDto;
import backend.backend.dto.Hotel.HotelSearchRequestDto;
import backend.backend.entity.Hotel;
import backend.backend.entity.Review;
import backend.backend.exception.ResourceNotFoundException;
import backend.backend.service.HotelService;
import backend.backend.service.CloudinaryService;
import backend.backend.specification.HotelSpecifications;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;
import backend.backend.mapper.HotelMapper;
import backend.backend.entity.HotelImage;
import backend.backend.entity.HotelImageId;
import backend.backend.entity.Image;
import backend.backend.entity.Province;
import java.util.Map;
import backend.backend.entity.HotelRoom;
import backend.backend.entity.HotelRoomVariant;
import backend.backend.dto.Hotel.HotelRoomDto;
import backend.backend.dto.Hotel.HotelRoomVariantDto;
import backend.backend.entity.Amenity;
import backend.backend.dao.Hotel.HotelRoomImageDAO;
import backend.backend.entity.HotelRoomImage;
import backend.backend.entity.HotelRoomImageId;
import backend.backend.dao.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import backend.backend.dao.HotelBookingDAO;
import backend.backend.entity.User;

@Service
public class HotelServiceImpl implements HotelService {

    private static final Logger logger = LoggerFactory.getLogger(HotelServiceImpl.class);

    @Autowired
    private HotelDAO hotelDAO;
    @Autowired
    private ReviewDAO reviewDAO;
    @Autowired
    private HotelRoomVariantDAO hotelRoomVariantDAO;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private HotelImageDAO hotelImageDAO;
    @Autowired
    private ImageDAO imageDAO;
    @Autowired
    private HotelRoomDAO hotelRoomDAO;
    @Autowired
    private AmenityDAO amenityDAO;
    @Autowired
    private HotelMapper hotelMapper;
    @Autowired
    private HotelRoomImageDAO hotelRoomImageDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private HotelBookingDAO hotelBookingDAO;

    @Override
    @Transactional(readOnly = true)
    public PageDto<HotelDto> searchHotels(HotelSearchRequestDto requestDto) {
        Specification<Hotel> spec = HotelSpecifications.from(requestDto);
        Pageable pageable = createPageable(requestDto);

        Page<HotelDto> hotelDtoPage = hotelDAO.findWithFiltersAndProjection(spec, pageable);

        return new PageDto<>(
                hotelDtoPage.getContent(),
                hotelDtoPage.getNumber(),
                hotelDtoPage.getSize(),
                hotelDtoPage.getTotalElements(),
                hotelDtoPage.getTotalPages());
    }

    @Override
    @Transactional(readOnly = true)
    public HotelDetailDto getHotelDetails(Integer hotelId, HotelSearchRequestDto requestDto) {
        Hotel hotel = hotelDAO.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách sạn với ID: " + hotelId));

        if (!"APPROVED".equals(hotel.getApprovalStatus()) || !"ACTIVE".equals(hotel.getStatus())) {
            throw new ResourceNotFoundException("Khách sạn này không khả dụng");
        }

        Double rating = reviewDAO.getAverageRatingByEntity("Hotel", hotelId);
        Integer reviewCount = reviewDAO.countByEntity("Hotel", hotelId);

        Set<Integer> bookedVariantIds = getBookedVariantIds(hotelId, requestDto);

        return HotelDetailDto.fromEntity(
                hotel,
                rating != null ? rating : 0.0,
                reviewCount != null ? reviewCount : 0,
                bookedVariantIds);
    }

    @Override
    @Transactional(readOnly = true)
    public HotelDetailDto getHotelDetailsForAdmin(Integer hotelId, HotelSearchRequestDto requestDto) {
        Hotel hotel = hotelDAO.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách sạn với ID: " + hotelId));
        Double rating = reviewDAO.getAverageRatingByEntity("Hotel", hotelId);
        Integer reviewCount = reviewDAO.countByEntity("Hotel", hotelId);

        Set<Integer> bookedVariantIds = getBookedVariantIds(hotelId, requestDto);

        return HotelDetailDto.fromEntity(
                hotel,
                rating != null ? rating : 0.0,
                reviewCount != null ? reviewCount : 0,
                bookedVariantIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsForHotel(Integer hotelId) {
        Hotel hotel = hotelDAO.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách sạn với ID: " + hotelId));

        // Check if hotel is approved and active for public access
        if (!"APPROVED".equals(hotel.getApprovalStatus()) || !"ACTIVE".equals(hotel.getStatus())) {
            throw new ResourceNotFoundException("Khách sạn này không khả dụng");
        }

        List<Review> reviews = reviewDAO.findByEntityTypeAndEntityId("Hotel", hotelId);
        return reviews.stream().map(ReviewDto::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HotelDetailDto createHotel(HotelDetailDto hotelDto, List<MultipartFile> images,
            Map<String, List<MultipartFile>> roomImagesMap, String currentUserEmail) {
        Hotel hotel = hotelMapper.toEntity(hotelDto);
        
        if (currentUserEmail != null) {
            Optional<User> ownerOpt = userDAO.findByEmail(currentUserEmail);
            if (ownerOpt.isPresent()) {
                hotel.setOwner(ownerOpt.get());
            }
        }
        
        if (hotelDto.getProvinceId() != null) {
            Province province = new Province();
            province.setId(hotelDto.getProvinceId());
            hotel.setProvince(province);
        }
        hotel = hotelDAO.save(hotel);
        if (hotelDto.getAvailableRooms() != null) {
            for (HotelRoomDto roomDto : hotelDto.getAvailableRooms()) {
                if (roomDto.getAvailableVariants() == null || roomDto.getAvailableVariants().isEmpty()) {
                    throw new IllegalArgumentException("Mỗi phòng phải có ít nhất một gói phòng (variant)");
                }
            }
            int roomIdx = 0;
            for (HotelRoomDto roomDto : hotelDto.getAvailableRooms()) {
                HotelRoom room = new HotelRoom();
                room.setHotel(hotel);
                room.setRoomType(roomDto.getRoomType());
                room.setBedType(roomDto.getBedType());
                room.setRoomArea(roomDto.getRoomArea());
                room.setRoomQuantity(roomDto.getRoomQuantity());
                room.setMaxAdults(roomDto.getMaxAdults());
                room.setMaxChildren(roomDto.getMaxChildren());
                if (roomDto.getAmenities() != null && !roomDto.getAmenities().isEmpty()) {
                    List<Integer> amenityIds = roomDto.getAmenities().stream()
                            .map(a -> a.getId())
                            .toList();
                    List<Amenity> amenityEntities = amenityDAO.findAllById(amenityIds);
                    room.setAmenities(amenityEntities);
                }
                room = hotelRoomDAO.save(room);
                String key = roomDto.getId() != null
                        ? ("roomimages_" + roomDto.getId())
                        : roomDto.getRoomType() != null
                                ? ("roomimages_" + removeVietnameseDiacritics(roomDto.getRoomType())
                                        .replaceAll("\\s+", "_").toLowerCase())
                                : ("roomimages_" + roomIdx);
                key = key.toLowerCase();
                if (roomImagesMap != null && roomImagesMap.containsKey(key)) {
                    List<MultipartFile> files = roomImagesMap.get(key);
                    if (files != null) {
                        for (MultipartFile file : files) {
                            try {
                                Map uploadResult = cloudinaryService.upload(file, "hotel_rooms");
                                String url = (String) uploadResult.get("secure_url");
                                String publicId = (String) uploadResult.get("public_id");
                                Image img = new Image();
                                img.setUrl(url);
                                img.setAltText(room.getRoomType());
                                img.setPublicId(publicId);
                                img = imageDAO.save(img);
                                HotelRoomImage hri = new HotelRoomImage();
                                HotelRoomImageId hriId = new HotelRoomImageId();
                                hriId.setRoomId(room.getId());
                                hriId.setImageId(img.getId());
                                hri.setId(hriId);
                                hri.setRoom(room);
                                hri.setImage(img);
                                hotelRoomImageDAO.save(hri);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (roomDto.getImageUrls() != null && !roomDto.getImageUrls().isEmpty()) {
                    for (String url : roomDto.getImageUrls()) {
                        Image img = imageDAO.findAll().stream().filter(i -> url.equals(i.getUrl())).findFirst().orElse(null);
                        if (img == null) {
                            img = new Image();
                            img.setUrl(url);
                            img.setAltText(room.getRoomType());
                            img = imageDAO.save(img);
                        }
                        HotelRoomImageId hriId = new HotelRoomImageId();
                        hriId.setRoomId(room.getId());
                        hriId.setImageId(img.getId());
                        boolean linkExists = hotelRoomImageDAO.findById(hriId).isPresent();
                        if (!linkExists) {
                            HotelRoomImage hri = new HotelRoomImage();
                            hri.setId(hriId);
                            hri.setRoom(room);
                            hri.setImage(img);
                            hotelRoomImageDAO.save(hri);
                        }
                    }
                }
                if (roomDto.getAvailableVariants() != null) {
                    for (HotelRoomVariantDto variantDto : roomDto.getAvailableVariants()) {
                        HotelRoomVariant variant = new HotelRoomVariant();
                        variant.setRoom(room);
                        variant.setVariantName(variantDto.getVariantName());
                        variant.setPrice(variantDto.getPrice());
                        variant.setHasBreakfast(variantDto.getHasBreakfast());
                        variant.setCancellable(variantDto.getCancellable());
                        variant.setPayAtHotel(variantDto.getPayAtHotel());
                        variant.setTaxAndFeeAmount(variantDto.getTaxAndFeeAmount());
                        variant.setDiscountType(variantDto.getDiscountType());
                        variant.setDiscountValue(variantDto.getDiscountValue());
                        hotelRoomVariantDAO.save(variant);
                    }
                }
                roomIdx++;
            }
        }
        if (images != null && images.size() > 0) {
            for (MultipartFile file : images) {
                try {
                    Map uploadResult = cloudinaryService.upload(file, "hotels");
                    String url = (String) uploadResult.get("secure_url");
                    String publicId = (String) uploadResult.get("public_id");
                    Image img = new Image();
                    img.setUrl(url);
                    img.setAltText(hotel.getName());
                    img.setPublicId(publicId);
                    img = imageDAO.save(img);
                    HotelImage hi = new HotelImage();
                    HotelImageId hiId = new HotelImageId();
                    hiId.setHotelId(hotel.getId());
                    hiId.setImageId(img.getId());
                    hi.setId(hiId);
                    hi.setHotel(hotel);
                    hi.setImage(img);
                    hotelImageDAO.save(hi);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (hotelDto.getImageUrls() != null && !hotelDto.getImageUrls().isEmpty()) {
            for (String url : hotelDto.getImageUrls()) {
                Image img = imageDAO.findAll().stream().filter(i -> url.equals(i.getUrl())).findFirst().orElse(null);
                if (img == null) {
                    img = new Image();
                    img.setUrl(url);
                    img.setAltText(hotel.getName());
                    img = imageDAO.save(img);
                }
                HotelImageId hiId = new HotelImageId();
                hiId.setHotelId(hotel.getId());
                hiId.setImageId(img.getId());
                boolean linkExists = hotelImageDAO.findById(hiId).isPresent();
                if (!linkExists) {
                    HotelImage hi = new HotelImage();
                    hi.setId(hiId);
                    hi.setHotel(hotel);
                    hi.setImage(img);
                    hotelImageDAO.save(hi);
                }
            }
        }
        hotel = hotelDAO.findById(hotel.getId()).orElse(hotel);
        return HotelDetailDto.fromEntity(hotel, 0, 0, Collections.emptySet());
    }

    @Override
    @Transactional
    public HotelDetailDto updateHotel(Integer id, HotelDetailDto hotelDto, List<MultipartFile> images,
            List<String> deleteImageUrls, Map<String, List<MultipartFile>> roomImagesMap,
            Map<String, List<String>> deleteRoomImageUrlsMap) {
        Hotel hotel = hotelDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách sạn với ID: " + id));
        hotel.setName(hotelDto.getName());
        hotel.setAddress(hotelDto.getAddress());
        hotel.setDescription(hotelDto.getDescription());
        hotel.setStarRating(hotelDto.getStarRating());
        hotel.setEmail(hotelDto.getEmail());
        hotel.setPhone(hotelDto.getPhone());
        if (hotelDto.getProvinceId() != null) {
            Province province = new Province();
            province.setId(hotelDto.getProvinceId());
            hotel.setProvince(province);
        }
        if (hotelDto.getStatus() != null) {
            hotel.setStatus(hotelDto.getStatus());
        }
        hotel.setUpdatedAt(java.time.LocalDateTime.now());
        hotel = hotelDAO.save(hotel);
        if (deleteImageUrls != null && !deleteImageUrls.isEmpty()) {
            for (String url : deleteImageUrls) {
                Image img = imageDAO.findAll().stream().filter(i -> url.equals(i.getUrl())).findFirst().orElse(null);
                if (img != null) {
                    try {
                        String[] parts = img.getUrl().split("/");
                        String publicId = parts[parts.length - 2] + "/" + parts[parts.length - 1].split("\\.")[0];
                        cloudinaryService.delete(publicId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    HotelImageId hiId = new HotelImageId();
                    hiId.setHotelId(hotel.getId());
                    hiId.setImageId(img.getId());
                    hotelImageDAO.deleteById(hiId);
                    long count1 = hotelImageDAO.countByIdImageId(img.getId());
                    long count2 = hotelRoomImageDAO.countByIdImageId(img.getId());
                    if (count1 + count2 == 0) {
                        imageDAO.deleteById(img.getId());
                    }
                }
            }
        }
        if (images != null && images.size() > 0) {
            for (MultipartFile file : images) {
                try {
                    Map uploadResult = cloudinaryService.upload(file, "hotels");
                    String url = (String) uploadResult.get("secure_url");
                    String publicId = (String) uploadResult.get("public_id");
                    Image img = new Image();
                    img.setUrl(url);
                    img.setAltText(hotel.getName());
                    img.setPublicId(publicId);
                    img = imageDAO.save(img);
                    HotelImage hi = new HotelImage();
                    HotelImageId hiId = new HotelImageId();
                    hiId.setHotelId(hotel.getId());
                    hiId.setImageId(img.getId());
                    hi.setId(hiId);
                    hi.setHotel(hotel);
                    hi.setImage(img);
                    hotelImageDAO.save(hi);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (hotelDto.getImageUrls() != null && !hotelDto.getImageUrls().isEmpty()) {
            for (String url : hotelDto.getImageUrls()) {
                Image img = imageDAO.findAll().stream().filter(i -> url.equals(i.getUrl())).findFirst().orElse(null);
                if (img == null) {
                    img = new Image();
                    img.setUrl(url);
                    img.setAltText(hotel.getName());
                    img = imageDAO.save(img);
                }
                HotelImageId hiId = new HotelImageId();
                hiId.setHotelId(hotel.getId());
                hiId.setImageId(img.getId());
                boolean linkExists = hotelImageDAO.findById(hiId).isPresent();
                if (!linkExists) {
                    HotelImage hi = new HotelImage();
                    hi.setId(hiId);
                    hi.setHotel(hotel);
                    hi.setImage(img);
                    hotelImageDAO.save(hi);
                }
            }
        }
        if (deleteRoomImageUrlsMap != null && !deleteRoomImageUrlsMap.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : deleteRoomImageUrlsMap.entrySet()) {
                List<String> urls = entry.getValue();
                if (urls != null) {
                    for (String url : urls) {
                        Image img = imageDAO.findAll().stream().filter(i -> url.equals(i.getUrl())).findFirst()
                                .orElse(null);
                        if (img != null) {
                            try {
                                String[] parts = img.getUrl().split("/");
                                String publicId = parts[parts.length - 2] + "/"
                                        + parts[parts.length - 1].split("\\.")[0];
                                cloudinaryService.delete(publicId);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            List<HotelRoomImage> links = hotelRoomImageDAO.findByIdImageId(img.getId());
                            for (HotelRoomImage hri : links) {
                                hotelRoomImageDAO.delete(hri);
                            }
                            long count1 = hotelImageDAO.countByIdImageId(img.getId());
                            long count2 = hotelRoomImageDAO.countByIdImageId(img.getId());
                            if (count1 + count2 == 0) {
                                imageDAO.deleteById(img.getId());
                            }
                        }
                    }
                }
            }
        }
        List<HotelRoom> currentRooms = hotelRoomDAO.findByHotelId(hotel.getId());
        List<HotelRoomDto> newRoomDtos = hotelDto.getAvailableRooms();
        for (HotelRoom oldRoom : currentRooms) {
            boolean stillExists = newRoomDtos.stream()
                    .anyMatch(r -> r.getId() != null && r.getId().equals(oldRoom.getId()));
            if (!stillExists) {
                if (oldRoom.getRoomImages() != null) {
                    for (HotelRoomImage hri : oldRoom.getRoomImages()) {
                        Image img = hri.getImage();
                        if (img != null) {
                            try {
                                String[] parts = img.getUrl().split("/");
                                String publicId = parts[parts.length - 2] + "/"
                                        + parts[parts.length - 1].split("\\.")[0];
                                cloudinaryService.delete(publicId);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            hotelRoomImageDAO.delete(hri);
                            imageDAO.delete(img);
                        }
                    }
                }
                if (oldRoom.getRoomVariants() != null) {
                    for (HotelRoomVariant variant : oldRoom.getRoomVariants()) {
                        if (hotelBookingDAO.existsByRoomVariantId(variant.getId())) {
                            throw new IllegalStateException("Không thể xóa gói phòng/phòng đã có booking!");
                        }
                        hotelRoomVariantDAO.delete(variant);
                    }
                }
                hotelRoomDAO.delete(oldRoom);
            }
        }
        int roomIdx = 0;
        for (HotelRoomDto roomDto : newRoomDtos) {
            HotelRoom room = null;
            boolean isNewRoom = false;
            if (roomDto.getId() != null) {
                room = hotelRoomDAO.findById(roomDto.getId()).orElse(null);
                if (room == null) {
                    continue;
                }
            } else {
                room = new HotelRoom();
                room.setHotel(hotel);
                isNewRoom = true;
            }
            room.setRoomType(roomDto.getRoomType());
            room.setBedType(roomDto.getBedType());
            room.setRoomArea(roomDto.getRoomArea());
            room.setRoomQuantity(roomDto.getRoomQuantity());
            room.setMaxAdults(roomDto.getMaxAdults());
            room.setMaxChildren(roomDto.getMaxChildren());
            if (roomDto.getAmenities() != null && !roomDto.getAmenities().isEmpty()) {
                List<Integer> amenityIds = roomDto.getAmenities().stream().map(a -> a.getId()).toList();
                List<Amenity> amenityEntities = amenityDAO.findAllById(amenityIds);
                room.setAmenities(amenityEntities);
            }
            room = hotelRoomDAO.save(room);
            String key = roomDto.getId() != null
                    ? ("roomimages_" + roomDto.getId())
                    : roomDto.getRoomType() != null
                            ? ("roomimages_" + removeVietnameseDiacritics(roomDto.getRoomType()).replaceAll("\\s+", "_")
                                    .toLowerCase())
                            : ("roomimages_" + roomIdx);
            key = key.toLowerCase();
            if (roomImagesMap != null && roomImagesMap.containsKey(key)) {
                List<MultipartFile> files = roomImagesMap.get(key);
                if (files != null && !files.isEmpty()) {
                    for (MultipartFile file : files) {
                        try {
                            Map uploadResult = cloudinaryService.upload(file, "hotel_rooms");
                            String url = (String) uploadResult.get("secure_url");
                            String publicId = (String) uploadResult.get("public_id");
                            Image img = new Image();
                            img.setUrl(url);
                            img.setAltText(room.getRoomType());
                            img.setPublicId(publicId);
                            img = imageDAO.save(img);
                            HotelRoomImage hri = new HotelRoomImage();
                            HotelRoomImageId hriId = new HotelRoomImageId();
                            hriId.setRoomId(room.getId());
                            hriId.setImageId(img.getId());
                            hri.setId(hriId);
                            hri.setRoom(room);
                            hri.setImage(img);
                            hotelRoomImageDAO.save(hri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (roomDto.getImageUrls() != null && !roomDto.getImageUrls().isEmpty()) {
                for (String url : roomDto.getImageUrls()) {
                    Image img = imageDAO.findAll().stream().filter(i -> url.equals(i.getUrl())).findFirst().orElse(null);
                    if (img == null) {
                        img = new Image();
                        img.setUrl(url);
                        img.setAltText(room.getRoomType());
                        img = imageDAO.save(img);
                    }
                    HotelRoomImageId hriId = new HotelRoomImageId();
                    hriId.setRoomId(room.getId());
                    hriId.setImageId(img.getId());
                    boolean linkExists = hotelRoomImageDAO.findById(hriId).isPresent();
                    if (!linkExists) {
                        HotelRoomImage hri = new HotelRoomImage();
                        hri.setId(hriId);
                        hri.setRoom(room);
                        hri.setImage(img);
                        hotelRoomImageDAO.save(hri);
                    }
                }
            }
            List<HotelRoomVariant> currentVariants = hotelRoomVariantDAO.findByRoomId(room.getId());
            List<HotelRoomVariantDto> newVariants = roomDto.getAvailableVariants();
            for (HotelRoomVariant oldVar : currentVariants) {
                boolean stillExists = newVariants.stream()
                        .anyMatch(v -> v.getId() != null && v.getId().equals(oldVar.getId()));
                if (!stillExists) {
                    if (hotelBookingDAO.existsByRoomVariantId(oldVar.getId())) {
                        throw new IllegalStateException("Không thể xóa gói phòng/phòng đã có booking!");
                    }
                    hotelRoomVariantDAO.delete(oldVar);
                }
            }
            for (HotelRoomVariantDto variantDto : newVariants) {
                HotelRoomVariant variant = null;
                if (variantDto.getId() != null) {
                    variant = hotelRoomVariantDAO.findById(variantDto.getId()).orElse(null);
                    if (variant == null) {
                        variant = new HotelRoomVariant();
                        variant.setRoom(room);
                    }
                } else {
                    variant = new HotelRoomVariant();
                    variant.setRoom(room);
                }
                variant.setVariantName(variantDto.getVariantName());
                variant.setPrice(variantDto.getPrice());
                variant.setHasBreakfast(variantDto.getHasBreakfast());
                variant.setCancellable(variantDto.getCancellable());
                variant.setPayAtHotel(variantDto.getPayAtHotel());
                variant.setTaxAndFeeAmount(variantDto.getTaxAndFeeAmount());
                variant.setDiscountType(variantDto.getDiscountType());
                variant.setDiscountValue(variantDto.getDiscountValue());
                hotelRoomVariantDAO.save(variant);
            }
            roomIdx++;
        }
        hotel = hotelDAO.save(hotel);
        hotel = hotelDAO.findById(hotel.getId()).orElse(hotel);
        return HotelDetailDto.fromEntity(hotel, 0, 0, Collections.emptySet());
    }

    @Override
    @Transactional
    public void deleteHotel(Integer id) {
        Hotel hotel = hotelDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách sạn với ID: " + id));
        if (hotel.getHotelRooms() != null) {
            for (HotelRoom room : hotel.getHotelRooms()) {
                if (room.getRoomImages() != null) {
                    for (HotelRoomImage hri : room.getRoomImages()) {
                        Image img = hri.getImage();
                        if (img != null) {
                            try {
                                String[] parts = img.getUrl().split("/");
                                String publicId = parts[parts.length - 2] + "/"
                                        + parts[parts.length - 1].split("\\.")[0];
                                cloudinaryService.delete(publicId);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            hotelRoomImageDAO.delete(hri);
                            long count1 = hotelImageDAO.countByIdImageId(img.getId());
                            long count2 = hotelRoomImageDAO.countByIdImageId(img.getId());
                            if (count1 + count2 == 0) {
                                imageDAO.delete(img);
                            }
                        }
                    }
                }
                if (room.getRoomVariants() != null) {
                    for (HotelRoomVariant variant : room.getRoomVariants()) {
                        if (hotelBookingDAO.existsByRoomVariantId(variant.getId())) {
                            throw new IllegalStateException("Không thể xóa gói phòng/phòng đã có booking!");
                        }
                        hotelRoomVariantDAO.delete(variant);
                    }
                }
                hotelRoomDAO.delete(room);
            }
        }
        if (hotel.getHotelImages() != null) {
            for (HotelImage hi : hotel.getHotelImages()) {
                Image img = hi.getImage();
                if (img != null) {
                    try {
                        String[] parts = img.getUrl().split("/");
                        String publicId = parts[parts.length - 2] + "/" + parts[parts.length - 1].split("\\.")[0];
                        cloudinaryService.delete(publicId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    hotelImageDAO.delete(hi);
                    long count1 = hotelImageDAO.countByIdImageId(img.getId());
                    long count2 = hotelRoomImageDAO.countByIdImageId(img.getId());
                    if (count1 + count2 == 0) {
                        imageDAO.delete(img);
                    }
                }
            }
        }
        hotelDAO.delete(hotel);
    }

    @Override
    @Transactional
    public void createHotelReview(Integer hotelId, String email, Integer rating, String content) {
        Hotel hotel = hotelDAO.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách sạn với ID: " + hotelId));
        if (!"APPROVED".equals(hotel.getApprovalStatus()) || !"ACTIVE".equals(hotel.getStatus())) {
            throw new ResourceNotFoundException("Khách sạn này không khả dụng");
        }

        var userOpt = userDAO.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy user với email: " + email);
        }
        if (rating == null) {
            throw new IllegalArgumentException("Rating không được để trống");
        }
        if (content == null) {
            content = "";
        }
        backend.backend.entity.User user = userOpt.get();
        Review review = new Review();
        review.setEntityType("Hotel");
        review.setEntityId(hotelId);
        review.setRating(rating.shortValue());
        review.setContent(content);
        review.setCreatedAt(java.time.LocalDateTime.now());
        review.setUser(user);
        reviewDAO.save(review);
    }

    private Set<Integer> getBookedVariantIds(Integer hotelId, HotelSearchRequestDto requestDto) {
        if (requestDto.getCheckInDate() != null && requestDto.getCheckOutDate() != null) {
            return hotelRoomVariantDAO.findBookedVariantIdsByHotelAndDateRange(
                    hotelId, requestDto.getCheckInDate(), requestDto.getCheckOutDate());
        }
        return Collections.emptySet();
    }

    private Pageable createPageable(HotelSearchRequestDto requestDto) {
        String sortBy = requestDto.getSortBy();
        Sort sort;
        switch (sortBy != null ? sortBy.toLowerCase() : "default") {
            case "priceasc":
                sort = Sort.by("startingPrice").ascending();
                break;
            case "pricedesc":
                sort = Sort.by("startingPrice").descending();
                break;
            case "ratingdesc":
                sort = Sort.by("rating").descending();
                break;
            case "popular":
            case "default":
            default:
                sort = Sort.by("rating").descending().and(Sort.by("createdAt").descending());
                break;
        }
        return PageRequest.of(requestDto.getPage(), requestDto.getSize(), sort);
    }

    public static String removeVietnameseDiacritics(String str) {
        if (str == null)
            return null;
        String temp = java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD);
        return temp.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelDto> getPopularHotelsByBookings(int size) {
        return hotelDAO.findPopularHotelsByBookings(size);
    }

    @Override
    @Transactional
    public void approveHotel(Integer id) {
        Hotel hotel = hotelDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách sạn với ID: " + id));
        
        hotel.setApprovalStatus("APPROVED");
        hotel.setApprovedAt(java.time.LocalDateTime.now());
        hotel.setApprovedBy("SUPER_ADMIN");
        
        hotelDAO.save(hotel);
    }

    @Override
    @Transactional
    public void rejectHotel(Integer id, String reason) {
        Hotel hotel = hotelDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách sạn với ID: " + id));
        
        hotel.setApprovalStatus("REJECTED");
        hotel.setApprovalReason(reason);
        hotel.setApprovedAt(java.time.LocalDateTime.now());
        hotel.setApprovedBy("SUPER_ADMIN");
        
        hotelDAO.save(hotel);
    }

    @Override
    @Transactional
    public void resubmitHotel(Integer id) {
        Hotel hotel = hotelDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách sạn với ID: " + id));
        
        hotel.setApprovalStatus("PENDING");
        hotel.setApprovalReason(null);
        hotel.setApprovedAt(null);
        hotel.setApprovedBy(null);
        
        hotelDAO.save(hotel);
    }

    @Override
    @Transactional
    public void updateHotelStatus(Integer id, String status) {
        Hotel hotel = hotelDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách sạn với ID: " + id));
        
        hotel.setStatus(status);
        hotel.setUpdatedAt(java.time.LocalDateTime.now());
        
        hotelDAO.save(hotel);
    }
}