-- Migration file created on 18/06/2025 at 15:33:59.80 
ALTER TABLE hotels
ADD CONSTRAINT FK_Hotels_Province
FOREIGN KEY (province_id) REFERENCES provinces(id);