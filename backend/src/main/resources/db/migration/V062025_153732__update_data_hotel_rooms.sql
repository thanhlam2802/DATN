-- Migration file created on 19/06/2025 at 15:37:32.03 

UPDATE dbo.hotel_rooms
SET
    room_type = N'Deluxe Frontview Studio',
    bed_type  = N'1 giường cỡ queen'
WHERE id = 1;

UPDATE dbo.hotel_rooms
SET
    room_type = N'Deluxe Backview Studio',
    bed_type  = N'1 giường cỡ queen'
WHERE id = 2;

UPDATE dbo.hotel_rooms
SET
    room_type = N'Sofa 1 - Bedroom',
    bed_type  = N'1 giường cỡ queen'
WHERE id = 3;

-- 2. Sửa variant_name trong hotel_room_variants
UPDATE dbo.hotel_room_variants SET variant_name = N'Deluxe Frontview Studio - Non Refundable' WHERE id IN (1,2);
UPDATE dbo.hotel_room_variants SET variant_name = N'Deluxe Frontview Studio - Standard Rate'   WHERE id = 3;
UPDATE dbo.hotel_room_variants SET variant_name = N'Deluxe Backview Studio - Non Refundable'  WHERE id = 4;
UPDATE dbo.hotel_room_variants SET variant_name = N'Deluxe Backview Studio - Standard Rate'   WHERE id = 5;
UPDATE dbo.hotel_room_variants SET variant_name = N'Sofa 1-bedroom - Non Refundable'          WHERE id = 6;
UPDATE dbo.hotel_room_variants SET variant_name = N'Sofa 1-bedroom - Standard Rate'           WHERE id IN (7,8);

-- 3. Sửa tên tiện nghi trong amenities
UPDATE dbo.amenities SET name = N'Wifi miễn phí'               WHERE id = 1;
UPDATE dbo.amenities SET name = N'TV màn hình phẳng'          WHERE id = 2;
UPDATE dbo.amenities SET name = N'Điều hòa'                    WHERE id = 3;
UPDATE dbo.amenities SET name = N'Máy sấy tóc'                 WHERE id = 4;
UPDATE dbo.amenities SET name = N'Bồn tắm'                     WHERE id = 5;
UPDATE dbo.amenities SET name = N'Tủ lạnh mini'                WHERE id = 6;
UPDATE dbo.amenities SET name = N'Két sắt'                     WHERE id = 7;
UPDATE dbo.amenities SET name = N'Ban công riêng'             WHERE id = 8;
UPDATE dbo.amenities SET name = N'Dép đi trong phòng'         WHERE id = 9;
UPDATE dbo.amenities SET name = N'Bàn làm việc'                WHERE id = 10;
UPDATE dbo.amenities SET name = N'Ấm đun nước'                 WHERE id = 11;
UPDATE dbo.amenities SET name = N'Dịch vụ phòng'               WHERE id = 12;
UPDATE dbo.amenities SET name = N'Giấy vệ sinh miễn phí'      WHERE id = 13;
UPDATE dbo.amenities SET name = N'Cửa sổ cách âm'             WHERE id = 14;
UPDATE dbo.amenities SET name = N'Thang máy'                   WHERE id = 15;
UPDATE dbo.amenities SET name = N'Bãi đỗ xe miễn phí'         WHERE id = 16;
UPDATE dbo.amenities SET name = N'Hồ bơi'                      WHERE id = 17;
UPDATE dbo.amenities SET name = N'Phòng gym'                   WHERE id = 18;
UPDATE dbo.amenities SET name = N'Spa & massage'               WHERE id = 19;
UPDATE dbo.amenities SET name = N'Nhà hàng'                    WHERE id = 20;
UPDATE dbo.amenities SET name = N'Quầy bar'                    WHERE id = 21;
UPDATE dbo.amenities SET name = N'Khu vực hút thuốc'          WHERE id = 22;
UPDATE dbo.amenities SET name = N'Giường em bé'                WHERE id = 23;
UPDATE dbo.amenities SET name = N'Dịch vụ giặt ủi'            WHERE id = 24;
UPDATE dbo.amenities SET name = N'Máy pha cà phê'              WHERE id = 25;
UPDATE dbo.amenities SET name = N'Bếp riêng'                   WHERE id = 26;
UPDATE dbo.amenities SET name = N'Lò vi sóng'                  WHERE id = 27;
UPDATE dbo.amenities SET name = N'Máy lạnh trung tâm'          WHERE id = 28;
UPDATE dbo.amenities SET name = N'Hệ thống sưởi'               WHERE id = 29;
UPDATE dbo.amenities SET name = N'Khăn tắm'                    WHERE id = 30;
UPDATE dbo.amenities SET name = N'TV cáp'                      WHERE id = 31;
UPDATE dbo.amenities SET name = N'Tivi thông minh (smart TV)' WHERE id = 32;
UPDATE dbo.amenities SET name = N'Tủ quần áo'                  WHERE id = 33;
