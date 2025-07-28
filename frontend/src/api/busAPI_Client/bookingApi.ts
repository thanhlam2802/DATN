import { graphqlRequest } from '../graphqlClient'
import type { 
  CreateBookingInput, 
  BookingResponse, 
  PassengerInfo,
  BusSlot 
} from './types'

// GraphQL Mutations
const CREATE_BOOKING = `
  mutation CreateBooking($input: CreateBookingInput!) {
    createBooking(input: $input) {
      bookingId
      busSlot {
        id
        slotDate
        departureTime
        arrivalTime
        price
        bus {
          name
          licensePlate
        }
        route {
          origin
          destination
        }
      }
      seats
      passenger {
        fullName
        phoneNumber
        notes
      }
      totalAmount
      status
      qrCode
      createdAt
    }
  }
`

const APPLY_VOUCHER = `
  mutation ApplyVoucher($code: String!, $amount: Float!) {
    applyVoucher(code: $code, amount: $amount) {
      valid
      discountPercentage
      discountAmount
      message
    }
  }
`

const GET_BOOKING_BY_ID = `
  query GetBookingById($bookingId: ID!) {
    getBookingById(bookingId: $bookingId) {
      bookingId
      busSlot {
        id
        slotDate
        departureTime
        arrivalTime
        price
        bus {
          name
          licensePlate
          categoryName
        }
        route {
          origin
          destination
        }
      }
      seats
      passenger {
        fullName
        phoneNumber
        notes
      }
      totalAmount
      status
      qrCode
      createdAt
    }
  }
`

export class BookingAPI {
  /**
   * Create a new bus booking
   */
  static async createBooking(bookingData: CreateBookingInput): Promise<BookingResponse> {
    try {
      const response = await graphqlRequest({
        query: CREATE_BOOKING,
        variables: { input: bookingData }
      })

      const booking = response.data?.createBooking
      
      if (!booking) {
        throw new Error('Không thể tạo đặt chỗ')
      }

      return {
        bookingId: booking.bookingId,
        busSlot: booking.busSlot,
        seats: booking.seats,
        passenger: booking.passenger,
        totalAmount: booking.totalAmount,
        status: booking.status,
        qrCode: booking.qrCode
      }
    } catch (error) {
      console.error('❌ Error creating booking:', error)
      
      // Handle specific booking errors
      const errorMessage = (error as any)?.message || ''
      
      if (errorMessage.includes('seat')) {
        throw new Error('Ghế đã được đặt bởi người khác. Vui lòng chọn ghế khác.')
      } else if (errorMessage.includes('full')) {
        throw new Error('Chuyến xe đã hết chỗ. Vui lòng chọn chuyến khác.')
      } else if (errorMessage.includes('payment')) {
        throw new Error('Lỗi thanh toán. Vui lòng thử lại.')
      } else {
        throw new Error('Không thể đặt vé. Vui lòng thử lại sau.')
      }
    }
  }

  /**
   * Apply voucher code and get discount
   */
  static async applyVoucher(code: string, amount: number): Promise<{
    valid: boolean
    discountPercentage: number
    discountAmount: number
    message: string
  }> {
    try {
      const response = await graphqlRequest({
        query: APPLY_VOUCHER,
        variables: { code: code.toUpperCase(), amount }
      })

      const result = response.data?.applyVoucher
      
      if (!result) {
        return {
          valid: false,
          discountPercentage: 0,
          discountAmount: 0,
          message: 'Mã voucher không hợp lệ'
        }
      }

      return {
        valid: result.valid,
        discountPercentage: result.discountPercentage || 0,
        discountAmount: result.discountAmount || 0,
        message: result.message || (result.valid ? 'Áp dụng voucher thành công' : 'Mã voucher không hợp lệ')
      }
    } catch (error) {
      console.error('❌ Error applying voucher:', error)
      return {
        valid: false,
        discountPercentage: 0,
        discountAmount: 0,
        message: 'Không thể áp dụng voucher. Vui lòng thử lại.'
      }
    }
  }

