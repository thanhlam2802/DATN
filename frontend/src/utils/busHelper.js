/**
 * Helper functions for Bus management
 * Chuyển đổi dữ liệu giữa form và backend entities
 */

/**
 * Chuyển đổi thời gian text thành phút
 * @param {string} timeString - VD: "8 giờ 30 phút" hoặc "12 giờ"
 * @returns {number} - Số phút
 */
export const parseTimeToMinutes = (timeString) => {
  if (!timeString || typeof timeString !== 'string') return 0
  
  // Remove extra spaces and normalize
  const normalized = timeString.toLowerCase().trim()
  
  // Extract hours and minutes
  const hourMatch = normalized.match(/(\d+)\s*giờ/)
  const minuteMatch = normalized.match(/(\d+)\s*phút/)
  
  const hours = hourMatch ? parseInt(hourMatch[1]) : 0
  const minutes = minuteMatch ? parseInt(minuteMatch[1]) : 0
  
  return hours * 60 + minutes
}

/**
 * Chuyển đổi phút thành text
 * @param {number} minutes - Số phút
 * @returns {string} - VD: "8 giờ 30 phút"
 */
export const minutesToTimeString = (minutes) => {
  if (!minutes || minutes <= 0) return ''
  
  const hours = Math.floor(minutes / 60)
  const remainingMinutes = minutes % 60
  
  if (remainingMinutes === 0) {
    return `${hours} giờ`
  }
  
  return `${hours} giờ ${remainingMinutes} phút`
}

/**
 * Lấy ID BusCategory từ busType
 * @param {string} busType - 'standard' hoặc 'sleeper'
 * @returns {number} - Category ID
 */
export const getBusCategoryId = (busType) => {
  const categoryMap = {
    'standard': 1,    // Trung chuyển
    'sleeper': 2      // Giường nằm
  }
  return categoryMap[busType] || 1
}

/**
 * Lấy tên loại xe từ busType
 * @param {string} busType - 'standard' hoặc 'sleeper'
 * @returns {string} - Tên loại xe
 */
export const getBusTypeName = (busType) => {
  const typeMap = {
    'standard': 'Trung chuyển (16 ghế)',
    'sleeper': 'Giường nằm (36 ghế)'
  }
  return typeMap[busType] || 'Không xác định'
}

/**
 * Kết hợp ngày và giờ thành LocalDateTime string
 * @param {string} time - VD: "20:00"
 * @param {Date} baseDate - Ngày cơ sở (mặc định hôm nay)
 * @returns {string} - ISO string cho LocalDateTime
 */
export const combineDateTime = (time, baseDate = new Date()) => {
  if (!time) return null
  
  const [hours, minutes] = time.split(':').map(num => parseInt(num))
  const dateTime = new Date(baseDate)
  dateTime.setHours(hours, minutes, 0, 0)
  
  // Return format compatible with LocalDateTime
  return dateTime.toISOString().slice(0, 19)
}

/**
 * Tính khoảng cách giữa 2 thành phố (mock data)
 * @param {string} origin - Thành phố đi
 * @param {string} destination - Thành phố đến
 * @returns {number} - Khoảng cách km
 */
