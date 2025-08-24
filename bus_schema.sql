-- =====================================================
-- SCHEMA CHO HỆ THỐNG QUẢN LÝ XE BUS
-- =====================================================

-- Bảng loại xe bus
CREATE TABLE bus_categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    owner_id INT NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- Bảng tiện ích xe bus
CREATE TABLE bus_amenities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Bảng xe bus chính
CREATE TABLE buses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    license_plate VARCHAR(20) NOT NULL UNIQUE,
    total_seats INT NOT NULL,
    category_id INT NOT NULL,
    owner_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES bus_categories(id),
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- Bảng trung gian xe bus - tiện ích
CREATE TABLE bus_amenity (
    bus_id INT NOT NULL,
    amenity_id INT NOT NULL,
    PRIMARY KEY (bus_id, amenity_id),
    FOREIGN KEY (bus_id) REFERENCES buses(id) ON DELETE CASCADE,
    FOREIGN KEY (amenity_id) REFERENCES bus_amenities(id) ON DELETE CASCADE
);

-- Bảng tuyến đường
CREATE TABLE routes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    origin_location_id INT NOT NULL,
    destination_location_id INT NOT NULL,
    owner_id INT NOT NULL,
    distance_km DOUBLE,
    estimated_duration_minutes INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (origin_location_id) REFERENCES locations(id),
    FOREIGN KEY (destination_location_id) REFERENCES locations(id),
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- Bảng trung gian xe bus - tuyến đường
CREATE TABLE bus_routes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bus_id INT NOT NULL,
    route_id INT NOT NULL,
    FOREIGN KEY (bus_id) REFERENCES buses(id),
    FOREIGN KEY (route_id) REFERENCES routes(id)
);

