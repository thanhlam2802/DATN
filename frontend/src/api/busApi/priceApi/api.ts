import { graphqlRequest, graphqlMutation } from '@/api/graphqlClient'
import {
  FIND_ALL_ROUTE_BUS_CATEGORY_PRICES,
  FIND_ROUTE_BUS_CATEGORY_PRICE_BY_ID,
  FIND_ACTIVE_ROUTE_BUS_CATEGORY_PRICE
} from './queries'
import {
  CREATE_ROUTE_BUS_CATEGORY_PRICE,
  UPDATE_ROUTE_BUS_CATEGORY_PRICE,
  DELETE_ROUTE_BUS_CATEGORY_PRICE
} from './mutations'
import {
  RouteBusCategoryPrice,
  CreateRouteBusCategoryPriceInput,
  UpdateRouteBusCategoryPriceInput,
  PriceCalculationRequest,
  PriceCalculationResult,
  PriceStatus
} from './types'

export class PriceAPI {

  // === BASIC CRUD OPERATIONS ===

  /**
   * Lấy tất cả quy tắc giá
   */
  static async findAllPrices(): Promise<RouteBusCategoryPrice[]> {
    try {
      const response = await graphqlRequest({ 
        query: FIND_ALL_ROUTE_BUS_CATEGORY_PRICES,
        variables: {} 
      })
      const prices = response.data?.findAllRouteBusCategoryPrices || []
      return prices
    } catch (error) {
      throw error
    }
  }

  /**
   * Lấy quy tắc giá theo ID
   */
  static async findPriceById(id: string): Promise<RouteBusCategoryPrice | null> {
    try {
      const response = await graphqlRequest({ 
        query: FIND_ROUTE_BUS_CATEGORY_PRICE_BY_ID, 
        variables: { id } 
      })
      const price = response.data?.findRouteBusCategoryPriceById
      
      if (price) {
        return price
      } else {
        return null
      }
    } catch (error) {
      throw error
    }
  }

  /**
   * Tạo quy tắc giá mới
   */
  static async createPrice(input: CreateRouteBusCategoryPriceInput): Promise<RouteBusCategoryPrice> {
    try {
      // Validate input before sending
      this.validatePriceInput(input)
      
      const response = await graphqlMutation({ 
        query: CREATE_ROUTE_BUS_CATEGORY_PRICE, 
        variables: { input } 
      })
      
      // Enhanced response validation
      if (!response || !response.data) {
        throw new Error('Invalid GraphQL response structure')
      }
      
      const createdPrice = response.data.createRouteBusCategoryPrice
        
        if (!createdPrice) {
          throw new Error('Failed to create price rule - no data returned')
        }
      
      // Validate essential fields
      if (!createdPrice.id || !createdPrice.route || !createdPrice.busCategory) {
        throw new Error('Incomplete price data received from server')
      }
        
        return createdPrice
    } catch (error) {
      throw error
    }
  }

  /**
   * Cập nhật quy tắc giá
   */
  static async updatePrice(id: string, input: UpdateRouteBusCategoryPriceInput): Promise<RouteBusCategoryPrice> {
    try {
      const response = await graphqlMutation({ 
        query: UPDATE_ROUTE_BUS_CATEGORY_PRICE, 
        variables: { id, input } 
      })
      
      // Enhanced response validation
      if (!response || !response.data) {
        throw new Error('Invalid GraphQL response structure')
      }
      
      const updatedPrice = response.data.updateRouteBusCategoryPrice
        
        if (!updatedPrice) {
          throw new Error('Failed to update price rule - no data returned')
        }
      
      // Validate essential fields
      if (!updatedPrice.id || !updatedPrice.route || !updatedPrice.busCategory) {
        throw new Error('Incomplete price data received from server')
      }
        
        return updatedPrice
    } catch (error) {
      throw error
    }
  }

  /**
   * Xóa quy tắc giá
   */
  static async deletePrice(id: string): Promise<boolean> {
    try {
      const response = await graphqlMutation({ 
        query: DELETE_ROUTE_BUS_CATEGORY_PRICE, 
        variables: { id } 
      })
      const success = response.data?.deleteRouteBusCategoryPrice
        
        if (success) {
          return true
        } else {
          return false
        }
    } catch (error) {
      throw error
    }
  }

  // === BUSINESS LOGIC OPERATIONS ===

