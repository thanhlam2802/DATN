# Activity Diagram - Kịch bản 2: Quản lý đơn hàng (Chi tiết code-level với Exception Handling)

## Các thành phần tham gia:
1. **User**: Người dùng hệ thống
2. **Frontend (Vue.js Components)**: OrderDetail.vue, PaymentModal.vue, RefundModal.vue
3. **Backend Controllers**: OrderController, VoucherSocketController, WebSocketController
4. **Service Layer**: OrderService, VoucherService, PaymentService, RefundService
5. **DAO/Repository**: OrderDAO, VoucherDAO, PaymentDAO
6. **Database**: SQL Server database
7. **Core Banking Service**: Xử lý thanh toán và hoàn tiền trực tiếp
8. **Notification Service**: Gửi OTP qua Kafka và email
9. **WebSocket**: Real-time communication cho voucher và order updates
10. **Exception Handlers**: GlobalExceptionHandler, GraphQLExceptionResolver

## Luồng hoạt động chi tiết với Exception Handling:

### 1. Áp dụng voucher vào đơn hàng (WebSocket)

#### **Happy Path:**
```
User -> OrderDetail.vue: Mở trang chi tiết đơn hàng
Frontend -> createWebSocketConnection(): Kết nối WebSocket
WebSocket -> VoucherSocketController: Subscribe to /topic/orders/{orderId}

User -> VoucherInput.vue: Nhập mã voucher và click "Áp dụng"
Frontend -> stompClient.send(): Gửi message qua WebSocket
WebSocket -> VoucherSocketController.applyVoucher(): Nhận message tại /app/orders/{orderId}/apply-voucher

VoucherSocketController -> OrderService.applyVoucherToOrder(): Xử lý logic áp dụng voucher
OrderService -> VoucherDAO.findByCode(): Kiểm tra voucher tồn tại
Database -> VoucherDAO: Trả Voucher entity
VoucherDAO -> OrderService: Trả voucher nếu tồn tại và valid

OrderService -> validateVoucher(): Kiểm tra điều kiện áp dụng
OrderService -> calculateEffectiveDiscount(): Tính toán discount
OrderService -> OrderDAO.save(): Cập nhật order với discount và voucher
Database -> OrderDAO: UPDATE orders SET amount = ?, voucher_id = ? WHERE id = ?
OrderDAO -> OrderService: Trả order đã cập nhật

OrderService -> eventPublisher.publishEvent(): Phát sự kiện VoucherUsedUpEvent nếu voucher hết
VoucherEventListener -> SimpMessagingTemplate: Gửi notification qua /topic/vouchers/updates

OrderService -> VoucherSocketController: Trả OrderDto đã cập nhật
VoucherSocketController -> SimpMessagingTemplate.convertAndSend(): Gửi response về client
SimpMessagingTemplate -> Frontend: Response qua /topic/orders/{orderId}

Frontend -> handleVoucherResponse(): Xử lý response từ WebSocket
Frontend -> ToastContainer.vue: Hiển thị "Áp dụng voucher thành công!"
Frontend -> OrderDetail.vue: Update hiển thị giá mới và thông tin voucher
```

#### **Exception Cases:**

**1.1. WebSocket Connection Errors:**
```
Frontend -> createWebSocketConnection(): Kết nối WebSocket
[EXCEPTION] WebSocket connection failed
Frontend -> onError callback: Xử lý lỗi kết nối
Frontend -> setTimeout(): Auto-retry sau 5 giây
Frontend -> showConnectionError(): Hiển thị "Lỗi kết nối real-time. Vui lòng thử lại."
Frontend -> fallbackToRestApi(): Sử dụng REST API thay thế
```

