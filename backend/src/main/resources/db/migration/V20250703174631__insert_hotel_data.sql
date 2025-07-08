-- Migration file created at 20250703174631 

USE [DATN];
GO

BEGIN TRANSACTION;

BEGIN TRY
    -- ===================================================================
    -- KHAI BÁO BIẾN
    -- ===================================================================
    DECLARE @hotel_id INT;
    DECLARE @room1_id INT, @room2_id INT, @room3_id INT, @room4_id INT, @room5_id INT;
    DECLARE @img_id INT;

    -- ===================================================================
    -- 1. INSERT THÔNG TIN KHÁCH SẠN
    -- ===================================================================
    PRINT 'Đang chèn thông tin Seashells Phu Quoc Hotel & Spa...';
    INSERT INTO [dbo].[hotels] 
        ([name], [address], [description], [star_rating], [province_id], [email], [phone], [created_at], [updated_at])
    VALUES 
        (
            N'Seashells Phu Quoc Hotel & Spa', 
            N'Số 1 Đường Võ Thị Sáu, Phường Dương Đông, Phú Quốc, Tỉnh Kiên Giang, Việt Nam', 
            N'Khi ở tại một khách sạn, thiết kế và kiến trúc là hai yếu tố quan trọng có thể làm hài lòng bạn. Với thiết kế độc đáo, Seashells Phu Quoc Hotel & Spa mang đến một chỗ ở dễ chịu cho kỳ nghỉ của bạn. Nếu bạn dự định có một kỳ nghỉ dài hạn, ở tại Seashells Phu Quoc Hotel & Spa là lựa chọn phù hợp cho bạn. Cung cấp nhiều tiện nghi và chất lượng dịch vụ tuyệt vời, chỗ ở này chắc chắn sẽ khiến bạn cảm thấy như ở nhà. Dịch vụ chất lượng cao nhất cùng với các tiện nghi phong phú sẽ giúp bạn có được trải nghiệm kỳ nghỉ tuyệt vời nhất. Trung tâm thể dục của khách sạn là một điều bạn nên thử trong thời gian lưu trú tại đây. Nhận ưu đãi tốt nhất cho dịch vụ spa chất lượng nhất để thư giãn và trẻ hóa bản thân. Quầy lễ tân 24 giờ luôn sẵn sàng phục vụ bạn, từ nhận phòng đến trả phòng hoặc bất kỳ sự hỗ trợ nào bạn cần. Nếu bạn muốn nhiều hơn, đừng ngần ngại hỏi quầy lễ tân, chúng tôi luôn sẵn sàng phục vụ bạn. Thưởng thức các món ăn yêu thích của bạn với các món ăn đặc biệt từ Seashells Phu Quoc Hotel & Spa dành riêng cho bạn.', 
            5, 
            8,
            N'info@seashellshotel.vn', 
            N'02973923999', 
            GETDATE(), 
            GETDATE()
        );
    SET @hotel_id = SCOPE_IDENTITY();

    -- ===================================================================
    -- 2. CHỖ TRỐNG ĐỂ INSERT HÌNH ẢNH CHUNG CỦA KHÁCH SẠN (7 HÌNH)
    -- ===================================================================
    PRINT 'Đang tạo sẵn chỗ chèn 7 hình ảnh chung cho Seashells Phu Quoc...';
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10043411-caca1692d690b83b9c3006d4381e29a0.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-332,pr-true,q-80,w-480', N'Seashells Phu Quoc Hotel & Spa', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/10043411-1c73c5db8b7dd1638f9dfa5102fff505.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Seashells Phu Quoc Hotel & Spa', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/10043411-322e36f804f5ae1e548248996fd80b67.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Seashells Phu Quoc Hotel & Spa', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/10043411-4f1778eba29f58366f564fc97441129d.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Seashells Phu Quoc Hotel & Spa', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/10043411-119a95d263f3bb1c37ebe22650451a07.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Seashells Phu Quoc Hotel & Spa', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/10043411-688d081f89ace782db65f9350fe91b06.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Seashells Phu Quoc Hotel & Spa', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/10043411-c764ab8b266eb6c2adddda47686ad8a7.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,h-360,pr-true,q-80,w-640', N'Seashells Phu Quoc Hotel & Spa', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);


    -- ===================================================================
    -- 3. XỬ LÝ PHÒNG 1: Classic City View
    -- ===================================================================
    PRINT 'Đang xử lý Phòng Classic City View...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Premium Twin City View', N'1 giường đôi lớn hoặc 2 giường đơn', 2, 1, 30, 33, GETDATE(), GETDATE());
    SET @room1_id = SCOPE_IDENTITY();

    -- Hình ảnh cho phòng 1
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/TzEv3ZUmG4-4Dz22hvmO9NUDzw1DGCIdWl4oPtKumOg=/lodging/20000000/19290000/19286800/19286763/64ba3151_z.jpg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Classic City View', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room1_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/TzEv3ZUmG4-4Dz22hvmO9NUDzw1DGCIdWl4oPtKumOg=/lodging/20000000/19290000/19286800/19286763/2e1e82bd_z.jpg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Classic City View', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room1_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/TzEv3ZUmG4-4Dz22hvmO9NUDzw1DGCIdWl4oPtKumOg=/lodging/20000000/19290000/19286800/19286763/3eeb401c_z.jpg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Classic City View', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room1_id, @img_id);

    -- Tiện nghi cho phòng 1
    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room1_id, 1), (@room1_id, 2), (@room1_id, 3), (@room1_id, 5);
    -- Biến thể cho phòng 1
    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES (@room1_id, N'Premium City View Twin Room', 1, 1, 1, 2375000.00, N'available', 200000.00, GETDATE(), GETDATE()),
           (@room1_id, N'Premium City View Twin Room', 1, 0, 0, 2150000.00, N'available', 180000.00, GETDATE(), GETDATE());

    -- ===================================================================
    -- 4. XỬ LÝ PHÒNG 2: Classic Ocean View
    -- ===================================================================
    PRINT 'Đang xử lý Phòng Classic Ocean View...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Premium Double Ocean View', N'1 giường đôi lớn hoặc 2 giường đơn', 2, 1, 25, 33, GETDATE(), GETDATE());
    SET @room2_id = SCOPE_IDENTITY();

    -- Hình ảnh cho phòng 2
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/10043411-987059ca3eed11f66de974f565e02c43.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Classic Ocean View', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/10043411-bc6cb7ce468da42f3fae7d0b4a823311.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Classic Ocean View', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/10043411-2d482b6c6e7c9b1e0298dbc13a1a4b9a.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Classic Ocean View', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);
    
    -- Tiện nghi cho phòng 2
    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room2_id, 1), (@room2_id, 2), (@room2_id, 3), (@room2_id, 5), (@room2_id, 8);
    -- Biến thể cho phòng 2
    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES (@room2_id, N'Premium Ocean View King Room', 1, 1, 1, 2850000.00, N'available', 240000.00, GETDATE(), GETDATE()),
           (@room2_id, N'Premium Ocean View King Room', 1, 0, 0, 2580000.00, N'available', 215000.00, GETDATE(), GETDATE());

    -- ===================================================================
    -- 5. XỬ LÝ PHÒNG 3: Junior Suite Ocean View
    -- ===================================================================
    PRINT 'Đang xử lý Junior Suite Ocean View...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Suite Executive Hướng Biển', N'1 Giường King', 2, 2, 10, 52, GETDATE(), GETDATE());
    SET @room3_id = SCOPE_IDENTITY();

    -- Hình ảnh cho phòng 3
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/TzEv3ZUmG4-4Dz22hvmO9NUDzw1DGCIdWl4oPtKumOg=/lodging/20000000/19290000/19286800/19286763/ffa228f7_z.jpg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Junior Suite Ocean View', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room3_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/TzEv3ZUmG4-4Dz22hvmO9NUDzw1DGCIdWl4oPtKumOg=/lodging/20000000/19290000/19286800/19286763/080b2f7b_z.jpg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Junior Suite Ocean View', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room3_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/TzEv3ZUmG4-4Dz22hvmO9NUDzw1DGCIdWl4oPtKumOg=/lodging/20000000/19290000/19286800/19286763/3538d6a6_z.jpg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Junior Suite Ocean View', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room3_id, @img_id);

    -- Tiện nghi cho phòng 3
    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room3_id, 1), (@room3_id, 3), (@room3_id, 5), (@room3_id, 8), (@room3_id, 12), (@room3_id, 25);
    -- Biến thể cho phòng 3
    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES (@room3_id, N'Executive Suite Ocean View Room', 1, 1, 1, 4180000.00, N'available', 350000.00, GETDATE(), GETDATE()),
           (@room3_id, N'Executive Suite Ocean View Room', 1, 0, 0, 3800000.00, N'available', 320000.00, GETDATE(), GETDATE());

    -- ===================================================================
    -- 6. XỬ LÝ PHÒNG 4: Club Suite Ocean View
    -- ===================================================================
    PRINT 'Đang xử lý Club Suite Ocean View...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Classic Twin City View', N'1 Giường King', 2, 2, 8, 80, GETDATE(), GETDATE());
    SET @room4_id = SCOPE_IDENTITY();
    
    -- Hình ảnh cho phòng 4
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/TzEv3ZUmG4-4Dz22hvmO9NUDzw1DGCIdWl4oPtKumOg=/lodging/20000000/19290000/19286800/19286763/e2b3631c_z.jpg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Club Suite Ocean View', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room4_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/TzEv3ZUmG4-4Dz22hvmO9NUDzw1DGCIdWl4oPtKumOg=/lodging/20000000/19290000/19286800/19286763/1391416b_z.jpg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Club Suite Ocean View', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room4_id, @img_id);

    -- Tiện nghi cho phòng 4
    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room4_id, 1),(@room4_id, 3),(@room4_id, 5),(@room4_id, 8),(@room4_id, 12),(@room4_id, 21);
    -- Biến thể cho phòng 4
    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES (@room4_id, N'Classic Ocean View Twin Room', 1, 1, 1, 5700000.00, N'available', 480000.00, GETDATE(), GETDATE()),
           (@room4_id, N'Classic Ocean View Twin Room', 1, 0, 0, 5200000.00, N'available', 440000.00, GETDATE(), GETDATE());

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