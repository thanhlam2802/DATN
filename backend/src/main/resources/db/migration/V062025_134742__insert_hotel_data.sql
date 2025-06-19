-- Migration file created on 19/06/2025 at 13:47:42.04 

INSERT INTO hotels (name, address, description, star_rating, province_id, email, phone, owner_id, created_at, updated_at)
VALUES
(N'Cozrum Homes The Gallery Residence', N'49/26B Nguyễn Văn Đậu, Phường 6, Quận Bình Thạnh, Thành phố Hồ Chí Minh', N'Cozrum Homes The Gallery Residence là lựa chọn sáng giá dành cho những ai đang tìm kiếm một trải nghiệm xa hoa đầy thú vị trong kỳ nghỉ của mình. Lưu trú tại đây cũng là cách để quý khách chiều chuộng bản thân với những dịch vụ xuất sắc nhất và khiến kỳ nghỉ của mình trở nên thật đáng nhớ. Cozrum Homes The Gallery Residence là nơi nghỉ sở hữu đầy đủ tiện nghi và dịch vụ xuất sắc theo nhận định của hầu hết khách lưu trú. Cozrum Homes The Gallery Residence là lựa chọn lý tưởng cho những ai đang tìm kiếm một phòng nghỉ thoải mái với giá thành hợp lý.', 3, 3, 'hotel1@gmail.com', '0898555889', null, GETDATE(), GETDATE());

INSERT INTO images (url, alt_text, uploaded_at) VALUES
('https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/67796006-7cc559d642830aa6d8778076fdea8546.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-332,pr-true,q-80,w-480', 'hotel 1', GETDATE()),
('https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/67796006-ecc11211f961b14be2dad35d06d8ccaa.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', 'hotel 1', GETDATE()),
('https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20042892-400184f5496cce80c69000cc04e88d5c.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', 'hotel 1', GETDATE()),
('https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/67796006-72e62eee321524fd054542bc8f3bad06.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', 'hotel 1', GETDATE());

