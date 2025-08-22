import { graphqlRequest } from '../graphqlClient'
import type { SeatOccupancy, SeatInfo, SeatStatus, BusSlot } from './types'

// GraphQL Queries
const GET_SEAT_OCCUPANCY = `
  query GetSeatOccupancy($busSlotId: ID!) {
    getSeatOccupancy(busSlotId: $busSlotId) {
      busSlotId
      totalSeats
      availableSeats
      occupiedSeats
      pendingSeats
      seatMap {
        seatNumber
        status
        passengerName
        bookingId
      }
    }
  }
`

const RESERVE_SEATS = `
  mutation ReserveSeats($busSlotId: ID!, $seatNumbers: [Int!]!) {
    reserveSeats(busSlotId: $busSlotId, seatNumbers: $seatNumbers) {
      success
      message
      reservationId
      expiresAt
    }
  }
`

const RELEASE_SEATS = `
  mutation ReleaseSeats($reservationId: ID!) {
    releaseSeats(reservationId: $reservationId) {
      success
      message
    }
  }
`

export class SeatAPI {
  /**
   * Get seat occupancy for a specific bus slot
   */
  static async getSeatOccupancy(busSlotId: string): Promise<SeatOccupancy> {
    try {
      const response = await graphqlRequest({
        query: GET_SEAT_OCCUPANCY,
        variables: { busSlotId }
      })

      const data = response.data?.getSeatOccupancy
      
      if (!data) {
        throw new Error('Không tìm thấy thông tin ghế')
      }

      return data
    } catch (error) {
      console.error('❌ Error fetching seat occupancy:', error)
      throw new Error('Không thể tải thông tin ghế. Vui lòng thử lại.')
    }
  }

  /**
   * Generate dynamic seat layout based on totalSeats
   */
  static generateSeatLayout(totalSeats: number): {
    layout: 'single' | 'double'
    rows: number
    seatsPerRow: number
    levels?: string[]
  } {
    if (totalSeats <= 20) {
      // Small bus - single level (shuttle bus style)
      return {
        layout: 'single',
        rows: Math.ceil(totalSeats / 4),
        seatsPerRow: 4
      }
    } else if (totalSeats <= 45) {
      // Medium bus - double level (sleeping bus style)
      return {
        layout: 'double',
        rows: Math.ceil(totalSeats / 6), // 3 seats per row per level
        seatsPerRow: 3,
        levels: ['Tầng trên', 'Tầng dưới']
      }
    } else {
      // Large bus - double level with more seats
      return {
        layout: 'double',
        rows: Math.ceil(totalSeats / 8), // 4 seats per row per level
        seatsPerRow: 4,
        levels: ['Tầng trên', 'Tầng dưới']
      }
    }
  }

  /**
   * Get seat status for display
   */
  static getSeatStatus(
    seatNumber: number, 
    seatMap: SeatInfo[], 
    selectedSeats: number[]
  ): SeatStatus {
    // Check if user selected this seat
    if (selectedSeats.includes(seatNumber)) {
      return SeatStatus.SELECTED
    }

    // Check real occupancy from server
    const seatInfo = seatMap.find(seat => seat.seatNumber === seatNumber)
    
    if (seatInfo) {
      return seatInfo.status as SeatStatus
    }

    // Default to available
    return SeatStatus.AVAILABLE
  }

  /**
   * Get seat display class for UI
   */
  static getSeatDisplayClass(status: SeatStatus): string {
    const baseClass = 'border-2 rounded-lg w-12 h-12 flex items-center justify-center text-sm font-medium transition-all duration-200 cursor-pointer'
    
    switch (status) {
      case SeatStatus.AVAILABLE:
        return `${baseClass} bg-green-100 border-green-300 text-green-700 hover:bg-green-200 hover:scale-105`
      case SeatStatus.OCCUPIED:
        return `${baseClass} bg-red-100 border-red-300 text-red-700 cursor-not-allowed opacity-75`
      case SeatStatus.PENDING:
        return `${baseClass} bg-yellow-100 border-yellow-300 text-yellow-700 cursor-not-allowed animate-pulse`
      case SeatStatus.SELECTED:
        return `${baseClass} bg-blue-600 border-blue-700 text-white shadow-lg scale-105`
      default:
        return baseClass
    }
  }

