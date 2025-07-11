# Đánh giá và Cải thiện DTO cho phần Flight

## Vấn đề đã phát hiện

### 1. **FlightDto thiếu thông tin quan trọng:**
- ❌ Không có thông tin về **airplane** (máy bay)
- ❌ Không có thông tin chi tiết về **airline** (chỉ có tên, thiếu code, location)
- ❌ Không có thông tin về **flight status** (trạng thái chuyến bay)
- ❌ Không có thông tin về **created_at, updated_at**
- ❌ Không có thông tin về **owner** (người tạo chuyến bay)
- ❌ Mapping sai: `dto.setAirline(flight.getName())` - đang set tên chuyến bay thay vì tên hãng bay

### 2. **FlightDetailDto thiếu thông tin:**
- ❌ Không có thông tin về **airplane details**
- ❌ Không có thông tin về **airline details** 
- ❌ Không có thông tin về **flight images**
- ❌ Không có thông tin về **reviews/ratings**
- ❌ Không có thông tin về **statistics** (tổng booking, occupancy rate)

### 3. **SeatDto không phù hợp:**
- ❌ Đang map từ FlightSlot thay vì từ entity Seat thực tế
- ❌ Thiếu thông tin về seat number, row, column
- ❌ Thiếu thông tin về seat features (window, aisle, emergency exit)

### 4. **Thiếu DTO cho các entity quan trọng:**
- ❌ Không có **AirplaneDto**
- ❌ Không có **FlightImageDto**
- ❌ Không có **FlightSlotDto**
- ❌ Không có **CustomerDto**
- ❌ Không có **PaymentDto**
- ❌ Không có **TicketDetailDto**

## Cải thiện đã thực hiện

### 1. **Cập nhật FlightDto:**
```java
// Thêm thông tin mới
private String flightNumber;
private String name;
private String departureAirport;
private String arrivalAirport;
private String status;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;

// Thêm object relationships
private AirplaneDto airplane;
private AirlineDto airline;

// Thêm thông tin pricing
private Double minPrice;
private Double maxPrice;
private Integer totalAvailableSeats;
private String availableSeatClasses;
```

### 2. **Cập nhật FlightDetailDto:**
```java
// Thêm thông tin chi tiết
private AirplaneDto airplane;
private AirlineDto airline;
private List<FlightSlotDto> flightSlots;
private List<FlightImageDto> images;
private List<ReviewDto> reviews;
private Double averageRating;
private Integer totalReviews;
private Integer totalBookings;
private Double occupancyRate;
```

### 3. **Tạo DTO mới:**
- ✅ **AirplaneDto**: Thông tin máy bay
- ✅ **FlightSlotDto**: Thông tin slot chuyến bay với pricing
- ✅ **FlightImageDto**: Thông tin hình ảnh
- ✅ **CustomerDto**: Thông tin hành khách
- ✅ **PaymentDto**: Thông tin thanh toán
- ✅ **TicketDetailDto**: Thông tin vé chi tiết

### 4. **Cập nhật SeatDto:**
```java
// Thêm thông tin chi tiết về ghế
private String seatNumber;
private String row;
private String column;
private Boolean isWindow;
private Boolean isAisle;
private Boolean isEmergencyExit;
```

### 5. **Cập nhật FlightServiceImpl:**
- ✅ Sử dụng Builder pattern cho DTO
- ✅ Mapping chính xác hơn
- ✅ Tính toán pricing từ flight slots
- ✅ Tính toán occupancy rate
- ✅ Thêm placeholder cho các thông tin cần bổ sung

## Đề xuất tiếp theo

### 1. **Cần bổ sung entity relationships:**
```java
// Trong Flight entity
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "airline_id")
private Airline airline;

@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "airplane_id")
private Airplane airplane;
```

### 2. **Cần tạo entity Airplane:**
```java
@Entity
@Table(name = "airplanes")
public class Airplane {
    private Long id;
    private String model;
    private Integer capacity;
    private String manufacturer;
    private Integer yearOfManufacture;
    private String registrationNumber;
}
```

### 3. **Cần bổ sung logic thực tế:**
- Logic lấy airline thực tế từ relationship
- Logic lấy airplane thực tế từ relationship
- Logic tính toán reviews và ratings
- Logic tính toán booking statistics
- Logic quản lý seat thực tế

### 4. **Cần cập nhật database schema:**
- Thêm bảng `airplanes`
- Thêm foreign key `airline_id` và `airplane_id` vào bảng `flights`
- Thêm bảng `seats` để quản lý ghế chi tiết
- Thêm các trường status, timestamps

## Kết luận

Các DTO đã được cải thiện đáng kể với:
- ✅ Thông tin đầy đủ và chi tiết hơn
- ✅ Cấu trúc rõ ràng và dễ hiểu
- ✅ Sử dụng Builder pattern
- ✅ Tách biệt rõ ràng các loại thông tin
- ✅ Sẵn sàng cho việc mở rộng

Tuy nhiên cần tiếp tục:
- 🔄 Cập nhật entity relationships
- 🔄 Bổ sung logic thực tế
- 🔄 Cập nhật database schema
- 🔄 Thêm validation và error handling 