import { ref, onMounted, onUnmounted } from 'vue'
import { useAuth } from './useAuth'
import { createWebSocketConnection } from '@/utils/webSocketUtils'
import { WS_TOPICS, WS_DESTINATIONS, wsHelpers } from '@/config/webSocketConfig'

/**
 * 🚌 Bus WebSocket Composable
 * Real-time updates cho Bus Management System
 */
export function useBusWebSocket() {
  const { requireUserId } = useAuth()
  
  // State
  const wsConnection = ref(null)
  const isConnected = ref(false)
  const lastUpdate = ref(null)
  const connectionError = ref(null)
  
  // Event handlers
  const statusUpdateHandlers = ref(new Set())
  const notificationHandlers = ref(new Set())
  
  /**
   * Kết nối WebSocket
   */
  const connect = () => {
    if (wsConnection.value && isConnected.value) {
      console.log('🚌 [WebSocket] Already connected')
      return
    }
    
    try {
      const ownerId = requireUserId()
      console.log('🚌 [WebSocket] Connecting for owner:', ownerId)
      
      // ✅ Sử dụng WebSocket utility với config centralized
      wsConnection.value = createWebSocketConnection({
        componentName: 'BusWebSocket',
        onConnect: (frame) => {
          wsHelpers.log('Connected to Bus system', frame)
          isConnected.value = true
          connectionError.value = null
          
          // ✅ Subscribe using config topics
          wsConnection.value.subscribe(
            WS_TOPICS.BUS.OWNER_STATUS_UPDATES(ownerId), 
            handleStatusUpdate
          )
          
          wsConnection.value.subscribe(
            WS_TOPICS.BUS.OWNER_NOTIFICATIONS(ownerId), 
            handleNotification
          )
          
          // ✅ Send using config destinations
          wsConnection.value.send(WS_DESTINATIONS.BUS.OWNER_CONNECT, {
            ownerId: parseInt(ownerId),
            timestamp: Date.now()
          })
        },
        onError: (error) => {
          wsHelpers.logError('Connection failed', error)
          isConnected.value = false
          connectionError.value = error.message || 'Connection failed'
        }
      })
      
      wsConnection.value.connect()
      
    } catch (error) {
      console.error('❌ [WebSocket] Setup error:', error)
      connectionError.value = error.message
    }
  }
  
  /**
   * Ngắt kết nối WebSocket
   */
  const disconnect = () => {
    if (wsConnection.value) {
      wsConnection.value.disconnect()
      isConnected.value = false
      wsConnection.value = null
    }
  }
  
  /**
   * Xử lý status updates
   */
  const handleStatusUpdate = (update) => {
    console.log('🔄 [WebSocket] Received status update:', update)
    lastUpdate.value = update
    
    // Notify all handlers
    statusUpdateHandlers.value.forEach(handler => {
      try {
        handler(update)
      } catch (error) {
        console.error('❌ [WebSocket] Error in status update handler:', error)
      }
    })
    
    // Show toast notification
    if (window.$toast) {
      const message = formatStatusMessage(update)
      const type = getNotificationType(update.action)
      window.$toast(message, type, 'Cập nhật trạng thái xe')
    }
  }
  
  /**
   * Xử lý notifications
   */
  const handleNotification = (notification) => {
    console.log('🔔 [WebSocket] Received notification:', notification)
    
    // Notify all handlers
    notificationHandlers.value.forEach(handler => {
      try {
        handler(notification)
      } catch (error) {
        console.error('❌ [WebSocket] Error in notification handler:', error)
      }
    })
    
    // Show toast for system notifications
    if (notification.type === 'CONNECTION_CONFIRMED' && window.$toast) {
      window.$toast(notification.message, 'success', 'Kết nối thành công')
    }
  }
  
  /**
   * Đăng ký handler cho status updates
   */
  const onStatusUpdate = (handler) => {
    statusUpdateHandlers.value.add(handler)
    
    // Return unsubscribe function
    return () => {
      statusUpdateHandlers.value.delete(handler)
    }
  }
  
  /**
   * Đăng ký handler cho notifications
   */
  const onNotification = (handler) => {
    notificationHandlers.value.add(handler)
    
    // Return unsubscribe function
    return () => {
      notificationHandlers.value.delete(handler)
    }
  }
  
  /**
   * Gửi manual status update
   */
  const sendStatusUpdate = (busSlotId, status, action = 'MANUAL_UPDATE') => {
    if (!wsConnection.value || !isConnected.value) {
      console.warn('⚠️ [WebSocket] Cannot send - not connected')
      return false
    }
    
    try {
      const ownerId = requireUserId()
      const update = {
        busSlotId,
        ownerId: parseInt(ownerId),
        status,
        action,
        timestamp: Date.now()
      }
      
      const success = wsConnection.value.send(WS_DESTINATIONS.BUS.STATUS_UPDATE, update)
      if (success) {
        console.log('📤 [WebSocket] Sent status update:', update)
      }
      return success
    } catch (error) {
      console.error('❌ [WebSocket] Failed to send status update:', error)
      return false
    }
  }
  
  /**
   * Format status message cho notification
   */
  const formatStatusMessage = (update) => {
    const { busName, routeInfo, action } = update
    
    switch (action) {
      case 'START_TRIP':
        return `🚌 ${busName} đã bắt đầu chuyến đi ${routeInfo}`
      case 'COMPLETE_TRIP':
        return `✅ ${busName} đã hoàn thành chuyến đi ${routeInfo}`
      case 'CHECK_DELAY':
        return `⚠️ ${busName} có thể bị trễ lịch trình ${routeInfo}`
      default:
        return `🔄 ${busName} cập nhật trạng thái: ${update.status}`
    }
  }
  
  /**
   * Get notification type for toast
   */
  const getNotificationType = (action) => {
    switch (action) {
      case 'START_TRIP':
        return 'info'
      case 'COMPLETE_TRIP':
        return 'success'
      case 'CHECK_DELAY':
        return 'warning'
      default:
        return 'info'
    }
  }
  
  // Auto-connect when composable is used
  onMounted(() => {
    connect()
  })
  
  // Auto-disconnect when component unmounts
  onUnmounted(() => {
    disconnect()
  })
  
  return {
    // State
    isConnected,
    lastUpdate,
    connectionError,
    
    // Methods
    connect,
    disconnect,
    sendStatusUpdate,
    onStatusUpdate,
    onNotification,
    
    // Computed
    isReady: () => isConnected.value && !connectionError.value
  }
}
