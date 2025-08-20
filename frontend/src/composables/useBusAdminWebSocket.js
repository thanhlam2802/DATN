/**
 * 🔔 Bus Admin WebSocket Composable
 * Quản lý kết nối WebSocket cho admin nhận thông báo real-time
 */

import { ref, reactive, onUnmounted } from 'vue'
import { createWebSocketConnection } from '@/utils/webSocketUtils'
import { WS_CONFIG, WS_TOPICS, WS_DESTINATIONS } from '@/config/webSocketConfig'

// Global state để share giữa các component
let globalState = null

const createGlobalState = () => {
  const notifications = ref([])
  const isConnected = ref(false)
  const stompClient = ref(null)
  const retryCount = ref(0)
  const maxRetries = 5
  
  return {
    notifications,
    isConnected,
    stompClient,
    retryCount,
    maxRetries
  }
}

export function useBusAdminWebSocket() {
  // Sử dụng singleton pattern cho global state
  if (!globalState) {
    globalState = createGlobalState()
  }
  
  const { notifications, isConnected, stompClient, retryCount, maxRetries } = globalState

  /**
   * 🔗 Kết nối WebSocket cho Admin
   */
  const connect = async () => {
    try {
      // Initiating connection
      
      const connectionResult = await createWebSocketConnection({
        stompClient: stompClient,
        onConnect: handleConnection,
        onError: handleError,
        retryDelay: WS_CONFIG.CONNECTION.RETRY_DELAY,
        headers: {
          'X-Admin-Connection': 'true',
          'X-Service': 'BUS_ADMIN'
        }
      })
      
      if (connectionResult.success) {
        // Connection established
        retryCount.value = 0
      }
      
    } catch (error) {
      handleConnectionError()
    }
  }

  /**
   * 📡 Xử lý khi kết nối thành công
   */
  const handleConnection = (frame) => {
          // Connected to server
    isConnected.value = true
    
    // Subscribe to admin notification topics
    subscribeToAdminTopics()
    
    // Gửi admin connection message
    sendAdminConnectionInfo()
  }

  /**
   * 📋 Subscribe tới các admin topics
   */
  const subscribeToAdminTopics = () => {
    if (!stompClient.value?.connected) {
      // Not connected, cannot subscribe
      return
    }

    try {
      // 🚌 Bus booking notifications
      stompClient.value.subscribe(WS_TOPICS.ADMIN_BOOKINGS, (message) => {
        const notification = JSON.parse(message.body)
        // Bus booking notification
        handleBusBookingNotification(notification)
      })

      // 💰 Payment notifications
      stompClient.value.subscribe(WS_TOPICS.ADMIN_PAYMENTS, (message) => {
        const notification = JSON.parse(message.body)
        // Payment notification
        handlePaymentNotification(notification)
      })

      // ⚠️ System alerts
      stompClient.value.subscribe(WS_TOPICS.ADMIN_SYSTEM, (message) => {
        const notification = JSON.parse(message.body)
        // System notification
        handleSystemNotification(notification)
      })

      // Subscribed to all admin topics
      
    } catch (error) {
    }
  }

  /**
   * 📤 Gửi thông tin admin connection
   */
  const sendAdminConnectionInfo = () => {
    try {
      if (!stompClient.value?.connected) return
      
      const connectionInfo = {
        adminId: 'admin_user', // TODO: Get from auth
        timestamp: Date.now(),
        service: 'BUS_ADMIN',
        version: '1.0.0'
      }
      
      stompClient.value.send(
        WS_DESTINATIONS.ADMIN_CONNECT,
        {},
        JSON.stringify(connectionInfo)
      )
      
      // Connection info sent
      
    } catch (error) {
    }
  }

  /**
   * 🚌 Xử lý bus booking notification
   */
  const handleBusBookingNotification = (data) => {
    const notification = {
      id: `booking_${data.orderId}_${Date.now()}`,
      type: 'BUS_BOOKING',
      title: '🚌 Đặt vé mới',
      message: `${data.customerName} vừa đặt ${data.seatCount} vé`,
      timestamp: data.timestamp || Date.now(),
      read: false,
      details: {
        customerName: data.customerName,
        seatCount: data.seatCount,
        routeName: data.routeName,
        routeId: data.routeId,
        amount: data.amount,
        orderId: data.orderId
      }
    }
    
    addNotification(notification)
    emitNotificationEvent(notification)
  }

  /**
   * 💰 Xử lý payment notification
   */
  const handlePaymentNotification = (data) => {
    const isSuccess = data.status === 'SUCCESS'
    const notification = {
      id: `payment_${data.orderId}_${Date.now()}`,
      type: isSuccess ? 'PAYMENT_SUCCESS' : 'PAYMENT_FAILED',
      title: isSuccess ? '💰 Thanh toán thành công' : '❌ Thanh toán thất bại',
      message: `Đơn hàng #${data.orderId} - ${formatCurrency(data.amount)}`,
      timestamp: data.timestamp || Date.now(),
      read: false,
      details: {
        orderId: data.orderId,
        amount: data.amount,
        status: data.status,
        paymentMethod: data.paymentMethod,
        service: data.service
      }
    }
    
    addNotification(notification)
    emitNotificationEvent(notification)
  }

  /**
   * ⚠️ Xử lý system notification
   */
  const handleSystemNotification = (data) => {
    const notification = {
      id: `system_${Date.now()}`,
      type: 'SYSTEM_ALERT',
      title: '⚠️ Thông báo hệ thống',
      message: data.message || 'Có cập nhật mới từ hệ thống',
      timestamp: data.timestamp || Date.now(),
      read: false,
      details: data.details || {}
    }
    
    addNotification(notification)
    emitNotificationEvent(notification)
  }

  /**
   * ➕ Thêm notification vào danh sách
   */
  const addNotification = (notification) => {
    notifications.value.unshift(notification)
    
    // Giới hạn số lượng notifications (giữ 100 cái gần nhất)
    if (notifications.value.length > 100) {
      notifications.value = notifications.value.slice(0, 100)
    }
    
    // Lưu vào localStorage
    saveNotificationsToStorage()
  }

  /**
   * 📣 Emit notification event cho component khác
   */
  const emitNotificationEvent = (notification) => {
    // Dispatch custom event
    const event = new CustomEvent('busAdminNotification', {
      detail: notification
    })
    window.dispatchEvent(event)
  }

  /**
   * ✅ Đánh dấu notification đã đọc
   */
  const markNotificationAsRead = (notificationId) => {
    const notification = notifications.value.find(n => n.id === notificationId)
    if (notification) {
      notification.read = true
      saveNotificationsToStorage()
    }
  }

  /**
   * ✅ Đánh dấu tất cả đã đọc
   */
  const markAllNotificationsAsRead = () => {
    notifications.value.forEach(notification => {
      notification.read = true
    })
    saveNotificationsToStorage()
  }

  /**
   * 💾 Lưu notifications vào localStorage
   */
  const saveNotificationsToStorage = () => {
    try {
      const data = {
        notifications: notifications.value,
        timestamp: Date.now()
      }
      localStorage.setItem('busAdminNotifications', JSON.stringify(data))
    } catch (error) {
    }
  }

  /**
   * 📥 Load notifications từ localStorage
   */
  const loadNotificationsFromStorage = () => {
    try {
      const data = localStorage.getItem('busAdminNotifications')
      if (data) {
        const parsed = JSON.parse(data)
        
        // Kiểm tra dữ liệu không quá cũ (7 ngày)
        const sevenDaysAgo = Date.now() - (7 * 24 * 60 * 60 * 1000)
        if (parsed.timestamp > sevenDaysAgo) {
          notifications.value = parsed.notifications || []
          // Loaded notifications from storage
        }
      }
    } catch (error) {
    }
  }

  /**
   * ❌ Xử lý lỗi kết nối
   */
  const handleError = (error) => {
    isConnected.value = false
    handleConnectionError()
  }

  /**
   * 🔄 Xử lý lỗi và retry
   */
  const handleConnectionError = () => {
    if (retryCount.value < maxRetries) {
      retryCount.value++
      const delay = Math.min(1000 * Math.pow(2, retryCount.value), 30000) // Exponential backoff, max 30s
      
      // Retrying connection
      
      setTimeout(() => {
        connect()
      }, delay)
    } else {
      console.error('🔔 [Admin WebSocket] Max retries reached, giving up')
    }
  }

  /**
   * 🔌 Ngắt kết nối
   */
  const disconnect = () => {
    try {
      if (stompClient.value?.connected) {
        stompClient.value.disconnect(() => {
          console.log('🔔 [Admin WebSocket] Disconnected')
        })
      }
      isConnected.value = false
    } catch (error) {
      console.error('🔔 [Admin WebSocket] Disconnect error:', error)
    }
  }

  /**
   * 💰 Format currency helper
   */
  const formatCurrency = (amount) => {
    try {
      return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
      }).format(amount)
    } catch {
      return `${amount} VND`
    }
  }

  // 📥 Load notifications on initialization
  if (notifications.value.length === 0) {
    loadNotificationsFromStorage()
  }

  return {
    // State
    notifications,
    isConnected,
    
    // Methods
    connect,
    disconnect,
    markNotificationAsRead,
    markAllNotificationsAsRead,
    
    // Utils
    formatCurrency
  }
}

/**
 * 🧹 Cleanup function for global state
 */
export const cleanupBusAdminWebSocket = () => {
  if (globalState) {
    globalState.stompClient.value?.disconnect()
    globalState = null
  }
}
