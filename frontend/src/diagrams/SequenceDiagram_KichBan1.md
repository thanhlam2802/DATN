# Sequence Diagram - Kịch bản 1: Tìm kiếm và đặt dịch vụ (Chi tiết code-level)

## Các thành phần tham gia:
1. **User**: Người dùng hệ thống
2. **Frontend (Vue.js)**: Giao diện người dùng
3. **Backend Controller (Spring Boot)**: Xử lý API requests
4. **Service Layer**: Xử lý nghiệp vụ
5. **DAO/Repository**: Truy cập database
6. **Database**: Lưu trữ dữ liệu
7. **Response Factory**: Xử lý response

## Luồng trình tự chi tiết:

### 1. Tìm kiếm dịch vụ
```
User -> Frontend: Nhập từ khóa tìm kiếm trong component SearchBar.vue
Frontend -> tourApi.searchTours(): Gọi API search với query parameters
tourApi -> Backend: GET /api/tours/search?keyword=...&page=0&size=10
Backend (TourController.searchTours()) -> TourService.searchTours(): Xử lý tìm kiếm
TourService -> TourRepository.findBySearchCriteria(): Query với Specification
TourRepository -> Database: SELECT * FROM tours WHERE ... 
Database -> TourRepository: Trả về List<Tour> phù hợp
TourRepository -> TourService: Trả kết quả tìm kiếm
TourService -> TourController: Trả Page<TourDto>
TourController -> ResponseFactory.success(): Đóng gói response
ResponseFactory -> Frontend: ApiResponse với statusCode=200 và data
Frontend -> SearchResults.vue: Hiển thị kết quả trong grid/list
```

### 2. Lọc và chọn dịch vụ
```
User -> FilterPanel.vue: Áp dụng bộ lọc (giá, địa điểm, ngày, ...)
Frontend -> tourApi.getToursWithFilters(): Gọi API với filter parameters
tourApi -> Backend: GET /api/tours?minPrice=1000000&maxPrice=5000000&destination=...
Backend (TourController.getTours()) -> TourService.getFilteredTours(): Xử lý filter
TourService -> TourSpecificationBuilder: Build JPA Specification
TourSpecificationBuilder -> TourRepository.findAll(): Query với multiple predicates
TourRepository -> Database: SELECT * FROM tours WHERE price BETWEEN ... AND destination = ...
Database -> TourRepository: Trả về List<Tour> đã lọc
TourRepository -> TourService: Trả kết quả đã lọc
TourService -> TourMapper.mapToDto(): Convert Entity to DTO
TourMapper -> TourController: Trả List<TourDto>
TourController -> ResponseFactory.success(): Đóng gói response
ResponseFactory -> Frontend: ApiResponse với danh sách tour đã lọc
Frontend -> TourList.vue: Hiển thị tour đã lọc
User -> TourCard.vue: Click chọn tour cụ thể
```

### 3. Xem chi tiết dịch vụ
```
Frontend -> tourApi.getTourDetail(): Gọi API chi tiết tour
tourApi -> Backend: GET /api/tours/{tourId}
Backend (TourController.getTourById()) -> TourService.getTourDetail(): Lấy chi tiết
TourService -> TourRepository.findById(): Query tour by ID
TourRepository -> Database: SELECT * FROM tours WHERE id = ?
Database -> TourRepository: Trả Tour entity
TourRepository -> TourService: Trả tour chi tiết
TourService -> DepartureService.getDeparturesByTourId(): Lấy danh sách ngày khởi hành
DepartureService -> DepartureRepository.findByTourId(): Query departures
DepartureRepository -> Database: SELECT * FROM departures WHERE tour_id = ?
Database -> DepartureRepository: Trả List<Departure>
DepartureRepository -> DepartureService: Trả danh sách departure
DepartureService -> TourService: Kết hợp thông tin
TourService -> TourDetailMapper.mapToDetailDto(): Convert to detailed DTO
TourDetailMapper -> TourController: Trả TourDetailDto
TourController -> ResponseFactory.success(): Đóng gói response
ResponseFactory -> Frontend: ApiResponse với chi tiết tour
Frontend -> TourDetailPage.vue: Hiển thị đầy đủ thông tin + calendar
User -> BookingForm.vue: Điền thông tin (số người, ngày khởi hành, thông tin liên hệ)
```