**1.2. Voucher Validation Errors:**
```
OrderService -> validateVoucher(): Kiểm tra điều kiện áp dụng

[EXCEPTION] VoucherStatus != ACTIVE
OrderService -> throw RuntimeException("Mã giảm giá này không còn hoạt động.")
VoucherSocketController -> catch Exception: Xử lý lỗi
VoucherSocketController -> SimpMessagingTemplate.convertAndSend(): Gửi error response
Frontend -> handleVoucherResponse(): Nhận error
Frontend -> ToastContainer.vue: Hiển thị "Mã giảm giá này không còn hoạt động."

[EXCEPTION] Voucher expired
OrderService -> throw RuntimeException("Mã giảm giá đã hết hạn hoặc chưa đến ngày sử dụng.")
Frontend -> ToastContainer.vue: Hiển thị "Mã giảm giá đã hết hạn hoặc chưa đến ngày sử dụng."

[EXCEPTION] Voucher usage limit exceeded
OrderService -> throw RuntimeException("Mã giảm giá đã hết lượt sử dụng.")
Frontend -> ToastContainer.vue: Hiển thị "Mã giảm giá đã hết lượt sử dụng."

[EXCEPTION] Order amount insufficient
OrderService -> throw RuntimeException("Đơn hàng chưa đủ điều kiện tối thiểu để áp dụng mã này.")
Frontend -> ToastContainer.vue: Hiển thị "Đơn hàng chưa đủ điều kiện tối thiểu để áp dụng mã này."
```

**1.3. Order Status Errors:**
```
OrderService -> OrderDAO.findById(): Tìm order
[EXCEPTION] Order not found
OrderService -> throw RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId)
Frontend -> ToastContainer.vue: Hiển thị "Không tìm thấy đơn hàng"

[EXCEPTION] Order status != PENDING_PAYMENT
OrderService -> throw RuntimeException("Chỉ có thể áp dụng voucher cho đơn hàng đang chờ thanh toán.")
Frontend -> ToastContainer.vue: Hiển thị "Chỉ có thể áp dụng voucher cho đơn hàng đang chờ thanh toán."

[EXCEPTION] Order already has voucher
OrderService -> throw RuntimeException("Mỗi đơn hàng chỉ được áp dụng một mã giảm giá.")
Frontend -> ToastContainer.vue: Hiển thị "Mỗi đơn hàng chỉ được áp dụng một mã giảm giá."
```

**1.4. Database Errors:**
```
OrderService -> OrderDAO.save(): Cập nhật order
[EXCEPTION] Database constraint violation
Database -> throw DataIntegrityViolationException
OrderService -> catch Exception: Xử lý lỗi database
OrderService -> throw RuntimeException("Lỗi cập nhật đơn hàng. Vui lòng thử lại.")
Frontend -> ToastContainer.vue: Hiển thị "Lỗi cập nhật đơn hàng. Vui lòng thử lại."
```

### 2. Thanh toán đơn hàng (2 giai đoạn)

