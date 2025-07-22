import { User, Route } from '../types/common.types';

// === Route Types ===

// Input types for Route mutations - Khớp với DTO backend
export interface CreateRouteInput {
  /**
   * Điểm xuất phát
   * @validation NotNull - không được để trống
   * @validation Size max=100 - không được vượt quá 100 ký tự
   */
  origin: string;
  
  /**
   * Điểm đến
   * @validation NotNull - không được để trống  
   * @validation Size max=100 - không được vượt quá 100 ký tự
   */
  destination: string;
  
  /**
   * Khoảng cách (km) - tùy chọn
   */
  distanceKm?: number;
  
  /**
   * Thời gian ước tính (phút) - tùy chọn
   */
  estimatedDurationMinutes?: number;
}

export interface UpdateRouteInput {
  /**
   * ID của route cần cập nhật
   * @validation NotNull - không được để trống
   */
  id: string; // ID type from GraphQL
  
  /**
   * Điểm xuất phát (có thể null nếu không muốn cập nhật)
   * @validation Size max=100 - không được vượt quá 100 ký tự
   */
  origin?: string;
  
  /**
   * Điểm đến (có thể null nếu không muốn cập nhật)
   * @validation Size max=100 - không được vượt quá 100 ký tự
   */
  destination?: string;
  
  /**
   * Khoảng cách (km) - tùy chọn
   */
  distanceKm?: number; // Float
  
  /**
   * Thời gian ước tính (phút) - tùy chọn
   */
  estimatedDurationMinutes?: number; // Int
}

// DeleteRouteInput không cần thiết vì schema chỉ dùng deleteRoute(id: ID!)

// === Re-export Common Types ===
export type { Route, User }; 