### 4. Tạo booking (Thêm vào giỏ hàng)
```
User -> BookingForm.vue: Click "Đặt ngay" hoặc "Thêm vào giỏ"
Frontend -> bookingApi.create(): Gọi API tạo booking
bookingApi -> Backend: POST /api/v1/bookings/tours
Request Body: BookingTourRequestDto {
  tourId: 123,
  departureDate: "2024-01-15",
  numberOfAdults: 2,
  numberOfChildren: 1,
  customerName: "Nguyen Van A",
  phone: "0912345678",
  email: "a@email.com",
  note: "Yêu cầu đặc biệt"
}
Backend (BookingTourController.createBooking()) -> BookingTourService.createBookingTour(): Xử lý tạo booking
BookingTourService -> TourRepository.findById(): Validate tour exists
TourRepository -> Database: SELECT * FROM tours WHERE id = 123
Database -> TourRepository: Trả tour
TourRepository -> BookingTourService: Tour valid
BookingTourService -> DepartureRepository.findByTourAndDate(): Validate departure date
DepartureRepository -> Database: SELECT * FROM departures WHERE tour_id = 123 AND date = '2024-01-15'
Database -> DepartureRepository: Trả departure
DepartureRepository -> BookingTourService: Departure valid
BookingTourService -> PriceCalculator.calculateTotal(): Tính tổng tiền
PriceCalculator -> BookingTourService: Trả totalPrice
BookingTourService -> BookingTourRepository.save(): Lưu booking mới
BookingTourRepository -> Database: INSERT INTO booking_tours (...)
Database -> BookingTourRepository: Trả booking đã lưu
BookingTourRepository -> BookingTourService: Trả BookingTour entity
BookingTourService -> BookingTourMapper.mapToDto(): Convert to DTO
BookingTourMapper -> BookingTourController: Trả BookingTourDto
BookingTourController -> ResponseFactory.created(): Đóng gói response thành công
ResponseFactory -> Frontend: ApiResponse với statusCode=201 và booking details
Frontend -> ToastContainer.vue: Hiển thị thông báo "Thêm tour vào giỏ hàng thành công!"
```

### 5. Quản lý đơn hàng
```
User -> NavigationMenu.vue: Click "Đơn hàng của tôi"
Frontend -> orderApi.getMyOrders(): Gọi API lấy đơn hàng
orderApi -> Backend: GET /api/v1/orders/my-orders?userId=456
Backend (OrderController.getMyOrders()) -> OrderService.getOrdersByUserId(): Xử lý
OrderService -> UserRepository.findById(): Validate user exists
UserRepository -> Database: SELECT * FROM users WHERE id = 456
Database -> UserRepository: Trả user
UserRepository -> OrderService: User valid
OrderService -> OrderRepository.findByUserId(): Query orders của user
OrderRepository -> Database: SELECT * FROM orders WHERE user_id = 456
Database -> OrderRepository: Trả List<Order>
OrderRepository -> OrderService: Trả orders
OrderService -> OrderMapper.mapToDtoList(): Convert to DTO list
OrderMapper -> OrderController: Trả List<OrderDto>
OrderController -> ResponseFactory.success(): Đóng gói response
ResponseFactory -> Frontend: ApiResponse với danh sách orders
Frontend -> MyOrdersPage.vue: Hiển thị danh sách đơn hàng với trạng thái
```