#### **Happy Path:**
```
User -> OrderDetail.vue: Click "Thanh toán" trên order đã áp voucher
Frontend -> PaymentModal.vue: Mở modal thanh toán

// GIAI ĐOẠN 1: Kiểm tra thông tin tài khoản và số dư
User -> PaymentForm.vue: Nhập thông tin ngân hàng (bankCode, accountNumber)
Frontend -> onAccountNumberBlur(): Validate account number
Frontend -> accountLookup(): Gọi API tra cứu tài khoản
accountLookup -> Core Banking Service: POST /api/v1/account-lookup
Request Body: { bankCode: "MB Bank", accountNumber: "123456789" }

Core Banking Service -> AccountService.lookupAccountDto(): Tra cứu thông tin tài khoản
AccountService -> AccountRepository.findByBankCodeAndAccountNumber(): Query từ database
Database -> AccountRepository: Trả Account entity
AccountRepository -> AccountService: Trả thông tin tài khoản
AccountService -> Core Banking Service: Trả AccountDto với số dư

Core Banking Service -> Frontend: Response với thông tin tài khoản
Frontend -> validateAmount(): Kiểm tra số dư có đủ thanh toán không
Frontend -> PaymentForm.vue: Hiển thị tên chủ tài khoản và số dư
Frontend -> PaymentForm.vue: Tự động điền số tiền và validate

// GIAI ĐOẠN 2: Thực hiện thanh toán
User -> PaymentForm.vue: Click "Xác nhận thanh toán"
Frontend -> proceedToPaymentGateway(): Gọi API thanh toán
Frontend -> servicePaymentMake(): Gọi core banking service
servicePaymentMake -> Core Banking Service: POST /api/v1/payments/service/make
Request Body: ServicePaymentRequestDto { customerBankCode, customerAccountNumber, amount, currency, remittanceInfo, idempotencyKey }

Core Banking Service -> PaymentService.makeServicePayment(): Xử lý thanh toán
PaymentService -> AccountRepository.findByBankCodeAndAccountNumber(): Validate tài khoản
PaymentService -> generateOTP(): Tạo OTP 6 số
PaymentService -> createPayment(): Tạo payment record với status PENDING
PaymentService -> RedisTemplate.opsForValue().set(): Lưu OTP vào Redis (10 phút)
PaymentService -> StreamBridge.send(): Gửi OTP qua Kafka topic "send-otp-mail"

Kafka -> Notification Service: Nhận message OTP
Notification Service -> NotificationServiceImpl.listenOtpMail(): Xử lý OTP mail
Notification Service -> sendOtpMail(): Gửi email OTP theo template ngân hàng
Notification Service -> Frontend: OTP được gửi đến email người dùng

PaymentService -> Frontend: Trả PaymentDto với paymentId và status "OTP_SENT"
Frontend -> PaymentModal.vue: Hiển thị form nhập OTP
Frontend -> startOtpCountdown(): Bắt đầu đếm ngược 10 phút

User -> PaymentForm.vue: Nhập OTP từ email
Frontend -> confirmOtp(): Gọi API xác nhận OTP
Frontend -> servicePaymentConfirm(): Gọi core banking service
servicePaymentConfirm -> Core Banking Service: POST /api/v1/payments/service/confirm
Request Body: { paymentId: "uuid", otp: "123456" }

Core Banking Service -> PaymentService.confirmServicePayment(): Xác nhận thanh toán
PaymentService -> RedisTemplate.opsForValue().get(): Lấy OTP từ Redis
PaymentService -> validateOTP(): Kiểm tra OTP hợp lệ
PaymentService -> RedisTemplate.delete(): Xóa OTP khỏi Redis
PaymentService -> validateBalance(): Kiểm tra số dư tài khoản
PaymentService -> transferMoney(): Thực hiện chuyển tiền
PaymentService -> updatePaymentStatus(): Cập nhật payment status thành COMPLETED
PaymentService -> createTransaction(): Tạo transaction records
PaymentService -> StreamBridge.send(): Gửi notification thành công qua Kafka

Kafka -> Notification Service: Nhận message thanh toán thành công
Notification Service -> sendPaymentSuccessMail(): Gửi email xác nhận thanh toán

PaymentService -> Frontend: Trả TransactionDto với transactionId
Frontend -> OrderDetail.vue: Cập nhật order status thành "PAID"
Frontend -> ToastContainer.vue: Hiển thị "Thanh toán thành công!"
```

#### **Exception Cases:**

**2.1. Account Lookup Errors:**
```
Frontend -> accountLookup(): Gọi API tra cứu tài khoản
[EXCEPTION] Account not found
Core Banking Service -> throw AccountNotFoundException("Không tìm thấy tài khoản")
GlobalExceptionHandler -> handleAccountNotFound(): Trả HTTP 404
Frontend -> catch error: Xử lý lỗi
Frontend -> notFound.value = true: Hiển thị "Tài khoản không tồn tại"
Frontend -> resetAccountInfo(): Reset form
```

**2.2. Insufficient Balance Errors:**
```
Frontend -> validateAmount(): Kiểm tra số dư
[EXCEPTION] Amount > availableBalance
Frontend -> amountError.value = true: Set error state
Frontend -> amountValidationMessage.value = "Số dư không đủ. Cần {amount}."
Frontend -> PaymentForm.vue: Disable "Xác nhận thanh toán" button
```

