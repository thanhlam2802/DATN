-- Migration file created at 20250623213906 

USE [DATN];
GO

BEGIN TRANSACTION;

BEGIN TRY
    DECLARE @hotel_id INT;
    DECLARE @img1_id INT, @img2_id INT, @img3_id INT, @img4_id INT, @img5_id INT, @img6_id INT, @img7_id INT;
    DECLARE @hotel_name NVARCHAR(200) = N'Angsana Quan Lạn Hạ Long Bay';
    DECLARE @alt_text_base NVARCHAR(255) = N'hotel 3';

    SELECT @hotel_id = id FROM [dbo].[hotels] WHERE name = @hotel_name;

    IF @hotel_id IS NULL
    BEGIN
        RAISERROR (N'Không tìm thấy khách sạn với tên: %s. Hủy bỏ script.', 16, 1, @hotel_name);
        ROLLBACK TRANSACTION;
        RETURN;
    END


    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/20076474-c89598e5bd46ce64104fd5912b622c0a.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-332,pr-true,q-80,w-480', @alt_text_base, GETDATE());
    SET @img1_id = SCOPE_IDENTITY();

    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/20076474-14d298721e7d921f7b72aefc91aea35b.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', @alt_text_base, GETDATE());
    SET @img2_id = SCOPE_IDENTITY();

    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/20076474-0019b82aabf461a70913d38ffe9c5efb.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', @alt_text_base, GETDATE());
    SET @img3_id = SCOPE_IDENTITY();

    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/20076474-c805351c2a325b12d669aacad773596d.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', @alt_text_base, GETDATE());
    SET @img4_id = SCOPE_IDENTITY();

    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/20076474-9ba52b7f29fbb5374ceafddd93d90dbe.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', @alt_text_base, GETDATE());
    SET @img5_id = SCOPE_IDENTITY();

    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/20076474-b9a5c19855e59ad5093e7364dd44aedc.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', @alt_text_base, GETDATE());
    SET @img6_id = SCOPE_IDENTITY();

    INSERT INTO [dbo].[images] ([url], [alt_text], [uploaded_at]) VALUES (N'https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/20076474-dd75a8674b8a491abaef3e56cae0b1a5.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,h-360,pr-true,q-80,w-640', @alt_text_base, GETDATE());
    SET @img7_id = SCOPE_IDENTITY();


    
    INSERT INTO [dbo].[hotel_images] ([hotel_id], [image_id])
    VALUES
        (@hotel_id, @img1_id),
        (@hotel_id, @img2_id),
        (@hotel_id, @img3_id),
        (@hotel_id, @img4_id),
        (@hotel_id, @img5_id),
        (@hotel_id, @img6_id),
        (@hotel_id, @img7_id);

    COMMIT TRANSACTION;
    PRINT '7 hình ảnh cho khách sạn Angsana Quan Lan đã được chèn và liên kết thành công!';

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