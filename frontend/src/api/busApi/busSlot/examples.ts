// @ts-nocheck
/**
 * BusSlot API - Ví dụ sử dụng
 * 
 * NOTE: File này chứa examples cũ, có thể có API calls lỗi thời
 * File này chứa các ví dụ cụ thể về cách sử dụng BusSlot API
 * cho các tình huống phổ biến trong ứng dụng.
 */

import { BusSlotAPI } from './api';
import { BusSlotStatus } from './types';
import type { CreateBusSlotInput, SearchBusSlotsInput } from './types';

// ===== VÍ DỤ CHO ADMIN - QUẢN LÝ CHUYẾN ĐI =====

/**
 * Ví dụ 1: Tạo chuyến đi mới
 * Use case: Admin tạo chuyến đi từ Hà Nội đến TP.HCM
 */
export async function createNewBusSlot_Example() {
  const newBusSlot: CreateBusSlotInput = {
    busId: "bus_123",
    routeId: "route_hanoi_hcm",
    slotDate: "2024-07-25",           // Ngày khởi hành
    departureTime: "08:00:00",        // 8:00 AM
    arrivalTime: "20:00:00",          // 8:00 PM (12 tiếng)
    price: 500000,                    // 500,000 VNĐ
    totalSeats: 40                    // 40 ghế
  };

  try {
    const createdSlot = await BusSlotAPI.createBusSlot(newBusSlot);
    console.log('Chuyến đi đã được tạo:', createdSlot);
    return createdSlot;
  } catch (error) {
    console.error('Lỗi tạo chuyến đi:', error);
    throw error;
  }
}

/**
 * Ví dụ 2: Quản lý trạng thái chuyến đi
 * Use case: Admin kích hoạt, hoàn thành, hoặc hủy chuyến đi
 */
export async function manageBusSlotStatus_Example(busSlotId: string) {
  try {
    // Kích hoạt chuyến đi (khi xe bắt đầu chạy)
    const activatedSlot = await BusSlotAPI.activateBusSlot(busSlotId);
    console.log('Chuyến đi đã được kích hoạt:', activatedSlot);

    // Hoàn thành chuyến đi (khi xe đến đích)
    const completedSlot = await BusSlotAPI.completeBusSlot(busSlotId);
    console.log('Chuyến đi đã hoàn thành:', completedSlot);

    return completedSlot;
  } catch (error) {
    console.error('Lỗi quản lý trạng thái:', error);
    throw error;
  }
}

/**
 * Ví dụ 3: Lấy danh sách chuyến đi của một xe bus
 * Use case: Admin xem lịch trình của xe bus cụ thể
 */
export async function getBusSchedule_Example(busId: string) {
  try {
    const busSlots = await BusSlotAPI.getBusSlotsByBusId(busId);
    console.log(`Xe bus ${busId} có ${busSlots.length} chuyến đi:`);
    
    busSlots.forEach(slot => {
      console.log(`- ${slot.slotDate} | ${slot.departureTime} → ${slot.arrivalTime} | ${slot.status}`);
    });
    
    return busSlots;
  } catch (error) {
    console.error('Lỗi lấy lịch trình xe:', error);
    throw error;
  }
}

// ===== VÍ DỤ CHO NGƯỜI DÙNG CUỐI - TÌM KIẾM VÀ ĐẶT VÉ =====

/**
 * Ví dụ 4: Tìm kiếm chuyến đi cơ bản
 * Use case: Khách hàng tìm chuyến từ Hà Nội đến TP.HCM vào ngày cụ thể
 */
export async function searchBasicBusTrips_Example() {
  const searchParams: SearchBusSlotsInput = {
    departureLocationId: "hanoi_city",     // Mã địa điểm Hà Nội
    arrivalLocationId: "hcm_city",         // Mã địa điểm TP.HCM
    slotDate: "2024-07-25",                // Ngày khởi hành
    status: BusSlotStatus.SCHEDULED        // Chỉ lấy chuyến đã lên lịch
  };

  try {
    const availableTrips = await BusSlotAPI.searchBusSlots(searchParams);
    console.log(`Tìm thấy ${availableTrips.length} chuyến đi:`);
    
    availableTrips.forEach(trip => {
      console.log(`
        🚌 ${trip.bus.name} (${trip.bus.licensePlate})
        📍 ${trip.route.origin} → ${trip.route.destination}
        🕐 ${trip.departureTime} → ${trip.arrivalTime}
        💰 ${trip.price.toLocaleString('vi-VN')} VNĐ
        🪑 ${trip.availableSeats}/${trip.totalSeats} ghế trống
      `);
    });
    
    return availableTrips;
  } catch (error) {
    console.error('Lỗi tìm kiếm chuyến đi:', error);
    throw error;
  }
}

/**
 * Ví dụ 5: Tìm kiếm chuyến đi với bộ lọc nâng cao
 * Use case: Khách hàng tìm chuyến xe limousine, giá dưới 800k, ít nhất 5 ghế trống
 */
