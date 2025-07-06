-- Chèn hoặc cập nhật (UPSERT) dữ liệu lịch trình chi tiết cho tour có id = 1

-- Bật chế độ chèn/cập nhật ID thủ công cho bảng tour_schedules
SET IDENTITY_INSERT tour_schedules ON;
GO

-- Dùng MERGE để chèn hoặc cập nhật các ngày trong lịch trình
-- SỬA LỖI: Thêm cột 'schedule_date' để tránh lỗi NOT NULL
MERGE INTO tour_schedules AS target
USING (
    VALUES
        (1, 1, 1, 'KHÁM PHÁ HANG ĐỘNG - ẨM THỰC ĐỊA PHƯƠNG', '2025-08-10'),
        (2, 1, 2, 'KHÁM PHÁ LÀNG NGHỀ - TRẢI NGHIỆM KAYAK', '2025-08-11'),
        (3, 1, 3, 'KHÁM PHÁ BIỂN ĐẢO - TẠM BIỆT', '2025-08-12')
) AS source (id, tour_id, day_number, activity, schedule_date) -- Thêm cột ở đây
ON (target.id = source.id)
WHEN MATCHED THEN
    UPDATE SET
        target.tour_id = source.tour_id,
        target.day_number = source.day_number,
        target.activity = source.activity,
        target.schedule_date = source.schedule_date -- Thêm cột ở đây
WHEN NOT MATCHED BY TARGET THEN
    INSERT (id, tour_id, day_number, activity, schedule_date) -- Thêm cột ở đây
    VALUES (source.id, source.tour_id, source.day_number, source.activity, source.schedule_date); -- Thêm cột ở đây
GO

-- Tắt chế độ chèn/cập nhật ID thủ công
SET IDENTITY_INSERT tour_schedules OFF;
GO

-- Bật chế độ chèn/cập nhật ID thủ công cho bảng tour_itinerary_activities
SET IDENTITY_INSERT tour_itinerary_activities ON;
GO

-- Dùng MERGE để chèn hoặc cập nhật các hoạt động chi tiết
MERGE INTO tour_itinerary_activities AS target
USING (
    VALUES
        -- Hoạt động cho NGÀY 1 (tour_schedule_id = 1)
        (1, 1, '07:00', 'Đón khách', 'Xe và HDV đón khách tại các điểm hẹn trong thành phố', 'fas fa-bus'),
        (2, 1, '09:30', 'Tham quan hang động', 'Khám phá hệ thống hang động với hướng dẫn viên chuyên nghiệp', 'fas fa-mountain'),
        (3, 1, '12:00', 'Ăn trưa', 'Thưởng thức ẩm thực địa phương tại nhà hàng', 'fas fa-utensils'),
        (4, 1, '14:00', 'Check-in khách sạn', 'Nhận phòng và nghỉ ngơi tại khách sạn 4 sao', 'fas fa-hotel'),
        (5, 1, '19:00', 'Ăn tối', 'Buffet tối tại khách sạn', 'fas fa-utensils'),
        -- Hoạt động cho NGÀY 2 (tour_schedule_id = 2)
        (6, 2, '07:00', 'Ăn sáng', 'Buffet sáng tại khách sạn', 'fas fa-coffee'),
        (7, 2, '09:00', 'Thăm làng nghề', 'Tham quan và tìm hiểu về làng nghề truyền thống', 'fas fa-hands'),
        (8, 2, '12:00', 'Ăn trưa', 'Thưởng thức đặc sản địa phương', 'fas fa-utensils'),
        (9, 2, '14:00', 'Chèo thuyền kayak', 'Trải nghiệm chèo thuyền kayak trên sông', 'fas fa-ship'),
        (10, 2, '19:00', 'Ăn tối', 'BBQ tối tại bãi biển', 'fas fa-fire'),
        -- Hoạt động cho NGÀY 3 (tour_schedule_id = 3)
        (11, 3, '07:00', 'Ăn sáng', 'Buffet sáng tại khách sạn', 'fas fa-coffee'),
        (12, 3, '09:00', 'Tham quan đảo', 'Khám phá các đảo nhỏ bằng cano', 'fas fa-island-tropical'),
        (13, 3, '12:00', 'Ăn trưa', 'Ăn trưa với hải sản tươi sống', 'fas fa-fish'),
        (14, 3, '14:00', 'Tự do mua sắm', 'Tham quan và mua sắm tại chợ địa phương', 'fas fa-shopping-bag'),
        (15, 3, '16:00', 'Trở về', 'Khởi hành về điểm đón ban đầu', 'fas fa-bus-alt')
) AS source (id, tour_schedule_id, activity_time, activity_title, description, icon)
ON (target.id = source.id)
WHEN MATCHED THEN
    UPDATE SET
        target.tour_schedule_id = source.tour_schedule_id,
        target.activity_time = source.activity_time,
        target.activity_title = source.activity_title,
        target.description = source.description,
        target.icon = source.icon
WHEN NOT MATCHED BY TARGET THEN
    INSERT (id, tour_schedule_id, activity_time, activity_title, description, icon)
    VALUES (source.id, source.tour_schedule_id, source.activity_time, source.activity_title, source.description, source.icon);
GO

-- Tắt chế độ chèn/cập nhật ID thủ công
SET IDENTITY_INSERT tour_itinerary_activities OFF;
GO
