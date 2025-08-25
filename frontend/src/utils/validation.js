// Helper functions for validation
const isValidEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

const isValidPhone = (phone) => {
  const phoneRegex = /^(\+84|84|0)[0-9]{9}$/
  return phoneRegex.test(phone)
}

const isValidPassport = (passport) => {
  const passportRegex = /^[A-Z0-9]{6,9}$/
  return passportRegex.test(passport)
}

const isValidDate = (date) => {
  const inputDate = new Date(date)
  return inputDate instanceof Date && !isNaN(inputDate)
}

const isFutureDate = (date) => {
  const inputDate = new Date(date)
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return inputDate > today
}

const isValidPrice = (price) => {
  const numValue = parseFloat(price)
  return !isNaN(numValue) && numValue >= 0
}

// Validation utilities for flight forms
export const validationRules = {
  // Email validation
  email: {
    pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
    message: 'Email không hợp lệ'
  },
  
  // Phone validation (Vietnamese format)
  phone: {
    pattern: /^(0|\+84)(\s|\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\d)(\s|\.)?(\d{3})(\s|\.)?(\d{3})$/,
    message: 'Số điện thoại không hợp lệ'
  },
  
  // Passport validation
  passport: {
    pattern: /^[A-Z0-9]{6,9}$/,
    message: 'Số hộ chiếu không hợp lệ (6-9 ký tự, chỉ chữ hoa và số)'
  },
  
  // Flight number validation
  flightNumber: {
    pattern: /^[A-Z]{2,3}\d{3,4}$/,
    message: 'Số hiệu chuyến bay không hợp lệ (VD: VN123, VNA1234)'
  },
  
  // Price validation
  price: {
    min: 0,
    message: 'Giá phải lớn hơn 0'
  },
  
  // Date validation
  date: {
    future: true,
    message: 'Ngày phải trong tương lai'
  },
  
  // Required field
  required: {
    message: 'Trường này là bắt buộc'
  }
}

// Validation functions
export const validateField = (value, rules, formData = {}) => {
  const errors = []
  
  // Required validation
  if (rules.required && (!value || value === '' || value === null || value === undefined)) {
    errors.push('Trường này là bắt buộc')
    return errors
  }
  
  // Skip other validations if value is empty and not required
  if (!value || value === '' || value === null || value === undefined) {
    return errors
  }
  
  // Email validation
  if (rules.email && !isValidEmail(value)) {
    errors.push('Email không hợp lệ')
  }
  
  // Phone validation
  if (rules.phone && !isValidPhone(value)) {
    errors.push('Số điện thoại không hợp lệ')
  }
  
  // Passport validation
  if (rules.passport && !isValidPassport(value)) {
    errors.push('Số hộ chiếu không hợp lệ')
  }
  
  // Date validation
  if (rules.date) {
    if (!isValidDate(value)) {
      errors.push('Ngày không hợp lệ')
    } else if (rules.date.future && !isFutureDate(value)) {
      errors.push('Ngày phải là ngày trong tương lai')
    }
  }
  
  // Price validation
  if (rules.price && !isValidPrice(value)) {
    errors.push('Giá không hợp lệ')
  }
  
  // Min length validation
  if (rules.minLength && value.length < rules.minLength) {
    errors.push(`Tối thiểu ${rules.minLength} ký tự`)
  }
  
  // Max length validation
  if (rules.maxLength && value.length > rules.maxLength) {
    errors.push(`Tối đa ${rules.maxLength} ký tự`)
  }
  
  // Custom validation
  if (rules.custom) {
    const customError = rules.custom(value, formData)
    if (customError) {
      errors.push(customError)
    }
  }
  
  return errors
}

// Validate form object
export const validateForm = (formData, validationSchema) => {
  const errors = {}
  let isValid = true
  
  Object.keys(validationSchema).forEach(field => {
    const fieldErrors = validateField(formData[field], validationSchema[field], formData)
    if (fieldErrors.length > 0) {
      errors[field] = fieldErrors
      isValid = false
    }
  })
  
  return { isValid, errors }
}

// Specific validation schemas
export const flightSearchSchema = {
  departureAirportId: { required: true },
  arrivalAirportId: { 
    required: true,
    custom: (value, formData) => {
      if (value === formData.departureAirportId) {
        return 'Sân bay đến phải khác sân bay đi'
      }
      return null
    }
  },
  departureDate: { required: true, date: { future: true } }
}

export const customerInfoSchema = {
  fullName: { required: true, minLength: 2, maxLength: 100 },
  email: { required: true, email: true },
  phone: { required: true, phone: true },
  passport: { required: true, passport: true },
  dob: { required: true }
}

export const flightFormSchema = {
  name: { required: true, minLength: 3, maxLength: 200 },
  airline: { required: true },
  departureAirport: { required: true },
  arrivalAirport: { 
    required: true,
    custom: (value, formData) => {

      console.log(formData);
      
      if (value && formData.departureAirport && value.id === formData.departureAirport.id) {
        return 'Sân bay đến phải khác sân bay đi'
      }
      return null
    }
  },
  departureTime: { required: true, date: { future: true } },
  arrivalTime: { 
    required: true, 
    date: { future: true },
    custom: (value, formData) => {
      if (value && formData.departureTime && new Date(value) <= new Date(formData.departureTime)) {
        return 'Thời gian đến phải sau thời gian đi'
      }
      return null
    }
  }
}

export const seatFormSchema = {
  seatNumber: { required: true, minLength: 1, maxLength: 10 },
  price: { required: true, price: true },
  carryOnLuggage: { required: true, price: true }
}

export const bankTransferSchema = {
  accountNumber: { required: true, minLength: 8, maxLength: 20 },
  amount: { required: true, price: true }
} 