import { ref, onMounted, onUnmounted } from 'vue'
import { useAuth } from './useAuth'

/**
 * 🚌 Bus WebSocket Composable (Native WebSocket)
 * Alternative implementation without SockJS if there are compatibility issues
 */
export function useBusWebSocketNative() {
  const { requireUserId } = useAuth()
  
  // State
  const socket = ref(null)
  const isConnected = ref(false)
  const lastUpdate = ref(null)
  const connectionError = ref(null)
  
  // Event handlers
  const statusUpdateHandlers = ref(new Set())
  const notificationHandlers = ref(new Set())
  
  /**
   * Kết nối Native WebSocket
   */
  const connect = () => {
    if (socket.value && isConnected.value) {
      console.log('🚌 [WebSocket] Already connected')
      return
    }
    
    try {
      const ownerId = requireUserId()
      console.log('🚌 [WebSocket] Connecting for owner:', ownerId)
      
      // Native WebSocket connection
      socket.value = new WebSocket('ws://localhost:8080/ws-direct')
      
      socket.value.onopen = (event) => {
        console.log('✅ [WebSocket] Connected to Bus system:', event)
        isConnected.value = true
        connectionError.value = null
        
        // Send subscription messages
        sendMessage({
          type: 'SUBSCRIBE',
          topic: `/topic/bus/owner/${ownerId}/status-updates`
        })
        
        sendMessage({
          type: 'SUBSCRIBE', 
          topic: `/topic/bus/owner/${ownerId}/notifications`
        })
        
        // Send connection confirmation
        sendMessage({
          type: 'CONNECT',
          data: {
            ownerId: parseInt(ownerId),
            timestamp: Date.now()
          }
        })
      }
      
      socket.value.onmessage = (event) => {
        try {
          const message = JSON.parse(event.data)
          console.log('📨 [WebSocket] Received:', message)
          
          if (message.topic && message.topic.includes('status-updates')) {
            handleStatusUpdate(message.data)
          } else if (message.topic && message.topic.includes('notifications')) {
            handleNotification(message.data)
          }
        } catch (error) {
          console.error('❌ [WebSocket] Failed to parse message:', error)
        }
      }
      
      socket.value.onclose = (event) => {
        console.log('🚌 [WebSocket] Connection closed:', event)
        isConnected.value = false
        
        // Auto-reconnect after 5 seconds
        setTimeout(connect, 5000)
      }
      
      socket.value.onerror = (error) => {
        console.error('❌ [WebSocket] Connection error:', error)
        isConnected.value = false
        connectionError.value = 'Connection failed'
      }
      
    } catch (error) {
      console.error('❌ [WebSocket] Setup error:', error)
      connectionError.value = error.message
    }
  }
  
  /**
   * Gửi message qua WebSocket
   */
  const sendMessage = (message) => {
    if (socket.value && socket.value.readyState === WebSocket.OPEN) {
      socket.value.send(JSON.stringify(message))
    }
  }
  
  /**
   * Ngắt kết nối WebSocket
   */
  const disconnect = () => {
    if (socket.value) {
      socket.value.close()
      console.log('🚌 [WebSocket] Disconnected from Bus system')
      isConnected.value = false
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
    onStatusUpdate,
    onNotification,
    
    // Computed
    isReady: () => isConnected.value && !connectionError.value
  }
}
