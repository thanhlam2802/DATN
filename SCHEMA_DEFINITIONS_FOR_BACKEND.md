# GraphQL Schema Definitions for Backend Implementation

## 📋 **Input Types**

```graphql
# Search input cho việc tìm kiếm busSlots
input SearchBusSlotsInput {
  origin: String!              # Điểm đi (Route.origin)
  destination: String!         # Điểm đến (Route.destination) 
  slotDate: String!            # Ngày khởi hành (YYYY-MM-DD)
  minAvailableSeats: Int       # Số ghế trống tối thiểu
  busCategoryId: ID            # ID loại xe (optional)
  minPrice: BigDecimal         # Giá tối thiểu (optional)
  maxPrice: BigDecimal         # Giá tối đa (optional)
  status: BusSlotStatus        # Trạng thái (optional, default: SCHEDULED)
}

# Input cho việc tạo booking
input CreateBookingInput {
  busSlotId: ID!
  seatNumbers: [Int!]!
  passengerInfo: PassengerInfoInput!
  paymentMethod: String!
  totalAmount: BigDecimal!
  voucherCode: String
}

input PassengerInfoInput {
  fullName: String!
  phoneNumber: String!
  notes: String
}
```

## 📋 **Response Types**

```graphql
# Thông tin location (origins/destinations)
type LocationInfo {
  location: String!     # Tên địa điểm
  count: Int!          # Số chuyến có sẵn
}

# Thông tin occupancy của ghế
type SeatOccupancy {
  busSlotId: ID!
  totalSeats: Int!
  availableSeats: Int!
  occupiedSeats: Int!
  pendingSeats: Int!
  seatMap: [SeatInfo!]!
}

type SeatInfo {
  seatNumber: Int!
  status: SeatStatus!      # AVAILABLE, OCCUPIED, PENDING
  passengerName: String
  bookingId: ID
}

# Kết quả reservation tạm thời
type ReservationResult {
  success: Boolean!
  message: String!
  reservationId: ID
  expiresAt: String       # ISO datetime
}

type ReleaseResult {
  success: Boolean!
  message: String!
}

# Kết quả booking
type BookingResult {
  bookingId: ID!
  busSlot: BusSlot!
  seats: [Int!]!
  passenger: PassengerInfo!
  totalAmount: BigDecimal!
  status: String!
  qrCode: String
  createdAt: String!
}

type PassengerInfo {
  fullName: String!
  phoneNumber: String!
  notes: String
}

# Kết quả voucher
type VoucherResult {
  valid: Boolean!
  discountPercentage: Int!
  discountAmount: BigDecimal!
  message: String!
}

# Enum cho seat status
enum SeatStatus {
  AVAILABLE
  OCCUPIED  
  PENDING
}
```

## 📋 **Queries Cần Implement**

```graphql
type Query {
  # 🔍 Main search query - TỤ ĐIỂM CHÍNH
  searchBusSlots(input: SearchBusSlotsInput!): [BusSlot!]!
  
  # 📍 Location queries  
  getAvailableOrigins: [LocationInfo!]!
  getAvailableDestinations(origin: String!): [LocationInfo!]!
  
  # 🪑 Seat management
  getSeatOccupancy(busSlotId: ID!): SeatOccupancy!
  
  # 📋 Booking query
  getBookingById(bookingId: ID!): BookingResult
}
```

## 📋 **Mutations Cần Implement**

```graphql
type Mutation {
  # 🪑 Seat reservation (tạm thời - cho booking flow)
  reserveSeats(busSlotId: ID!, seatNumbers: [Int!]!): ReservationResult!
  releaseSeats(reservationId: ID!): ReleaseResult!
  
  # 📋 Booking creation
  createBooking(input: CreateBookingInput!): BookingResult!
  
  # 🎫 Voucher system
  applyVoucher(code: String!, amount: BigDecimal!): VoucherResult!
}
```

## 🔧 **Implementation Strategy**

### 1. **searchBusSlots** - Phức tạp nhất
```java
// Cần join giữa BusSlot, Bus, Route
// Filter theo:
// - Route.origin = input.origin  
// - Route.destination = input.destination
// - BusSlot.slotDate = input.slotDate
// - BusSlot.availableSeats >= input.minAvailableSeats
// - BusSlot.price BETWEEN input.minPrice AND input.maxPrice
// - Bus.categoryId = input.busCategoryId (nếu có)
// - BusSlot.status = input.status (default SCHEDULED)
```

### 2. **getAvailableOrigins/Destinations**
```java
// SELECT DISTINCT Route.origin, COUNT(*) 
// FROM routes r JOIN bus_slots bs ON r.id = bs.route_id
// WHERE bs.status = 'SCHEDULED' AND bs.slot_date >= CURRENT_DATE
// GROUP BY Route.origin

// Similar cho destinations với WHERE Route.origin = :origin
```

### 3. **getSeatOccupancy**
```java
// Cần join với BusBooking để lấy thông tin ghế đã đặt
// Return array of 1-totalSeats với status tương ứng
```

### 4. **Seat Reservation Flow**
```java
// reserveSeats: Tạo temporary reservation (expire after 15 minutes)
// releaseSeats: Xóa temporary reservation
// Cần table: seat_reservations(id, bus_slot_id, seat_numbers, expires_at)
```

## ⚠️ **Lưu Ý Quan Trọng**

1. **Dynamic Seat Layout**: Frontend sẽ generate layout based on `Bus.totalSeats`
2. **Real-time Updates**: `availableSeats` phải accurate theo bookings
3. **Seat Numbering**: 1-based indexing (ghế 1, 2, 3,...)
4. **Error Handling**: Specific error messages cho từng case
5. **Performance**: Index on (route.origin, route.destination, slot_date)

## 🎯 **Priority Order**

1. ✅ **searchBusSlots** - Main functionality
2. ✅ **getAvailableOrigins/Destinations** - Location data  
3. ✅ **getSeatOccupancy** - Real seat display
4. ⚡ **reserveSeats/releaseSeats** - Booking flow
5. 🎫 **createBooking** - Final booking
6. 🎁 **applyVoucher** - Discount system 