import { graphqlRequest } from '../graphqlClient'
import type { 
  SearchCriteria, 
  SearchBusSlotsInput, 
  SearchResponse, 
  BusSlot 
} from './types'

// GraphQL Queries
const SEARCH_BUS_SLOTS = `
  query SearchBusSlots($input: SearchBusSlotsInput!) {
    searchBusSlots(input: $input) {
      id
      slotDate
      departureTime
      arrivalTime
      price
      totalSeats
      availableSeats
      status
      bus {
        id
        name
        licensePlate
        categoryName
        totalSeats
        busImages {
          id
          image {
            url
            altText
          }
        }
      }
      route {
        id
        origin
        destination
        distanceKm
        estimatedDurationMinutes
      }
      actualDepartureTime
      actualArrivalTime
      createdAt
      updatedAt
    }
  }
`

const GET_BUS_SLOT_BY_ID = `
  query GetBusSlotById($id: ID!) {
    findBusSlotById(id: $id) {
      id
      slotDate
      departureTime
      arrivalTime
      price
      totalSeats
      availableSeats
      status
      bus {
        id
        name
        licensePlate
        categoryName
        totalSeats
        busImages {
          id
          image {
            url
            altText
          }
        }
      }
      route {
        id
        origin
        destination
        distanceKm
        estimatedDurationMinutes
      }
      actualDepartureTime
      actualArrivalTime
      createdAt
      updatedAt
    }
  }
`

export class BusSearchAPI {
  /**
   * Search bus slots based on user criteria
   */
  static async searchBusSlots(criteria: SearchCriteria): Promise<BusSlot[]> {
    try {
      const input: SearchBusSlotsInput = {
        origin: criteria.from,
        destination: criteria.to,
        slotDate: criteria.departureDate,
        minAvailableSeats: criteria.seats,
        status: 'SCHEDULED' // Only show scheduled trips for booking
      }

      const response = await graphqlRequest({
        query: SEARCH_BUS_SLOTS,
        variables: { input }
      })

      return response.data?.searchBusSlots || []
    } catch (error) {
      console.error('❌ Error searching bus slots:', error)
      throw new Error('Không thể tìm kiếm chuyến xe. Vui lòng thử lại.')
    }
  }

  /**
   * Get detailed bus slot information
   */
  static async getBusSlotById(id: string): Promise<BusSlot | null> {
    try {
      const response = await graphqlRequest({
        query: GET_BUS_SLOT_BY_ID,
        variables: { id }
      })

      return response.data?.findBusSlotById || null
    } catch (error) {
      console.error('❌ Error fetching bus slot:', error)
      throw new Error('Không thể tải thông tin chuyến xe.')
    }
  }

  /**
   * Search with advanced filters
   */
  static async searchWithFilters(
    criteria: SearchCriteria,
    filters: {
      minPrice?: number
      maxPrice?: number
      busCategoryId?: string
    }
  ): Promise<BusSlot[]> {
    try {
      const input: SearchBusSlotsInput = {
        origin: criteria.from,
        destination: criteria.to,
        slotDate: criteria.departureDate,
        minAvailableSeats: criteria.seats,
        minPrice: filters.minPrice,
        maxPrice: filters.maxPrice,
        busCategoryId: filters.busCategoryId,
        status: 'SCHEDULED'
      }

      const response = await graphqlRequest({
        query: SEARCH_BUS_SLOTS,
        variables: { input }
      })

      return response.data?.searchBusSlots || []
    } catch (error) {
      console.error('❌ Error searching with filters:', error)
      throw new Error('Không thể tìm kiếm chuyến xe với bộ lọc.')
    }
  }

  /**
   * Check real-time availability for a specific slot
   */
  static async checkAvailability(busSlotId: string): Promise<{
    available: boolean
    availableSeats: number
    totalSeats: number
  }> {
    try {
      const busSlot = await this.getBusSlotById(busSlotId)
      
      if (!busSlot) {
        return { available: false, availableSeats: 0, totalSeats: 0 }
      }

      return {
        available: busSlot.availableSeats > 0,
        availableSeats: busSlot.availableSeats,
        totalSeats: busSlot.totalSeats
      }
    } catch (error) {
      console.error('❌ Error checking availability:', error)
      return { available: false, availableSeats: 0, totalSeats: 0 }
    }
  }
} 