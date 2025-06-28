-- Migration file created at 20250625154028 

USE [DATN];
GO

BEGIN TRANSACTION;

BEGIN TRY
    PRINT 'Đang lấy ID của khách sạn và các phòng...';
    DECLARE @hotel_id INT;
    DECLARE @room1_id INT, @room2_id INT, @room3_id INT, @room4_id INT;
    
    DECLARE @hotel_name NVARCHAR(200) = N'Khách sạn Melia Vinpearl Riverfront Đà Nẵng';
    
    SELECT @hotel_id = id FROM [dbo].[hotels] WHERE name = @hotel_name;

    IF @hotel_id IS NULL
    BEGIN
        RAISERROR (N'Không tìm thấy khách sạn với tên: %s. Hủy bỏ script.', 16, 1, @hotel_name);
        ROLLBACK TRANSACTION;
        RETURN;
    END

    SELECT @room1_id = id FROM [dbo].[hotel_rooms] WHERE hotel_id = @hotel_id AND room_type = N'Phòng Deluxe';
    SELECT @room2_id = id FROM [dbo].[hotel_rooms] WHERE hotel_id = @hotel_id AND room_type = N'Executive Suite River View';
    SELECT @room3_id = id FROM [dbo].[hotel_rooms] WHERE hotel_id = @hotel_id AND room_type = N'Premier Suite';
    SELECT @room4_id = id FROM [dbo].[hotel_rooms] WHERE hotel_id = @hotel_id AND room_type = N'Căn Hộ Hai Phòng Ngủ';

    IF @room1_id IS NULL OR @room2_id IS NULL OR @room3_id IS NULL OR @room4_id IS NULL
    BEGIN
        RAISERROR (N'Không tìm thấy một hoặc nhiều loại phòng của khách sạn Melia Vinpearl. Hủy bỏ script.', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END

    DECLARE @img_id INT;

    PRINT 'Đang chèn 7 hình ảnh chung cho Melia Vinpearl...';
    
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20012109-4c65e4e808a2cf0c2bfee7a8e6e233fe.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-332,pr-true,q-80,w-480', N'Toàn cảnh Melia Vinpearl Đà Nẵng', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20012109-a44997b6c4b5ef491b1926ba8e6a3835.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Hồ bơi Melia Vinpearl Đà Nẵng', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20012109-1b2aaabb09642bd1aa96112d8f17705c.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Phòng chờ Melia Vinpearl Đà Nẵng', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20012109-d81e03176f67646544fefd52d8413007.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Khu vực ăn uống Melia Vinpearl', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20012109-7770fb4d67b838eecfde79310fed87c1.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Phòng họp Melia Vinpearl', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20012109-faeb1bf4368426bb93f14c8241c60b23.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', N'Nhà hàng Melia Vinpearl', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20012109-66a2d29ce7ef7a9d731c2efa60779154.jpeg?_src=imagekit&tr=dpr-2,h-145,q-80,w-145', N'Spa Melia Vinpearl', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id]) VALUES (@hotel_id, @img_id);

    PRINT 'Đang chèn 4 hình ảnh cho Phòng Deluxe...';
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20012109-4a2f4dbcd1a608dcbd060ebd0b2ff368.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Deluxe', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room1_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20012109-61fcefe382fa89c9ab4d43a93af89299.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Deluxe', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room1_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20012109-fa21933e4b333398c6b276eb030f1f18.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Deluxe', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room1_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20012109-e835a504776df3d229ac83120e0cffda.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Phòng Deluxe', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room1_id, @img_id);

    PRINT 'Đang chèn 3 hình ảnh cho Executive Suite...';
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20012109-4a2f4dbcd1a608dcbd060ebd0b2ff368.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Executive Suite', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20012109-4979ac22114d22003c1c0d597fde525a.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Executive Suite', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20012109-61fcefe382fa89c9ab4d43a93af89299.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Executive Suite', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);

    PRINT 'Đang chèn 3 hình ảnh cho Premier Suite...';
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20012109-0013e0d7a30f83202b72360349079ca8.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Premier Suite', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room3_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20012109-d13880d7cc2f67d8ceb0c77d8d76a2b0.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Premier Suite', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room3_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20012109-ff0ae7e47d024ce0f9435d5bd737e6c4.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Premier Suite', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room3_id, @img_id);

    PRINT 'Đang chèn 3 hình ảnh cho Căn Hộ Hai Phòng Ngủ...';
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20012109-ef7098ecef9499589fd624d37b054b7b.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Căn Hộ Hai Phòng Ngủ', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room4_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20012109-0013e0d7a30f83202b72360349079ca8.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Căn Hộ Hai Phòng Ngủ', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room4_id, @img_id);
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20012109-3e53a6b780514fad382820fff27fa48e.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Căn Hộ Hai Phòng Ngủ', GETDATE()); SET @img_id = SCOPE_IDENTITY(); INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room4_id, @img_id);

    COMMIT TRANSACTION;
    PRINT 'Toàn bộ hình ảnh cho Melia Vinpearl Đà Nẵng đã được chèn và liên kết thành công!';

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