**2.3. Payment Creation Errors:**
```
PaymentService -> makeServicePayment(): Tạo payment
[EXCEPTION] System account not found
PaymentService -> throw AccountNotFoundException("Không tìm thấy tài khoản nguồn hệ thống")
GlobalExceptionHandler -> handleAccountNotFound(): Trả HTTP 404
Frontend -> ToastContainer.vue: Hiển thị "Lỗi hệ thống. Vui lòng thử lại sau."
```

**2.4. OTP Errors:**
```
PaymentService -> confirmServicePayment(): Xác nhận OTP
[EXCEPTION] OTP expired or not found
PaymentService -> throw RuntimeException("OTP expired or not found")
GlobalExceptionHandler -> handleRuntimeException(): Trả HTTP 500
Frontend -> otpError.value = "OTP không đúng hoặc đã hết hạn."
Frontend -> showOtpDialog.value = false: Đóng dialog OTP
Frontend -> ToastContainer.vue: Hiển thị "OTP đã hết hạn. Vui lòng thử lại."

[EXCEPTION] OTP invalid
PaymentService -> throw RuntimeException("OTP invalid")
Frontend -> otpError.value = "Mã OTP không đúng. Vui lòng kiểm tra lại."
```

**2.5. Insufficient Funds Errors:**
```
PaymentService -> validateBalance(): Kiểm tra số dư
[EXCEPTION] Insufficient funds
PaymentService -> throw InsufficientFundsException("Số dư tài khoản khách hàng không đủ")
GlobalExceptionHandler -> handleInsufficientFunds(): Trả HTTP 400
Frontend -> ToastContainer.vue: Hiển thị "Số dư không đủ để thực hiện thanh toán."
```

**2.6. Payment Already Completed:**
```
PaymentService -> confirmServicePayment(): Xác nhận payment
[EXCEPTION] Payment already completed
PaymentService -> throw RuntimeException("Giao dịch đã được xác nhận trước đó")
GlobalExceptionHandler -> handleRuntimeException(): Trả HTTP 500
Frontend -> ToastContainer.vue: Hiển thị "Giao dịch đã được xác nhận trước đó."
```

**2.7. OTP Countdown Expired:**
```
Frontend -> startOtpCountdown(): Bắt đầu đếm ngược 10 phút
[EXCEPTION] OTP countdown reaches 0
Frontend -> otpExpired.value = true: Set expired state
Frontend -> showOtpDialog.value = false: Tự động đóng dialog
Frontend -> ToastContainer.vue: Hiển thị "OTP đã hết hạn. Vui lòng thử lại thanh toán."
```

**2.8. Kafka/Notification Service Errors:**
```
PaymentService -> StreamBridge.send(): Gửi OTP qua Kafka
[EXCEPTION] Kafka connection failed
PaymentService -> catch Exception: Log error
PaymentService -> continue: Tiếp tục xử lý (OTP vẫn được lưu trong Redis)
Frontend -> ToastContainer.vue: Hiển thị "Đã gửi OTP. Vui lòng kiểm tra email."
```

### 3. Hủy đơn hàng (Scheduler tự động)

