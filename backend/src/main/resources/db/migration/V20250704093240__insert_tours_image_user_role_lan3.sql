-- File migration tổng hợp, đã sửa cú pháp IDENTITY_INSERT cho SQL Server

-- 1. Chèn một người dùng mẫu để làm chủ sở hữu (owner)
SET IDENTITY_INSERT users ON;
IF NOT EXISTS (SELECT 1 FROM users WHERE id = 1)
BEGIN
    INSERT INTO users (id, name, email, phone, password_hash, address, created_at, updated_at)
    VALUES (
        1,
        'Admin Owner',
        'owner@example.com',
        '0987654321',
        '$2a$10$8.IdA5v4E9i95btr8aT3S.GHhT5Jz4sWHQLg4Dq5fGqjJ6fJ0u4aO', -- Mật khẩu mẫu là 'password'
        '123 Nguyễn Huệ, TP. Quy Nhơn, Bình Định',
        GETDATE(),
        GETDATE()
    );
END;
SET IDENTITY_INSERT users OFF;

-- 2. Chèn các vai trò (roles) mẫu
SET IDENTITY_INSERT roles ON;
IF NOT EXISTS (SELECT 1 FROM roles WHERE id = 1)
BEGIN
    INSERT INTO roles (id, name) VALUES (1, 'ADMIN');
END;

IF NOT EXISTS (SELECT 1 FROM roles WHERE id = 2)
BEGIN
    INSERT INTO roles (id, name) VALUES (2, 'AdminTours');
END;
SET IDENTITY_INSERT roles OFF;


-- 3. Gán một vai trò duy nhất là AdminTours cho user_id=1
IF NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = 1 AND role_id = 2)
BEGIN
    INSERT INTO user_roles (user_id, role_id, assigned_at) VALUES (1, 2, GETDATE());
END;


-- 4. Chèn 24 ảnh mẫu với URL theo định dạng localhost (BẮT ĐẦU TỪ ID 106)
SET IDENTITY_INSERT images ON;
INSERT INTO images (id, url, alt_text) VALUES
(106, 'http://localhost:8080/images/tours/quynhon-1.jpg', 'Ảnh tour Quy Nhơn 1'),
(107, 'http://localhost:8080/images/tours/quynhon-2.jpg', 'Ảnh tour Quy Nhơn 2'),
(108, 'http://localhost:8080/images/tours/quynhon-3.jpg', 'Ảnh tour Quy Nhơn 3'),
(109, 'http://localhost:8080/images/tours/quynhon-4.jpg', 'Ảnh tour Quy Nhơn 4'),
(110, 'http://localhost:8080/images/tours/danang-1.jpg', 'Ảnh tour Đà Nẵng 1'),
(111, 'http://localhost:8080/images/tours/danang-2.jpg', 'Ảnh tour Đà Nẵng 2'),
(112, 'http://localhost:8080/images/tours/danang-3.jpg', 'Ảnh tour Đà Nẵng 3'),
(113, 'http://localhost:8080/images/tours/danang-4.jpg', 'Ảnh tour Đà Nẵng 4'),
(114, 'http://localhost:8080/images/tours/hagiang-1.jpg', 'Ảnh tour Hà Giang 1'),
(115, 'http://localhost:8080/images/tours/hagiang-2.jpg', 'Ảnh tour Hà Giang 2'),
(116, 'http://localhost:8080/images/tours/hagiang-3.jpg', 'Ảnh tour Hà Giang 3'),
(117, 'http://localhost:8080/images/tours/hagiang-4.jpg', 'Ảnh tour Hà Giang 4'),
(118, 'http://localhost:8080/images/tours/mientay-1.jpg', 'Ảnh tour Miền Tây 1'),
(119, 'http://localhost:8080/images/tours/mientay-2.jpg', 'Ảnh tour Miền Tây 2'),
(120, 'http://localhost:8080/images/tours/mientay-3.jpg', 'Ảnh tour Miền Tây 3'),
(121, 'http://localhost:8080/images/tours/mientay-4.jpg', 'Ảnh tour Miền Tây 4'),
(122, 'http://localhost:8080/images/tours/phuquoc-1.jpg', 'Ảnh tour Phú Quốc 1'),
(123, 'http://localhost:8080/images/tours/phuquoc-2.jpg', 'Ảnh tour Phú Quốc 2'),
(124, 'http://localhost:8080/images/tours/phuquoc-3.jpg', 'Ảnh tour Phú Quốc 3'),
(125, 'http://localhost:8080/images/tours/phuquoc-4.jpg', 'Ảnh tour Phú Quốc 4'),
(126, 'http://localhost:8080/images/tours/halong-1.jpg', 'Ảnh tour Hạ Long 1'),
(127, 'http://localhost:8080/images/tours/halong-2.jpg', 'Ảnh tour Hạ Long 2'),
(128, 'http://localhost:8080/images/tours/halong-3.jpg', 'Ảnh tour Hạ Long 3'),
(129, 'http://localhost:8080/images/tours/halong-4.jpg', 'Ảnh tour Hạ Long 4');
SET IDENTITY_INSERT images OFF;