export async function searchAdvancedBusTrips_Example() {
  const advancedSearchParams: SearchBusSlotsInput = {
    departureLocationId: "hanoi_city",
    arrivalLocationId: "hcm_city", 
    slotDate: "2024-07-25",
    busCategoryId: "limousine_category",   // Chỉ xe limousine
    maxPrice: 800000,                      // Giá tối đa 800k
    minAvailableSeats: 5,                  // Ít nhất 5 ghế trống
    status: BusSlotStatus.SCHEDULED
  };

  try {
    const premiumTrips = await BusSlotAPI.searchBusSlots(advancedSearchParams);
    console.log(`Tìm thấy ${premiumTrips.length} chuyến xe limousine phù hợp:`);
    
    premiumTrips.forEach(trip => {
      console.log(`
        ✨ ${trip.bus.name} - ${trip.bus.category?.name || 'N/A'}
        💰 ${trip.price.toLocaleString('vi-VN')} VNĐ
        🪑 ${trip.availableSeats} ghế trống
        ⭐ Chất lượng cao
      `);
    });
    
    return premiumTrips;
  } catch (error) {
    console.error('Lỗi tìm kiếm nâng cao:', error);
    throw error;
  }
}

/**
 * Ví dụ 6: Kiểm tra ghế trống trước khi đặt vé
 * Use case: Kiểm tra availability trước khi cho phép khách hàng đặt vé
 */
export async function checkSeatAvailability_Example(busSlotId: string, requiredSeats: number = 1) {
  try {
    const availableSeats = await BusSlotAPI.checkSeatAvailability(busSlotId);
    
    if (availableSeats >= requiredSeats) {
      console.log(`✅ Đủ ghế! Còn ${availableSeats} ghế trống, yêu cầu ${requiredSeats} ghế`);
      return true;
    } else {
      console.log(`❌ Không đủ ghế! Chỉ còn ${availableSeats} ghế, yêu cầu ${requiredSeats} ghế`);
      return false;
    }
  } catch (error) {
    console.error('Lỗi kiểm tra ghế trống:', error);
    return false;
  }
}

/**
 * Ví dụ 7: Lấy danh sách chuyến đi có sẵn (để hiển thị trên trang chủ)
 * Use case: Hiển thị các chuyến đi hot/phổ biến
 */
export async function getPopularTrips_Example() {
  try {
    const availableSlots = await BusSlotAPI.getAvailableBusSlots();
    
    // Sắp xếp theo số ghế đã đặt (popularity)
    const popularTrips = availableSlots
      .map(slot => ({
        ...slot,
        bookedSeats: slot.totalSeats - slot.availableSeats,
        occupancyRate: ((slot.totalSeats - slot.availableSeats) / slot.totalSeats) * 100
      }))
      .sort((a, b) => b.bookedSeats - a.bookedSeats)
      .slice(0, 5); // Top 5 chuyến phổ biến

    console.log('Top 5 chuyến đi phổ biến:');
    popularTrips.forEach((trip, index) => {
      console.log(`
        ${index + 1}. ${trip.route.origin} → ${trip.route.destination}
        📅 ${trip.slotDate} ${trip.departureTime}
        🔥 ${trip.occupancyRate.toFixed(1)}% đã đặt
        💰 ${trip.price.toLocaleString('vi-VN')} VNĐ
      `);
    });
    
    return popularTrips;
  } catch (error) {
    console.error('Lỗi lấy chuyến đi phổ biến:', error);
    throw error;
  }
}

// ===== VÍ DỤ TÍCH HỢP VỚI COMPONENT =====

/**
 * Ví dụ 8: Hook tùy chỉnh cho Vue Component
 * Use case: Sử dụng trong Vue component để tìm kiếm chuyến đi
 */
export function useBusSlotSearch() {
  const searchBusSlots = async (searchParams: SearchBusSlotsInput) => {
    try {
      const results = await BusSlotAPI.searchBusSlots(searchParams);
      return {
        success: true,
        data: results,
        count: results.length
      };
    } catch (error) {
      return {
        success: false,
        error: error instanceof Error ? error.message : 'Unknown error',
        data: [],
        count: 0
      };
    }
  };

  const checkAvailability = async (busSlotId: string) => {
    try {
      const seats = await BusSlotAPI.checkSeatAvailability(busSlotId);
      return {
        available: seats > 0,
        count: seats
      };
    } catch (error) {
      return {
        available: false,
        count: 0
      };
    }
  };

  return {
    searchBusSlots,
    checkAvailability
  };
}

/**
 * Ví dụ 9: Utility functions cho formatting
 */
export const BusSlotUtils = {
  /**
   * Format giá vé
   */
  formatPrice: (price: number): string => {
    return `${price.toLocaleString('vi-VN')} VNĐ`;
  },

  /**
   * Format thời gian
   */
  formatDateTime: (datetime: string): string => {
    return new Date(datetime).toLocaleString('vi-VN');
  },

  /**
   * Format trạng thái
   */
  formatStatus: (status: BusSlotStatus): string => {
    const statusMap = {
      [BusSlotStatus.SCHEDULED]: '📅 Đã lên lịch',
      [BusSlotStatus.ACTIVE]: '🚌 Đang chạy',
      [BusSlotStatus.COMPLETED]: '✅ Hoàn thành',
      [BusSlotStatus.CANCELLED]: '❌ Đã hủy',
      [BusSlotStatus.DELAYED]: '⏰ Bị trì hoãn'
    };
    return statusMap[status] || status;
  },

  /**
   * Tính độ phổ biến của chuyến đi
   */
  calculatePopularity: (totalSeats: number, availableSeats: number): number => {
    return Math.round(((totalSeats - availableSeats) / totalSeats) * 100);
  }
}; 