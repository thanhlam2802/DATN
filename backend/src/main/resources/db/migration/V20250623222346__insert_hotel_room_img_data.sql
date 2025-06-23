USE [DATN];
GO

BEGIN TRANSACTION;
BEGIN TRY
    PRINT 'Đang lấy ID của khách sạn và các phòng...';
    DECLARE @hotel_id INT;
    DECLARE @room1_id INT, @room2_id INT, @room3_id INT;
    
    DECLARE @hotel_name NVARCHAR(200) = N'Ohana Hotel';
    
    SELECT @hotel_id = id FROM [dbo].[hotels] WHERE name = @hotel_name;

    IF @hotel_id IS NULL
    BEGIN
        RAISERROR (N'Không tìm thấy khách sạn với tên: %s. Hủy bỏ script.', 16, 1, @hotel_name);
        ROLLBACK TRANSACTION;
        RETURN;
    END

    SELECT @room1_id = id FROM [dbo].[hotel_rooms] WHERE hotel_id = @hotel_id AND room_type = N'Phòng Tiêu Chuẩn Giường Đôi';
    SELECT @room2_id = id FROM [dbo].[hotel_rooms] WHERE hotel_id = @hotel_id AND room_type = N'Phòng Superior Giường Đôi';
    SELECT @room3_id = id FROM [dbo].[hotel_rooms] WHERE hotel_id = @hotel_id AND room_type = N'Phòng Deluxe Giường Đôi';

    IF @room1_id IS NULL OR @room2_id IS NULL OR @room3_id IS NULL
    BEGIN
        RAISERROR (N'Không tìm thấy một hoặc nhiều loại phòng của khách sạn Ohana. Hủy bỏ script.', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END

    DECLARE @img_id INT;

    PRINT 'Đang chèn 7 hình ảnh chung cho Ohana Hotel...';
    DECLARE @h_img1 INT, @h_img2 INT, @h_img3 INT, @h_img4 INT, @h_img5 INT, @h_img6 INT, @h_img7 INT;

    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20030679-7804cfa7b562d78079f0494bb81f730f.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-332,pr-true,q-80,w-480', N'Ohana Hotel - Sảnh', GETDATE()); SET @h_img1 = SCOPE_IDENTITY();
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20030679-ec6f24c940e2cf5e0124e6c4e15af3e9.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Ohana Hotel - Lối vào', GETDATE()); SET @h_img2 = SCOPE_IDENTITY();
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20030679-1cdf18fa01cc93ca65709e770f5e4d56.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Ohana Hotel - Phòng tắm', GETDATE()); SET @h_img3 = SCOPE_IDENTITY();
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20030679-598f8ee6cc2ccc32f11fb3871cf69737.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Ohana Hotel - Giường ngủ', GETDATE()); SET @h_img4 = SCOPE_IDENTITY();
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20030679-be4985c5386998e3a9c263e22068dfca.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Ohana Hotel - Ban công', GETDATE()); SET @h_img5 = SCOPE_IDENTITY();
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20030679-2d8ff3c5847d7fa70c453b2fb6c4e3b2.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Ohana Hotel - Góc làm việc', GETDATE()); SET @h_img6 = SCOPE_IDENTITY();
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20030679-f5f7238b2742b2217ffeecfb95971697.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,h-360,pr-true,q-80,w-640', N'Ohana Hotel - Toàn cảnh', GETDATE()); SET @h_img7 = SCOPE_IDENTITY();

    INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @h_img1), (@hotel_id, @h_img2), (@hotel_id, @h_img3), (@hotel_id, @h_img4), (@hotel_id, @h_img5), (@hotel_id, @h_img6), (@hotel_id, @h_img7);

    PRINT 'Đang chèn 3 hình ảnh cho Phòng Tiêu Chuẩn...';
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20030679-985c89bd5a7eb25fad4e20f70b89b518.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Tiêu Chuẩn', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room1_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20030679-fb90d2db4034bfd7bba8ebfc22691063.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Tiêu Chuẩn', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room1_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20030679-2d8ff3c5847d7fa70c453b2fb6c4e3b2.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Tiêu Chuẩn', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room1_id, @img_id);

    PRINT 'Đang chèn 3 hình ảnh cho Phòng Superior...';
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20030679-85ff8a38ef33d8feafaed681b11d3763.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Superior', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20030679-ec6f24c940e2cf5e0124e6c4e15af3e9.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Superior', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20030679-7a52b686edacc20999c21d90a0e1d39e.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Superior', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);

    PRINT 'Đang chèn 3 hình ảnh cho Phòng Deluxe...';
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20030679-47700e3aca74af694e8ef3bc95a51e7e.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Deluxe', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room3_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20030679-e71319676ce8d0237892421e050b4fda.jpeg?_src=imagejet&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Deluxe', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room3_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20030679-92e85dd5e6e11987378104b68883990b.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Deluxe', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room3_id, @img_id);

    COMMIT TRANSACTION;
    PRINT 'Tất cả hình ảnh cho Ohana Hotel đã được chèn và liên kết thành công!';

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