#### **Happy Path:**
```
// Scheduler chạy mỗi 30 giây để kiểm tra đơn hàng hết hạn
Backend -> OrderCleanupService.cancelExpiredOrders(): Chạy scheduled task
OrderCleanupService -> OrderDAO.findAllByStatusAndExpiresAtBefore(): Tìm orders PENDING_PAYMENT đã hết hạn
Database -> OrderDAO: Trả List<Order> expired

OrderCleanupService -> FlightBookingDAO.findByOrderId(): Lấy flight bookings của order
OrderCleanupService -> FlightBookingService.cancelBooking(): Hủy từng flight booking
FlightBookingService -> FlightSlotDAO.save(): Cập nhật flight slot status thành AVAILABLE

OrderCleanupService -> HotelBookingDAO.findByOrderId(): Lấy hotel bookings của order
OrderCleanupService -> HotelRoomDAO.save(): Hoàn lại số lượng phòng đã đặt
OrderCleanupService -> SimpMessagingTemplate.convertAndSend(): Gửi WebSocket notification

OrderCleanupService -> handleExpireBusBooking(): Xử lý bus bookings hết hạn
OrderCleanupService -> BusSeatDAO.save(): Giải phóng ghế đã đặt
OrderCleanupService -> BusBookingDAO.save(): Cập nhật booking status thành EXPIRED

OrderCleanupService -> OrderDAO.save(): Cập nhật order status thành CANCELLED
OrderCleanupService -> ApplicationEventPublisher.publishEvent(): Phát sự kiện order cancelled
OrderCleanupService -> AdminWebSocketController.sendExpiredOrderNotification(): Thông báo admin

// WebSocket notification cho admin
AdminWebSocketController -> SimpMessagingTemplate.convertAndSend(): Gửi notification qua /topic/admin/hotel/cancellations
```

#### **Exception Cases:**

**3.1. Database Connection Errors:**
```
OrderCleanupService -> OrderDAO.findAllByStatusAndExpiresAtBefore(): Query database
[EXCEPTION] Database connection failed
OrderCleanupService -> catch Exception: Log error
OrderCleanupService -> continue: Tiếp tục với orders đã load được
System -> log: "Database connection failed during order cleanup"
```

**3.2. Flight Booking Cancellation Errors:**
```
OrderCleanupService -> FlightBookingService.cancelBooking(): Hủy flight booking
[EXCEPTION] FlightSlot not found
FlightBookingService -> throw ResourceNotFoundException("Không tìm thấy flight slot")
OrderCleanupService -> catch Exception: Log error
OrderCleanupService -> continue: Tiếp tục với các booking khác
System -> log: "Failed to cancel flight booking: {bookingId}"
```

**3.3. Hotel Room Restoration Errors:**
```
OrderCleanupService -> HotelRoomDAO.save(): Hoàn lại số lượng phòng
[EXCEPTION] HotelRoom not found
OrderCleanupService -> catch Exception: Log error
OrderCleanupService -> continue: Tiếp tục với các booking khác
System -> log: "Failed to restore hotel room: {roomId}"
```

**3.4. WebSocket Notification Errors:**
```
OrderCleanupService -> SimpMessagingTemplate.convertAndSend(): Gửi WebSocket notification
[EXCEPTION] WebSocket connection failed
OrderCleanupService -> catch Exception: Log error
OrderCleanupService -> continue: Tiếp tục xử lý order
System -> log: "Không thể gửi WebSocket notification: {errorMessage}"
```

**3.5. Bus Seat Release Errors:**
```
OrderCleanupService -> BusSeatDAO.save(): Giải phóng ghế
[EXCEPTION] BusSeat not found
OrderCleanupService -> catch Exception: Log error
OrderCleanupService -> continue: Tiếp tục với các seat khác
System -> log: "Failed to release bus seat: {seatId}"
```

### 4. Hoàn tiền cho đơn hàng đã hủy

