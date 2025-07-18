package backend.backend.controller;

import backend.backend.dto.TourDetailAdminDTO;
import backend.backend.dto.TourRequestDTO;
import backend.backend.entity.ApiResponse;
import backend.backend.service.TourAdminService;
import backend.backend.utils.ResponseFactory;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tours")
@CrossOrigin(origins = "*")
public class TourAdminController {

@Autowired
private TourAdminService tourAdminService;

		/**
		 * API [GET] /api/admin/tours?userId=5 - Lấy danh sách tour theo userId
		 */
		@GetMapping(params = "userId")
		public ResponseEntity<ApiResponse<List<TourDetailAdminDTO>>> getToursByUserId(@RequestParam Integer userId) {
		    List<TourDetailAdminDTO> tours = tourAdminService.getToursByUserId(userId);
		    return ResponseFactory.success(tours, "Lấy danh sách tour theo người dùng thành công.");
		}

		/**
		* API [GET] /api/admin/tours - Lấy danh sách tour
		*/
		@GetMapping
		public ResponseEntity<ApiResponse<List<TourDetailAdminDTO>>> getAllTours() {
		List<TourDetailAdminDTO> tours = tourAdminService.getAllToursForAdmin();
		// Sửa: Dùng ResponseFactory để đóng gói dữ liệu
		return ResponseFactory.success(tours, "Lấy danh sách tour thành công.");
		}
		
		/**
		* API [GET] /api/admin/tours/{id} - Lấy chi tiết một tour
		*/
		@GetMapping("/{id}")
		public ResponseEntity<ApiResponse<TourDetailAdminDTO>> getTourById(@PathVariable Long id) {
		TourDetailAdminDTO tour = tourAdminService.getTourById(id);
		// Sửa: Dùng ResponseFactory
		return ResponseFactory.success(tour);
		}
		
		/**
		* API [POST] /api/admin/tours - Tạo tour mới
		*/
		@PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
		public ResponseEntity<ApiResponse<TourDetailAdminDTO>> createTour(
		@RequestPart("tourData") TourRequestDTO tourRequestDTO,
		@RequestPart(value = "images", required = false) List<MultipartFile> images) {
		TourDetailAdminDTO newTour = tourAdminService.createTour(tourRequestDTO, images);
		// Sửa: Dùng phương thức 'created' của ResponseFactory để trả về mã 201
		return ResponseFactory.created(newTour, "Tạo tour mới thành công.");
		}
		
		/**
		* API [PUT] /api/admin/tours/{id} - Cập nhật tour
		*/
		@PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
		public ResponseEntity<ApiResponse<TourDetailAdminDTO>> updateTour(
		@PathVariable Long id,
		@RequestPart("tourData") TourRequestDTO tourRequestDTO,
		@RequestPart(value = "newImages", required = false) List<MultipartFile> newImages) {
		TourDetailAdminDTO updatedTour = tourAdminService.updateTour(id, tourRequestDTO, newImages);
		// Sửa: Dùng ResponseFactory
		return ResponseFactory.success(updatedTour, "Cập nhật tour thành công.");
		}
		
		/**
		* API [DELETE] /api/admin/tours/{id} - Xóa tour
		*/
		@DeleteMapping("/{id}")
		public ResponseEntity<ApiResponse<Object>> deleteTour(@PathVariable Long id) {
		tourAdminService.deleteTour(id);
		
		return ResponseFactory.success(null, "Xóa tour thành công.");
		}
		
		/**
		* Bổ sung: Xử lý tập trung cho lỗi không tìm thấy (404 Not Found)
		* Khi service ném ra EntityNotFoundException, hàm này sẽ được gọi.
		*/
		@ExceptionHandler(EntityNotFoundException.class)
		public ResponseEntity<ApiResponse<Object>> handleEntityNotFound(EntityNotFoundException ex) {
		return ResponseFactory.error(HttpStatus.NOT_FOUND, ex.getMessage());
		}
}