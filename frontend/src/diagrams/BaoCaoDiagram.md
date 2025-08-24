# Báo cáo về sơ đồ Sequence Diagram và Activity Diagram cho dự án DATN

## Tổng quan dự án
Dự án DATN là một hệ thống đặt vé và dịch vụ du lịch trực tuyến, bao gồm cả frontend và backend. Hệ thống hỗ trợ các tính năng chính:
- Tìm kiếm và đặt tour du lịch
- Quản lý đơn hàng
- Áp dụng voucher giảm giá
- Thanh toán trực tuyến
- Hủy đơn hàng và hoàn tiền

## Kịch bản 1: Từ trang chủ tìm kiếm, lọc để chọn dịch vụ

### Sequence Diagram (Chi tiết code-level)
**Mục đích**: Mô tả chi tiết luồng tương tác giữa các thành phần hệ thống từ góc độ code implementation.

**Các thành phần tham gia**:
- **User**: Người dùng cuối
- **Frontend (Vue.js Components)**: SearchBar.vue, FilterPanel.vue, TourList.vue, TourDetailPage.vue, BookingForm.vue, MyOrdersPage.vue
- **Backend Controllers**: TourController, BookingTourController, OrderController
- **Service Layer**: TourService, BookingTourService, OrderService, DepartureService
- **DAO/Repository**: TourRepository, BookingTourRepository, OrderRepository, DepartureRepository, UserRepository
- **Database**: MySQL/PostgreSQL database
- **Utility Classes**: TourMapper, BookingTourMapper, OrderMapper, PriceCalculator, ResponseFactory

**Luồng trình tự chi tiết**:
1. **Tìm kiếm dịch vụ**: User nhập từ khóa → SearchBar.vue gọi tourApi.searchTours() → TourController.searchTours() → TourService.searchTours() → TourRepository.findBySearchCriteria() → Database query → Trả Page<TourDto>
2. **Lọc dịch vụ**: User áp dụng bộ lọc → FilterPanel.vue gọi tourApi.getToursWithFilters() → TourController.getTours() → TourService.getFilteredTours() → TourSpecificationBuilder → TourRepository.findAll() với JPA Specification → Database query với multiple predicates
3. **Xem chi tiết**: User chọn tour → TourDetailPage.vue gọi tourApi.getTourDetail() → TourController.getTourById() → TourService.getTourDetail() → TourRepository.findById() + DepartureService.getDeparturesByTourId() → Kết hợp thông tin → Trả TourDetailDto
4. **Tạo booking**: User điền thông tin → BookingForm.vue gọi bookingApi.create() → BookingTourController.createBooking() → BookingTourService.createBookingTour() → Validate tour và departure → PriceCalculator.calculateTotal() → BookingTourRepository.save() → INSERT vào database
5. **Quản lý đơn hàng**: User vào trang đơn hàng → MyOrdersPage.vue gọi orderApi.getMyOrders() → OrderController.getMyOrders() → OrderService.getOrdersByUserId() → OrderRepository.findByUserId() → Query database → Convert to DTO list
6. **Chỉnh sửa đơn hàng**: User chỉnh sửa → OrderEditModal.vue gọi orderApi.updateOrder() → OrderController.updateOrder() → OrderService.updateOrder() → Validate order có thể chỉnh sửa → PriceCalculator.recalculate() → OrderRepository.save() → UPDATE database
7. **Xóa đơn hàng**: User xóa order → OrderCard.vue gọi orderApi.deleteOrder() → OrderController.deleteOrder() → OrderService.deleteOrder() → Validate order có thể xóa → OrderRepository.deleteById() → DELETE từ database

**Các lớp và phương thức chính**:
- **Frontend**: tourApi.searchTours(), tourApi.getToursWithFilters(), tourApi.getTourDetail(), bookingApi.create(), orderApi.getMyOrders(), orderApi.updateOrder(), orderApi.deleteOrder()
- **Backend**: 
  - TourController.searchTours(), getTours(), getTourById()
  - BookingTourController.createBooking()
  - OrderController.getMyOrders(), updateOrder(), deleteOrder()
  - Các service và repository tương ứng
  - Các mapper để convert Entity ↔ DTO

**Database operations**:
- SELECT queries với various conditions và joins
- INSERT cho tạo booking mới
- UPDATE cho chỉnh sửa đơn hàng
- DELETE cho xóa đơn hàng
- Transaction management cho data consistency

## Kịch bản 2: Từ đơn hàng đã tạo, áp voucher, thanh toán, hủy tự động, hoàn tiền

### Activity Diagram
**Mục đích**: Mô tả các hoạt động và quyết định trong quá trình quản lý đơn hàng sau khi đã tạo.

**Các thành phần tham gia**:
- User: Người dùng cuối
- Frontend: Giao diện người dùng
- Backend: Xử lý nghiệp vụ
- Payment Gateway: Cổng thanh toán

**Luồng hoạt động**:
1. **Áp dụng voucher**:
   - User chọn đơn hàng và nhập mã voucher
   - Frontend gọi API apply voucher
   - Backend kiểm tra tính hợp lệ của voucher
   - Cập nhật giá đơn hàng nếu voucher hợp lệ

2. **Thanh toán**:
   - User chọn phương thức thanh toán
   - Frontend gọi API checkout
   - Backend tích hợp với Payment Gateway
   - Cập nhật trạng thái đơn hàng sau thanh toán

3. **Hủy đơn hàng**:
   - User yêu cầu hủy đơn hàng
   - Frontend gọi API hủy đơn hàng
   - Backend kiểm tra điều kiện hủy
   - Cập nhật trạng thái đơn hàng thành "Đã hủy"

4. **Hoàn tiền**:
   - User yêu cầu hoàn tiền cho đơn hàng đã hủy
   - Frontend gọi API hoàn tiền
   - Backend tích hợp với Payment Gateway để xử lý hoàn tiền
   - Cập nhật trạng thái hoàn tiền

**API endpoints sử dụng**:
- `POST /api/v1/orders/{id}/apply-voucher` - Áp dụng voucher
- `POST /api/v1/orders/checkout` - Thanh toán
- `PUT /api/v1/orders/{id}/cancel` - Hủy đơn hàng
- `POST /api/v1/orders/{id}/refund` - Hoàn tiền

## Lựa chọn loại sơ đồ phù hợp

### Sequence Diagram cho Kịch bản 1
**Lý do**: Kịch bản 1 liên quan đến chuỗi các tương tác tuần tự giữa các thành phần hệ thống. Sequence Diagram là lựa chọn tốt nhất để:
- Hiển thị thứ tự các message được gửi giữa các object
- Mô tả rõ ràng luồng điều khiển
- Cho thấy sự tương tác theo thời gian

### Activity Diagram cho Kịch bản 2
**Lý do**: Kịch bản 2 liên quan đến các hoạt động và quyết định phức tạp. Activity Diagram phù hợp để:
- Mô tả luồng công việc (workflow)
- Hiển thị các điều kiện rẽ nhánh
- Mô tả các hoạt động song song
- Cho thấy quá trình xử lý từ đầu đến cuối

## Kết luận
Cả hai sơ đồ đều quan trọng để hiểu rõ luồng nghiệp vụ của hệ thống:
- **Sequence Diagram** giúp hiểu cách các thành phần tương tác với nhau
- **Activity Diagram** giúp hiểu quy trình xử lý nghiệp vụ

Việc sử dụng kết hợp cả hai loại sơ đồ sẽ cung cấp cái nhìn toàn diện về hệ thống, từ góc độ kỹ thuật (Sequence) đến góc độ nghiệp vụ (Activity).
