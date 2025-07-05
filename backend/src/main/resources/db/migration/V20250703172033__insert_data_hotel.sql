-- Migration file created at 20250703172033 

USE [DATN];
GO

BEGIN TRANSACTION;

BEGIN TRY
    -- ===================================================================
    -- KHAI BÁO BIẾN
    -- ===================================================================
    DECLARE @hotel_id INT;
    DECLARE @room1_id INT, @room2_id INT, @room3_id INT, @room4_id INT;
    DECLARE @img_id INT;

    -- ===================================================================
    -- 1. INSERT THÔNG TIN KHÁCH SẠN
    -- ===================================================================
    PRINT 'Đang chèn thông tin Shining Riverside Hoi An Boutique and Spa...';
    INSERT INTO [dbo].[hotels] 
        ([name], [address], [description], [star_rating], [province_id], [email], [phone], [created_at], [updated_at])
    VALUES 
        (
            N'Shining Riverside Hoi An Boutique and Spa', 
            N'Tổ 1, Khối Xuyên Trung, Phường Cẩm Nam, Hội An, Tỉnh Quảng Nam, Việt Nam', 
            N'Dù quý khách muốn tổ chức một sự kiện hay các dịp kỷ niệm đặc biệt khác, Shining Riverside Hoi An Boutique and Spa là lựa chọn tuyệt vời cho quý khách với phòng chức năng rộng lớn, được trang bị đầy đủ để sẵn sàng đáp ứng mọi yêu cầu. Khách sạn này là lựa chọn hoàn hảo cho các kỳ nghỉ mát lãng mạn hay tuần trăng mật của các cặp đôi. Quý khách hãy tận hưởng những đêm đáng nhớ nhất cùng người thương của mình tại Shining Riverside Hoi An Boutique and Spa Shining Riverside Hoi An Boutique and Spa là lựa chọn sáng giá dành cho những ai đang tìm kiếm một trải nghiệm xa hoa đầy thú vị trong kỳ nghỉ của mình. Lưu trú tại đây cũng là cách để quý khách chiều chuộng bản thân với những dịch vụ xuất sắc nhất và khiến kỳ nghỉ của mình trở nên thật đáng nhớ. Một trong những đặc điểm chính của khách sạn này là các liệu pháp spa đa dạng. Hãy nâng niu bản thân bằng các liệu pháp thư giãn, phục hồi giúp quý khách tươi trẻ thân, tâm. Khi lưu trú tại khách sạn thì nội thất và kiến trúc hẳn là hai yếu tố quan trọng khiến quý khách mãn nhãn. Với thiết kế độc đáo, Shining Riverside Hoi An Boutique and Spa mang đến không gian lưu trú làm hài lòng quý khách. Hãy tận hưởng thời gian vui vẻ cùng cả gia đình với hàng loạt tiện nghi giải trí tại Shining Riverside Hoi An Boutique and Spa, một khách sạn tuyệt vời phù hợp cho mọi kỳ nghỉ bên người thân. Nếu dự định có một kỳ nghỉ dài, thì Shining Riverside Hoi An Boutique and Spa chính là lựa chọn dành cho quý khách. Với đầy đủ tiện nghi với chất lượng dịch vụ tuyệt vời, Shining Riverside Hoi An Boutique and Spa sẽ khiến quý khách cảm thấy thoải mái như đang ở nhà vậy. Khách sạn này là nơi tốt nhất dành cho những ai mong muốn một nơi thanh bình, thư thái để ẩn mình khỏi đám đông ồn ã, xô bồ. Hãy sẵn sàng đón nhận trải nghiệm khó quên bằng dịch vụ độc đáo và hoàn hảo của khách sạn cùng các tiện nghi đầy đủ, đáp ứng mọi nhu cầu của quý khách.', 
            4, 
            7, 
            N'info@shiningriverside.com', 
            N'02353939888', 
            GETDATE(), 
            GETDATE()
        );
    SET @hotel_id = SCOPE_IDENTITY();

    -- ===================================================================
    -- 2. CHỖ TRỐNG ĐỂ INSERT HÌNH ẢNH CHUNG CỦA KHÁCH SẠN (7 HÌNH)
    -- ===================================================================
    PRINT 'Đang tạo sẵn chỗ chèn 7 hình ảnh chung cho Shining Riverside...';
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20048736-52eed53f8cf01420da846690e0c4768e.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-332,pr-true,q-80,w-480', N'Shining Riverside Hoi An', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20048736-0789e38b7114fa9a0063927b19bb82a3.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Shining Riverside Hoi An', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20048736-443aab284e68107c6c06c66e104fd347.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Shining Riverside Hoi An', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/20048736-f1fe9eb47f2558fd9eec6ec1d203c471.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Shining Riverside Hoi An', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20048736-55dabf4e34764588f55b525f53b54149.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Shining Riverside Hoi An', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20048736-b35a376a163af64b225d707398f69bcf.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Shining Riverside Hoi An', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/TzEv3ZUmG4-4Dz22hvmO9NUDzw1DGCIdWl4oPtKumOg=/lodging/49000000/49000000/48998600/48998502/4b89cfd8_z.jpg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,h-360,pr-true,q-40,w-640', N'Shining Riverside Hoi An', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);


    -- ===================================================================
    -- 3. XỬ LÝ PHÒNG 1: Deluxe Double or Twin with Balcony
    -- ===================================================================
    PRINT 'Đang xử lý Phòng Deluxe Double or Twin with Balcony...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Deluxe Double or Twin with Balcony', N'1 giường đôi hoặc 2 giường đơn', 2, 1, 20, 32, GETDATE(), GETDATE());
    SET @room1_id = SCOPE_IDENTITY();

    -- Hình ảnh cho phòng 1 (3 hình)
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20048736-42404dec8e10d09bd36cbee27fe3d345.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Deluxe Double or Twin with Balcony', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room1_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20048736-0a3937fbf36684472cf24d908be3e3f5.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Deluxe Double or Twin with Balcony', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room1_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20048736-563405b1d93570ded759a8c520b3372f.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Deluxe Double or Twin with Balcony', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room1_id, @img_id);

    -- Tiện nghi cho phòng 1
    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room1_id, 1), (@room1_id, 3), (@room1_id, 5), (@room1_id, 8);
    -- Biến thể cho phòng 1
    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES (@room1_id, N'Deluxe có ban công ', 1, 0, 0, 804043.00, N'available', 68000.00, GETDATE(), GETDATE()),
           (@room1_id, N'Deluxe có ban công', 1, 1, 1, 950000.00, N'available', 80000.00, GETDATE(), GETDATE());

    -- ===================================================================
    -- 4. XỬ LÝ PHÒNG 2: Grand Deluxe River View
    -- ===================================================================
    PRINT 'Đang xử lý Grand Deluxe River View...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Grand Deluxe River View', N'1 giường đôi hoặc 2 giường đơn', 2, 2, 15, 38, GETDATE(), GETDATE());
    SET @room2_id = SCOPE_IDENTITY();

    -- Hình ảnh cho phòng 2 (4 hình)
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/2bHVGnnWooIqvSBA6ae4odylBix7UChbwSnyQroeq5E=/tdcoverseahoteldark/04db7fbee320e2b11ad1aa352cc2984167091.jpg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Grand Deluxe River View', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/2bHVGnnWooIqvSBA6ae4odylBix7UChbwSnyQroeq5E=/tdcoverseahoteldark/265b0b9035ba529a2a2a715b16ba5067412521.jpg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Grand Deluxe River View', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/2bHVGnnWooIqvSBA6ae4odylBix7UChbwSnyQroeq5E=/tdcoverseahoteldark/2c7a7e9c2f2bbd5b1c70273bccf47487407076.jpg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Grand Deluxe River View', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/2bHVGnnWooIqvSBA6ae4odylBix7UChbwSnyQroeq5E=/tdcoverseahoteldark/370ff1b4171fd5a50e5c55002eb5fc6f469041.jpg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Grand Deluxe River View', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);
    
    -- Tiện nghi cho phòng 2
    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room2_id, 1), (@room2_id, 3), (@room2_id, 5), (@room2_id, 8), (@room2_id, 6);
    -- Biến thể cho phòng 2
    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES (@room2_id, N'Grand Deluxe View Sông', 1, 0, 0, 1150000.00, N'available', 97000.00, GETDATE(), GETDATE()),
           (@room2_id, N'Grand Deluxe View Sông', 1, 1, 1, 1320000.00, N'available', 112000.00, GETDATE(), GETDATE());

    -- ===================================================================
    -- 5. XỬ LÝ PHÒNG 3: Shining Suite River View
    -- ===================================================================
    PRINT 'Đang xử lý Shining Suite River View...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Shining Suite River View', N'1 Giường đôi lớn', 2, 2, 8, 45, GETDATE(), GETDATE());
    SET @room3_id = SCOPE_IDENTITY();

    -- Hình ảnh cho phòng 3 (3 hình)
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20048736-42404dec8e10d09bd36cbee27fe3d345.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Shining Suite River View', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room3_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20048736-03999a12dbbef97747e32cce402894ba.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Shining Suite River View', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room3_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20048736-04b9fe4f983517a3b63983a5d061af43.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Shining Suite River View', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room3_id, @img_id);

    -- Tiện nghi cho phòng 3
    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room3_id, 1), (@room3_id, 3), (@room3_id, 5), (@room3_id, 8), (@room3_id, 12), (@room3_id, 19);
    -- Biến thể cho phòng 3
    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES (@room3_id, N'Suite View Sông', 1, 0, 0, 1530000.00, N'available', 129000.00, GETDATE(), GETDATE()),
           (@room3_id, N'Suite View Sông', 1, 1, 1, 1750000.00, N'available', 148000.00, GETDATE(), GETDATE());

    -- ===================================================================
    -- 6. XỬ LÝ PHÒNG 4: Family Suite with Balcony
    -- ===================================================================
    PRINT 'Đang xử lý Family Suite with Balcony...';
    INSERT INTO [dbo].[hotel_rooms] ([hotel_id], [room_type], [bed_type], [max_adults], [max_children], [room_quantity], [room_area], [created_at], [updated_at])
    VALUES (@hotel_id, N'Family Suite with Balcony', N'1 giường đơn và 1 giường đôi', 3, 2, 6, 55, GETDATE(), GETDATE());
    SET @room4_id = SCOPE_IDENTITY();

    -- Hình ảnh cho phòng 4 (4 hình)
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20048736-6e6c452fcfb948ced96a828346dc9483.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Family Suite with Balcony', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room4_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20048736-534a4c4cde1b9b92c3685def564c0d01.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Family Suite with Balcony', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room4_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20048736-42404dec8e10d09bd36cbee27fe3d345.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Family Suite with Balcony', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room4_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20048736-70e5c2462ce75d880a53bc142e69a956.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Family Suite with Balcony', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room4_id, @img_id);

    -- Tiện nghi cho phòng 4
    INSERT INTO [dbo].[hotel_room_amenities] ([room_id], [amenity_id]) VALUES (@room4_id, 1),(@room4_id, 3),(@room4_id, 5),(@room4_id, 8),(@room4_id, 10),(@room4_id, 23);
    -- Biến thể cho phòng 4
    INSERT INTO [dbo].[hotel_room_variants] ([room_id], [variant_name], [has_breakfast], [cancellable], [pay_at_hotel], [price], [status], [tax_and_fee_amount], [created_at], [updated_at])
    VALUES (@room4_id, N'Family Suite', 1, 0, 0, 1880000.00, N'available', 159000.00, GETDATE(), GETDATE()),
           (@room4_id, N'Family Suite', 1, 1, 1, 2150000.00, N'available', 182000.00, GETDATE(), GETDATE());

    COMMIT TRANSACTION;
    PRINT 'Toàn bộ dữ liệu cho Shining Riverside Hoi An đã được chèn thành công!';

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