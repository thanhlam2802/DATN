-- Migration file created at 20250703165158 

USE [DATN];
GO

BEGIN TRANSACTION;

BEGIN TRY
    DECLARE @hotel_id INT;
    DECLARE @room1_id INT, @room2_id INT, @room3_id INT, @room4_id INT;
    DECLARE @img_id INT;

    PRINT 'Đang chèn thông tin Hotel D''Melin...';
    INSERT INTO [dbo].[hotels] 
        ([name], [address], [description], [star_rating], [province_id], [email], [phone], [created_at], [updated_at])
    VALUES 
        (
            N'Hotel DMelin', 
            N'11-13-15, Lê Thánh Tôn, Phường Bến Nghé, Quận 1, Thành phố Hồ Chí Minh', 
            N'Không chỉ nằm trong tầm tay của nhiều địa điểm tham quan cho cuộc phiêu lưu của bạn, mà việc ở tại Hotel DMelin cũng sẽ mang đến cho bạn một kỳ nghỉ dễ chịu. Hotel DMelin rất được khuyến khích cho những người đi du lịch ba lô, những người muốn có một kỳ nghỉ giá cả phải chăng nhưng đồng thời vẫn thoải mái. Đối với bạn, những du khách muốn đi du lịch thoải mái với ngân sách eo hẹp, Hotel DMelin là nơi hoàn hảo để ở, nơi cung cấp các tiện nghi đầy đủ cũng như các dịch vụ tuyệt vời. Khách sạn này là sự lựa chọn hoàn hảo cho các cặp đôi đang tìm kiếm một kỳ nghỉ lãng mạn hoặc một nơi nghỉ dưỡng hưởng tuần trăng mật. Tận hưởng những đêm đáng nhớ nhất với người thân yêu của bạn bằng cách ở tại Hotel DMelin. Hotel DMelin là sự lựa chọn tuyệt vời cho những ai đang tìm kiếm một kỳ nghỉ sang trọng cho kỳ nghỉ của mình. Hãy nuông chiều bản thân với những dịch vụ tuyệt vời nhất và làm cho kỳ nghỉ của bạn đáng nhớ bằng cách ở lại đây. Bạn là một người nghiện mua sắm? Ở tại Hotel DMelin chắc chắn sẽ làm bạn thích thú với vô số trung tâm mua sắm gần đó. Khi ở trong một khách sạn, thiết kế và kiến ​​trúc là hai yếu tố quan trọng có thể làm hỏng đôi mắt của bạn. Với khung cảnh độc đáo, Hotel DMelin mang đến một chỗ ở dễ chịu cho kỳ nghỉ của bạn. Từ sự kiện kinh doanh đến các cuộc họp công ty, Hotel DMelin cung cấp các dịch vụ và tiện nghi đầy đủ mà bạn và đồng nghiệp của bạn cần. Hãy vui vẻ với nhiều tiện nghi giải trí dành cho bạn và cả gia đình tại Hotel DMelin, một chỗ ở tuyệt vời cho kỳ nghỉ gia đình của bạn.', 
            3, 
            3,
            N'rsvn.dmelin@gmail.com', 
            N'02838225858', 
            GETDATE(), 
            GETDATE()
        );
    SET @hotel_id = SCOPE_IDENTITY();

    PRINT 'Đang tạo sẵn chỗ chèn 7 hình ảnh chung cho Hotel D''Melin...';
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10027445-91a26f8df8c1c6939ea834b1ec78f26b.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-332,pr-true,q-80,w-480', N'Hotel D''Melin', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10027445-1c3c131e8704c8652471166c771b0e75.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Hotel D''Melin', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10027445-efa93e93979e687358635392264f3c32.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Hotel D''Melin', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10027445-31c030963697602f63b32baf977db6f7.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Hotel D''Melin', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10027445-05bec5a90f65bf72c9ff25864121fa51.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Hotel D''Melin', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10027445-31ebd4127db0f49c5623bc70ab4a888e.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Hotel D''Melin', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10027445-814691480542fde35a63de71fded65d0.jpeg?_src=imagekit&tr=dpr-2,h-145,q-80,w-145', N'Hotel D''Melin', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);


    -- ===================================================================
    -- 3. XỬ LÝ PHÒNG 1: Phòng Superior Giường Đôi
    -- ===================================================================
    PRINT 'Đang xử lý Phòng Superior Giường Đôi...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Superior', N'1 Giường đôi', 2, 1, 15, 25, GETDATE(), GETDATE());
    SET @room1_id = SCOPE_IDENTITY();

    -- Hình ảnh cho phòng 1 (3 hình)
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10027445-efa93e93979e687358635392264f3c32.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Superior Giường Đôi', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room1_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10027445-85c244c43cc57bfd1186292b9eb61fd6.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Superior Giường Đôi', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room1_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10027445-9cfef05b27d53ca4b079ed69c6f87df0.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Superior Giường Đôi', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room1_id, @img_id);

    -- Tiện nghi cho phòng 1
    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room1_id, 1), (@room1_id, 2), (@room1_id, 3), (@room1_id, 4);
    -- Biến thể cho phòng 1
    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES (@room1_id, N'Superior Room - Free Breakfast', 1, 1, 1, 750000.00, N'available', 60000.00, GETDATE(), GETDATE()),
           (@room1_id, N'Superior Room - Free Breakfast', 1, 1, 1, 890000.00, N'available', 71200.00, GETDATE(), GETDATE());

    -- ===================================================================
    -- 4. XỬ LÝ PHÒNG 2: Phòng Deluxe Giường Đôi
    -- ===================================================================
    PRINT 'Đang xử lý Phòng Deluxe Giường Đôi...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Deluxe', N'1 Giường đôi', 2, 1, 12, 28, GETDATE(), GETDATE());
    SET @room2_id = SCOPE_IDENTITY();

    -- Hình ảnh cho phòng 2 (4 hình)
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10027445-c23140337606655ab086cf7fdc4345bd.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Deluxe Giường Đôi', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10027445-8d1cc4b1ff622fd91d2934da44d1fb7a.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Deluxe Giường Đôi', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10027445-31ebd4127db0f49c5623bc70ab4a888e.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Deluxe Giường Đôi', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10027445-c13b5e35dc5e437e5c4feb07d863b15b.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Deluxe Giường Đôi', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);
    
    -- Tiện nghi cho phòng 2
    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room2_id, 1), (@room2_id, 2), (@room2_id, 3), (@room2_id, 5), (@room2_id, 6);
    -- Biến thể cho phòng 2
    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES (@room2_id, N'Deluxe', 0, 0, 0, 850000.00, N'available', 68000.00, GETDATE(), GETDATE()),
           (@room2_id, N'Deluxe - Free Breakfast', 1, 1, 1, 990000.00, N'available', 79200.00, GETDATE(), GETDATE());

    -- ===================================================================
    -- 5. XỬ LÝ PHÒNG 3: Phòng Deluxe 2 Giường Đơn
    -- ===================================================================
    PRINT 'Đang xử lý Phòng Deluxe 2 Giường Đơn...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Phòng Deluxe', N'2 Giường đơn', 2, 1, 10, 28, GETDATE(), GETDATE());
    SET @room3_id = SCOPE_IDENTITY();

    -- Hình ảnh cho phòng 3 (3 hình)
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/TzEv3ZUmG4-4Dz22hvmO9NUDzw1DGCIdWl4oPtKumOg=/lodging/84000000/83080000/83078700/83078626/57e8ceb4_z.jpg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Deluxe 2 Giường Đơn', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room3_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/TzEv3ZUmG4-4Dz22hvmO9NUDzw1DGCIdWl4oPtKumOg=/lodging/84000000/83080000/83078700/83078626/85d9ac3f_z.jpg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Deluxe 2 Giường Đơn', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room3_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/TzEv3ZUmG4-4Dz22hvmO9NUDzw1DGCIdWl4oPtKumOg=/lodging/84000000/83080000/83078700/83078626/8e0b4ea7_z.jpg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Deluxe 2 Giường Đơn', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room3_id, @img_id);

    -- Tiện nghi cho phòng 3
    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room3_id, 1), (@room3_id, 2), (@room3_id, 3), (@room3_id, 5), (@room3_id, 6), (@room3_id, 10);
    -- Biến thể cho phòng 3
    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES (@room3_id, N'Deluxe Twin', 0, 1, 1, 990000.00, N'available', 79200.00, GETDATE(), GETDATE()),
           (@room3_id, N'Deluxe Twin', 1, 1, 1, 1130000.00, N'available', 90400.00, GETDATE(), GETDATE());

    -- ===================================================================
    -- 6. XỬ LÝ PHÒNG 4: D'Melin Suite
    -- ===================================================================
    PRINT 'Đang xử lý Phòng D''Melin Suite...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'DMelin Suite', N'1 Giường đôi lớn', 2, 2, 5, 45, GETDATE(), GETDATE());
    SET @room4_id = SCOPE_IDENTITY();

    -- Hình ảnh cho phòng 4 (3 hình)
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10027445-42e33610e05584f7e41b22773f7ad217.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'D''Melin Suite', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room4_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10027445-e897c994d118a5e6831ce1dd5f335b31.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'D''Melin Suite', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room4_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10027445-1c5d177fdb8e26f86a774ca6e072520f.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'D''Melin Suite', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room4_id, @img_id);

    -- Tiện nghi cho phòng 4
    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room4_id, 1),(@room4_id, 2),(@room4_id, 3),(@room4_id, 5),(@room4_id, 6),(@room4_id, 7),(@room4_id, 8),(@room4_id, 10),(@room4_id, 12);
    -- Biến thể cho phòng 4
    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES (@room4_id, N'Suite', 1, 1, 1, 1550000.00, N'available', 124000.00, GETDATE(), GETDATE()),
           (@room4_id, N'Suite', 1, 0, 0, 1400000.00, N'available', 112000.00, GETDATE(), GETDATE());

    COMMIT TRANSACTION;
    PRINT 'Toàn bộ dữ liệu cho Hotel D''Melin đã được chèn thành công!';

END TRY
BEGIN CATCH
    -- Nếu có lỗi xảy ra, hủy bỏ mọi thay đổi
    ROLLBACK TRANSACTION;
    PRINT 'Đã xảy ra lỗi. Hủy bỏ mọi thay đổi.';
    
    -- In thông tin lỗi chi tiết
    DECLARE @ErrorMessage NVARCHAR(4000) = ERROR_MESSAGE();
    DECLARE @ErrorSeverity INT = ERROR_SEVERITY();
    DECLARE @ErrorState INT = ERROR_STATE();

    RAISERROR (@ErrorMessage, @ErrorSeverity, @ErrorState);
END CATCH
GO