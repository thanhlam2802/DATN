-- 1. CREATE DATABASE
CREATE DATABASE FlightBookingDB;
GO
USE FlightBookingDB;
GO

-- 2. CREATE TABLES

-- Role
CREATE TABLE roles (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(50) NOT NULL
);

-- User
CREATE TABLE users (
    id INT PRIMARY KEY IDENTITY(1,1),
    username NVARCHAR(50) NOT NULL,
    email NVARCHAR(100) NOT NULL,
    password NVARCHAR(100) NOT NULL
);

-- User-Role
CREATE TABLE user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Airline (Airport)
CREATE TABLE airlines (
    id INT PRIMARY KEY IDENTITY(1,1),
    code NVARCHAR(10) NOT NULL,
    name NVARCHAR(100) NOT NULL,
    city NVARCHAR(100) NOT NULL
);

-- Airplane
CREATE TABLE airplanes (
    id INT PRIMARY KEY IDENTITY(1,1),
    model NVARCHAR(50) NOT NULL,
    capacity INT NOT NULL
);

-- Flight
CREATE TABLE flights (
    id INT PRIMARY KEY IDENTITY(1,1),
    airplane_id INT NOT NULL,
    flight_number NVARCHAR(10) NOT NULL,
    depart_airline_id INT NOT NULL,
    arrive_airline_id INT NOT NULL,
    depart_time DATETIME NOT NULL,
    arrive_time DATETIME NOT NULL,
    price DECIMAL(18,2) NOT NULL,
    FOREIGN KEY (airplane_id) REFERENCES airplanes(id),
    FOREIGN KEY (depart_airline_id) REFERENCES airlines(id),
    FOREIGN KEY (arrive_airline_id) REFERENCES airlines(id)
);

-- Booking
CREATE TABLE bookings (
    id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    flight_id INT NOT NULL,
    booking_date DATETIME NOT NULL,
    total_price DECIMAL(18,2) NOT NULL,
    status NVARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (flight_id) REFERENCES flights(id)
);

-- Customer (Passenger)
CREATE TABLE customers (
    id INT PRIMARY KEY IDENTITY(1,1),
    full_name NVARCHAR(100) NOT NULL,
    gender NVARCHAR(10),
    dob DATE,
    passport NVARCHAR(20),
    email NVARCHAR(100),
    phone NVARCHAR(20),
    seat_number VARCHAR(5),
    booking_id INT NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

-- Payment
CREATE TABLE payments (
    id INT PRIMARY KEY IDENTITY(1,1),
    booking_id INT NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    payment_method NVARCHAR(50) NOT NULL,
    payment_date DATETIME NOT NULL,
    status NVARCHAR(20) NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

-- Notification
CREATE TABLE notifications (
    id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    content NVARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    is_read BIT NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Review
CREATE TABLE reviews (
    id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    flight_id INT NULL,
    content NVARCHAR(255) NOT NULL,
    rating INT NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (flight_id) REFERENCES flights(id)
);

-- Voucher
CREATE TABLE vouchers (
    id INT PRIMARY KEY IDENTITY(1,1),
    code NVARCHAR(20) NOT NULL,
    discount DECIMAL(5,2) NOT NULL,
    expiry_date DATE NOT NULL
);

-- UserVoucher
CREATE TABLE user_vouchers (
    user_id INT NOT NULL,
    voucher_id INT NOT NULL,
    PRIMARY KEY (user_id, voucher_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (voucher_id) REFERENCES vouchers(id)
);

-- Tour (structure only)
CREATE TABLE tours (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) NOT NULL
    -- ... các trường khác ...
);

-- TourBooking (structure only)
CREATE TABLE tour_bookings (
    id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    tour_id INT NOT NULL
    -- ... các trường khác ...
);

-- Hotel (structure only)
CREATE TABLE hotels (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) NOT NULL
    -- ... các trường khác ...
);

-- HotelBooking (structure only)
CREATE TABLE hotel_bookings (
    id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    hotel_id INT NOT NULL
    -- ... các trường khác ...
);

-- Bus (structure only)
CREATE TABLE buses (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) NOT NULL
    -- ... các trường khác ...
);

-- BusBooking (structure only)
CREATE TABLE bus_bookings (
    id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    bus_id INT NOT NULL
    -- ... các trường khác ...
);

-- 3. INSERT SAMPLE DATA FOR FLIGHT MODULE ONLY

-- Roles
INSERT INTO roles (name) VALUES (N'USER'), (N'ADMIN');

-- Users
INSERT INTO users (username, email, password) VALUES
(N'nguyenvana', N'nguyenvana@gmail.com', N'123456'),
(N'tranthib', N'tranthib@gmail.com', N'123456'),
(N'levanc', N'levanc@gmail.com', N'123456'),
(N'phamthid', N'phamthid@gmail.com', N'123456'),
(N'hoangvane', N'hoangvane@gmail.com', N'123456');

-- User Roles
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), (2, 1), (3, 1), (4, 1), (5, 1), (1, 2);

