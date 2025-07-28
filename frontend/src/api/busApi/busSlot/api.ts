/**
 * BusSlot API operations
 * Quản lý các chuyến đi xe bus
 */

import { graphqlRequest, graphqlMutation } from '@/api/graphqlClient'
import type { 
  BusSlotResponse, 
  CreateBusSlotRequest, 
  UpdateBusSlotRequest,
  UpdateActualTimesRequest,
  QuickStatusUpdateRequest
} from '../types/common.types'
import { BusSlotStatus, DelayReason } from '../types/common.types'

import {
  FIND_ALL_BUS_SLOTS,
  FIND_BUS_SLOT_BY_ID,
  FIND_BUS_SLOTS_BY_BUS_ID,
  FIND_BUS_SLOTS_BY_ROUTE_ID,
  FIND_BUS_SLOTS_BY_STATUS,
  SEARCH_BUS_SLOTS,
  FIND_TRIPS_NEEDING_STATUS_UPDATE,
  FIND_COMPLETED_TRIPS_FOR_ARCHIVE
} from './queries'

import {
  CREATE_BUS_SLOT,
  UPDATE_BUS_SLOT,
  DELETE_BUS_SLOT,
  ACTIVATE_BUS_SLOT,
  COMPLETE_BUS_SLOT,
  CANCEL_BUS_SLOT,
  UPDATE_ACTUAL_TIMES,
  QUICK_STATUS_UPDATE,
  MARK_BUS_SLOT_IN_PROGRESS,
  MARK_BUS_SLOT_COMPLETED,
  ARCHIVE_BUS_SLOT
} from './mutations'

export class BusSlotAPI {
  
  // === BASIC CRUD OPERATIONS ===
  
  static async findAllBusSlots(): Promise<BusSlotResponse[]> {
    const response = await graphqlRequest({ query: FIND_ALL_BUS_SLOTS })
    return response.data.findAllBusSlots
  }

  static async findBusSlotById(id: string): Promise<BusSlotResponse | null> {
    const response = await graphqlRequest({ query: FIND_BUS_SLOT_BY_ID, variables: { id } })
    return response.data.findBusSlotById
  }

  static async findBusSlotsByBusId(busId: string): Promise<BusSlotResponse[]> {
    const response = await graphqlRequest({ query: FIND_BUS_SLOTS_BY_BUS_ID, variables: { busId } })
    return response.data.findBusSlotsByBusId
  }

  static async findBusSlotsByRouteId(routeId: string): Promise<BusSlotResponse[]> {
    const response = await graphqlRequest({ query: FIND_BUS_SLOTS_BY_ROUTE_ID, variables: { routeId } })
    return response.data.findBusSlotsByRouteId
  }

  static async findBusSlotsByStatus(status: BusSlotStatus): Promise<BusSlotResponse[]> {
    const response = await graphqlRequest({ query: FIND_BUS_SLOTS_BY_STATUS, variables: { status } })
    return response.data.findBusSlotsByStatus
  }

  static async searchBusSlots(searchParams: {
    departureLocationId: string
    arrivalLocationId: string
    slotDate: string
    busCategoryId?: string
    minPrice?: number
    maxPrice?: number
    minAvailableSeats?: number
    status?: BusSlotStatus
  }): Promise<BusSlotResponse[]> {
    const response = await graphqlRequest({ query: SEARCH_BUS_SLOTS, variables: searchParams })
    return response.data.searchBusSlots
  }

  static async createBusSlot(input: CreateBusSlotRequest): Promise<BusSlotResponse> {
    try {
      const response = await graphqlMutation({ query: CREATE_BUS_SLOT, variables: { input } })
      return response.data.createBusSlot
    } catch (error) {
      console.error('❌ [API] Lỗi khi tạo chuyến xe:', error)
      console.error('Chi tiết lỗi:', JSON.stringify(error, null, 2))
      throw error
    }
  }