#### **Happy Path:**
```
User -> OrderDetail.vue: Click "Yêu cầu hoàn tiền" trên order CANCELLED
Frontend -> openRefundDialog(): Mở modal hoàn tiền

// VALIDATION: Kiểm tra có thể hủy vé không
Frontend -> getFlightDetail(): Lấy thông tin chuyến bay
Frontend -> validateRefundEligibility(): Kiểm tra thời gian khởi hành
Frontend -> formatRemainingTime(): Tính thời gian còn lại đến departure

// Kiểm tra điều kiện hủy vé
Frontend -> validationLogic(): 
- Nếu departure < 24h: "Không thể hủy vé"
- Nếu departure > 24h: "Có thể hủy và hoàn tiền"

// Nếu validation pass, mở dialog
Frontend -> RefundModal.vue: Hiển thị form hoàn tiền
User -> RefundForm.vue: Nhập lý do hoàn tiền
User -> RefundForm.vue: Click "Gửi yêu cầu hoàn tiền"

Frontend -> submitRefund(): Gọi API hoàn tiền
Frontend -> refundMake(): Gọi core banking service
refundMake -> Core Banking Service: POST /api/v1/refunds/make/{transactionId}
Request Body: RefundRequest { reason: "Không thể tham gia" }

Core Banking Service -> PaymentService.makeRefund(): Xử lý hoàn tiền
PaymentService -> TransactionRepository.findByTransactionId(): Tìm transaction gốc
PaymentService -> createRefundPayment(): Tạo payment record cho refund
PaymentService -> generateOTP(): Tạo OTP cho refund
PaymentService -> RedisTemplate.opsForValue().set(): Lưu OTP vào Redis
PaymentService -> StreamBridge.send(): Gửi OTP qua Kafka topic "otp-mail"

Kafka -> Notification Service: Nhận message OTP refund
Notification Service -> sendOtpMail(): Gửi email OTP hoàn tiền

PaymentService -> Frontend: Trả PaymentDto với paymentId
Frontend -> RefundModal.vue: Hiển thị form nhập OTP hoàn tiền
Frontend -> startRefundOtpCountdown(): Bắt đầu đếm ngược 10 phút

User -> RefundForm.vue: Nhập OTP từ email
Frontend -> confirmRefundOtp(): Gọi API xác nhận OTP
Frontend -> refundConfirm(): Gọi core banking service
refundConfirm -> Core Banking Service: POST /api/v1/refunds/confirm/{paymentId}
Request Body: { paymentId: "uuid", otp: "123456" }

Core Banking Service -> PaymentService.confirmRefund(): Xác nhận hoàn tiền
PaymentService -> validateRefundOTP(): Kiểm tra OTP hợp lệ
PaymentService -> processRefund(): Thực hiện hoàn tiền
PaymentService -> updateRefundStatus(): Cập nhật status thành COMPLETED
PaymentService -> createRefundTransaction(): Tạo transaction records
PaymentService -> StreamBridge.send(): Gửi notification hoàn tiền thành công

Kafka -> Notification Service: Nhận message hoàn tiền thành công
Notification Service -> sendRefundSuccessMail(): Gửi email xác nhận hoàn tiền

PaymentService -> Frontend: Trả RefundDto
Frontend -> OrderDetail.vue: Cập nhật order status thành "REFUNDED"
Frontend -> ToastContainer.vue: Hiển thị "Hoàn tiền thành công!"

// Cập nhật lại các dịch vụ để hoàn tác
Backend -> OrderService.cancelOrderAfterRefund(): Xử lý hủy order sau hoàn tiền
OrderService -> FlightSlotDAO.save(): Cập nhật flight slots thành AVAILABLE
OrderService -> HotelRoomDAO.save(): Hoàn lại số lượng phòng
OrderService -> BusSeatDAO.save(): Giải phóng ghế xe
OrderService -> OrderDAO.save(): Cập nhật order status thành REFUNDED
```

#### **Exception Cases:**

**4.1. Refund Eligibility Validation Errors:**
```
Frontend -> validateRefundEligibility(): Kiểm tra thời gian khởi hành
[EXCEPTION] Departure time < 24h
Frontend -> validationErrors.push(): Thêm lỗi validation
Frontend -> validationErrors.length > 0: Kiểm tra có lỗi
Frontend -> ToastContainer.vue: Hiển thị "Chuyến bay khởi hành trong {time}. Không thể hủy vé."
Frontend -> return: Không mở dialog hoàn tiền
```

**4.2. Transaction Not Found Errors:**
```
PaymentService -> TransactionRepository.findByTransactionId(): Tìm transaction gốc
[EXCEPTION] Transaction not found
PaymentService -> throw AccountNotFoundException("Không tìm thấy giao dịch gốc")
GlobalExceptionHandler -> handleAccountNotFound(): Trả HTTP 404
Frontend -> ToastContainer.vue: Hiển thị "Không tìm thấy giao dịch gốc để hoàn tiền."
```

