-- Migration file created at 20250703173212 

USE [DATN];
GO

BEGIN TRANSACTION;

BEGIN TRY
    DECLARE @hotel_id INT;
    DECLARE @room1_id INT, @room2_id INT, @room3_id INT, @room4_id INT, @room5_id INT;
    DECLARE @img_id INT;

    -- ===================================================================
    -- 1. INSERT THÔNG TIN KHÁCH SẠN
    -- ===================================================================
    PRINT 'Đang chèn thông tin Fleur De Lys Resort & Spa Long Hai...';
    INSERT INTO [dbo].[hotels] 
        ([name], [address], [description], [star_rating], [province_id], [email], [phone], [created_at], [updated_at])
    VALUES 
        (
            N'Fleur De Lys Resort & Spa Long Hai', 
            N'Tỉnh Lộ 44A, Thị Trấn Long Hải, Huyện Long Điền, Tỉnh Bà Rịa - Vũng Tàu', 
            N'Fleur De Lys Resort & Spa Long Hai là một khu nghỉ dưỡng 4 sao sang trọng, tọa lạc tại một trong những vị trí đẹp nhất của Long Hải. Với bãi biển riêng, hồ bơi vô cực và các biệt thự có hồ bơi riêng, khu nghỉ dưỡng mang đến một không gian yên bình và đẳng cấp cho du khách.', 
            5, 
            4,
            N'inquiry.longhai@fleurdelys.vn', 
            N'02543662222', 
            GETDATE(), 
            GETDATE()
        );
    SET @hotel_id = SCOPE_IDENTITY();

    -- ===================================================================
    -- 2. CHỖ TRỐNG ĐỂ INSERT HÌNH ẢNH CHUNG CỦA KHÁCH SẠN (7 HÌNH)
    -- ===================================================================
    PRINT 'Đang tạo sẵn chỗ chèn 7 hình ảnh chung cho Fleur De Lys...';
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/10026141-e6627049ad13419cdfd02cb321d7fd50.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-332,pr-true,q-80,w-480', N'Fleur De Lys Resort & Spa Long Hai', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10026141-23634d302baf41a3f8a2cf1f78ffb319.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Fleur De Lys Resort & Spa Long Hai', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/10026141-5962a1dbf2af86782ecdc033f37cbd0b.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Fleur De Lys Resort & Spa Long Hai', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/10026141-451c6a0cb9f9a24c352a2e94bbbfc12f.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Fleur De Lys Resort & Spa Long Hai', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10026141-48ae92b9f7e7dc3e93151bcff1a93a23.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Fleur De Lys Resort & Spa Long Hai', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/10026141-ab952d0c4d63c9822b3fcddafc1fea4f.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Fleur De Lys Resort & Spa Long Hai', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/10026141-7b291c49ddbef6410ee2326bd6d8ec5e.jpg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,h-360,pr-true,q-80,w-640', N'Fleur De Lys Resort & Spa Long Hai', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);


    -- ===================================================================
    -- 3. XỬ LÝ PHÒNG 1: Deluxe Room
    -- ===================================================================
    PRINT 'Đang xử lý Phòng Deluxe Room...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Twin Garden Bungalow', N'1 Giường King', 2, 1, 20, 43, GETDATE(), GETDATE());
    SET @room1_id = SCOPE_IDENTITY();

    -- Hình ảnh cho phòng 1
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10026141-5f529e4ce69e50b9b810aa3a9b78c038.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Deluxe Room', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room1_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10026141-49c090734a313089feaa0ad9b2b4546c.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Deluxe Room', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room1_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10026141-289c5bf16a789f5596d29c237138ed85.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Deluxe Room', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room1_id, @img_id);

    -- Tiện nghi cho phòng 1
    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room1_id, 1), (@room1_id, 3), (@room1_id, 5), (@room1_id, 8);
    -- Biến thể cho phòng 1
    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES (@room1_id, N'Garden Bungalow Twin - Best Deal', 0, 1, 1, 2088462.00, N'available', 170000.00, GETDATE(), GETDATE()),
           (@room1_id, N'Garden Bungalow Twin - Best Deal', 1, 1, 1, 2529846.00, N'available', 210000.00, GETDATE(), GETDATE()),
           (@room1_id, N'Garden Bungalow Twin - Best Deal', 1, 0, 0, 2300000.00, N'available', 190000.00, GETDATE(), GETDATE());

    -- ===================================================================
    -- 4. XỬ LÝ PHÒNG 2: Premium Deluxe
    -- ===================================================================
    PRINT 'Đang xử lý Phòng Premium Deluxe...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Pavilion Bungalow Double', N'1 Giường King', 2, 2, 15, 61, GETDATE(), GETDATE());
    SET @room2_id = SCOPE_IDENTITY();

    -- Hình ảnh cho phòng 2
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10026141-15138ac426179d3d2210c3ba4329d5f8.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Premium Deluxe', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10026141-7da6247aeadd4ab470387e2262af4fb3.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Premium Deluxe', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10026141-abcf473a0ada96a4cf4b9dd70b9295ba.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Premium Deluxe', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);
    
    -- Tiện nghi cho phòng 2
    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room2_id, 1), (@room2_id, 3), (@room2_id, 5), (@room2_id, 8), (@room2_id, 6);
    -- Biến thể cho phòng 2
    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES (@room2_id, N'Pavilion Bungalow Double - Best Deal', 0, 1, 1, 2697231.00, N'available', 220000.00, GETDATE(), GETDATE()),
           (@room2_id, N'Pavilion Bungalow Double - Best Deal', 1, 1, 1, 3138615.00, N'available', 260000.00, GETDATE(), GETDATE());

    -- ===================================================================
    -- 5. XỬ LÝ PHÒNG 3: Sky Villa 2-Bedroom with Private Pool
    -- ===================================================================
    PRINT 'Đang xử lý Sky Villa 2-Bedroom...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Pavilion Bungalow Twin', N'1 giường King và 2 giường đơn', 4, 2, 10, 203, GETDATE(), GETDATE());
    SET @room3_id = SCOPE_IDENTITY();

    -- Hình ảnh cho phòng 3
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10026141-8c7054fedadda73edc11477bc51bc706.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Sky Villa 2-Bedroom', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room3_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10026141-db1ca7a60ea516769f647ae5228adfa8.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Sky Villa 2-Bedroom', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room3_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10026141-81bb348e05d1664081d4fa8cb174f583.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Sky Villa 2-Bedroom', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room3_id, @img_id);

    -- Tiện nghi cho phòng 3
    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room3_id, 1), (@room3_id, 3), (@room3_id, 5), (@room3_id, 8), (@room3_id, 17), (@room3_id, 26);
    -- Biến thể cho phòng 3
    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES (@room3_id, N'Pavilion Bungalow Twin - Best Deal', 0, 1, 1, 6210000.00, N'available', 520000.00, GETDATE(), GETDATE()),
           (@room3_id, N'Pavilion Bungalow Twin - Best Deal', 1, 1, 1, 7470000.00, N'available', 630000.00, GETDATE(), GETDATE());

    -- ===================================================================
    -- 6. XỬ LÝ PHÒNG 4: Beachfront Villa 2-Bedroom with Private Pool
    -- ===================================================================
    PRINT 'Đang xử lý Beachfront Villa 2-Bedroom...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Bungalow Double Terrace', N'1 giường King và 2 giường đơn', 4, 2, 8, 203, GETDATE(), GETDATE());
    SET @room4_id = SCOPE_IDENTITY();
    
    -- Hình ảnh cho phòng 4
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10026141-605cac725474b29ce227653e819525c1.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Beachfront Villa 2-Bedroom', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room4_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10026141-304d13092e28446b6540f5168e647e96.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Beachfront Villa 2-Bedroom', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room4_id, @img_id);

    -- Tiện nghi cho phòng 4
    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room4_id, 1),(@room4_id, 3),(@room4_id, 5),(@room4_id, 8),(@room4_id, 17),(@room4_id, 26);
    -- Biến thể cho phòng 4
    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES (@room4_id, N'Terrace Bungalow Double - Best Deal', 0, 1, 1, 7470000.00, N'available', 630000.00, GETDATE(), GETDATE()),
           (@room4_id, N'Terrace Bungalow Double - Best Deal', 1, 1, 1, 8740000.00, N'available', 730000.00, GETDATE(), GETDATE());

    -- ===================================================================
    -- 7. XỬ LÝ PHÒNG 5: Royal Villa 4-Bedroom with Private Pool
    -- ===================================================================
    PRINT 'Đang xử lý Royal Villa 4-Bedroom...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Sanctuary Pool Villa 2 Bedrooms', N'2 giường King và 4 giường đơn', 8, 4, 2, 1000, GETDATE(), GETDATE());
    SET @room5_id = SCOPE_IDENTITY();
    
    -- Hình ảnh cho phòng 5
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10026141-1890x1262-FIT_AND_TRIM-ee88550ee76d221c76672f249b82fedf.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Royal Villa 4-Bedroom', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room5_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10026141-732ecf39e1555ea1e9c44c0e95b2f7ec.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Royal Villa 4-Bedroom', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room5_id, @img_id);
    
    -- Tiện nghi cho phòng 5
    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room5_id, 1),(@room5_id, 3),(@room5_id, 5),(@room5_id, 8),(@room5_id, 17),(@room5_id, 26), (@room5_id, 20);
    -- Biến thể cho phòng 5
    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES (@room5_id, N'Sanctuary Pool Villa 2 Bedrooms - Best Deal', 1, 1, 0, 47100000.00, N'available', 4100000.00, GETDATE(), GETDATE());

    COMMIT TRANSACTION;
    PRINT 'Toàn bộ dữ liệu cho Fleur De Lys Resort & Spa Long Hai đã được chèn thành công!';

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