// Customer API for validation and management
import type { CustomerInfo, ValidationResult } from './types'

export class CustomerAPI {
  /**
   * Validate thông tin khách hàng
   */
  static validateCustomerInfo(customerInfo: CustomerInfo): ValidationResult {
    const errors: string[] = []

    // Validate full name
    if (!customerInfo.fullName || customerInfo.fullName.trim().length === 0) {
      errors.push('Họ và tên là bắt buộc')
    } else if (customerInfo.fullName.trim().length < 2) {
      errors.push('Họ và tên phải có ít nhất 2 ký tự')
    } else if (customerInfo.fullName.length > 100) {
      errors.push('Họ và tên không được quá 100 ký tự')
    } else if (!/^[a-zA-ZÀ-ỹ\s]+$/.test(customerInfo.fullName)) {
      errors.push('Họ và tên chỉ được chứa chữ cái và khoảng trắng')
    }

    // Validate phone number
    if (!customerInfo.phoneNumber || customerInfo.phoneNumber.trim().length === 0) {
      errors.push('Số điện thoại là bắt buộc')
    } else if (!/^[0-9]{10,11}$/.test(customerInfo.phoneNumber.replace(/\s/g, ''))) {
      errors.push('Số điện thoại phải có 10-11 chữ số')
    } else if (!this.isValidVietnamesePhoneNumber(customerInfo.phoneNumber)) {
      errors.push('Số điện thoại không đúng định dạng Việt Nam')
    }

    // Validate email (optional)
    if (customerInfo.email && customerInfo.email.trim().length > 0) {
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(customerInfo.email)) {
        errors.push('Email không đúng định dạng')
      } else if (customerInfo.email.length > 255) {
        errors.push('Email không được quá 255 ký tự')
      }
    }

    // Validate notes (optional)
    if (customerInfo.notes && customerInfo.notes.length > 500) {
      errors.push('Ghi chú không được quá 500 ký tự')
    }

    return {
      valid: errors.length === 0,
      errors
    }
  }

  /**
   * Kiểm tra số điện thoại Việt Nam hợp lệ
   */
  static isValidVietnamesePhoneNumber(phone: string): boolean {
    const cleanPhone = phone.replace(/\s/g, '')
    
    // Các đầu số hợp lệ tại Việt Nam
    const validPrefixes = [
      // Viettel
      '032', '033', '034', '035', '036', '037', '038', '039',
      '086', '096', '097', '098',
      // Vinaphone
      '081', '082', '083', '084', '085', '088', '091', '094',
      // Mobiphone
      '070', '079', '077', '076', '078', '090', '093',
      // Vietnamobile
      '052', '056', '058', '092',
      // Gmobile
      '099', '059'
    ]

    // Kiểm tra số điện thoại 10 chữ số
    if (cleanPhone.length === 10) {
      const prefix = cleanPhone.substring(0, 3)
      return validPrefixes.includes(prefix)
    }

    // Kiểm tra số điện thoại 11 chữ số (bắt đầu bằng 0)
    if (cleanPhone.length === 11 && cleanPhone.startsWith('0')) {
      const prefix = cleanPhone.substring(1, 4)
      return validPrefixes.includes(prefix)
    }

    return false
  }

  /**
   * Format số điện thoại để hiển thị
   */
  static formatPhoneNumber(phone: string): string {
    const cleanPhone = phone.replace(/\s/g, '')
    
    if (cleanPhone.length === 10) {
      return `${cleanPhone.substring(0, 3)} ${cleanPhone.substring(3, 6)} ${cleanPhone.substring(6)}`
    }
    
    if (cleanPhone.length === 11) {
      return `${cleanPhone.substring(0, 4)} ${cleanPhone.substring(4, 7)} ${cleanPhone.substring(7)}`
    }
    
    return phone
  }

  /**
   * Chuẩn hóa tên khách hàng
   */
  static normalizeName(name: string): string {
    return name
      .trim()
      .replace(/\s+/g, ' ') // Replace multiple spaces with single space
      .split(' ')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
      .join(' ')
  }

  /**
   * Tạo customer object từ form data
   */
  static createCustomerFromForm(formData: any): CustomerInfo {
    return {
      fullName: this.normalizeName(formData.fullName || ''),
      phoneNumber: (formData.phoneNumber || '').replace(/\s/g, ''),
      email: (formData.email || '').trim().toLowerCase(),
      notes: (formData.notes || '').trim()
    }
  }

  /**
   * Kiểm tra thông tin đầy đủ để tiếp tục
   */
  static isCompleteCustomerInfo(customerInfo: CustomerInfo): boolean {
    const validation = this.validateCustomerInfo(customerInfo)
    return validation.valid && 
           customerInfo.fullName.trim().length > 0 && 
           customerInfo.phoneNumber.trim().length > 0
  }

  /**
   * Tạo summary text cho customer
   */
  static getCustomerSummary(customerInfo: CustomerInfo): string {
    if (!this.isCompleteCustomerInfo(customerInfo)) {
      return 'Thông tin chưa đầy đủ'
    }

    const parts = [
      customerInfo.fullName,
      this.formatPhoneNumber(customerInfo.phoneNumber)
    ]

    if (customerInfo.email) {
      parts.push(customerInfo.email)
    }

    return parts.join(' • ')
  }

  /**
   * Kiểm tra email có phải business email không
   */
  static isBusinessEmail(email: string): boolean {
    if (!email) return false
    
    const businessDomains = [
      'gmail.com', 'yahoo.com', 'hotmail.com', 'outlook.com',
      'icloud.com', 'me.com', 'live.com', 'msn.com'
    ]
    
    const domain = email.split('@')[1]?.toLowerCase()
    return domain && !businessDomains.includes(domain)
  }

  /**
   * Suggest corrections cho common errors
   */
  static suggestCorrections(customerInfo: CustomerInfo): string[] {
    const suggestions: string[] = []

    // Check name
    if (customerInfo.fullName && customerInfo.fullName.length < 5) {
      suggestions.push('Tên có vẻ quá ngắn, vui lòng kiểm tra lại họ và tên đầy đủ')
    }

    // Check phone
    if (customerInfo.phoneNumber) {
      const cleanPhone = customerInfo.phoneNumber.replace(/\s/g, '')
      if (cleanPhone.length === 9) {
        suggestions.push('Số điện thoại thiếu 1 chữ số, vui lòng kiểm tra lại')
      } else if (cleanPhone.length === 12) {
        suggestions.push('Số điện thoại có vẻ dài quá, vui lòng bỏ mã quốc gia +84')
      }
    }

    // Check email
    if (customerInfo.email && customerInfo.email.includes('..')) {
      suggestions.push('Email có dấu chấm liên tiếp, vui lòng kiểm tra lại')
    }

    return suggestions
  }
} 