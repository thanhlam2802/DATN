/**
 * Trip Auto Management Service
 * Tự động quản lý trạng thái chuyến xe và reset ghế
 */

import { BusSlotAPI } from '@/api/busApi'

// Simulate server-side cron job logic in frontend for demo
export class TripAutoManager {
  
  /**
   * Check và update status cho tất cả trips
   */
  static async checkAndUpdateTripStatuses() {
    try {
      console.log('🔄 [AUTO] Checking trip statuses...')
      
      // Get all active trips
      const trips = await BusSlotAPI.getAllBusSlots()
      const now = new Date()
      
      for (const trip of trips) {
        await this.processTrip(trip, now)
      }
      
      console.log('✅ [AUTO] Trip status check completed')
      
    } catch (error) {
      console.error('❌ [AUTO] Error checking trip statuses:', error)
    }
  }
  
  /**
   * Process single trip status và auto actions
   */
  static async processTrip(trip, currentTime) {
    const departureTime = new Date(trip.departureTime)
    const arrivalTime = new Date(trip.arrivalTime)
    
    console.log(`🔍 [AUTO] Processing trip ${trip.id}:`, {
      current: currentTime.toLocaleTimeString(),
      departure: departureTime.toLocaleTimeString(),
      arrival: arrivalTime.toLocaleTimeString(),
      status: trip.status
    })
    
    // Auto status transitions
    if (trip.autoStatusUpdate) {
      const newStatus = this.calculateNewStatus(trip, departureTime, arrivalTime, currentTime)
      
      if (newStatus !== trip.status) {
        console.log(`🔄 [AUTO] Trip ${trip.id}: ${trip.status} → ${newStatus}`)
        await this.updateTripStatus(trip.id, newStatus)
      }
    }
    
    // Auto reset for recurring trips
    if (trip.tripType === 'RECURRING' && trip.autoResetSeats) {
      await this.handleRecurringTrip(trip, arrivalTime, currentTime)
    }
    
    // Archive old completed trips
    if (trip.status === 'COMPLETED') {
      await this.archiveOldTrip(trip, arrivalTime, currentTime)
    }
  }
  
  /**
   * Calculate new status based on time with tolerance
   */
  static calculateNewStatus(trip, departureTime, arrivalTime, currentTime) {
    const timeBeforeDeparture = departureTime.getTime() - currentTime.getTime()
    const timeAfterArrival = currentTime.getTime() - arrivalTime.getTime()
    
    // Get tolerance (default 30 minutes if not specified)
    const tolerance = (trip.timeToleranceMinutes || 30) * 60 * 1000
    
    // Check if manual override is active (actual times provided)
    const actualDeparture = trip.actualDepartureTime ? new Date(trip.actualDepartureTime) : null
    const actualArrival = trip.actualArrivalTime ? new Date(trip.actualArrivalTime) : null
    
    // Use actual times if provided by manual override
    const effectiveDeparture = actualDeparture || departureTime
    const effectiveArrival = actualArrival || arrivalTime
    
    console.log(`📊 [AUTO] Trip ${trip.id} timing analysis:`, {
      scheduled: {
        departure: departureTime.toLocaleTimeString(),
        arrival: arrivalTime.toLocaleTimeString()
      },
      actual: {
        departure: actualDeparture?.toLocaleTimeString() || 'Not set',
        arrival: actualArrival?.toLocaleTimeString() || 'Not set'
      },
      current: currentTime.toLocaleTimeString(),
      tolerance: `±${trip.timeToleranceMinutes || 30} minutes`
    })
    
    // 15 phút trước khởi hành (theo effective time)
    const timeBeforeEffectiveDeparture = effectiveDeparture.getTime() - currentTime.getTime()
    if (timeBeforeEffectiveDeparture <= 15 * 60 * 1000 && timeBeforeEffectiveDeparture > -tolerance) {
      return trip.status === 'SCHEDULED' ? 'SCHEDULED' : trip.status
    }
    
    // Đã khởi hành (với tolerance) nhưng chưa đến
    if (currentTime.getTime() >= (effectiveDeparture.getTime() - tolerance) && 
        currentTime.getTime() < (effectiveArrival.getTime() + tolerance)) {
      return 'IN_PROGRESS'
    }
    
    // Đã đến nơi (với tolerance)
    if (currentTime.getTime() >= (effectiveArrival.getTime() - tolerance)) {
      return 'COMPLETED'
    }
    
    return trip.status
  }
  
