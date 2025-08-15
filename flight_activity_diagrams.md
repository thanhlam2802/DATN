# Sơ đồ Activity Diagram cho Flight System

## CN1: Hiển thị danh sách theo bộ lọc -> Xem chi tiết chuyến bay

```mermaid
flowchart TD
    A[User truy cập trang Flight] --> B[Load danh sách sân bay, hãng bay, danh mục]
    B --> C[Hiển thị form tìm kiếm với bộ lọc]
    C --> D[User nhập thông tin tìm kiếm]
    D --> E{Validate form}
    E -->|Invalid| F[Hiển thị lỗi validation]
    F --> D
    E -->|Valid| G[Gọi API searchFlights]
    G --> H[Backend xử lý tìm kiếm]
    H --> I[Query database với filters]
    I --> J[Trả về danh sách flights]
    J --> K[Hiển thị danh sách flights với pagination]
    K --> L[User chọn chuyến bay]
    L --> M[Gọi API getFlightDetail]
    M --> N[Backend lấy thông tin chi tiết flight]
    N --> O[Trả về flight detail + slots]
    O --> P[Hiển thị trang chi tiết chuyến bay]
    P --> Q[User xem thông tin chi tiết]
    Q --> R[Hiển thị thông tin: flight, slots, giá, hành lý]
    R --> S[User có thể đặt vé hoặc quay lại]
    S --> T[Chuyển đến quy trình đặt vé]
```

## CN2: Đặt vé từ chi tiết -> Thanh toán OTP

```mermaid
flowchart TD
    A[User chọn đặt vé từ chi tiết flight] --> B[Kiểm tra đăng nhập]
    B -->|Chưa đăng nhập| C[Redirect đến trang login]
    C --> D[User đăng nhập]
    D --> E[Quay lại trang đặt vé]
    B -->|Đã đăng nhập| F[Lấy thông tin customer]
    F --> G[Hiển thị form thông tin khách hàng]
    G --> H[User nhập thông tin passenger]
    H --> I[Validate thông tin customer]
    I -->|Invalid| J[Hiển thị lỗi validation]
    J --> H
    I -->|Valid| K[Gọi API bookFlight]
    K --> L[Backend tạo FlightBooking]
    L --> M[Cập nhật Order với tổng tiền]
    M --> N[Trả về booking detail]
    N --> O[Hiển thị trang Order Detail]
    O --> P[User xem đơn hàng]
    P --> Q[User có thể chỉnh sửa/xóa dịch vụ]
    Q --> R{User thao tác}
    R -->|Thêm dịch vụ| S[Gọi API addItemToOrder]
    S --> T[Quay lại từ đầu]
    R -->|Xóa dịch vụ| U[Gọi API removeItemFromOrder]
    U --> T
    R -->|Chỉnh sửa| V[Gọi API updateOrderItem]
    V --> T
    R -->|Thanh toán| W[Gọi API makeServicePayment]
    W --> X[Core Banking tạo payment]
    X --> Y[Tạo OTP và lưu vào Redis]
    Y --> Z[Gửi OTP qua Kafka]
    Z --> AA[Notification Service gửi email OTP]
    AA --> BB[Trả về paymentId]
    BB --> CC[Hiển thị dialog nhập OTP]
    CC --> DD[User nhập OTP]
    DD --> EE[Gọi API confirmServicePayment]
    EE --> FF[Kiểm tra OTP trong Redis]
    FF -->|OTP đúng| GG[Thực hiện giao dịch thanh toán]
    GG --> HH[Trừ tiền customer, cộng tiền system]
    HH --> II[Cập nhật trạng thái payment]
    II --> JJ[Gửi notification thành công qua Kafka]
    JJ --> KK[Gọi API markOrderSuccess]
    KK --> LL[Cập nhật trạng thái order]
    LL --> MM[Hiển thị thông báo thành công]
    FF -->|OTP sai| NN[Hiển thị lỗi OTP]
    NN --> DD
```

## CN3: Quy trình thêm chuyến bay (Admin)

```mermaid
flowchart TD
    A[Admin truy cập trang quản lý flight] --> B[Load danh sách flights hiện có]
    B --> C[Admin click "Thêm chuyến bay mới"]
    C --> D[Hiển thị form tạo flight]
    D --> E[Load danh sách airports, airlines, categories]
    E --> F[Admin nhập thông tin cơ bản]
    F --> G[Admin chọn sân bay đi/đến]
    G --> H[Admin nhập thời gian bay]
    H --> I[Admin chọn hãng bay và danh mục]
    I --> J[Admin nhập thông tin vé]
    J --> K[Admin cấu hình số lượng ghế Economy/Business]
    K --> L[Admin nhập giá vé từng loại]
    L --> M[Admin cấu hình hành lý xách tay]
    M --> N[Admin upload ảnh chuyến bay]
    N --> O[Validate form]
    O -->|Invalid| P[Hiển thị lỗi validation]
    P --> F
    O -->|Valid| Q[Gọi API createFlightWithDetails]
    Q --> R[Backend tạo Flight entity]
    R --> S[Tạo FlightSlots theo cấu hình]
    S --> T[Lưu thông tin flight vào database]
    T --> U[Trả về flight created]
    U --> V[Upload images nếu có]
    V --> W[Gọi API uploadFlightImages]
    W --> X[Image Storage Service upload ảnh]
    X --> Y[Lưu thông tin ảnh vào database]
    Y --> Z[Hiển thị thông báo thành công]
    Z --> AA[Redirect về trang danh sách flights]
    AA --> BB[Admin có thể xem, chỉnh sửa, xóa flight]
```

## Chi tiết các thành phần chính:

### Backend Services:
- **FlightService**: Xử lý tìm kiếm và hiển thị flights
- **FlightBookingService**: Xử lý đặt vé và thanh toán
- **AdminFlightService**: Quản lý CRUD flights cho admin
- **PaymentService**: Xử lý thanh toán qua core banking
- **NotificationService**: Gửi email OTP qua Kafka

### Frontend Components:
- **FlightHome.vue**: Trang tìm kiếm và hiển thị flights
- **FlightDetail.vue**: Chi tiết chuyến bay
- **OrderDetail.vue**: Quản lý đơn hàng và thanh toán
- **formAdminFlight.vue**: Form tạo/chỉnh sửa flight cho admin

### Database Entities:
- **Flight**: Thông tin chuyến bay
- **FlightSlot**: Thông tin ghế và giá
- **FlightBooking**: Đặt vé
- **Order**: Đơn hàng tổng hợp
- **Customer**: Thông tin khách hàng

### External Services:
- **Core Banking Service**: Xử lý thanh toán và OTP
- **Redis**: Lưu trữ OTP tạm thời
- **Kafka**: Message queue cho notifications
- **Notification Service**: Gửi email OTP
