# Flight System Architecture Diagram

## System Architecture Overview

```mermaid
graph TB
    %% View Layer
    subgraph "View Layer (UI)"
        FV[FlightView]
        AFV[AdminFlightView]
    end
    
    %% Controller Layer
    subgraph "Controller Layer"
        FC[FlightController]
        FBC[FlightBookingController]
        AFC[AdminFlightController]
    end
    
    %% DTO Layer
    subgraph "DTO Layer"
        FD[FlightDto]
        FBD[FlightBookingDto]
        FSD[FlightSlotDto]
    end
    
    %% Service Layer
    subgraph "Service Layer (Interfaces)"
        FS[<<Interface>> FlightService<Flight>]
        FBS[<<Interface>> FlightBookingService<FlightBooking>]
        AFS[<<Interface>> AdminFlightService<Flight>]
    end
    
    %% Database Layer
    subgraph "Database Tables"
        FT[<<Table>> flights]
        FBT[<<Table>> flight_bookings]
        FST[<<Table>> flight_slots]
        AT[<<Table>> airports]
        ALT[<<Table>> airlines]
        CT[<<Table>> customers]
        OT[<<Table>> orders]
    end
    
    %% Relationships
    FV --> FC
    AFV --> AFC
    FC --> FD
    FC --> FS
    FBC --> FBD
    FBC --> FBS
    AFC --> FD
    AFC --> AFS
    
    FD --> FS
    FBD --> FBS
    FSD --> FS
    
    FS --> FT
    FS --> FST
    FBS --> FBT
    FBS --> FST
    AFS --> FT
    AFS --> FBT
    AFS --> FST
    
    FS --> AT
    FS --> ALT
    FBS --> CT
    FBS --> OT
    
    %% Bidirectional relationships
    FS -.-> FBS
    FBS -.-> FS
```

## Detailed Component Relationships

```mermaid
graph LR
    subgraph "Flight Management Flow"
        A[User Search] --> B[FlightController]
        B --> C[FlightService]
        C --> D[FlightDAO]
        D --> E[Database]
    end
    
    subgraph "Booking Flow"
        F[User Booking] --> G[FlightBookingController]
        G --> H[FlightBookingService]
        H --> I[FlightBookingDAO]
        I --> E
    end
    
    subgraph "Admin Management"
        J[Admin Panel] --> K[AdminFlightController]
        K --> L[AdminFlightService]
        L --> M[FlightDAO]
        M --> E
    end
```

## Database Schema Relationships

```mermaid
erDiagram
    flights ||--o{ flight_slots : "has"
    flights ||--o{ flight_bookings : "contains"
    flights }o--|| airlines : "belongs_to"
    flights }o--|| airports : "departs_from"
    flights }o--|| airports : "arrives_at"
    flight_slots ||--o{ flight_bookings : "booked_in"
    flight_bookings }o--|| customers : "booked_by"
    flight_bookings }o--|| orders : "part_of"
    orders }o--|| customers : "belongs_to"
    
    flights {
        int id PK
        string flight_number
        string name
        datetime departure_time
        datetime arrival_time
        int airline_id FK
        int category_id FK
        int owner_id FK
        int departure_airport_id FK
        int arrival_airport_id FK
        datetime created_at
        datetime updated_at
    }
    
    flight_slots {
        int id PK
        decimal price
        boolean is_business
        string seat_number
        boolean is_window
        boolean is_aisle
        int carry_on_luggage
        int flight_id FK
        string status
    }
    
    flight_bookings {
        int id PK
        datetime booking_date
        decimal total_price
        int flight_slot_id FK
        int customer_id FK
        int order_id FK
    }
    
    airports {
        int id PK
        string name
    }
    
    airlines {
        int id PK
        string name
    }
    
    customers {
        int id PK
        string full_name
        string email
        string phone
        string passport
        date dob
        boolean gender
    }
    
    orders {
        int id PK
        string transaction_id
        int user_id FK
        decimal amount
        decimal original_amount
        string status
        datetime pay_date
        datetime created_at
        datetime expires_at
    }
```

## API Endpoints Structure

```mermaid
graph TD
    subgraph "Public Flight APIs"
        A1[POST /api/flights/search]
        A2[GET /api/flights/{id}]
        A3[GET /api/flights/{id}/available-seats]
        A4[POST /api/flights/find-available-slot]
        A5[GET /api/flights/airlines]
    end
    
    subgraph "Flight Booking APIs"
        B1[POST /api/bookings/flights]
        B2[GET /api/bookings/flights/{id}]
        B3[PUT /api/bookings/flights/{id}/cancel]
    end
    
    subgraph "Admin Flight APIs"
        C1[GET /api/admin/flights]
        C2[POST /api/admin/flights]
        C3[PUT /api/admin/flights/{id}]
        C4[DELETE /api/admin/flights/{id}]
        C5[GET /api/admin/flights/{id}/seats]
        C6[PUT /api/admin/flights/{id}/seats]
    end
    
    A1 --> FC[FlightController]
    A2 --> FC
    A3 --> FC
    A4 --> FC
    A5 --> FC
    
    B1 --> FBC[FlightBookingController]
    B2 --> FBC
    B3 --> FBC
    
    C1 --> AFC[AdminFlightController]
    C2 --> AFC
    C3 --> AFC
    C4 --> AFC
    C5 --> AFC
    C6 --> AFC
```

## Service Layer Methods

```mermaid
graph TD
    subgraph "FlightService Interface"
        FS1[searchFlights]
        FS2[getFlightDetail]
        FS3[getAvailableSeats]
        FS4[getAllFlights]
        FS5[findFirstAvailableSlot]
    end
    
    subgraph "FlightBookingService Interface"
        FBS1[bookFlight]
        FBS2[payForFlight]
        FBS3[getCustomerFlightBookings]
        FBS4[cancelFlightBooking]
    end
    
    subgraph "AdminFlightService Interface"
        AFS1[getFlights]
        AFS2[createFlight]
        AFS3[updateFlight]
        AFS4[deleteFlight]
        AFS5[getFlightStatistics]
    end
    
    FS1 --> FS[FlightService]
    FS2 --> FS
    FS3 --> FS
    FS4 --> FS
    FS5 --> FS
    
    FBS1 --> FBS[FlightBookingService]
    FBS2 --> FBS
    FBS3 --> FBS
    FBS4 --> FBS
    
    AFS1 --> AFS[AdminFlightService]
    AFS2 --> AFS
    AFS3 --> AFS
    AFS4 --> AFS
    AFS5 --> AFS
```

## Key Features and Capabilities

- **Flight Search & Filtering**: Tìm kiếm chuyến bay theo điểm đi, điểm đến, ngày bay
- **Seat Management**: Quản lý ghế ngồi với các loại khác nhau (Economy, Business)
- **Real-time Availability**: Kiểm tra ghế còn trống theo thời gian thực
- **Booking System**: Hệ thống đặt vé với thanh toán tích hợp
- **Admin Dashboard**: Giao diện quản trị toàn diện cho admin
- **Statistics & Reporting**: Thống kê chuyến bay, doanh thu, hiệu suất
- **Image Management**: Quản lý hình ảnh chuyến bay
- **Customer Management**: Quản lý thông tin khách hàng và lịch sử đặt vé
- **Payment Integration**: Tích hợp nhiều phương thức thanh toán
- **Order Management**: Quản lý đơn hàng và trạng thái thanh toán
