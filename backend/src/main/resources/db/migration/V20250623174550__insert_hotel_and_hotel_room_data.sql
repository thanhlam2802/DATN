-- Migration file created at 20250623174550 

BEGIN TRANSACTION;

BEGIN TRY

    DECLARE @hotel_id INT;
    DECLARE @room1_id INT, @room2_id INT, @room3_id INT;

    INSERT INTO [dbo].[hotels] 
        ([name], [address], [description], [star_rating], [province_id], [email], [phone], [owner_id], [created_at], [updated_at])
    VALUES 
        (
            N'Angsana Quan Lạn Hạ Long Bay', 
            N'Thôn Sơn Hào, Xã Quan Lạn, Huyện Vân Đồn, Tỉnh Quảng Ninh, Việt Nam', 
            N'Khách sạn này là nơi tốt nhất dành cho những ai mong muốn một nơi thanh bình, thư thái để ẩn mình khỏi đám đông ồn ã, xô bồ. Dịch vụ thượng hạng song hành với hàng loạt tiện nghi phong phú sẽ đem đến cho quý khách trải nghiệm của một kỳ nghỉ viên mãn nhất. Hưởng thụ một ngày thư thái đầy thú vị tại hồ bơi dù quý khách đang du lịch một mình hay cùng người thân. Sóng WiFi phủ khắp các khu vực chung của khách sạn cho phép quý khách luôn kết nối với gia đình và bè bạn. Angsana Quan Lan Ha Long Bay là khách sạn sở hữu đầy đủ tiện nghi và dịch vụ xuất sắc theo nhận định của hầu hết khách lưu trú. Tận hưởng trải nghiệm lưu trú xa hoa đầy thú vị không đâu sánh bằng tại Angsana Quan Lan Ha Long Bay.',  
            5, 
            10, 
            N'contact@angsanaquanlan.com', 
            N'02033993388', 
            NULL, 
            GETDATE(), 
            GETDATE()
        );

    SET @hotel_id = SCOPE_IDENTITY();
    
    INSERT INTO [dbo].[hotel_rooms] 
        ([hotel_id], [room_type], [bed_type], [description], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES 
        (@hotel_id, N'King Oceanview', N'1 giường đôi lớn', N'Suite rộng rãi với tầm nhìn ra hồ bơi, được trang bị đầy đủ tiện nghi hiện đại.', 2, 1, 15, 50, GETDATE(), GETDATE());
    SET @room1_id = SCOPE_IDENTITY();

    INSERT INTO [dbo].[hotel_rooms] 
        ([hotel_id], [room_type], [bed_type], [description], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES 
        (@hotel_id, N'Junior Suite', N'1 giường đôi và 2 giường đơn', N'Dinh thự sang trọng với 2 phòng ngủ, không gian sinh hoạt chung và tầm nhìn tuyệt đẹp ra biển.', 4, 2, 10, 60, GETDATE(), GETDATE());
    SET @room2_id = SCOPE_IDENTITY();

    INSERT INTO [dbo].[hotel_rooms]
        ([hotel_id], [room_type], [bed_type], [description], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES
        (@hotel_id, N'Twin Oceanview', N'1 giường đôi lớn', N'Suite trang nhã với ban công riêng nhìn ra biển, mang đến không gian nghỉ dưỡng thư thái.', 2, 1, 20, 55, GETDATE(), GETDATE());
    SET @room3_id = SCOPE_IDENTITY();




    INSERT INTO [dbo].[hotel_room_variants] 
        ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES
        (@room1_id, N'Double Oceanview King Room', 1, 0, 1, 4449150.00, N'available', 422170.00, GETDATE(), GETDATE()),
        (@room1_id, N'Double Oceanview King Room', 1, 1, 1, 5190875.00, N'available', 492633.00, GETDATE(), GETDATE());

    INSERT INTO [dbo].[hotel_room_variants] 
        ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES
        (@room2_id, N'Junior Suite Junior Suite', 0, 0, 0, 7860250.00, N'available', 745854.00, GETDATE(), GETDATE()),
        (@room2_id, N'Junior Suite Junior Suite', 1, 1, 1, 9170292.00, N'available', 869968.00, GETDATE(), GETDATE());

    INSERT INTO [dbo].[hotel_room_variants]
        ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES
        (@room3_id, N'Twin Oceanview Twin Room', 0, 0, 0, 5296875.00, N'available', 502688.00, GETDATE(), GETDATE()),
        (@room3_id, N'Twin Oceanview Twin Room', 1, 1, 1, 6179688.00, N'available', 586520.00, GETDATE(), GETDATE());


    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES
        (@room1_id, 1), 
        (@room1_id, 2), 
        (@room1_id, 3), 
        (@room1_id, 5), 
        (@room1_id, 7), 
        (@room1_id, 10),
        (@room1_id, 11);

    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES
        (@room2_id, 1), 
        (@room2_id, 2), 
        (@room2_id, 3), 
        (@room2_id, 5), 
        (@room2_id, 7), 
        (@room2_id, 10),
        (@room2_id, 11),
        (@room2_id, 26);

    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES
        (@room3_id, 1), 
        (@room3_id, 2), 
        (@room3_id, 3), 
        (@room3_id, 5), 
        (@room3_id, 8), 
        (@room3_id, 10),
        (@room3_id, 12);

    COMMIT TRANSACTION;
    PRINT 'Dữ liệu khách sạn Angsana Quan Lan đã được chèn thành công!';

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