### 6. Chỉnh sửa đơn hàng
```
User -> OrderCard.vue: Click "Chỉnh sửa" trên order pending
Frontend -> OrderEditModal.vue: Mở modal chỉnh sửa
User -> OrderEditForm.vue: Thay đổi thông tin (số người, ngày, ...)
User -> OrderEditForm.vue: Click "Lưu thay đổi"
Frontend -> orderApi.updateOrder(): Gọi API cập nhật
orderApi -> Backend: PUT /api/v1/orders/{orderId}
Request Body: Updated order data
Backend (OrderController.updateOrder()) -> OrderService.updateOrder(): Xử lý cập nhật
OrderService -> OrderRepository.findById(): Validate order exists và có thể chỉnh sửa
OrderRepository -> Database: SELECT * FROM orders WHERE id = ? AND status = 'PENDING'
Database -> OrderRepository: Trả order
OrderRepository -> OrderService: Order valid để chỉnh sửa
OrderService -> PriceCalculator.recalculate(): Tính lại giá nếu có thay đổi
PriceCalculator -> OrderService: Trả new total
OrderService -> OrderRepository.save(): Lưu thay đổi
OrderRepository -> Database: UPDATE orders SET ... WHERE id = ?
Database -> OrderRepository: Xác nhận update
OrderRepository -> OrderService: Trả order đã cập nhật
OrderService -> OrderMapper.mapToDto(): Convert to DTO
OrderMapper -> OrderController: Trả OrderDto
OrderController -> ResponseFactory.success(): Đóng gói response
ResponseFactory -> Frontend: ApiResponse với order đã cập nhật
Frontend -> ToastContainer.vue: Hiển thị "Cập nhật đơn hàng thành công!"
Frontend -> MyOrdersPage.vue: Refresh danh sách orders
```

### 7. Xóa đơn hàng
```
User -> OrderCard.vue: Click "Xóa" trên order pending
Frontend -> ConfirmDialog.vue: Hiển thị dialog xác nhận "Bạn có chắc muốn xóa?"
User -> ConfirmDialog.vue: Click "Xác nhận"
Frontend -> orderApi.deleteOrder(): Gọi API xóa
orderApi -> Backend: DELETE /api/v1/orders/{orderId}
Backend (OrderController.deleteOrder()) -> OrderService.deleteOrder(): Xử lý xóa
OrderService -> OrderRepository.findById(): Validate order exists và có thể xóa
OrderRepository -> Database: SELECT * FROM orders WHERE id = ? AND status = 'PENDING'
Database -> OrderRepository: Trả order
OrderRepository -> OrderService: Order valid để xóa
OrderService -> OrderRepository.deleteById(): Xóa order
OrderRepository -> Database: DELETE FROM orders WHERE id = ?
Database -> OrderRepository: Xác nhận xóa
OrderRepository -> OrderService: Xóa thành công
OrderService -> OrderController: Trả confirmation
OrderController -> ResponseFactory.success(): Đóng gói response
ResponseFactory -> Frontend: ApiResponse với message thành công
Frontend -> ToastContainer.vue: Hiển thị "Xóa đơn hàng thành công!"
Frontend -> MyOrdersPage.vue: Remove order khỏi danh sách hiển thị
```

## Các lớp và phương thức liên quan:

**Frontend (Vue.js Components)**:
- `SearchBar.vue` - Component tìm kiếm
- `FilterPanel.vue` - Component lọc
- `TourList.vue` - Hiển thị danh sách tour
- `TourDetailPage.vue` - Trang chi tiết tour
- `BookingForm.vue` - Form đặt tour
- `MyOrdersPage.vue` - Trang quản lý đơn hàng
- `OrderCard.vue` - Component hiển thị order
- `OrderEditModal.vue` - Modal chỉnh sửa order

**Backend (Spring Boot)**:
- `TourController` - Xử lý API tour
- `BookingTourController` - Xử lý API booking
- `OrderController` - Xử lý API order
- `TourService`, `BookingTourService`, `OrderService` - Service layer
- `TourRepository`, `BookingTourRepository`, `OrderRepository` - Data access
- `TourMapper`, `BookingTourMapper`, `OrderMapper` - DTO mapping
- `PriceCalculator` - Tính toán giá
- `ResponseFactory` - Xử lý response standardization

**Database Entities**:
- `Tour` - Bảng tours
- `Departure` - Bảng departures
- `BookingTour` - Bảng booking_tours
- `Order` - Bảng orders
- `User` - Bảng users