**4.3. Refund OTP Errors:**
```
PaymentService -> confirmRefund(): Xác nhận OTP hoàn tiền
[EXCEPTION] OTP không đúng hoặc đã hết hạn
PaymentService -> throw RuntimeException("OTP không đúng hoặc đã hết hạn")
GlobalExceptionHandler -> handleRuntimeException(): Trả HTTP 500
Frontend -> refundOtpError.value = "OTP không đúng hoặc đã hết hạn."
```

**4.4. Insufficient System Balance Errors:**
```
PaymentService -> validateSystemBalance(): Kiểm tra số dư hệ thống
[EXCEPTION] System balance insufficient
PaymentService -> throw InsufficientFundsException("Số dư hệ thống không đủ để hoàn tiền")
GlobalExceptionHandler -> handleInsufficientFunds(): Trả HTTP 400
Frontend -> ToastContainer.vue: Hiển thị "Hệ thống không đủ số dư để hoàn tiền. Vui lòng thử lại sau."
```

**4.5. Refund Status Errors:**
```
PaymentService -> confirmRefund(): Xác nhận refund
[EXCEPTION] Refund not in PENDING status
PaymentService -> throw RuntimeException("Giao dịch refund không ở trạng thái PENDING")
GlobalExceptionHandler -> handleRuntimeException(): Trả HTTP 500
Frontend -> ToastContainer.vue: Hiển thị "Giao dịch refund không hợp lệ."
```

**4.6. Service Rollback Errors:**
```
OrderService -> cancelOrderAfterRefund(): Hoàn tác các dịch vụ
[EXCEPTION] FlightSlot update failed
OrderService -> catch Exception: Log error
OrderService -> continue: Tiếp tục với các service khác
System -> log: "Failed to rollback flight slot: {slotId}"

[EXCEPTION] HotelRoom update failed
OrderService -> catch Exception: Log error
OrderService -> continue: Tiếp tục với các service khác
System -> log: "Failed to rollback hotel room: {roomId}"
```

## **Global Exception Handling:**

### **Backend Exception Handler:**
```
GlobalExceptionHandler -> handleRuntimeException(): Xử lý RuntimeException
GlobalExceptionHandler -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR): Trả HTTP 500
GlobalExceptionHandler -> response.put("message", "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.")

GlobalExceptionHandler -> handleValidationExceptions(): Xử lý validation errors
GlobalExceptionHandler -> ResponseEntity.badRequest(): Trả HTTP 400
GlobalExceptionHandler -> response.put("errors", fieldErrors): Trả chi tiết lỗi validation

GlobalExceptionHandler -> handleAuthException(): Xử lý authentication errors
GlobalExceptionHandler -> ResponseEntity.status(401): Trả HTTP 401
GlobalExceptionHandler -> response.put("message", "Bạn chưa đăng nhập")
```

### **Core Banking Exception Handler:**
```
GlobalExceptionHandler -> handleAccountNotFound(): Xử lý AccountNotFoundException
GlobalExceptionHandler -> ResponseEntity.status(HttpStatus.NOT_FOUND): Trả HTTP 404
GlobalExceptionHandler -> response.put("message", ex.getMessage())

GlobalExceptionHandler -> handleInsufficientFunds(): Xử lý InsufficientFundsException
GlobalExceptionHandler -> ResponseEntity.status(HttpStatus.BAD_REQUEST): Trả HTTP 400
GlobalExceptionHandler -> response.put("message", ex.getMessage())
```

### **GraphQL Exception Handler:**
```
GraphQLExceptionResolver -> resolveException(): Xử lý GraphQL exceptions
GraphQLExceptionResolver -> buildError(): Tạo GraphQLError
GraphQLExceptionResolver -> ErrorType.BAD_REQUEST: Xử lý validation errors
GraphQLExceptionResolver -> ErrorType.NOT_FOUND: Xử lý resource not found
GraphQLExceptionResolver -> ErrorType.INTERNAL_ERROR: Xử lý server errors
```

## **Error Recovery Mechanisms:**

