-- Migration file created at 20250704110632
-- Migration file created at 20250704110436
-- Thêm cột số ngày vào bảng lịch trình tổng quan (bảng này giờ là "Ngày")
ALTER TABLE tour_schedules ADD day_number INT;
GO -- Dùng GO để phân tách các batch trong SQL Server

-- Tạo bảng mới để lưu các hoạt động chi tiết trong một ngày
CREATE TABLE tour_itinerary_activities (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    tour_schedule_id INT NOT NULL,
    activity_time VARCHAR(10),
    activity_title NVARCHAR(255) NOT NULL,
    description NVARCHAR(1000),
    icon VARCHAR(50),
    CONSTRAINT FK_activity_to_schedule FOREIGN KEY (tour_schedule_id) REFERENCES tour_schedules(id)
);
GO