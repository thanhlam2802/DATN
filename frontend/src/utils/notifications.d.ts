/**
 * Type definitions for notifications.js
 */

export interface ToastOptions {
  type?: 'success' | 'error' | 'warning' | 'info'
  title?: string
  message: string
  duration?: number
  showProgress?: boolean
}

export interface ConfirmOptions {
  type?: 'danger' | 'warning' | 'info' | 'success'
  title?: string
  subtitle?: string
  message: string
  details?: string
  confirmText?: string
  cancelText?: string
  requireConfirmText?: boolean
  loading?: boolean
  loadingText?: string
  closeOnBackdrop?: boolean
}

export const TOAST_TYPES: {
  SUCCESS: 'success'
  ERROR: 'error'
  WARNING: 'warning'
  INFO: 'info'
}

export const CONFIRM_TYPES: {
  DANGER: 'danger'
  WARNING: 'warning'
  INFO: 'info'
  SUCCESS: 'success'
}

export const toast: {
  success(message: string, title?: string, options?: Partial<ToastOptions>): void
  error(message: string, title?: string, options?: Partial<ToastOptions>): void
  warning(message: string, title?: string, options?: Partial<ToastOptions>): void
  info(message: string, title?: string, options?: Partial<ToastOptions>): void
  deleted(itemName?: string): void
  created(itemName?: string): void
  updated(itemName?: string): void
  foreignKeyError(itemName?: string, relatedItems?: string): void
  networkError(action?: string): void
}

export const confirm: {
  delete(itemName?: string, options?: Partial<ConfirmOptions>): Promise<boolean>
  deleteWithConfirmText(itemName?: string, confirmText?: string, options?: Partial<ConfirmOptions>): Promise<boolean>
  save(message?: string, options?: Partial<ConfirmOptions>): Promise<boolean>
  discard(message?: string, options?: Partial<ConfirmOptions>): Promise<boolean>
  action(title: string, message: string, confirmText?: string, options?: Partial<ConfirmOptions>): Promise<boolean>
}

export const handleError: {
  api(error: any, action?: string): void
  validation(errors: string | string[], title?: string): void
  permission(action?: string): void
}

export const notify: {
  success: typeof toast.success
  error: typeof toast.error
  warning: typeof toast.warning
  info: typeof toast.info
  deleted: typeof toast.deleted
  created: typeof toast.created
  updated: typeof toast.updated
  confirmDelete: typeof confirm.delete
  confirmSave: typeof confirm.save
  confirmDiscard: typeof confirm.discard
}

export function setupNotifications(toastRef: any, confirmRef: any): void

declare const _default: {
  toast: typeof toast
  confirm: typeof confirm
  handleError: typeof handleError
  notify: typeof notify
  setupNotifications: typeof setupNotifications
  TOAST_TYPES: typeof TOAST_TYPES
  CONFIRM_TYPES: typeof CONFIRM_TYPES
}

export default _default 