-- Airlines (Airports)
INSERT INTO airlines (code, name, city) VALUES
(N'HAN', N'Sân bay Nội Bài', N'Hà Nội'),
(N'SGN', N'Sân bay Tân Sơn Nhất', N'Hồ Chí Minh'),
(N'DAD', N'Sân bay Đà Nẵng', N'Đà Nẵng'),
(N'CXR', N'Sân bay Cam Ranh', N'Khánh Hòa'),
(N'VCA', N'Sân bay Trà Nóc', N'Cần Thơ');

-- Airplanes
INSERT INTO airplanes (model, capacity) VALUES
(N'Airbus A321', 180),
(N'Boeing 787', 250),
(N'Airbus A350', 300),
(N'Boeing 777', 350),
(N'ATR 72', 70);

-- Flights
INSERT INTO flights (airplane_id, flight_number, depart_airline_id, arrive_airline_id, depart_time, arrive_time, price) VALUES
(1, N'VN201', 1, 2, '2024-07-01 08:00', '2024-07-01 10:00', 1500000),
(2, N'VN202', 2, 1, '2024-07-01 11:00', '2024-07-01 13:00', 1550000),
(3, N'VN203', 1, 3, '2024-07-02 09:00', '2024-07-02 10:30', 1200000),
(4, N'VN204', 3, 2, '2024-07-02 14:00', '2024-07-02 15:30', 1250000),
(5, N'VN205', 2, 4, '2024-07-03 07:30', '2024-07-03 09:30', 1700000);

-- Bookings
INSERT INTO bookings (user_id, flight_id, booking_date, total_price, status) VALUES
(1, 1, '2024-06-20 09:00', 3000000, N'CONFIRMED'),
(2, 2, '2024-06-20 10:00', 1550000, N'CONFIRMED'),
(3, 3, '2024-06-21 11:00', 2400000, N'CONFIRMED'),
(4, 4, '2024-06-21 12:00', 1250000, N'CANCELLED'),
(5, 5, '2024-06-22 13:00', 1700000, N'CONFIRMED');

-- Customers (Passengers)
INSERT INTO customers (full_name, gender, dob, passport, email, phone, seat_number, booking_id) VALUES
(N'Nguyễn Văn A', N'male', '1990-01-01', N'A1234567', N'vana@gmail.com', N'0912345678', N'12A', 1),
(N'Trần Thị B', N'female', '1992-05-10', N'B7654321', N'thib@gmail.com', N'0934567890', N'12B', 1),
(N'Lê Văn C', N'male', '1988-03-15', N'C1122334', N'levanc@gmail.com', N'0978123456', N'3C', 2),
(N'Phạm Thị D', N'female', '1995-07-20', N'D5566778', N'phamthid@gmail.com', N'0901234567', N'7B', 3),
(N'Hoàng Văn E', N'male', '1985-11-30', N'E9988776', N'hoangvane@gmail.com', N'0987654321', N'15D', 4),
(N'Vũ Thị F', N'female', '1993-09-25', N'F3344556', N'vuthif@gmail.com', N'0922334455', N'1A', 5);

-- Payments
INSERT INTO payments (booking_id, amount, payment_method, payment_date, status) VALUES
(1, 3000000, N'credit_card', '2024-06-20 09:05', N'SUCCESS'),
(2, 1550000, N'bank_transfer', '2024-06-20 10:10', N'SUCCESS'),
(3, 2400000, N'credit_card', '2024-06-21 11:10', N'SUCCESS'),
(4, 1250000, N'cash', '2024-06-21 12:10', N'FAILED'),
(5, 1700000, N'credit_card', '2024-06-22 13:10', N'SUCCESS'); 