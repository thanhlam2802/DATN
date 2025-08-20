/**
 * Notification Service
 * Centralized notification system for toasts and confirm dialogs
 */

import { ref, nextTick } from 'vue'

// Global state
const toastContainer = ref(null)
const confirmDialog = ref(null)

// Toast types
export const TOAST_TYPES = {
  SUCCESS: 'success',
  ERROR: 'error',
  WARNING: 'warning', 
  INFO: 'info'
}

// Confirm types
export const CONFIRM_TYPES = {
  DANGER: 'danger',
  WARNING: 'warning',
  INFO: 'info',
  SUCCESS: 'success'
}

// Setup refs (called from main app)
export function setupNotifications(toastRef, confirmRef) {
  toastContainer.value = toastRef
  confirmDialog.value = confirmRef
}

/**
 * Toast Notifications
 */
export const toast = {
  success(message, title = null, options = {}) {
    return addToast({
      type: TOAST_TYPES.SUCCESS,
      title,
      message,
      ...options
    })
  },

  error(message, title = null, options = {}) {
    return addToast({
      type: TOAST_TYPES.ERROR,
      title: title || 'Lỗi',
      message,
      duration: 6000,
      ...options
    })
  },

  warning(message, title = null, options = {}) {
    return addToast({
      type: TOAST_TYPES.WARNING,
      title: title || 'Cảnh báo',
      message,
      ...options
    })
  },

  info(message, title = null, options = {}) {
    return addToast({
      type: TOAST_TYPES.INFO,
      title,
      message,
      ...options
    })
  },

  // Specialized toasts
  deleted(itemName = 'mục') {
    return this.success(`Đã xóa ${itemName} thành công`)
  },

  created(itemName = 'mục') {
    return this.success(`Đã tạo ${itemName} thành công`)
  },

  updated(itemName = 'mục') {
    return this.success(`Đã cập nhật ${itemName} thành công`)
  },

  foreignKeyError(itemName = 'mục', relatedItems = 'dữ liệu liên quan') {
    return this.error(
      `Không thể xóa ${itemName} vì vẫn còn ${relatedItems} đang sử dụng`,
      'Ràng buộc dữ liệu',
      { duration: 8000 }
    )
  },

  networkError(action = 'thực hiện thao tác') {
    return this.error(
      `Không thể ${action}. Vui lòng kiểm tra kết nối mạng và thử lại`,
      'Lỗi kết nối'
    )
  }
}

/**
 * Confirm Dialogs
 */
export const confirm = {
  delete(itemName = 'mục này', options = {}) {
    return showConfirm({
      type: CONFIRM_TYPES.DANGER,
      title: 'Xác nhận xóa',
      message: `Bạn có chắc chắn muốn xóa ${itemName}?`,
      details: 'Hành động này không thể hoàn tác.',
      confirmText: 'Xóa',
      ...options
    })
  },

  deleteWithConfirmText(itemName = 'mục này', confirmText = 'XÓA', options = {}) {
    return showConfirm({
      type: CONFIRM_TYPES.DANGER,
      title: 'Xác nhận xóa',
      message: `Bạn có chắc chắn muốn xóa ${itemName}?`,
      details: 'Hành động này không thể hoàn tác và có thể ảnh hưởng đến dữ liệu liên quan.',
      requireConfirmText: true,
      confirmText,
      ...options
    })
  },

  save(message = 'Bạn có muốn lưu thay đổi?', options = {}) {
    return showConfirm({
      type: CONFIRM_TYPES.INFO,
      title: 'Lưu thay đổi',
      message,
      confirmText: 'Lưu',
      ...options
    })
  },

  discard(message = 'Bạn có muốn hủy bỏ thay đổi?', options = {}) {
    return showConfirm({
      type: CONFIRM_TYPES.WARNING,
      title: 'Hủy thay đổi',
      message,
      details: 'Những thay đổi chưa lưu sẽ bị mất.',
      confirmText: 'Hủy bỏ',
      ...options
    })
  },

  action(title, message, confirmText = 'Xác nhận', options = {}) {
    return showConfirm({
      type: CONFIRM_TYPES.WARNING,
      title,
      message,
      confirmText,
      ...options
    })
  }
}

/**
 * Helper functions
 */
function addToast(options) {
  if (!toastContainer.value) {
    console.warn('Toast container not initialized. Call setupNotifications() first.')
    return
  }
  
  return toastContainer.value.addToast(options)
}

function showConfirm(options) {
  if (!confirmDialog.value) {
    console.warn('Confirm dialog not initialized. Call setupNotifications() first.')
    return Promise.resolve(false)
  }

  return confirmDialog.value.showDialog(options)
}

/**
 * Specialized error handlers
 */
export const handleError = {
  api(error, action = 'thực hiện thao tác') {
    console.error('API Error:', error)
    
    // Check for specific error types
    if (error.message?.includes('foreign key') || error.message?.includes('constraint')) {
      toast.foreignKeyError()
    } else if (error.message?.includes('network') || error.code === 'NETWORK_ERROR') {
      toast.networkError(action)
    } else {
      toast.error(
        error.message || `Có lỗi xảy ra khi ${action}`,
        'Lỗi hệ thống'
      )
    }
  },

  validation(errors, title = 'Dữ liệu không hợp lệ') {
    const errorMessage = Array.isArray(errors) 
      ? errors.join(', ')
      : typeof errors === 'string' 
        ? errors 
        : 'Vui lòng kiểm tra lại thông tin đã nhập'
    
    toast.warning(errorMessage, title)
  },

  permission(action = 'thực hiện thao tác này') {
    toast.error(
      `Bạn không có quyền ${action}. Vui lòng liên hệ quản trị viên.`,
      'Không có quyền truy cập'
    )
  }
}

/**
 * Quick utilities
 */
export const notify = {
  // Quick shortcuts
  success: toast.success,
  error: toast.error,
  warning: toast.warning,
  info: toast.info,
  
  // Operations
  deleted: toast.deleted,
  created: toast.created,
  updated: toast.updated,
  
  // Confirm
  confirmDelete: confirm.delete,
  confirmSave: confirm.save,
  confirmDiscard: confirm.discard
}

// Default export for convenience
export default {
  toast,
  confirm,
  handleError,
  notify,
  setupNotifications,
  TOAST_TYPES,
  CONFIRM_TYPES
} 