  /**
   * Calculate real-time status (early/on-time/delayed)
   */
  static calculateRealTimeStatus(trip, currentTime) {
    const departureTime = new Date(trip.departureTime)
    const arrivalTime = new Date(trip.arrivalTime)
    const actualDeparture = trip.actualDepartureTime ? new Date(trip.actualDepartureTime) : null
    const actualArrival = trip.actualArrivalTime ? new Date(trip.actualArrivalTime) : null
    
    // If trip is completed, compare actual vs scheduled arrival
    if (trip.status === 'COMPLETED' && actualArrival) {
      const timeDifference = actualArrival.getTime() - arrivalTime.getTime()
      if (timeDifference < -10 * 60 * 1000) return 'EARLY'      // Sớm hơn 10 phút
      if (timeDifference > 10 * 60 * 1000) return 'DELAYED'     // Trễ hơn 10 phút
      return 'ON_SCHEDULE'
    }
    
    // If trip is in progress, estimate based on departure
    if (trip.status === 'IN_PROGRESS') {
      if (actualDeparture) {
        const departureDifference = actualDeparture.getTime() - departureTime.getTime()
        if (departureDifference < -5 * 60 * 1000) return 'EARLY'
        if (departureDifference > 15 * 60 * 1000) return 'DELAYED'
      }
      return 'ON_SCHEDULE'
    }
    
    return 'UNKNOWN'
  }
  
  /**
   * Handle recurring trip reset
   */
  static async handleRecurringTrip(trip, arrivalTime, currentTime) {
    // Reset sau 30 phút kể từ khi đến nơi
    const resetTime = arrivalTime.getTime() + (30 * 60 * 1000)
    
    if (currentTime.getTime() >= resetTime && trip.status === 'COMPLETED') {
      console.log(`🔄 [AUTO] Resetting recurring trip ${trip.id}`)
      
      // Tạo chuyến mới cho ngày mai (example)
      const nextTripData = this.generateNextTrip(trip)
      
      if (nextTripData) {
        console.log('🆕 [AUTO] Creating next recurring trip:', nextTripData)
        // await BusSlotAPI.createBusSlot(nextTripData)
        
        // Reset current trip seats for demo
        await this.resetTripSeats(trip.id)
      }
    }
  }
  
  /**
   * Generate data cho chuyến tiếp theo
   */
  static generateNextTrip(currentTrip) {
    if (!currentTrip.recurringPattern) return null
    
    const tomorrow = new Date()
    tomorrow.setDate(tomorrow.getDate() + 1)
    
    // Giữ nguyên giờ, thay đổi ngày
    const nextDeparture = new Date(currentTrip.departureTime)
    nextDeparture.setDate(tomorrow.getDate())
    nextDeparture.setMonth(tomorrow.getMonth())
    nextDeparture.setFullYear(tomorrow.getFullYear())
    
    const nextArrival = new Date(currentTrip.arrivalTime)
    nextArrival.setDate(tomorrow.getDate())
    nextArrival.setMonth(tomorrow.getMonth())  
    nextArrival.setFullYear(tomorrow.getFullYear())
    
    return {
      busId: currentTrip.bus.id,
      routeId: currentTrip.route.id,
      departureTime: nextDeparture.toISOString(),
      arrivalTime: nextArrival.toISOString(),
      basePrice: currentTrip.basePrice,
      totalSeats: currentTrip.totalSeats,
      tripType: 'RECURRING',
      recurringPattern: currentTrip.recurringPattern,
      autoStatusUpdate: true,
      autoResetSeats: true
    }
  }
  
  /**
   * Archive old completed trips (sau 24h)
   */
  static async archiveOldTrip(trip, arrivalTime, currentTime) {
    const archiveTime = arrivalTime.getTime() + (24 * 60 * 60 * 1000)
    
    if (currentTime.getTime() >= archiveTime) {
      console.log(`📁 [AUTO] Archiving old trip ${trip.id}`)
      await this.updateTripStatus(trip.id, 'ARCHIVED')
    }
  }
  
  /**
   * API calls wrappers
   */
  static async updateTripStatus(tripId, newStatus) {
    try {
      // await BusSlotAPI.updateBusSlot(tripId, { status: newStatus })
      console.log(`✅ [AUTO] Updated trip ${tripId} status to ${newStatus}`)
    } catch (error) {
      console.error(`❌ [AUTO] Failed to update trip ${tripId}:`, error)
    }
  }
  
  static async resetTripSeats(tripId) {
    try {
      // await BusSlotAPI.resetSeats(tripId)
      console.log(`✅ [AUTO] Reset seats for trip ${tripId}`)
    } catch (error) {
      console.error(`❌ [AUTO] Failed to reset seats for trip ${tripId}:`, error)
    }
  }
  