### **WebSocket Auto-reconnection:**
```
Frontend -> onError callback: WebSocket connection failed
Frontend -> setTimeout(): Auto-retry sau 5 giây
Frontend -> connectWebSocket(): Thử kết nối lại
Frontend -> maxRetries: Giới hạn số lần retry
Frontend -> fallbackToRestApi(): Sử dụng REST API nếu WebSocket fail
```

### **Database Transaction Rollback:**
```
Service -> @Transactional: Annotate method
Service -> catch Exception: Xử lý lỗi
Service -> TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(): Rollback transaction
Service -> throw new RuntimeException(): Re-throw exception
```

### **Kafka Message Retry:**
```
PaymentService -> StreamBridge.send(): Gửi message qua Kafka
[EXCEPTION] Kafka connection failed
PaymentService -> retry mechanism: Thử gửi lại message
PaymentService -> dead letter queue: Lưu message failed để xử lý sau
PaymentService -> notification fallback: Gửi notification qua email trực tiếp
```

## **Các lớp và phương thức liên quan:**

**Frontend (Vue.js Components)**:
- `OrderDetail.vue` - Trang chi tiết đơn hàng với WebSocket
- `PaymentModal.vue` - Modal thanh toán 2 giai đoạn
- `RefundModal.vue` - Modal hoàn tiền với validation
- `VoucherInput.vue` - Component nhập voucher qua WebSocket

**Backend (Spring Boot)**:
- `VoucherSocketController` - Xử lý voucher qua WebSocket
- `OrderController` - API REST cho order operations
- `WebSocketController` - Xử lý WebSocket notifications
- `OrderService`, `VoucherService`, `PaymentService` - Service layer
- `OrderDAO`, `VoucherDAO`, `PaymentDAO` - Data access

**Exception Handling**:
- `GlobalExceptionHandler` - Xử lý REST API exceptions
- `GraphQLExceptionResolver` - Xử lý GraphQL exceptions
- `BaseException`, `BusBookingException`, `ResourceNotFoundException` - Custom exceptions
- `ErrorCode` - Enum định nghĩa error codes

**Core Banking Integration**:
- `PaymentService.makeServicePayment()` - Giai đoạn 1: Tạo payment và gửi OTP
- `PaymentService.confirmServicePayment()` - Giai đoạn 2: Xác nhận OTP và thực hiện thanh toán
- `PaymentService.makeRefund()` - Tạo refund và gửi OTP
- `PaymentService.confirmRefund()` - Xác nhận OTP và thực hiện hoàn tiền

**Kafka & Notification Service**:
- `StreamBridge.send("otpMail-out-0")` - Gửi OTP qua Kafka
- `StreamBridge.send("refundMail-out-0")` - Gửi refund OTP qua Kafka
- `NotificationServiceImpl.listenOtpMail()` - Consumer OTP mail
- `NotificationServiceImpl.listenPaymentSuccessMail()` - Consumer payment success
- `NotificationServiceImpl.listenRefundSuccessMail()` - Consumer refund success

**Scheduler & Auto-cancellation**:
- `OrderCleanupService.cancelExpiredOrders()` - Scheduled task mỗi 30s
- `OrderCleanupService.handleExpiredBusBookings()` - Xử lý bus bookings hết hạn
- `BusBookingService.releaseExpiredReservations()` - Scheduled task mỗi 5 phút

**Database Entities**:
- `Order` - Bảng orders với các trạng thái: PENDING_PAYMENT, PAID, CANCELLED, REFUNDED
- `Voucher` - Bảng vouchers với điều kiện áp dụng
- `Payment` - Bảng payments lưu thông tin thanh toán
- `FlightSlot`, `HotelRoom`, `BusSeat` - Các resource cần hoàn tác khi hủy

**WebSocket Topics**:
- `/topic/orders/{orderId}` - Order updates cho specific order
- `/topic/vouchers/updates` - Voucher availability updates
- `/topic/admin/hotel/cancellations` - Admin notifications cho cancellations
- `/topic/hotels/{hotelId}/room-updates` - Hotel room updates