  static async updateBusSlot(id: string, input: UpdateBusSlotRequest): Promise<BusSlotResponse> {
    const response = await graphqlMutation({ query: UPDATE_BUS_SLOT, variables: { id, input } })
    return response.data.updateBusSlot
  }

  static async deleteBusSlot(id: string): Promise<boolean> {
    const response = await graphqlMutation({ query: DELETE_BUS_SLOT, variables: { id } })
    return response.data.deleteBusSlot
  }

  // === LEGACY STATUS UPDATES ===
  
  static async activateBusSlot(id: string): Promise<BusSlotResponse> {
    const response = await graphqlMutation({ query: ACTIVATE_BUS_SLOT, variables: { id } })
    return response.data.activateBusSlot
  }

  static async completeBusSlot(id: string): Promise<BusSlotResponse> {
    const response = await graphqlMutation({ query: COMPLETE_BUS_SLOT, variables: { id } })
    return response.data.completeBusSlot
  }

  static async cancelBusSlot(id: string): Promise<BusSlotResponse> {
    const response = await graphqlMutation({ query: CANCEL_BUS_SLOT, variables: { id } })
    return response.data.cancelBusSlot
  }

  // === REAL-TIME MANAGEMENT ===

  static async updateActualTimes(id: string, input: UpdateActualTimesRequest): Promise<BusSlotResponse> {
    const response = await graphqlMutation({ query: UPDATE_ACTUAL_TIMES, variables: { id, input } })
    return response.data.updateActualTimes
  }

  static async quickStatusUpdate(id: string, input: QuickStatusUpdateRequest): Promise<BusSlotResponse> {
    const response = await graphqlMutation({ query: QUICK_STATUS_UPDATE, variables: { id, input } })
    return response.data.quickStatusUpdate
  }

  static async markInProgress(id: string): Promise<BusSlotResponse> {
    const response = await graphqlMutation({ query: MARK_BUS_SLOT_IN_PROGRESS, variables: { id } })
    return response.data.markBusSlotInProgress
  }

  static async markCompleted(id: string): Promise<BusSlotResponse> {
    const response = await graphqlMutation({ query: MARK_BUS_SLOT_COMPLETED, variables: { id } })
    return response.data.markBusSlotCompleted
  }

  static async archiveBusSlot(id: string): Promise<BusSlotResponse> {
    const response = await graphqlMutation({ query: ARCHIVE_BUS_SLOT, variables: { id } })
    return response.data.archiveBusSlot
  }

  // === AUTO-MANAGEMENT QUERIES ===

  static async findTripsNeedingStatusUpdate(): Promise<BusSlotResponse[]> {
    const response = await graphqlRequest({ query: FIND_TRIPS_NEEDING_STATUS_UPDATE })
    return response.data.findTripsNeedingStatusUpdate
  }

  static async findCompletedTripsForArchive(daysOld: number = 7): Promise<BusSlotResponse[]> {
    const response = await graphqlRequest({ query: FIND_COMPLETED_TRIPS_FOR_ARCHIVE, variables: { daysOld } })
    return response.data.findCompletedTripsForArchive
  }
  


  // === CONVENIENCE METHODS FOR UI ===

  static async checkIn(tripId: string, options?: {
    actualDepartureTime?: string
    currentLocation?: string
    delayReason?: DelayReason
  }): Promise<BusSlotResponse> {
    const input: QuickStatusUpdateRequest = {
      status: BusSlotStatus.IN_PROGRESS,
      autoSetActualTime: true,
      ...options
    }
    return await this.quickStatusUpdate(tripId, input)
  }

  static async checkOut(tripId: string, options?: {
    actualArrivalTime?: string
    currentLocation?: string
    delayReason?: DelayReason
  }): Promise<BusSlotResponse> {
    const input: QuickStatusUpdateRequest = {
      status: BusSlotStatus.COMPLETED,
      autoSetActualTime: true,
      ...options
    }
    return await this.quickStatusUpdate(tripId, input)
  }

