// Booking API for Direct Bus Booking
import type { 
  BusBookingRequest, 
  BookingResponse, 
  ApiResponse,
  ValidationResult 
} from './types'

const BASE_URL = '/api/v1/bus-booking'

export class BookingAPI {
  /**
   * Đặt vé xe trực tiếp (không qua giỏ hàng)
   */
  static async createDirectBooking(
    bookingRequest: any
  ): Promise<ApiResponse<BookingResponse>> {
    try {
      // 🔑 Lấy token từ localStorage (using TOKEN_KEY from TokenService)
      const token = localStorage.getItem('t_') || localStorage.getItem('accessToken') || localStorage.getItem('token')
      
      const response = await fetch(`${BASE_URL}/direct`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { Authorization: `Bearer ${token}` }),
        },
        body: JSON.stringify(bookingRequest),
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
          : 'Không thể đặt vé xe. Vui lòng thử lại.'
      )
    }
  }

  /**
   * Lấy chi tiết booking
   */
  static async getBookingDetail(bookingId: number): Promise<ApiResponse<BookingResponse>> {
    try {
      // 🔑 Lấy token từ localStorage (using TOKEN_KEY from TokenService)
      const token = localStorage.getItem('t_') || localStorage.getItem('accessToken') || localStorage.getItem('token')
      
      const response = await fetch(`${BASE_URL}/${bookingId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { Authorization: `Bearer ${token}` }),
        },
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return await response.json()
    } catch (error) {
      throw new Error('Không thể lấy thông tin đặt vé.')
    }
  }

  /**
   * Hủy booking
   */
  static async cancelBooking(bookingId: number): Promise<ApiResponse<any>> {
    try {
      // 🔑 Lấy token từ localStorage (using TOKEN_KEY from TokenService)
      const token = localStorage.getItem('t_') || localStorage.getItem('accessToken') || localStorage.getItem('token')
      
      const response = await fetch(`${BASE_URL}/${bookingId}/cancel`, {
        method: 'POST',
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
          : 'Không thể hủy đặt vé. Vui lòng thử lại.'
      )
    }
  }

  /**
   * Lấy danh sách booking của khách hàng
   */
  static async getCustomerBookings(customerId: number): Promise<ApiResponse<BookingResponse[]>> {
    try {
      // 🔑 Lấy token từ localStorage (using TOKEN_KEY from TokenService)
      const token = localStorage.getItem('t_') || localStorage.getItem('accessToken') || localStorage.getItem('token')
      
      const response = await fetch(`${BASE_URL}/customer/${customerId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { Authorization: `Bearer ${token}` }),
        },
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return await response.json()
    } catch (error) {
      throw new Error('Không thể lấy danh sách đặt vé.')
    }
  }

  /**
   * Validate thông tin direct booking (cho DirectBusReservationRequest)
   */
  static validateDirectBookingRequest(request: any): ValidationResult {
    const errors: string[] = []

    // Validate bus slot ID
    if (!request.busSlotId || request.busSlotId <= 0) {
      errors.push('ID chuyến xe không hợp lệ')
    }

    // Validate selected seats (cho direct booking dùng selectedSeatNumbers)
    if (!request.selectedSeatNumbers || request.selectedSeatNumbers.length === 0) {
      errors.push('Vui lòng chọn ít nhất một ghế')
    }

    if (request.selectedSeatNumbers && request.selectedSeatNumbers.length > 10) {
      errors.push('Không thể chọn quá 10 ghế cho một lần đặt')
    }

    // Validate customer name (cho direct booking dùng customerName)
    if (!request.customerName || request.customerName.trim().length < 2) {
      errors.push('Tên hành khách phải có ít nhất 2 ký tự')
    }

    if (request.customerName && request.customerName.length > 100) {
      errors.push('Tên hành khách không được quá 100 ký tự')
    }

    // Validate phone number (cho direct booking dùng phone)
    if (!request.phone) {
      errors.push('Số điện thoại là bắt buộc')
    } else if (!/^[0-9]{10,11}$/.test(request.phone)) {
      errors.push('Số điện thoại phải có 10-11 chữ số')
    }

    // Validate email (optional)
    if (request.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(request.email)) {
      errors.push('Email không hợp lệ')
    }

    // Validate notes length (optional)
    if (request.notes && request.notes.length > 500) {
      errors.push('Ghi chú không được quá 500 ký tự')
    }

    // Validate userId
    if (!request.userId || request.userId <= 0) {
      errors.push('Người dùng không hợp lệ')
    }

    return {
      valid: errors.length === 0,
      errors
    }
  }

  /**
   * Validate thông tin booking (cho Cart Booking)
   */
  static validateBookingRequest(request: BusBookingRequest): ValidationResult {
    const errors: string[] = []

    // Validate bus slot ID
    if (!request.busSlotId || request.busSlotId <= 0) {
      errors.push('ID chuyến xe không hợp lệ')
    }

    // Validate selected seats
    if (!request.selectedSeatIds || request.selectedSeatIds.length === 0) {
      errors.push('Vui lòng chọn ít nhất một ghế')
    }

    if (request.selectedSeatIds && request.selectedSeatIds.length > 10) {
      errors.push('Không thể chọn quá 10 ghế cho một lần đặt')
    }

    // Validate passenger name
    if (!request.passengerName || request.passengerName.trim().length < 2) {
      errors.push('Tên hành khách phải có ít nhất 2 ký tự')
    }

    if (request.passengerName && request.passengerName.length > 100) {
      errors.push('Tên hành khách không được quá 100 ký tự')
    }

    // Validate phone number
    if (!request.passengerPhone) {
      errors.push('Số điện thoại là bắt buộc')
    } else if (!/^[0-9]{10,11}$/.test(request.passengerPhone)) {
      errors.push('Số điện thoại phải có 10-11 chữ số')
    }

    // Validate email (optional)
    if (request.passengerEmail && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(request.passengerEmail)) {
      errors.push('Email không hợp lệ')
    }

    // Validate notes length (optional)
    if (request.notes && request.notes.length > 500) {
      errors.push('Ghi chú không được quá 500 ký tự')
    }

    return {
      valid: errors.length === 0,
      errors
    }
  }

  /**
   * Format passenger info cho hiển thị
   */
  static formatPassengerInfo(booking: BookingResponse): string {
    if (!booking.customer) return 'Thông tin khách hàng không có sẵn'
    
    const parts = [
      booking.customer.fullName,
      booking.customer.phone
    ]
    
    if (booking.customer.email) {
      parts.push(booking.customer.email)
    }
    
    return parts.join(' • ')
  }

  /**
   * Format thời gian cho hiển thị
   */
  static formatTripTime(booking: BookingResponse): string {
    if (!booking.busSlot) return ''
    
    const { departureDate, departureTime, arrivalTime } = booking.busSlot
    
    // Format: "01/02/2024 • 08:00 - 12:00"
    const formattedDate = new Date(departureDate).toLocaleDateString('vi-VN')
    const timeRange = `${departureTime.substring(0, 5)} - ${arrivalTime.substring(0, 5)}`
    
    return `${formattedDate} • ${timeRange}`
  }

  /**
   * Format route cho hiển thị
   */
  static formatRoute(booking: BookingResponse): string {
    if (!booking.busSlot) return ''
    
    const { departureLocation, arrivalLocation } = booking.busSlot
    return `${departureLocation} → ${arrivalLocation}`
  }

  /**
   * Kiểm tra trạng thái có thể hủy
   */
  static canCancel(booking: BookingResponse): boolean {
    if (!booking) return false
    
    const now = new Date()
    const departureDate = new Date(booking.busSlot.departureDate)
    
    // Có thể hủy nếu:
    // 1. Status là RESERVED hoặc CONFIRMED
    // 2. Chưa quá thời gian departure
    const cancellableStatuses = ['RESERVED', 'CONFIRMED']
    const isBeforeDeparture = now < departureDate
    
    return cancellableStatuses.includes(booking.status) && isBeforeDeparture
  }

  /**
   * Tính thời gian còn lại để hủy
   */
  static getTimeToCancel(booking: BookingResponse): string {
    if (!booking.busSlot) return ''
    
    const now = new Date()
    const departureDate = new Date(booking.busSlot.departureDate)
    const timeDiff = departureDate.getTime() - now.getTime()
    
    if (timeDiff <= 0) return 'Hết hạn hủy'
    
    const hours = Math.floor(timeDiff / (1000 * 60 * 60))
    const minutes = Math.floor((timeDiff % (1000 * 60 * 60)) / (1000 * 60))
    
    if (hours > 24) {
      const days = Math.floor(hours / 24)
      return `Còn ${days} ngày ${hours % 24} giờ`
    } else if (hours > 0) {
      return `Còn ${hours} giờ ${minutes} phút`
    } else {
      return `Còn ${minutes} phút`
    }
  }
} 