  /**
   * Get booking details by ID
   */
  static async getBookingById(bookingId: string): Promise<BookingResponse | null> {
    try {
      const response = await graphqlRequest({
        query: GET_BOOKING_BY_ID,
        variables: { bookingId }
      })

      const booking = response.data?.getBookingById
      
      if (!booking) {
        return null
      }

      return {
        bookingId: booking.bookingId,
        busSlot: booking.busSlot,
        seats: booking.seats,
        passenger: booking.passenger,
        totalAmount: booking.totalAmount,
        status: booking.status,
        qrCode: booking.qrCode
      }
    } catch (error) {
      console.error('❌ Error fetching booking:', error)
      throw new Error('Không thể tải thông tin đặt chỗ.')
    }
  }

  /**
   * Validate booking data before submission
   */
  static validateBookingData(bookingData: CreateBookingInput): {
    valid: boolean
    errors: string[]
  } {
    const errors: string[] = []

    // Validate passenger info
    if (!bookingData.passengerInfo.fullName?.trim()) {
      errors.push('Vui lòng nhập họ và tên')
    }

    if (!bookingData.passengerInfo.phoneNumber?.trim()) {
      errors.push('Vui lòng nhập số điện thoại')
    } else if (!this.isValidPhoneNumber(bookingData.passengerInfo.phoneNumber)) {
      errors.push('Số điện thoại không hợp lệ')
    }

    // Validate seats
    if (!bookingData.seatNumbers || bookingData.seatNumbers.length === 0) {
      errors.push('Vui lòng chọn ít nhất 1 ghế')
    }

    // Validate payment method
    if (!bookingData.paymentMethod?.trim()) {
      errors.push('Vui lòng chọn phương thức thanh toán')
    }

    // Validate amount
    if (!bookingData.totalAmount || bookingData.totalAmount <= 0) {
      errors.push('Số tiền không hợp lệ')
    }

    return {
      valid: errors.length === 0,
      errors
    }
  }

  /**
   * Validate Vietnamese phone number
   */
  private static isValidPhoneNumber(phoneNumber: string): boolean {
    // Remove spaces and dashes
    const cleaned = phoneNumber.replace(/[\s-]/g, '')
    
    // Vietnamese phone number patterns
    const vnPhoneRegex = /^(\+84|84|0)([3-9]\d{8}|\d{9})$/
    
    return vnPhoneRegex.test(cleaned)
  }

  /**
   * Calculate final price with discounts
   */
  static calculateFinalPrice(
    basePrice: number,
    seatCount: number,
    discountPercentage: number = 0
  ): {
    subtotal: number
    discount: number
    total: number
  } {
    const subtotal = basePrice * seatCount
    const discount = (subtotal * discountPercentage) / 100
    const total = subtotal - discount

    return {
      subtotal: Math.round(subtotal),
      discount: Math.round(discount),
      total: Math.round(total)
    }
  }

  /**
   * Format price for display
   */
  static formatPrice(price: number): string {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND'
    }).format(price)
  }

  /**
   * Generate booking reference (for display before API response)
   */
  static generateBookingReference(): string {
    const timestamp = Date.now().toString(36)
    const random = Math.random().toString(36).substr(2, 5)
    return `BUS${timestamp}${random}`.toUpperCase()
  }

  /**
   * Mock voucher validation (for fallback)
   */
  static getMockVoucherDiscount(code: string): {
    valid: boolean
    discountPercentage: number
    message: string
  } {
    const vouchers: Record<string, { discount: number; message: string }> = {
      'GIAM10': { discount: 10, message: 'Giảm 10% cho đơn hàng' },
      'GIAM20': { discount: 20, message: 'Giảm 20% cho đơn hàng' },
      'NEWUSER': { discount: 15, message: 'Giảm 15% cho khách hàng mới' },
      'SUMMER2024': { discount: 25, message: 'Giảm 25% khuyến mãi hè' },
      'STUDENT': { discount: 12, message: 'Giảm 12% cho sinh viên' }
    }

    const voucher = vouchers[code.toUpperCase()]
    
    if (voucher) {
      return {
        valid: true,
        discountPercentage: voucher.discount,
        message: voucher.message
      }
    }

    return {
      valid: false,
      discountPercentage: 0,
      message: 'Mã voucher không tồn tại hoặc đã hết hạn'
    }
  }
} 