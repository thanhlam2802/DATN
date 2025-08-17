import { User, Route, Location } from '../types/common.types';

// === Location Input Types === (NEW)
export interface CreateLocationInput {
  /**
   * Tên địa điểm
   * @validation NotNull - không được để trống
   */
  name: string;
  
  /**
   * Tỉnh thành
   */
  provinceCity?: string;
  
  /**
   * Quận huyện 
   */
  district?: string;
  
  /**
   * Địa chỉ chi tiết
   */
  addressDetails?: string;
}

// === Route Types ===

// Input types for Route mutations - Khớp với DTO backend
export interface CreateRouteInput {
  /**
   * Thông tin điểm xuất phát
   * @validation NotNull - không được để trống
   */
  originLocationDetails: CreateLocationInput;
  
  /**
   * Thông tin điểm đến
   * @validation NotNull - không được để trống  
   */
  destinationLocationDetails: CreateLocationInput;
  
  /**
   * ID doanh nghiệp sở hữu route
   * @validation NotNull - route phải thuộc về doanh nghiệp
   */
  ownerId: number;
  
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
   * Thông tin điểm xuất phát (có thể null nếu không muốn cập nhật)
   */
  originLocationDetails?: CreateLocationInput;
  
  /**
   * Thông tin điểm đến (có thể null nếu không muốn cập nhật)
   */
  destinationLocationDetails?: CreateLocationInput;
  
  /**
   * Khoảng cách (km) - tùy chọn
   */
  distanceKm?: number;
  
  /**
   * Thời gian ước tính (phút) - tùy chọn
   */
  estimatedDurationMinutes?: number;
}

// DeleteRouteInput không cần thiết vì schema chỉ dùng deleteRoute(id: ID!)

// === Re-export Common Types ===
export type { Route, User, Location }; 