  /**
   * Start auto-management interval
   */
  static startAutoManagement(intervalMinutes = 5) {
    console.log(`🚀 [AUTO] Starting trip auto-management (every ${intervalMinutes} minutes)`)
    
    // Initial check
    this.checkAndUpdateTripStatuses()
    
    // Periodic checks
    return setInterval(() => {
      this.checkAndUpdateTripStatuses()
    }, intervalMinutes * 60 * 1000)
  }
  
  /**
   * Manual trigger for testing
   */
  static async manualTrigger() {
    console.log('🔧 [MANUAL] Triggering trip auto-management...')
    await this.checkAndUpdateTripStatuses()
  }
  
  /**
   * Manual override for early/late scenarios
   */
  static async updateActualTimes(tripId, actualTimes, reason = null) {
    try {
      console.log(`🕐 [MANUAL] Updating actual times for trip ${tripId}:`, actualTimes)
      
      const updateData = {
        ...actualTimes,
        delayReason: reason,
        updatedAt: new Date().toISOString()
      }
      
      // API call to update actual times
      // await BusSlotAPI.updateActualTimes(tripId, updateData)
      
      // Trigger immediate status check after manual update
      await this.manualTrigger()
      
      console.log(`✅ [MANUAL] Updated actual times for trip ${tripId}`)
      return true
      
    } catch (error) {
      console.error(`❌ [MANUAL] Failed to update actual times for trip ${tripId}:`, error)
      return false
    }
  }
  
  /**
   * Quick status update for driver/dispatcher
   */
  static async quickStatusUpdate(tripId, newStatus, reason = null, location = null) {
    try {
      console.log(`🚦 [QUICK] Quick status update for trip ${tripId}: ${newStatus}`)
      
      const currentTime = new Date()
      const updateData = {
        status: newStatus,
        currentLocation: location,
        delayReason: reason,
        updatedAt: currentTime.toISOString()
      }
      
      // If marking as IN_PROGRESS, set actual departure
      if (newStatus === 'IN_PROGRESS') {
        updateData.actualDepartureTime = currentTime.toISOString()
      }
      
      // If marking as COMPLETED, set actual arrival
      if (newStatus === 'COMPLETED') {
        updateData.actualArrivalTime = currentTime.toISOString()
      }
      
      // API call
      // await BusSlotAPI.quickStatusUpdate(tripId, updateData)
      
      console.log(`✅ [QUICK] Quick status updated for trip ${tripId}`)
      return true
      
    } catch (error) {
      console.error(`❌ [QUICK] Failed quick status update for trip ${tripId}:`, error)
      return false
    }
  }
  
  /**
   * Estimate delay based on current progress
   */
  static estimateDelay(trip, currentTime) {
    const departureTime = new Date(trip.departureTime)
    const arrivalTime = new Date(trip.arrivalTime)
    const tripDuration = arrivalTime.getTime() - departureTime.getTime()
    
    if (trip.status === 'IN_PROGRESS') {
      const actualDeparture = trip.actualDepartureTime ? new Date(trip.actualDepartureTime) : departureTime
      const elapsedTime = currentTime.getTime() - actualDeparture.getTime()
      const progress = elapsedTime / tripDuration
      
      // Estimate arrival based on current progress
      const estimatedArrival = new Date(actualDeparture.getTime() + tripDuration)
      const estimatedDelay = estimatedArrival.getTime() - arrivalTime.getTime()
      
      return {
        estimatedArrival: estimatedArrival.toISOString(),
        estimatedDelayMinutes: Math.round(estimatedDelay / (60 * 1000)),
        progress: Math.round(progress * 100)
      }
    }
    
    return null
  }
}

/**
 * Trip Status Helper
 */