  static async reportDelay(tripId: string, delayReason: DelayReason, currentLocation?: string): Promise<BusSlotResponse> {
    const input: QuickStatusUpdateRequest = {
      status: BusSlotStatus.DELAYED,
      delayReason,
      currentLocation,
      autoSetActualTime: false
    }
    return await this.quickStatusUpdate(tripId, input)
  }

  // === UTILITY METHODS ===

  static calculateRealTimeStatus(busSlot: BusSlotResponse): 'EARLY' | 'ON_SCHEDULE' | 'DELAYED' | 'UNKNOWN' {
    if (!busSlot.departureTime || !busSlot.arrivalTime) return 'UNKNOWN'

    const now = new Date()
    const scheduledDeparture = new Date(busSlot.departureTime)
    const scheduledArrival = new Date(busSlot.arrivalTime)

    // If trip hasn't started yet
    if (busSlot.status === BusSlotStatus.SCHEDULED) {
      const timeToDeparture = scheduledDeparture.getTime() - now.getTime()
      if (timeToDeparture > 30 * 60 * 1000) return 'ON_SCHEDULE' // More than 30 min early
      return 'UNKNOWN'
    }

    // If trip has actual times
    if (busSlot.actualDepartureTime) {
      const actualDeparture = new Date(busSlot.actualDepartureTime)
      const departureDelay = actualDeparture.getTime() - scheduledDeparture.getTime()
      
      if (departureDelay < -5 * 60 * 1000) return 'EARLY' // 5+ min early
      if (departureDelay > 15 * 60 * 1000) return 'DELAYED' // 15+ min late
      return 'ON_SCHEDULE'
    }

    if (busSlot.actualArrivalTime) {
      const actualArrival = new Date(busSlot.actualArrivalTime)
      const arrivalDelay = actualArrival.getTime() - scheduledArrival.getTime()
      
      if (arrivalDelay < -10 * 60 * 1000) return 'EARLY' // 10+ min early
      if (arrivalDelay > 10 * 60 * 1000) return 'DELAYED' // 10+ min late
      return 'ON_SCHEDULE'
    }

    return 'UNKNOWN'
  }

  static calculateDelayMinutes(busSlot: BusSlotResponse): number {
    if (busSlot.actualArrivalTime && busSlot.arrivalTime) {
      const scheduled = new Date(busSlot.arrivalTime)
      const actual = new Date(busSlot.actualArrivalTime)
      return Math.round((actual.getTime() - scheduled.getTime()) / (1000 * 60))
    }

    if (busSlot.actualDepartureTime && busSlot.departureTime) {
      const scheduled = new Date(busSlot.departureTime)
      const actual = new Date(busSlot.actualDepartureTime)
      return Math.round((actual.getTime() - scheduled.getTime()) / (1000 * 60))
    }

    return 0
  }

  static getStatusColor(status: BusSlotStatus): string {
    switch (status) {
      case BusSlotStatus.SCHEDULED: return 'blue'
      case BusSlotStatus.IN_PROGRESS: return 'green'
      case BusSlotStatus.COMPLETED: return 'gray'
      case BusSlotStatus.CANCELLED: return 'red'
      case BusSlotStatus.DELAYED: return 'orange'
      case BusSlotStatus.ARCHIVED: return 'purple'
      default: return 'gray'
    }
  }

  static getStatusText(status: BusSlotStatus): string {
    switch (status) {
      case BusSlotStatus.SCHEDULED: return 'Đã lên lịch'
      case BusSlotStatus.IN_PROGRESS: return 'Đang chạy'
      case BusSlotStatus.COMPLETED: return 'Hoàn thành'
      case BusSlotStatus.CANCELLED: return 'Đã hủy'
      case BusSlotStatus.DELAYED: return 'Bị trễ'
      case BusSlotStatus.ARCHIVED: return 'Lưu trữ'
      default: return 'Không xác định'
    }
  }
} 