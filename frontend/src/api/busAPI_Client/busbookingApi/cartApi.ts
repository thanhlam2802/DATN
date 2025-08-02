// Cart API for Bus Booking
import type { 
  AddToCartRequest, 
  CartResponse, 
  ApiResponse 
} from './types'

const BASE_URL = '/api/v1/cart'

export class CartAPI {
  /**
   * Tạo giỏ hàng mới cho user
   */
  static async createCart(userId: number): Promise<ApiResponse<CartResponse>> {
    try {
      const response = await fetch(`${BASE_URL}/create?userId=${userId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return await response.json()
    } catch (error) {
      console.error('❌ Error creating cart:', error)
      throw new Error('Không thể tạo giỏ hàng. Vui lòng thử lại.')
    }
  }

  /**
   * Lấy thông tin giỏ hàng
   */
  static async getCart(orderId: number): Promise<ApiResponse<CartResponse>> {
    try {
      const response = await fetch(`${BASE_URL}/${orderId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return await response.json()
    } catch (error) {
      console.error('❌ Error getting cart:', error)
      throw new Error('Không thể lấy thông tin giỏ hàng.')
    }
  }

  /**
   * Thêm bus vào giỏ hàng
   */
  static async addBusToCart(
    orderId: number, 
    busRequest: AddToCartRequest
  ): Promise<ApiResponse<CartResponse>> {
    try {
      const response = await fetch(`${BASE_URL}/${orderId}/items`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          itemId: busRequest.itemId, // busSlotId
          itemType: 'BUS',
          customerId: busRequest.customerId,
          selectedSeatIds: busRequest.selectedSeatIds,
          totalPrice: busRequest.totalPrice,
          notes: busRequest.notes,
          passengerName: busRequest.passengerName,
          passengerPhone: busRequest.passengerPhone,
          passengerEmail: busRequest.passengerEmail
        }),
      })

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}))
        throw new Error(errorData.message || `HTTP error! status: ${response.status}`)
      }
   

      return await response.json()
    } catch (error) {
      console.error('❌ Error adding bus to cart:', error)
      throw new Error(
        error instanceof Error 
          ? error.message 
          : 'Không thể thêm vé xe vào giỏ hàng. Vui lòng thử lại.'
      )
    }
  }

  /**
   * Xóa item khỏi giỏ hàng
   */
  static async removeFromCart(
    itemId: number, 
    itemType: string = 'BUS'
  ): Promise<ApiResponse<CartResponse>> {
    try {
      const response = await fetch(`${BASE_URL}/items?itemId=${itemId}&itemType=${itemType}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
        },
      })

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      return await response.json()
    } catch (error) {
      console.error('❌ Error removing from cart:', error)
      throw new Error('Không thể xóa khỏi giỏ hàng.')
    }
  }

  /**
   * Validate dữ liệu trước khi thêm vào giỏ hàng
   */
  static validateCartRequest(request: AddToCartRequest): { valid: boolean; errors: string[] } {
    const errors: string[] = []

    if (!request.itemId || request.itemId <= 0) {
      errors.push('ID chuyến xe không hợp lệ')
    }

    if (!request.selectedSeatIds || request.selectedSeatIds.length === 0) {
      errors.push('Vui lòng chọn ít nhất một ghế')
    }

    if (!request.passengerName || request.passengerName.trim().length < 2) {
      errors.push('Tên hành khách phải có ít nhất 2 ký tự')
    }

    if (!request.passengerPhone || !/^[0-9]{10,11}$/.test(request.passengerPhone)) {
      errors.push('Số điện thoại phải có 10-11 chữ số')
    }

    if (request.passengerEmail && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(request.passengerEmail)) {
      errors.push('Email không hợp lệ')
    }

    if (!request.totalPrice || request.totalPrice <= 0) {
      errors.push('Tổng tiền phải lớn hơn 0')
    }

    return {
      valid: errors.length === 0,
      errors
    }
  }
} 