export const TripStatusHelper = {
  getStatusColor(status) {
    const colors = {
      'SCHEDULED': 'blue',
      'IN_PROGRESS': 'green', 
      'COMPLETED': 'gray',
      'CANCELLED': 'red',
      'ARCHIVED': 'gray'
    }
    return colors[status] || 'gray'
  },
  
  getStatusText(status) {
    const texts = {
      'SCHEDULED': 'Đã lên lịch',
      'IN_PROGRESS': 'Đang chạy',
      'COMPLETED': 'Hoàn thành',
      'CANCELLED': 'Đã hủy', 
      'ARCHIVED': 'Lưu trữ'
    }
    return texts[status] || status
  },
  
  getStatusIcon(status) {
    const icons = {
      'SCHEDULED': '📅',
      'IN_PROGRESS': '🚌',
      'COMPLETED': '✅',
      'CANCELLED': '❌',
      'ARCHIVED': '📁'
    }
    return icons[status] || '❓'
  },
  
  // Real-time status helpers
  getRealTimeStatusColor(realTimeStatus) {
    const colors = {
      'ON_SCHEDULE': 'green',
      'EARLY': 'blue',
      'DELAYED': 'yellow',
      'UNKNOWN': 'gray'
    }
    return colors[realTimeStatus] || 'gray'
  },
  
  getRealTimeStatusText(realTimeStatus) {
    const texts = {
      'ON_SCHEDULE': 'Đúng giờ',
      'EARLY': 'Sớm hơn dự kiến',
      'DELAYED': 'Trễ hơn dự kiến',
      'UNKNOWN': 'Chưa xác định'
    }
    return texts[realTimeStatus] || realTimeStatus
  },
  
  getRealTimeStatusIcon(realTimeStatus) {
    const icons = {
      'ON_SCHEDULE': '⏰',
      'EARLY': '⚡',
      'DELAYED': '⏳',
      'UNKNOWN': '❓'
    }
    return icons[realTimeStatus] || '❓'
  },
  
  // Delay reason helpers
  getDelayReasonText(reason) {
    const texts = {
      'TRAFFIC_JAM': 'Kẹt xe',
      'WEATHER': 'Thời tiết xấu',
      'VEHICLE_ISSUE': 'Sự cố xe',
      'PASSENGER_DELAY': 'Khách trễ',
      'ROAD_ACCIDENT': 'Tai nạn giao thông',
      'FUEL_STOP': 'Dừng đổ xăng',
      'DRIVER_BREAK': 'Nghỉ giải lao',
      'EARLY_ARRIVAL': 'Đến sớm',
      'OTHER': 'Lý do khác'
    }
    return texts[reason] || reason
  },
  
  getDelayReasonIcon(reason) {
    const icons = {
      'TRAFFIC_JAM': '🚗',
      'WEATHER': '🌧️',
      'VEHICLE_ISSUE': '🔧',
      'PASSENGER_DELAY': '👥',
      'ROAD_ACCIDENT': '⚠️',
      'FUEL_STOP': '⛽',
      'DRIVER_BREAK': '☕',
      'EARLY_ARRIVAL': '🏃‍♂️',
      'OTHER': '❓'
    }
    return icons[reason] || '❓'
  },
  
  // Time difference display
  formatTimeDifference(minutes) {
    if (minutes === 0) return 'Đúng giờ'
    
    const absMinutes = Math.abs(minutes)
    const hours = Math.floor(absMinutes / 60)
    const mins = absMinutes % 60
    
    let result = ''
    if (hours > 0) result += `${hours}h `
    if (mins > 0) result += `${mins}p`
    
    if (minutes > 0) return `Trễ ${result}`
    if (minutes < 0) return `Sớm ${result}`
    
    return result
  },
  
  // Trip progress helpers
  calculateProgress(trip, currentTime) {
    const departureTime = new Date(trip.departureTime)
    const arrivalTime = new Date(trip.arrivalTime)
    const now = new Date(currentTime)
    
    if (now < departureTime) return 0
    if (now > arrivalTime) return 100
    
    const totalDuration = arrivalTime.getTime() - departureTime.getTime()
    const elapsed = now.getTime() - departureTime.getTime()
    
    return Math.round((elapsed / totalDuration) * 100)
  },
  
  // Display helpers for UI
  getDisplayTime(scheduledTime, actualTime) {
    if (actualTime) {
      const scheduled = new Date(scheduledTime)
      const actual = new Date(actualTime)
      const difference = actual.getTime() - scheduled.getTime()
      const diffMinutes = Math.round(difference / (60 * 1000))
      
      const actualTimeStr = actual.toLocaleTimeString('vi-VN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })
      
      if (diffMinutes === 0) {
        return `${actualTimeStr} (đúng giờ)`
      } else if (diffMinutes > 0) {
        return `${actualTimeStr} (trễ ${diffMinutes}p)`
      } else {
        return `${actualTimeStr} (sớm ${Math.abs(diffMinutes)}p)`
      }
    }
    
    // Show scheduled time if no actual time
    const scheduled = new Date(scheduledTime)
    return scheduled.toLocaleTimeString('vi-VN', { 
      hour: '2-digit', 
      minute: '2-digit' 
    })
  }
}

export default TripAutoManager 