export const calculateDistance = (origin, destination) => {
  // Mock distance map - thực tế sẽ dùng Google Maps API
  const distanceMap = {
    // Hà Nội
    'hanoi-hcm': 1700,
    'hanoi-danang': 800,
    'hanoi-cantho': 1800,
    'hanoi-nhatrang': 1200,
    'hanoi-dalat': 1400,
    
    // TP.HCM
    'hcm-hanoi': 1700,
    'hcm-danang': 900,
    'hcm-cantho': 170,
    'hcm-nhatrang': 450,
    'hcm-dalat': 300,
    
    // Đà Nẵng
    'danang-hanoi': 800,
    'danang-hcm': 900,
    'danang-cantho': 1000,
    'danang-nhatrang': 500,
    'danang-dalat': 600,
    
    // Cần Thơ
    'cantho-hanoi': 1800,
    'cantho-hcm': 170,
    'cantho-danang': 1000,
    'cantho-nhatrang': 500,
    'cantho-dalat': 400,
    
    // Nha Trang
    'nhatrang-hanoi': 1200,
    'nhatrang-hcm': 450,
    'nhatrang-danang': 500,
    'nhatrang-cantho': 500,
    'nhatrang-dalat': 200,
    
    // Đà Lạt
    'dalat-hanoi': 1400,
    'dalat-hcm': 300,
    'dalat-danang': 600,
    'dalat-cantho': 400,
    'dalat-nhatrang': 200
  }
  
  if (!origin || !destination) return 0
  if (origin === destination) return 0
  
  const key = `${origin}-${destination}`.toLowerCase()
  return distanceMap[key] || 500 // Default 500km if not found
}

/**
 * Format giá tiền VND
 * @param {number} price - Giá tiền
 * @returns {string} - Giá đã format
 */
export const formatPrice = (price) => {
  if (!price || price <= 0) return '0'
  return new Intl.NumberFormat('vi-VN').format(price)
}

/**
 * Validate thời gian (departure phải trước arrival)
 * @param {string} departureTime - Giờ đi
 * @param {string} arrivalTime - Giờ đến
 * @returns {boolean} - True nếu hợp lệ
 */
export const validateTimeOrder = (departureTime, arrivalTime) => {
  if (!departureTime || !arrivalTime) return true // Let required validation handle empty values
  
  const [depHour, depMinute] = departureTime.split(':').map(n => parseInt(n))
  const [arrHour, arrMinute] = arrivalTime.split(':').map(n => parseInt(n))
  
  const depMinutes = depHour * 60 + depMinute
  const arrMinutes = arrHour * 60 + arrMinute
  
  // Arrival can be next day, so if arrival < departure, assume next day
  if (arrMinutes < depMinutes) {
    return true // Assume next day arrival
  }
  
  return arrMinutes > depMinutes
}

/**
 * Tạo tên Bus từ form data
 * @param {object} formData - Dữ liệu form
 * @returns {string} - Tên bus
 */
export const generateBusName = (formData) => {
  const typeName = getBusTypeName(formData.busType).split(' ')[0] // "Trung" hoặc "Giường"
  return `${typeName} chuyển ${formData.licensePlate}`
}

/**
 * Tạo code cho route
 * @param {string} origin - Điểm đi
 * @param {string} destination - Điểm đến
 * @returns {string} - Route code
 */
export const generateRouteCode = (origin, destination) => {
  const originCode = getCityCode(origin)
  const destinationCode = getCityCode(destination)
  const timestamp = Date.now().toString().slice(-4)
  
  return `${originCode}-${destinationCode}-${timestamp}`
}

/**
 * Lấy mã thành phố
 * @param {string} city - Tên thành phố
 * @returns {string} - Mã thành phố
 */
export const getCityCode = (city) => {
  const cityMap = {
    'hanoi': 'HN',
    'hcm': 'HCM',
    'danang': 'DN',
    'cantho': 'CT',
    'nhatrang': 'NT',
    'dalat': 'DL'
  }
  return cityMap[city] || city.toUpperCase().slice(0, 3)
}

/**
 * Lấy tên đầy đủ thành phố
 * @param {string} cityCode - Mã thành phố
 * @returns {string} - Tên đầy đủ
 */
export const getCityFullName = (cityCode) => {
  const cityMap = {
    'hanoi': 'Hà Nội',
    'hcm': 'TP. Hồ Chí Minh',
    'danang': 'Đà Nẵng',
    'cantho': 'Cần Thơ',
    'nhatrang': 'Nha Trang',
    'dalat': 'Đà Lạt'
  }
  return cityMap[cityCode] || cityCode
} 