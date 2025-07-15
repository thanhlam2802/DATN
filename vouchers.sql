INSERT INTO [datn].[dbo].[vouchers]
(code, description, condition_min_amount, discount_amount, expiry_date, created_at)
VALUES
    ('WELCOME100', 'Giảm 100K cho đơn đầu tiên', 500000, 100000, '2025-12-31 23:59:59', '2025-07-13 10:00:00'),
    ('SUMMER50', 'Ưu đãi hè: giảm 50K', 300000, 50000, '2025-08-31 23:59:59', '2025-06-01 09:00:00'),
    ('FREESHIP', 'Miễn phí vận chuyển toàn quốc', 0, 30000, '2025-12-31 23:59:59', '2025-07-01 12:30:00'),
    ('VIPCUSTOMER', 'Giảm 10% cho khách VIP', 1000000, 100000, '2026-01-01 00:00:00', '2025-07-10 15:15:00'),
    ('BLACKFRIDAY', 'Siêu sale Black Friday', 500000, 200000, '2025-11-30 23:59:59', '2025-07-13 16:00:00');