  /**
   * Tìm quy tắc giá hiệu lực cho tuyến đường và loại xe tại một ngày cụ thể
   * Core business logic cho price calculation
   */
  static async findActivePrice(request: PriceCalculationRequest): Promise<PriceCalculationResult> {
    try {
      const response = await graphqlRequest({
        query: FIND_ACTIVE_ROUTE_BUS_CATEGORY_PRICE,
        variables: {
          routeId: request.routeId,
          busCategoryId: request.busCategoryId,
          date: request.date
        }
      })
      
      const price = response.data?.findActiveRouteBusCategoryPrice
      
      if (!price) {
        return {
          found: false,
          effectivePrice: 0,
          isPromotionPrice: false,
          message: `Không tìm thấy quy tắc giá cho tuyến đường và loại xe này tại ngày ${request.date}`
        }
      }

      // Determine effective price (promotion price has priority)
      const hasValidPromotion = price.promotionPrice && price.promotionPrice > 0
      const effectivePrice = hasValidPromotion ? price.promotionPrice! : price.basePrice
      
      return {
        found: true,
        price,
        effectivePrice,
        isPromotionPrice: hasValidPromotion,
        message: hasValidPromotion 
          ? `Đang áp dụng giá khuyến mãi: ${this.formatPrice(effectivePrice)}`
          : `Đang áp dụng giá cơ sở: ${this.formatPrice(effectivePrice)}`
      }
    } catch (error) {
      throw error
    }
  }

  // === UTILITY METHODS ===

  /**
   * Tính toán trạng thái của quy tắc giá dựa trên thời gian hiệu lực
   */
  static calculatePriceStatus(price: RouteBusCategoryPrice): PriceStatus {
    const now = new Date()
    const today = now.toISOString().split('T')[0] // YYYY-MM-DD
    
    const validFrom = price.validFrom
    const validTo = price.validTo
    
    if (today < validFrom) {
      return PriceStatus.SCHEDULED // Sắp có hiệu lực
    } else if (today <= validTo) {
      return PriceStatus.ACTIVE // Đang hiệu lực
    } else {
      return PriceStatus.EXPIRED // Đã hết hiệu lực
    }
  }

  /**
   * Kiểm tra xung đột thời gian giữa các quy tắc giá
   */
  static checkTimeConflict(
    existingPrices: RouteBusCategoryPrice[],
    newPrice: { routeId: string; busCategoryId: string; validFrom: string; validTo: string },
    excludeId?: string
  ): { hasConflict: boolean; conflictingPrices: RouteBusCategoryPrice[] } {
    
    const conflictingPrices = existingPrices.filter(price => {
      // Skip self when updating
      if (excludeId && price.id === excludeId) return false
      
      // Must be same route and bus category
      if (price.route.id !== newPrice.routeId || price.busCategory.id !== newPrice.busCategoryId) {
        return false
      }
      
      // Check time overlap
      const existingStart = price.validFrom
      const existingEnd = price.validTo
      const newStart = newPrice.validFrom
      const newEnd = newPrice.validTo
      
      return !(newEnd < existingStart || newStart > existingEnd)
    })
    
    return {
      hasConflict: conflictingPrices.length > 0,
      conflictingPrices
    }
  }

  /**
   * Format giá tiền
   */
  static formatPrice(price: number): string {
    return price.toLocaleString('vi-VN') + 'đ'
  }

  /**
   * Validate price input
   */
  private static validatePriceInput(input: CreateRouteBusCategoryPriceInput | UpdateRouteBusCategoryPriceInput): void {
    if ('basePrice' in input && input.basePrice !== undefined) {
      if (input.basePrice <= 0) {
        throw new Error('Giá cơ sở phải lớn hơn 0')
      }
    }
    
    if (input.promotionPrice !== undefined && input.promotionPrice !== null && input.promotionPrice <= 0) {
      throw new Error('Giá khuyến mãi phải lớn hơn 0 hoặc để trống')
    }
    
    if ('validFrom' in input && 'validTo' in input && input.validFrom && input.validTo) {
      if (input.validFrom > input.validTo) {
        throw new Error('Ngày bắt đầu không thể lớn hơn ngày kết thúc')
      }
    }
  }

  /**
   * Bulk update prices
   */
  static async bulkUpdatePrices(
    priceIds: string[], 
    updateType: 'percentage' | 'fixed', 
    value: number
  ): Promise<RouteBusCategoryPrice[]> {
    try {
      const updatedPrices: RouteBusCategoryPrice[] = []
      
      for (const priceId of priceIds) {
        const existingPrice = await this.findPriceById(priceId)
        if (!existingPrice) continue
        
        let newBasePrice = existingPrice.basePrice
        
        if (updateType === 'percentage') {
          newBasePrice = Math.round(existingPrice.basePrice * (1 + value / 100))
        } else if (updateType === 'fixed') {
          newBasePrice = existingPrice.basePrice + value
        }
        
        if (newBasePrice <= 0) {
          console.warn(`⚠️ [PriceAPI] Skipping price ${priceId}: would result in non-positive price`)
          continue
        }
        
        const updatedPrice = await this.updatePrice(priceId, { basePrice: newBasePrice })
        updatedPrices.push(updatedPrice)
      }
      
      return updatedPrices
    } catch (error) {
      throw error
    }
  }
} 