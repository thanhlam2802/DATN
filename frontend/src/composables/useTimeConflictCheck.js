import { ref, computed } from 'vue'

export const useTimeConflictCheck = () => {
  const existingTrips = ref([])
  
  // Kiểm tra xem hai khoảng thời gian có chồng chéo không
  const isTimeOverlapping = (start1, end1, start2, end2) => {
    // Chuyển đổi thời gian thành phút để dễ so sánh
    const toMinutes = (timeStr) => {
      const [hours, minutes] = timeStr.split(':').map(Number)
      return hours * 60 + minutes
    }
    
    const start1Min = toMinutes(start1)
    const end1Min = toMinutes(end1)
    const start2Min = toMinutes(start2)
    const end2Min = toMinutes(end2)
    
    // Hai khoảng thời gian chồng chéo khi:
    // - Khoảng 1 không kết thúc trước khoảng 2 bắt đầu
    // - VÀ khoảng 1 không bắt đầu sau khoảng 2 kết thúc
    return !(end1Min <= start2Min || start1Min >= end2Min)
  }
  
  // Kiểm tra xem chuyến xe mới có trùng với chuyến xe hiện có không
  const checkTimeConflict = (newTrip, existingTripsList) => {
    if (!newTrip.departureTime || !newTrip.arrivalTime || !newTrip.busId || !newTrip.slotDate) {
      return { hasConflict: false, conflictingTrips: [] }
    }
    
    const conflictingTrips = existingTripsList.filter(existingTrip => {
      // Chỉ kiểm tra cùng xe bus và cùng ngày
      if (existingTrip.busId !== newTrip.busId || existingTrip.slotDate !== newTrip.slotDate) {
        return false
      }
      
      // Chỉ kiểm tra các chuyến xe đang hoạt động
      if (!['SCHEDULED', 'IN_PROGRESS'].includes(existingTrip.status)) {
        return false
      }
      
      // Kiểm tra trùng thời gian
      return isTimeOverlapping(
        newTrip.departureTime,
        newTrip.arrivalTime,
        existingTrip.departureTime,
        existingTrip.arrivalTime
      )
    })
    
    return {
      hasConflict: conflictingTrips.length > 0,
      conflictingTrips
    }
  }
  
  // Lấy tất cả chuyến xe của một xe bus trong một ngày
  const getTripsForBusAndDate = (busId, slotDate, allTrips) => {
    return allTrips.filter(trip => 
      trip.busId === busId && 
      trip.slotDate === slotDate &&
      ['SCHEDULED', 'IN_PROGRESS'].includes(trip.status)
    )
  }
  
  // Kiểm tra xem có thể tạo chuyến xe mới không
  const canCreateTrip = (newTrip, allTrips) => {
    const conflict = checkTimeConflict(newTrip, allTrips)
    return !conflict.hasConflict
  }
  
  // Tạo thông báo lỗi chi tiết
  const getConflictMessage = (conflictingTrips) => {
    if (conflictingTrips.length === 0) return ''
    
    const tripInfo = conflictingTrips.map(trip => 
      `${trip.departureTime} - ${trip.arrivalTime}`
    ).join(', ')
    
    return `Thời gian chuyến xe bị trùng với các chuyến hiện có: ${tripInfo}`
  }
  
  return {
    isTimeOverlapping,
    checkTimeConflict,
    getTripsForBusAndDate,
    canCreateTrip,
    getConflictMessage
  }
} 