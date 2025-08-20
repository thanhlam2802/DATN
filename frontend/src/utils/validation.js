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
export const validateField = (value, rules) => {
  const errors = []
  
  // Required validation
  if (rules.required && (!value || value.toString().trim() === '')) {
    errors.push(rules.required.message || validationRules.required.message)
    return errors
  }
  
  if (!value) return errors
  
  // Email validation
  if (rules.email && !validationRules.email.pattern.test(value)) {
    errors.push(validationRules.email.message)
  }
  
  // Phone validation
  if (rules.phone && !validationRules.phone.pattern.test(value)) {
    errors.push(validationRules.phone.message)
  }
  
  // Passport validation
  if (rules.passport && !validationRules.passport.pattern.test(value)) {
    errors.push(validationRules.passport.message)
  }
  
  // Flight number validation
  if (rules.flightNumber && !validationRules.flightNumber.pattern.test(value)) {
    errors.push(validationRules.flightNumber.message)
  }
  
  // Price validation
  if (rules.price) {
    const numValue = parseFloat(value)
    if (isNaN(numValue) || numValue < validationRules.price.min) {
      errors.push(validationRules.price.message)
    }
  }
  
  // Date validation
  if (rules.date && rules.date.future) {
    const inputDate = new Date(value)
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    if (inputDate <= today) {
      errors.push(validationRules.date.message)
    }
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
    const customError = rules.custom(value)
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
    const fieldErrors = validateField(formData[field], validationSchema[field])
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