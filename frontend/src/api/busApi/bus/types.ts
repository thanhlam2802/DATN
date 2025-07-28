import { User, BusCategory, Bus } from '../types/common.types';

// === BusAmenity Types ===
export interface BusAmenity {
  id: string;
  name: string;
  description?: string;
  createdAt?: string;
  updatedAt?: string;
}

// === BusCategory Types ===

// Input types for BusCategory mutations - Khớp với DTO backend
export interface CreateBusCategoryInput {
  /**
   * Tên của danh mục xe bus
   * @validation NotBlank - không được để trống
   * @validation Size max=100 - không được vượt quá 100 ký tự
   */
  name: string;
}

export interface UpdateBusCategoryInput {
  /**
   * Tên mới của danh mục (có thể null nếu không muốn cập nhật tên)
   * @validation Size max=100 - không được vượt quá 100 ký tự
   */
  name?: string;
}

// === Bus Types ===

// Input types for Bus mutations - Khớp với Bus.graphqls
export interface CreateBusInput {
  name: string;
  licensePlate: string;
  totalSeats: number;
  categoryId: string;
  ownerId: number; // Changed to number
  imageIds?: string[]; // Optional, as images can be added later
  amenityNames?: string[]; // Thêm field amenityNames
}

export interface UpdateBusInput {
  name?: string;
  licensePlate?: string;
  totalSeats?: number;
  categoryId?: string;
  ownerId?: number; // Changed to number
  imageIds?: string[];
  imagesToDelete?: string[]; // IDs of images to delete
  amenityNames?: string[]; // Thêm field amenityNames
}

// === Re-export Common Types ===
export type { BusCategory, Bus, User }; 