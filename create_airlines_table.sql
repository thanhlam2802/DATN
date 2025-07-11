-- Tạo bảng airlines
CREATE TABLE airlines (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Insert dữ liệu mẫu cho các hãng hàng không Việt Nam
INSERT INTO airlines (name) VALUES 
('Vietnam Airlines'),
('VietJet Air'),
('Bamboo Airways'),
('Pacific Airlines'),
('Vietravel Airlines');

-- Cập nhật bảng flights để thêm cột airline_id
ALTER TABLE flights ADD COLUMN airline_id INT;

-- Thêm foreign key constraint
ALTER TABLE flights ADD CONSTRAINT fk_flights_airline 
FOREIGN KEY (airline_id) REFERENCES airlines(id);

-- Cập nhật dữ liệu hiện có trong bảng flights (nếu có)
-- Giả sử tất cả chuyến bay hiện tại thuộc Vietnam Airlines (id = 1)
UPDATE flights SET airline_id = 1 WHERE airline_id IS NULL;

-- Xóa cột airline cũ (nếu có)
-- ALTER TABLE flights DROP COLUMN airline; 