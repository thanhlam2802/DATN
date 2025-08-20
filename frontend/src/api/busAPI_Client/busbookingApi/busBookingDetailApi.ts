// Bus Booking Detail API
import type { ApiResponse } from './types'

export interface BusBookingDetailDto {
  // ✅ BASIC BOOKING INFO
  id: number
  bookingReference: string
  status: string
  numPassengers: number
  totalPrice: number
  bookingDate: string
  
  // ✅ ROUTE INFORMATION
  departureLocation: string
  arrivalLocation: string
  departureDate: string
  departureTime: string
  arrivalTime: string
  tripDuration: string
  
  // ✅ BUS INFORMATION
  busName: string
  busLicensePlate: string
  busCategoryName: string
  
  // ✅ SEAT INFORMATION
  selectedSeats: SeatInfoDto[]
  totalSeats: number
  
  // ✅ CUSTOMER INFORMATION
  customerName: string
  customerPhone: string
  customerEmail: string
}

export interface SeatInfoDto {
  id: number
  seatNumber: string
  seatType: string
  seatStatus: string
}

const BASE_URL = 'http://localhost:8080/api/v1/bus-booking'

export class BusBookingDetailAPI {
  /**
   * ✅ NEW: Lấy thông tin chi tiết vé xe để hiển thị
   */
  static async getBookingDetailForDisplay(bookingId: number): Promise<ApiResponse<BusBookingDetailDto>> {
    try {
      // 🔑 Lấy token từ localStorage (using TOKEN_KEY from TokenService)
      const token = localStorage.getItem('t_') || localStorage.getItem('accessToken') || localStorage.getItem('token')
      
      const response = await fetch(`${BASE_URL}/${bookingId}/detail`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { Authorization: `Bearer ${token}` }),
        },
      })

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}))
        throw new Error(errorData.message || `HTTP error! status: ${response.status}`)
      }

      return await response.json()
    } catch (error) {
      throw new Error(
        error instanceof Error 
          ? error.message 
          : 'Không thể lấy thông tin chi tiết vé xe. Vui lòng thử lại.'
      )
    }
  }
} 