  /**
   * Temporarily reserve seats (for booking process)
   */
  static async reserveSeats(busSlotId: string, seatNumbers: number[]): Promise<{
    success: boolean
    reservationId?: string
    expiresAt?: string
    message: string
  }> {
    try {
      const response = await graphqlRequest({
        query: RESERVE_SEATS,
        variables: { busSlotId, seatNumbers }
      })

      const result = response.data?.reserveSeats
      
      return {
        success: result?.success || false,
        reservationId: result?.reservationId,
        expiresAt: result?.expiresAt,
        message: result?.message || 'Đặt chỗ tạm thời thành công'
      }
    } catch (error) {
      console.error('❌ Error reserving seats:', error)
      return {
        success: false,
        message: 'Không thể đặt chỗ tạm thời. Vui lòng thử lại.'
      }
    }
  }

  /**
   * Release temporarily reserved seats
   */
  static async releaseSeats(reservationId: string): Promise<{
    success: boolean
    message: string
  }> {
    try {
      const response = await graphqlRequest({
        query: RELEASE_SEATS,
        variables: { reservationId }
      })

      const result = response.data?.releaseSeats
      
      return {
        success: result?.success || false,
        message: result?.message || 'Hủy đặt chỗ thành công'
      }
    } catch (error) {
      console.error('❌ Error releasing seats:', error)
      return {
        success: false,
        message: 'Không thể hủy đặt chỗ.'
      }
    }
  }

  /**
   * Generate mock seat map for fallback (when API fails)
   */
  static generateMockSeatMap(busSlot: BusSlot): SeatInfo[] {
    const seatMap: SeatInfo[] = []
    const totalSeats = busSlot.totalSeats
    const occupiedCount = totalSeats - busSlot.availableSeats
    
    // Generate random occupied seats
    const occupiedSeats = new Set<number>()
    while (occupiedSeats.size < occupiedCount) {
      const randomSeat = Math.floor(Math.random() * totalSeats) + 1
      occupiedSeats.add(randomSeat)
    }
    
    // Generate seat map
    for (let i = 1; i <= totalSeats; i++) {
      seatMap.push({
        seatNumber: i,
        status: occupiedSeats.has(i) ? SeatStatus.OCCUPIED : SeatStatus.AVAILABLE,
        passengerName: occupiedSeats.has(i) ? 'Đã đặt' : undefined,
        bookingId: occupiedSeats.has(i) ? `booking_${i}` : undefined
      })
    }
    
    return seatMap
  }

  /**
   * Validate seat selection
   */
  static validateSeatSelection(
    seatNumbers: number[], 
    seatMap: SeatInfo[],
    maxSeats: number = 10
  ): { valid: boolean; message: string } {
    if (seatNumbers.length === 0) {
      return { valid: false, message: 'Vui lòng chọn ít nhất 1 ghế' }
    }
    
    if (seatNumbers.length > maxSeats) {
      return { valid: false, message: `Chỉ được chọn tối đa ${maxSeats} ghế` }
    }
    
    // Check if any selected seat is occupied
    for (const seatNumber of seatNumbers) {
      const seatInfo = seatMap.find(seat => seat.seatNumber === seatNumber)
      if (seatInfo && seatInfo.status !== SeatStatus.AVAILABLE) {
        return { 
          valid: false, 
          message: `Ghế ${seatNumber} không khả dụng. Vui lòng chọn ghế khác.` 
        }
      }
    }
    
    return { valid: true, message: 'Lựa chọn ghế hợp lệ' }
  }
} 