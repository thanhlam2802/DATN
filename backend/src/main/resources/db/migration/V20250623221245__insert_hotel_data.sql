USE [DATN];
GO

BEGIN TRANSACTION;

BEGIN TRY
    DECLARE @hotel_id INT;
    DECLARE @room1_id INT, @room2_id INT, @room3_id INT;

    PRINT 'Đang chèn thông tin Ohana Hotel (Hà Nội)...';
    INSERT INTO [dbo].[hotels] 
        ([name], [address], [description], [star_rating], [province_id], [email], [phone], [created_at], [updated_at])
    VALUES 
        (
            N'Ohana Hotel', 
            N'Số 18, Ngõ 120, Phố Hoàng Quốc Việt, Quận Cầu Giấy, Hà Nội', 
            N'Ohana Hotel là một khách sạn sở hữu vị trí đắc địa, tọa lạc tại trung tâm Hà Nội. Khách sạn có giá cả phải chăng và là lựa chọn sáng giá cho những ai đang tìm kiếm một nơi nghỉ ngơi thoải mái sau một ngày dài khám phá thủ đô.', 
            3, 
            1, 
            N'contact@ohanahotelhanoi.com', 
            N'02438251234', 
            GETDATE(), 
            GETDATE()
        );
    SET @hotel_id = SCOPE_IDENTITY();

    PRINT 'Đang xử lý Phòng Tiêu Chuẩn Giường Đôi...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Phòng Tiêu Chuẩn Giường Đôi', N'1 Giường đôi', 2, 1, 10, 18, GETDATE(), GETDATE());
    SET @room1_id = SCOPE_IDENTITY();

    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room1_id, 3), (@room1_id, 2), (@room1_id, 1);

    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES (@room1_id, N'Tiêu chuẩn - Không hoàn hủy', 0, 0, 1, 421574.00, N'available', 33726.00, GETDATE(), GETDATE());


    PRINT 'Đang xử lý Phòng Superior Giường Đôi...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Phòng Superior Giường Đôi', N'1 Giường đôi', 2, 1, 8, 20, GETDATE(), GETDATE());
    SET @room2_id = SCOPE_IDENTITY();

    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room2_id, 3), (@room2_id, 2), (@room2_id, 1), (@room2_id, 10);

    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES (@room2_id, N'Superior - Không hoàn hủy', 0, 0, 1, 481799.00, N'available', 38544.00, GETDATE(), GETDATE());


    PRINT 'Đang xử lý Phòng Deluxe Giường Đôi...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Phòng Deluxe Giường Đôi', N'1 Giường đôi', 2, 1, 5, 22, GETDATE(), GETDATE());
    SET @room3_id = SCOPE_IDENTITY();

    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room3_id, 3), (@room3_id, 2), (@room3_id, 1), (@room3_id, 10), (@room3_id, 6);

    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES 
        (@room3_id, N'Deluxe - Không hoàn hủy', 0, 0, 1, 542024.00, N'available', 43362.00, GETDATE(), GETDATE()),
        (@room3_id, N'Deluxe - Có thể hủy', 0, 1, 1, 585000.00, N'available', 46800.00, GETDATE(), GETDATE()),
        (@room3_id, N'Deluxe - Gói bữa sáng', 1, 1, 0, 650000.00, N'available', 52000.00, GETDATE(), GETDATE());

    COMMIT TRANSACTION;
    PRINT 'Toàn bộ dữ liệu cho Ohana Hotel (Hà Nội) đã được chèn thành công!';

END TRY
BEGIN CATCH
    ROLLBACK TRANSACTION;
    PRINT 'Đã xảy ra lỗi. Hủy bỏ mọi thay đổi.';
    
    DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
    DECLARE @ErrorSeverity INT = ERROR_SEVERITY();
    DECLARE @ErrorState INT = ERROR_STATE();

    RAISERROR (@ErrorMessage, @ErrorSeverity, @ErrorState);
END CATCH
GO