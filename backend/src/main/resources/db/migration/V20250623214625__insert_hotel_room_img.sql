-- Migration file created at 20250623214625 

USE [DATN];
GO

BEGIN TRANSACTION;

BEGIN TRY
    DECLARE @room1_id INT = 7;
    DECLARE @room2_id INT = 8;
    DECLARE @room3_id INT = 9;

    IF NOT EXISTS (SELECT 1 FROM dbo.hotel_rooms WHERE id IN (@room1_id, @room2_id, @room3_id))
    BEGIN
        RAISERROR (N'Một hoặc nhiều ID phòng (7, 8, 9) không tồn tại trong bảng hotel_rooms. Vui lòng kiểm tra lại.', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END

    DECLARE @img_id INT;

    PRINT 'Chèn hình ảnh cho phòng có ID = 7';

    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/20076474-54428fe0f029e5ad65ebedfa9b508240.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Hình ảnh phòng khách sạn', GETDATE());
    SET @img_id = SCOPE_IDENTITY();
    INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room1_id, @img_id);

    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/20076474-f8ebd19289965140e90ce5e0809da052.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Hình ảnh phòng khách sạn', GETDATE());
    SET @img_id = SCOPE_IDENTITY();
    INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room1_id, @img_id);

    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/20076474-19297bd7e532522e4518554014ce174c.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Hình ảnh phòng khách sạn', GETDATE());
    SET @img_id = SCOPE_IDENTITY();
    INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room1_id, @img_id);


    PRINT 'Chèn hình ảnh cho phòng có ID = 8';
    
    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/20076474-7945f3ab6f3ac4d781074bdba9c0e57b.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Hình ảnh phòng khách sạn', GETDATE());
    SET @img_id = SCOPE_IDENTITY();
    INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);

    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/20076474-bddaba7ba677f0ff76d88b2d521049b8.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Hình ảnh phòng khách sạn', GETDATE());
    SET @img_id = SCOPE_IDENTITY();
    INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);

    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/20076474-9e12de45dfb474aa5e5e3e1e6f6cabb0.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Hình ảnh phòng khách sạn', GETDATE());
    SET @img_id = SCOPE_IDENTITY();
    INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);

    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/20076474-036c290b1525a1bfb6bb67b84f9d6ca4.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Hình ảnh phòng khách sạn', GETDATE());
    SET @img_id = SCOPE_IDENTITY();
    INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room2_id, @img_id);

    PRINT 'Chèn hình ảnh cho phòng có ID = 9';

    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/20076474-03f97f276412ad78972a09819dbf928a.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Hình ảnh phòng khách sạn', GETDATE());
    SET @img_id = SCOPE_IDENTITY();
    INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room3_id, @img_id);

    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/20076474-d6483efbedf46e97513027516f02c4bf.jpeg?_src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Hình ảnh phòng khách sạn', GETDATE());
    SET @img_id = SCOPE_IDENTITY();
    INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room3_id, @img_id);

    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/generic-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/20076474-1de75d75db4b92e6c331146e3ed8d0f3.jpeg?src=imagekit&tr=dpr-2,f-jpg,fo-auto,h-222,pr-true,q-40,w-320', N'Hình ảnh phòng khách sạn', GETDATE());
    SET @img_id = SCOPE_IDENTITY();
    INSERT INTO [dbo].[hotel_room_images] ([room_id], [image_id]) VALUES (@room3_id, @img_id);


    COMMIT TRANSACTION;
    PRINT 'Tất cả hình ảnh cho các phòng đã được chèn và liên kết thành công!';

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