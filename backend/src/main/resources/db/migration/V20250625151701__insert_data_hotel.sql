-- Migration file created at 20250625151701 

USE [DATN];
GO

BEGIN TRANSACTION;

BEGIN TRY

    DECLARE @hotel_id INT;
    DECLARE @room1_id INT, @room2_id INT, @room3_id INT, @room4_id INT;


    PRINT 'Đang chèn thông tin Khách sạn Melia Vinpearl Riverfront Đà Nẵng...';
    INSERT INTO [dbo].[hotels] 
        ([name], [address], [description], [star_rating], [province_id], [email], [phone], [created_at], [updated_at])
    VALUES 
        (
            N'Khách sạn Melia Vinpearl Riverfront Đà Nẵng', 
            N'341 Đường Trần Hưng Đạo, Phường An Hải Bắc, Quận Sơn Trà, Đà Nẵng', 
            N'Cho dù bạn đang lên kế hoạch cho một sự kiện hay những dịp đặc biệt khác, Melia Vinpearl Danang Riverfront là một lựa chọn tuyệt vời cho bạn với một phòng chức năng lớn và được trang bị tốt để phù hợp với yêu cầu của bạn. Melia Vinpearl Danang Riverfront là sự lựa chọn tuyệt vời cho những ai đang tìm kiếm một kỳ nghỉ sang trọng. Hãy nuông chiều bản thân với những dịch vụ tuyệt vời nhất và làm cho kỳ nghỉ của bạn đáng nhớ bằng cách ở lại đây. Từ sự kiện kinh doanh đến các cuộc họp công ty, Melia Vinpearl Danang Riverfront cung cấp các dịch vụ và tiện nghi đầy đủ mà bạn và đồng nghiệp của bạn cần. Hãy vui vẻ với nhiều tiện nghi giải trí khác nhau dành cho bạn và cả gia đình tại Melia Vinpearl Danang Riverfront, một chỗ ở tuyệt vời cho kỳ nghỉ gia đình của bạn. Nếu bạn dự định có một kỳ nghỉ dài hạn, việc ở lại Melia Vinpearl Danang Riverfront là lựa chọn phù hợp với bạn. Cung cấp nhiều tiện nghi và chất lượng dịch vụ tuyệt vời, chỗ ở này chắc chắn sẽ khiến bạn cảm thấy như ở nhà. Trong khi đi du lịch với bạn bè có thể rất vui, thì việc đi du lịch một mình có những đặc quyền riêng. Đối với chỗ ở, Melia Vinpearl Danang Riverfront phù hợp với những người coi trọng sự riêng tư trong thời gian lưu trú của bạn. Khách sạn này là địa điểm tốt nhất cho những ai mong muốn một kỳ nghỉ thanh bình và yên tĩnh, cách xa đám đông. Dịch vụ chất lượng cao nhất cùng với các tiện nghi phong phú sẽ giúp bạn có được trải nghiệm kỳ nghỉ tuyệt vời nhất.', 
            5, 
            2,
            N'res.mvprdn@melia.com', 
            N'02363642888', 
            GETDATE(), 
            GETDATE()
        );
    SET @hotel_id = SCOPE_IDENTITY();

    PRINT 'Đang xử lý Phòng Deluxe...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Phòng Deluxe', N'1 giường đôi lớn hoặc 2 giường đơn', 2, 1, 50, 45, GETDATE(), GETDATE());
    SET @room1_id = SCOPE_IDENTITY();

    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room1_id, 1), (@room1_id, 2), (@room1_id, 3), (@room1_id, 5), (@room1_id, 6), (@room1_id, 10);

    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES 
        (@room1_id, N'Deluxe Room - Room Only - Semi-flexible Rate (bf1)', 0, 1, 0, 2150000.00, N'available', 180000.00, GETDATE(), GETDATE()),
        (@room1_id, N'Deluxe Room - Breakfast - Semi-flexible Rate Bb (bf1)', 1, 1, 0, 2550000.00, N'available', 215000.00, GETDATE(), GETDATE()),
        (@room1_id, N'Deluxe Room - Breakfast - Flexible Rate Bb (clg)', 1, 1, 1, 2850000.00, N'available', 240000.00, GETDATE(), GETDATE());


    PRINT 'Đang xử lý Executive Suite River View...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Executive Suite River View', N'1 giường đôi lớn', 2, 2, 30, 53, GETDATE(), GETDATE());
    SET @room2_id = SCOPE_IDENTITY();

    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room2_id, 1), (@room2_id, 2), (@room2_id, 3), (@room2_id, 5), (@room2_id, 7), (@room2_id, 10), (@room2_id, 12), (@room2_id, 25);

    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES 
        (@room2_id, N'Deluxe Room Horizon View - Room Only - Semi-flexible Rate (bf1)', 0, 1, 1, 3200000.00, N'available', 270000.00, GETDATE(), GETDATE()),
        (@room2_id, N'Deluxe Room Horizon View - Breakfast - Semi-flexible Rate Bb (bf1)', 1, 1, 1, 3600000.00, N'available', 305000.00, GETDATE(), GETDATE()),
        (@room2_id, N'Deluxe Room Horizon View - Breakfast - Flexible Rate Bb (clg)', 1, 1, 0, 4200000.00, N'available', 355000.00, GETDATE(), GETDATE()),
        (@room2_id, N'Deluxe Room Horizon View - Breakfast - Flexible Rate Bb (clg)', 1, 0, 0, 3900000.00, N'available', 330000.00, GETDATE(), GETDATE());

    PRINT 'Đang xử lý Premier Suite...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Premier Suite', N'1 giường đôi lớn', 2, 2, 20, 56, GETDATE(), GETDATE());
    SET @room3_id = SCOPE_IDENTITY();

    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room3_id, 1), (@room3_id, 2), (@room3_id, 3), (@room3_id, 5), (@room3_id, 8), (@room3_id, 19), (@room3_id, 25);

    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES 
        (@room3_id, N'Bao gồm bữa sáng', 1, 1, 1, 4800000.00, N'available', 400000.00, GETDATE(), GETDATE()),
        (@room3_id, N'Bao gồm bữa sáng & Đưa đón sân bay', 1, 1, 0, 5500000.00, N'available', 460000.00, GETDATE(), GETDATE());

    PRINT 'Đang xử lý Căn Hộ Hai Phòng Ngủ...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Căn Hộ Hai Phòng Ngủ', N'1 giường đôi và 2 giường đơn', 4, 2, 10, 91, GETDATE(), GETDATE());
    SET @room4_id = SCOPE_IDENTITY();

    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room4_id, 1), (@room4_id, 2), (@room4_id, 3), (@room4_id, 5), (@room4_id, 10), (@room4_id, 26), (@room4_id, 27); -- Thêm Bếp riêng, Lò vi sóng

    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES 
        (@room4_id, N'Grand Premium Room - Room Only - Semi-flexible Rate (bf1)', 0, 1, 1, 6500000.00, N'available', 550000.00, GETDATE(), GETDATE()),
        (@room4_id, N'Grand Premium Room - Room Only - Semi-flexible Rate (bf1)', 1, 1, 0, 7500000.00, N'available', 640000.00, GETDATE(), GETDATE());


    COMMIT TRANSACTION;
    PRINT 'Toàn bộ dữ liệu cho Khách sạn Melia Vinpearl Riverfront Đà Nẵng đã được chèn thành công!';

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