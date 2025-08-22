-- Thêm các field mới cho tính năng duyệt khách sạn và trạng thái
ALTER TABLE hotels ADD approval_status VARCHAR(20) NOT NULL DEFAULT 'PENDING';
ALTER TABLE hotels ADD approval_reason TEXT NULL;
ALTER TABLE hotels ADD status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
ALTER TABLE hotels ADD approved_at DATETIME NULL;
ALTER TABLE hotels ADD approved_by VARCHAR(100) NULL;

-- Tạo index để tối ưu query
CREATE INDEX idx_hotels_approval_status ON hotels(approval_status);
CREATE INDEX idx_hotels_status ON hotels(status);
CREATE INDEX idx_hotels_approval_status_status ON hotels(approval_status, status);

-- Cập nhật dữ liệu hiện có
UPDATE hotels SET approval_status = 'APPROVED' WHERE approval_status IS NULL;
UPDATE hotels SET status = 'ACTIVE' WHERE status IS NULL;
