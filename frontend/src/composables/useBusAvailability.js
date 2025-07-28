import { ref } from 'vue'
import { BusSlotAPI, BusSlotStatus } from '@/api/busApi/busSlot'

export function useBusAvailability() {
  const checking = ref(false)
  const conflictError = ref('')
  const conflictDetails = ref(null)
  
  /**
   * Kiểm tra xem xe bus có sẵn trong khoảng thời gian chỉ định không
   * @param {Object} params - Thông tin cần kiểm tra
   * @param {string} params.busId - ID của xe bus
   * @param {string} params.slotDate - Ngày chuyến đi (YYYY-MM-DD)
   * @param {string} params.departureTime - Giờ khởi hành (HH:MM)
   * @param {string} params.arrivalTime - Giờ đến (HH:MM) 
   * @param {string} [params.excludeTripId] - ID của chuyến đang chỉnh sửa (để loại trừ)
   * @param {number} [params.bufferMinutes=60] - Khoảng cách tối thiểu giữa các chuyến (phút)
   */
  async function checkAvailability({
    busId,
    slotDate,
    departureTime,
    arrivalTime,
    excludeTripId = null,
    bufferMinutes = 60
  }) {
    if (!busId || !slotDate || !departureTime) {
      conflictError.value = ''
      conflictDetails.value = null
      return { available: true }
    }
    
    try {
      checking.value = true
      conflictError.value = ''
      conflictDetails.value = null
      
   
      
      // Lấy tất cả chuyến xe của bus này
      const existingSlots = await BusSlotAPI.findBusSlotsByBusId(busId)
      
      // Lọc ra các chuyến có thể conflict
      const potentialConflicts = existingSlots.filter(slot => {
        // Bỏ qua chuyến đang chỉnh sửa
        if (excludeTripId && slot.id === excludeTripId) return false
        
        // Bỏ qua các chuyến không cùng ngày
        if (slot.slotDate !== slotDate) return false
        
        // Bỏ qua các chuyến đã hoàn thành hoặc đã hủy
        if (slot.status === BusSlotStatus.COMPLETED || 
            slot.status === BusSlotStatus.CANCELLED) return false
        
        return true
      })
      
      
      // Kiểm tra conflict về thời gian
      const conflicts = []
      const newDepartureMinutes = timeToMinutes(departureTime)
      const newArrivalMinutes = arrivalTime ? timeToMinutes(arrivalTime) : newDepartureMinutes + 60 // Default 1h trip
      
      for (const slot of potentialConflicts) {
        const slotDepartureMinutes = timeToMinutes(slot.departureTime)
        const slotArrivalMinutes = slot.arrivalTime ? 
          timeToMinutes(slot.arrivalTime) : 
          slotDepartureMinutes + 60
        
        // Kiểm tra overlap với buffer
        const conflict = checkTimeOverlap({
          new: { start: newDepartureMinutes, end: newArrivalMinutes },
          existing: { start: slotDepartureMinutes, end: slotArrivalMinutes },
          buffer: bufferMinutes
        })
        
        if (conflict.hasOverlap) {
          conflicts.push({
            slot,
            ...conflict
          })
        }
      }
      
      if (conflicts.length > 0) {
        const primaryConflict = conflicts[0]
        const conflictSlot = primaryConflict.slot
        
        conflictError.value = generateConflictMessage(conflictSlot, primaryConflict)
        conflictDetails.value = {
          conflictingSlot: conflictSlot,
          conflicts: conflicts,
          suggestions: generateSuggestions(conflicts, {
            newDeparture: newDepartureMinutes,
            newArrival: newArrivalMinutes,
            buffer: bufferMinutes
          })
        }
        
        return { 
          available: false, 
          conflicts,
          suggestions: conflictDetails.value.suggestions
        }
      }
      
      return { available: true }
      
    } catch (error) {
      console.error('❌ Error checking bus availability:', error)
      
      // Trong trường hợp lỗi, cho phép tạo chuyến (fail-safe)
      conflictError.value = 'Không thể kiểm tra lịch trình xe. Vui lòng kiểm tra thủ công.'
      return { 
        available: true, 
        warning: 'Không thể kiểm tra conflict, vui lòng xác nhận thủ công'
      }
    } finally {
      checking.value = false
    }
  }
  
  /**
   * Chuyển đổi thời gian từ HH:MM hoặc HH:MM:SS sang phút từ 00:00
   */
  function timeToMinutes(timeString) {
    if (!timeString) return 0
    
    const timeParts = timeString.split(':')
    const hours = parseInt(timeParts[0])
    const minutes = parseInt(timeParts[1])
    
    return hours * 60 + minutes
  }
  
  /**
   * Kiểm tra overlap giữa 2 khoảng thời gian với buffer
   */
  function checkTimeOverlap({ new: newTime, existing, buffer }) {
    // Mở rộng khoảng thời gian với buffer
    const newStart = newTime.start
    const newEnd = newTime.end
    const existingStart = existing.start - buffer
    const existingEnd = existing.end + buffer
    
    // Kiểm tra overlap
    const hasOverlap = !(newEnd <= existingStart || newStart >= existingEnd)
    
    if (hasOverlap) {
      const overlapStart = Math.max(newStart, existingStart)
      const overlapEnd = Math.min(newEnd, existingEnd)
      const overlapMinutes = overlapEnd - overlapStart
      
      return {
        hasOverlap: true,
        overlapMinutes,
        details: {
          newSlot: { start: newStart, end: newEnd },
          existingSlot: { start: existing.start, end: existing.end },
          buffer
        }
      }
    }
    
    return { hasOverlap: false }
  }
  
  /**
   * Tạo thông báo lỗi conflict
   */
  function generateConflictMessage(conflictSlot, conflictInfo) {
    const conflictTime = minutesToTime(timeToMinutes(conflictSlot.departureTime))
    const conflictRoute = conflictSlot.route ? 
      `${conflictSlot.route.originLocation.name} - ${conflictSlot.route.destinationLocation.name}` : 
      'Tuyến không xác định'
    
    return `Xe đã có lịch trình khác vào ${conflictTime} (${conflictRoute}). ` +
           `Cần ít nhất ${conflictInfo.details.buffer} phút giữa các chuyến.`
  }
  
  /**
   * Tạo gợi ý thời gian thay thế
   */
  function generateSuggestions(conflicts, { newDeparture, newArrival, buffer }) {
    const suggestions = []
    
    // Tìm khoảng trống trước conflict đầu tiên
    const firstConflict = conflicts[0]
    const firstConflictStart = timeToMinutes(firstConflict.slot.departureTime)
    
    if (firstConflictStart - buffer > newDeparture) {
      const suggestedTime = Math.max(0, firstConflictStart - buffer - (newArrival - newDeparture))
      suggestions.push({
        type: 'earlier',
        departureTime: minutesToTime(suggestedTime),
        arrivalTime: minutesToTime(suggestedTime + (newArrival - newDeparture)),
        reason: 'Khởi hành sớm hơn để tránh conflict'
      })
    }
    
    // Tìm khoảng trống sau conflict cuối cùng
    const lastConflict = conflicts[conflicts.length - 1]
    const lastConflictEnd = lastConflict.slot.arrivalTime ? 
      timeToMinutes(lastConflict.slot.arrivalTime) : 
      timeToMinutes(lastConflict.slot.departureTime) + 60
    
    const suggestedLaterTime = lastConflictEnd + buffer
    if (suggestedLaterTime < 24 * 60) { // Trong cùng ngày
      suggestions.push({
        type: 'later',
        departureTime: minutesToTime(suggestedLaterTime),
        arrivalTime: minutesToTime(suggestedLaterTime + (newArrival - newDeparture)),
        reason: 'Khởi hành muộn hơn để tránh conflict'
      })
    }
    
    return suggestions
  }
  
  /**
   * Chuyển đổi phút thành HH:MM
   */
  function minutesToTime(minutes) {
    const hours = Math.floor(minutes / 60)
    const mins = minutes % 60
    return `${hours.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}`
  }
  
  /**
   * Kiểm tra nhanh có conflict không (dùng cho validation real-time)
   */
  async function quickCheck(busId, slotDate, departureTime, excludeTripId = null) {
    const result = await checkAvailability({
      busId,
      slotDate,
      departureTime,
      excludeTripId,
      bufferMinutes: 30 // Buffer ngắn hơn cho quick check
    })
    
    return result.available
  }
  
  /**
   * Clear tất cả error states
   */
  function clearErrors() {
    conflictError.value = ''
    conflictDetails.value = null
  }
  
  /**
   * Lấy thông tin chi tiết về conflicts
   */
  function getConflictDetails() {
    return conflictDetails.value
  }
  
  return {
    // State
    checking,
    conflictError,
    conflictDetails,
    
    // Methods
    checkAvailability,
    quickCheck,
    clearErrors,
    getConflictDetails,
    
    // Utility methods
    timeToMinutes,
    minutesToTime
  }
} 