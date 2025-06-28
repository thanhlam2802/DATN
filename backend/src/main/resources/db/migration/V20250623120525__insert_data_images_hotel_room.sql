-- Migration file created at 20250623120525 

INSERT INTO images(url, alt_text, uploaded_at) VALUES
('https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20042892-6fab544f6dee9bd7e99de36f25e98eee.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', 'hotel 1', GETDATE()),
('https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20042892-767ae233836537e8902dc592e081153e.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', 'hotel 1', GETDATE()),
('https://ik.imagekit.io/tvlk/apr-asset/Ixf4aptF5N2Qdfmh4fGGYhTN274kJXuNMkUAzpL5HuD9jzSxIGG5kZNhhHY-p7nw/hotel/asset/67796006-09eaf44167941ef18e6021e84221bb73.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,h-360,pr-true,q-80,w-640', 'hotel 1', GETDATE()),
('https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20035611-0e6d92effe35829532d1b069d81195f6.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,fo-auto,h-162,pr-true,q-80,w-234.66666666666666', 'hotel 2', GETDATE()),
('https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20035611-16e0da83c9e691ca1d009d2549d348c0.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,h-360,pr-true,q-80,w-640', 'hotel 2', GETDATE()),
('https://ik.imagekit.io/tvlk/apr-asset/dgXfoyh24ryQLRcGq00cIdKHRmotrWLNlvG-TxlcLxGkiDwaUSggleJNPRgIHCX6/hotel/asset/20035611-19fdf6e884abccc8dcccd588c18d906d.jpeg?_src=imagekit&tr=dpr-2,c-at_max,f-jpg,h-360,pr-true,q-80,w-640', 'hotel 2', GETDATE());

GO

INSERT INTO hotel_images(hotel_id, image_id) VALUES
(1,27),
(1,28),
(1,29),
(2,30),
(2,31),
(2,32);

GO
