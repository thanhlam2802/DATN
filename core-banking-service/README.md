# Core Banking Service - Tài liệu cấu trúc dữ liệu & API

## 1. Cấu trúc dữ liệu (Entity & Database)

### **Account** (Bảng tài khoản ngân hàng)
| Trường             | Kiểu dữ liệu      | Ý nghĩa                        |
|--------------------|------------------|--------------------------------|
| id                 | BIGINT (PK)      | Khóa chính tự tăng             |
| bankCode           | VARCHAR(10)      | Mã ngân hàng                   |
| accountNumber      | VARCHAR(20)      | Số tài khoản (unique)          |
| accountHolderName  | VARCHAR(100)     | Tên chủ tài khoản              |
| currency           | CHAR(3)          | Loại tiền tệ (VND, USD, ...)   |
| availableBalance   | DECIMAL(18,2)    | Số dư khả dụng                 |
| currentBalance     | DECIMAL(18,2)    | Số dư hiện tại                 |
| overdraftLimit     | DECIMAL(18,2)    | Hạn mức thấu chi               |
| createdAt          | DATETIME2        | Thời điểm tạo                  |
| updatedAt          | DATETIME2        | Thời điểm cập nhật cuối        |


### **Payment** (Bảng giao dịch thanh toán)
| Trường             | Kiểu dữ liệu      | Ý nghĩa                        |
|--------------------|------------------|--------------------------------|
| id                 | BIGINT (PK)      | Khóa chính tự tăng             |
| paymentId          | UUID (unique)    | Mã giao dịch thanh toán        |
| debtorAccountId    | BIGINT (FK)      | Tài khoản ghi nợ               |
| creditorAccountId  | BIGINT (FK)      | Tài khoản ghi có               |
| amount             | DECIMAL(18,2)    | Số tiền giao dịch              |
| currency           | CHAR(3)          | Loại tiền tệ                   |
| status             | VARCHAR(20)      | Trạng thái (PENDING, COMPLETED, FAILED, CANCELLED) |
| remittanceInfo     | VARCHAR(255)     | Thông tin chuyển khoản         |
| idempotencyKey     | VARCHAR(50)      | Khóa kiểm tra trùng lặp        |
| createdAt          | DATETIME2        | Thời điểm tạo                  |
| updatedAt          | DATETIME2        | Thời điểm cập nhật cuối        |

### **Transaction** (Bảng lịch sử giao dịch tài khoản)
| Trường             | Kiểu dữ liệu      | Ý nghĩa                        |
|--------------------|------------------|--------------------------------|
| id                 | BIGINT (PK)      | Khóa chính tự tăng             |
| transactionId      | UUID (unique)    | Mã giao dịch                   |
| accountId          | BIGINT (FK)      | Tài khoản liên quan            |
| bookingDate        | DATE             | Ngày ghi sổ                    |
| amount             | DECIMAL(18,2)    | Số tiền giao dịch (+/-)        |
| description        | VARCHAR(255)     | Mô tả giao dịch                |
| createdAt          | DATETIME2        | Thời điểm tạo                  |

### **Refund** (Bảng hoàn tiền)
| Trường             | Kiểu dữ liệu      | Ý nghĩa                        |
|--------------------|------------------|--------------------------------|
| id                 | BIGINT (PK)      | Khóa chính tự tăng             |
| refundId           | UUID (unique)    | Mã hoàn tiền                   |
| paymentDbId        | BIGINT (FK)      | Giao dịch thanh toán liên quan |
| amount             | DECIMAL(18,2)    | Số tiền hoàn                   |
| reason             | VARCHAR(255)     | Lý do hoàn tiền                |
| status             | VARCHAR(20)      | Trạng thái (PENDING, COMPLETED, FAILED) |
| createdAt          | DATETIME2        | Thời điểm tạo                  |

## 2. Quan hệ giữa các bảng
- **Payment** liên kết với **Account** qua debtorAccountId và creditorAccountId.
- **Transaction** liên kết với **Account** qua accountId.
- **Refund** liên kết với **Payment** qua paymentDbId.

## 3. API & Hướng dẫn sử dụng

### Xác thực
- Tất cả các API `/api/v1/*` yêu cầu header:
  ```
  X-API-KEY: test-api-key
  ```

### Account API
- **POST /api/v1/account-lookup**
  - Tra cứu thông tin tài khoản theo bankCode và accountNumber.
  - Request body:
    ```json
    {
      "bankCode": "VCB",
      "accountNumber": "0123456789"
    }
    ```
  - Response:
    ```json
    {
      "bankCode": "VCB",
      "accountNumber": "0123456789",
      "accountHolderName": "Nguyen Van A",
      "currency": "VND",
      "availableBalance": 1000000.00,
      "currentBalance": 1000000.00,
      "overdraftLimit": 500000.00
    }
    ```

- **GET /api/v1/accounts/{id}/balances**
  - Lấy số dư tài khoản theo id.
  - Response: như trên.

- **GET /api/v1/accounts/{id}/transactions?from=YYYY-MM-DD&to=YYYY-MM-DD**
  - Lấy lịch sử giao dịch tài khoản trong khoảng thời gian.
  - Response:
    ```json
    [
      {
        "transactionId": "uuid-1",
        "bookingDate": "2024-06-01",
        "amount": 100000.00,
        "description": "Nhận tiền chuyển khoản"
      },
      {
        "transactionId": "uuid-2",
        "bookingDate": "2024-06-02",
        "amount": -50000.00,
        "description": "Chuyển khoản"
      }
    ]
    ```

### Payment API
- **POST /api/v1/payments**
  - Khởi tạo giao dịch thanh toán.
  - Request body:
    ```json
    {
      "debtorAccountId": 1,
      "creditorAccountId": 2,
      "amount": 50000.00,
      "currency": "VND",
      "remittanceInfo": "Thanh toán vé máy bay",
      "idempotencyKey": "abc123xyz"
    }
    ```
  - Response:
    ```json
    {
      "paymentId": "uuid-1",
      "status": "COMPLETED",
      "amount": 50000.00,
      "currency": "VND",
      "createdAt": "2024-06-01T12:00:00Z"
    }
    ```

- **GET /api/v1/payments/{id}/status**
  - Lấy trạng thái giao dịch thanh toán.
  - Response: như trên.

- **DELETE /api/v1/payments/{id}**
  - Hủy giao dịch thanh toán.
  - Response: 200 OK

- **POST /api/v1/payments/{id}/refunds**
  - Hoàn tiền giao dịch.
  - Request body:
    ```json
    {
      "amount": 10000.00,
      "reason": "Khách hàng yêu cầu hoàn"
    }
    ```
  - Response:
    ```json
    {
      "refundId": "uuid-refund",
      "status": "COMPLETED",
      "amount": 10000.00,
      "createdAt": "2024-06-01T13:00:00Z"
    }
    ```

## 4. Lưu ý
- Tất cả các API đều trả về lỗi chuẩn JSON nếu có lỗi nghiệp vụ (không tìm thấy tài khoản, số dư không đủ, ...).
- Có thể thử API trực tiếp trên Swagger UI: `/swagger-ui.html` hoặc `/swagger-ui/index.html`.
- Để sử dụng, cần tạo sẵn dữ liệu tài khoản trong database hoặc qua API.