-- Bảng giá theo loại xe và tuyến đường
CREATE TABLE route_bus_category_prices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    route_id INT NOT NULL,
    bus_category_id INT NOT NULL,
    owner_id INT NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    promotion_price DECIMAL(10,2),
    valid_from DATE NOT NULL,
    valid_to DATE NOT NULL,
    notes VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (route_id) REFERENCES routes(id),
    FOREIGN KEY (bus_category_id) REFERENCES bus_categories(id),
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- Bảng slot chuyến xe
CREATE TABLE bus_slots (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bus_id INT NOT NULL,
    route_id INT NOT NULL,
    owner_id INT NOT NULL,
    slot_date DATE NOT NULL,
    departure_time TIME NOT NULL,
    arrival_time TIME NOT NULL,
    actual_departure_time DATETIME,
    actual_arrival_time DATETIME,
    price DECIMAL(12,2) NOT NULL,
    total_seats INT,
    available_seats INT,
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    delay_reason VARCHAR(50),
    current_location VARCHAR(255),
    driver_contact_info VARCHAR(255),
    allow_manual_override BOOLEAN NOT NULL DEFAULT FALSE,
    time_tolerance_minutes INT NOT NULL DEFAULT 30,
    trip_type VARCHAR(20) NOT NULL DEFAULT 'ONE_TIME',
    recurring_pattern VARCHAR(20),
    recurring_end_date DATE,
    auto_status_update BOOLEAN NOT NULL DEFAULT FALSE,
    auto_reset_seats BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (bus_id) REFERENCES buses(id),
    FOREIGN KEY (route_id) REFERENCES routes(id),
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- Bảng ghế xe bus
CREATE TABLE bus_seats (
    id INT AUTO_INCREMENT PRIMARY KEY,
    seat_number VARCHAR(10) NOT NULL,
    bus_slot_id INT NOT NULL,
    is_booked BOOLEAN NOT NULL DEFAULT FALSE,
    price DECIMAL(12,2) NOT NULL,
    seat_type VARCHAR(20),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (bus_slot_id) REFERENCES bus_slots(id) ON DELETE CASCADE
);

-- Bảng đặt vé xe bus
CREATE TABLE bus_bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bus_slot_id INT NOT NULL,
    customer_id INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'RESERVED',
    booking_reference VARCHAR(20) NOT NULL UNIQUE,
    num_passengers INT NOT NULL,
    total_price DECIMAL(12,2) NOT NULL,
    booking_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME,
    notes VARCHAR(500),
    order_id INT,
    FOREIGN KEY (bus_slot_id) REFERENCES bus_slots(id),
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- Bảng trung gian đặt vé - ghế
CREATE TABLE bus_booking_seats (
    booking_id INT NOT NULL,
    seat_id INT NOT NULL,
    PRIMARY KEY (booking_id, seat_id),
    FOREIGN KEY (booking_id) REFERENCES bus_bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (seat_id) REFERENCES bus_seats(id) ON DELETE CASCADE
);

-- Bảng ảnh xe bus
CREATE TABLE bus_images (
    bus_id INT NOT NULL,
    image_id INT NOT NULL,
    PRIMARY KEY (bus_id, image_id),
    FOREIGN KEY (bus_id) REFERENCES buses(id) ON DELETE CASCADE,
    FOREIGN KEY (image_id) REFERENCES images(id) ON DELETE CASCADE
);

-- =====================================================
-- INDEXES CHO HIỆU SUẤT
-- =====================================================

-- Index cho bảng buses
CREATE INDEX idx_buses_owner ON buses(owner_id);
CREATE INDEX idx_buses_category ON buses(category_id);
CREATE INDEX idx_buses_license_plate ON buses(license_plate);

-- Index cho bảng bus_slots
CREATE INDEX idx_bus_slots_bus ON bus_slots(bus_id);
CREATE INDEX idx_bus_slots_route ON bus_slots(route_id);
CREATE INDEX idx_bus_slots_date ON bus_slots(slot_date);
CREATE INDEX idx_bus_slots_status ON bus_slots(status);
CREATE INDEX idx_bus_slots_owner ON bus_slots(owner_id);

-- Index cho bảng bus_seats
CREATE INDEX idx_bus_seats_slot ON bus_seats(bus_slot_id);
CREATE INDEX idx_bus_seats_booked ON bus_seats(is_booked);

-- Index cho bảng bus_bookings
CREATE INDEX idx_bus_bookings_slot ON bus_bookings(bus_slot_id);
CREATE INDEX idx_bus_bookings_customer ON bus_bookings(customer_id);
CREATE INDEX idx_bus_bookings_status ON bus_bookings(status);
CREATE INDEX idx_bus_bookings_reference ON bus_bookings(booking_reference);

-- Index cho bảng routes
CREATE INDEX idx_routes_origin ON routes(origin_location_id);
CREATE INDEX idx_routes_destination ON routes(destination_location_id);
CREATE INDEX idx_routes_owner ON routes(owner_id);

-- Index cho bảng route_bus_category_prices
CREATE INDEX idx_route_prices_route ON route_bus_category_prices(route_id);
CREATE INDEX idx_route_prices_category ON route_bus_category_prices(bus_category_id);
CREATE INDEX idx_route_prices_validity ON route_bus_category_prices(valid_from, valid_to);

-- =====================================================
-- COMMENTS
-- =====================================================

-- Thêm comments cho các bảng chính
ALTER TABLE buses COMMENT 'Bảng lưu thông tin xe bus';
ALTER TABLE bus_slots COMMENT 'Bảng lưu thông tin các chuyến xe';
ALTER TABLE bus_seats COMMENT 'Bảng lưu thông tin ghế xe';
ALTER TABLE bus_bookings COMMENT 'Bảng lưu thông tin đặt vé xe bus';
ALTER TABLE routes COMMENT 'Bảng lưu thông tin tuyến đường';
ALTER TABLE bus_categories COMMENT 'Bảng lưu thông tin loại xe bus';
ALTER TABLE bus_amenities COMMENT 'Bảng lưu thông tin tiện ích xe bus';