-- 5. Chèn 6 tour du lịch mẫu, sử dụng owner_id=1
SET IDENTITY_INSERT tours ON;
INSERT INTO tours (id, name, description, price, duration_days, departure_point, destination, status, owner_id, created_at) VALUES
(1, 'Khám phá Quy Nhơn - Eo Gió - Kỳ Co', 'Hành trình 3 ngày 2 đêm khám phá vẻ đẹp hoang sơ của biển Quy Nhơn, với các địa danh nổi tiếng như Eo Gió, Kỳ Co.', 3500000.00, 3, 'Quy Nhơn', 'Bình Định', 'ACTIVE', 1, GETDATE()),
(2, 'Hành trình Di sản Miền Trung: Đà Nẵng - Hội An', 'Tour 4 ngày 3 đêm tham quan Bà Nà Hills, phố cổ Hội An và thưởng thức ẩm thực đặc sắc của Đà Nẵng.', 4200000.00, 4, 'Đà Nẵng', 'Đà Nẵng - Quảng Nam', 'ACTIVE', 1, GETDATE()),
(3, 'Chinh phục Cung đường Hạnh phúc Hà Giang', 'Tour phượt 5 ngày 4 đêm dành cho những người yêu thích mạo hiểm, khám phá Mã Pí Lèng, cột cờ Lũng Cú.', 5500000.00, 5, 'Hà Nội', 'Hà Giang', 'ACTIVE', 1, GETDATE()),
(4, 'Miền Tây Sông Nước: Cần Thơ - Châu Đốc', 'Trải nghiệm cuộc sống trên sông nước, tham quan chợ nổi Cái Răng, rừng tràm Trà Sư trong 3 ngày 2 đêm.', 3100000.00, 3, 'TP. Hồ Chí Minh', 'Cần Thơ - An Giang', 'ACTIVE', 1, GETDATE()),
(5, 'Nghỉ dưỡng Thiên đường Đảo ngọc Phú Quốc', 'Kỳ nghỉ 4 ngày 3 đêm tại các resort sang trọng, khám phá VinWonders, Safari và các bãi biển đẹp nhất Phú Quốc.', 6800000.00, 4, 'Phú Quốc', 'Kiên Giang', 'ACTIVE', 1, GETDATE()),
(6, 'Khám phá Kỳ quan Thế giới Vịnh Hạ Long', 'Du thuyền 2 ngày 1 đêm trên Vịnh Hạ Long, tham quan hang Sửng Sốt, chèo kayak và ngắm hoàng hôn trên biển.', 2900000.00, 2, 'Hà Nội', 'Quảng Ninh', 'INACTIVE', 1, GETDATE());
SET IDENTITY_INSERT tours OFF;

-- 6. Liên kết ảnh với tour tương ứng (ID ảnh đã được cập nhật)
INSERT INTO tour_images (tour_id, image_id) VALUES
(1, 106), (1, 107), (1, 108), (1, 109),
(2, 110), (2, 111), (2, 112), (2, 113),
(3, 114), (3, 115), (3, 116), (3, 117),
(4, 118), (4, 119), (4, 120), (4, 121),
(5, 122), (5, 123), (5, 124), (5, 125),
(6, 126), (6, 127), (6, 128), (6, 129);

-- 7. Chèn lịch trình mẫu cho một vài tour
INSERT INTO tour_schedules (tour_id, schedule_date, activity) VALUES
(1, '2025-08-10', 'Ngày 1: Đón khách tại sân bay Phù Cát, tham quan Tháp Đôi, nhận phòng khách sạn.'),
(1, '2025-08-11', 'Ngày 2: Khám phá Kỳ Co bằng cano, lặn ngắm san hô, tham quan Eo Gió.'),
(1, '2025-08-12', 'Ngày 3: Mua sắm đặc sản, tiễn khách ra sân bay.'),
(2, '2025-09-01', 'Ngày 1: Tham quan Ngũ Hành Sơn, di chuyển đến Hội An nhận phòng.'),
(2, '2025-09-02', 'Ngày 2: Dạo chơi phố cổ Hội An, thưởng thức ẩm thực.'),
(3, '2025-10-05', 'Ngày 1: Khởi hành từ Hà Nội, chinh phục các cung đèo đầu tiên của Hà Giang.');

-- 8. Chèn các ngày khởi hành mẫu cho tour
INSERT INTO departures (tour_id, departure_date, adult_price, child_price, discount, seat_count, booked_seats) VALUES
(1, '2025-08-10', 3500000.00, 2800000.00, 100000.00, 30, 0),
(1, '2025-08-20', 3500000.00, 2800000.00, 0.00, 30, 0),
(2, '2025-09-01', 4200000.00, 3500000.00, 200000.00, 25, 0),
(2, '2025-09-15', 4200000.00, 3500000.00, 0.00, 25, 0),
(3, '2025-10-05', 5500000.00, 4800000.00, 0.00, 20, 0),
(4, '2025-08-25', 3100000.00, 2500000.00, 50000.00, 40, 0),
(5, '2025-09-10', 6800000.00, 5500000.00, 500000.00, 20, 0);