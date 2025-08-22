import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs'
import { WS_CONFIG, WS_STATES, WS_ERRORS, wsHelpers } from '@/config/webSocketConfig'

/**
 * 🔌 WebSocket Utility Functions
 * Common WebSocket connection helpers for all components
 */

/**
 * Tạo STOMP client với cấu hình chuẩn
 * @param {string} wsUrl - WebSocket URL (default: từ config)
 * @returns {Object} STOMP client đã được cấu hình
 */
export function createStompClient(wsUrl = null) {
  try {
    // ✅ Sử dụng URL từ config
    const url = wsUrl || WS_CONFIG.WS_URL
    wsHelpers.log(`Creating STOMP client for: ${url}`)
    
    const socket = new SockJS(url)
    
    // ✅ Fix STOMP factory warning - provide factory function
    const stompClient = Stomp.over(() => socket)
    
    // ✅ Fix STOMP debug configuration
    stompClient.debug = function (str) {
      if (WS_CONFIG.DEBUG.VERBOSE_LOGGING) {
        wsHelpers.log('[STOMP Debug]', str)
      }
    }
    
    return stompClient
  } catch (error) {
    wsHelpers.logError('Failed to create STOMP client', error)
    throw error
  }
}

/**
 * Kết nối WebSocket với retry logic
 * @param {Object} config - Configuration object
 * @param {Object} config.stompClient - STOMP client instance
 * @param {Function} config.onConnect - Callback khi connect thành công
 * @param {Function} config.onError - Callback khi có lỗi
 * @param {number} config.retryDelay - Delay retry (ms, default: 5000)
 * @param {Object} config.headers - Headers cho connection
 */
export function connectWebSocket({
  stompClient,
  onConnect,
  onError,
  retryDelay = WS_CONFIG.CONNECTION.RETRY_DELAY,
  headers = {}
}) {
  if (!stompClient) {
    throw new Error('STOMP client is required')
  }

  stompClient.connect(headers, 
    // Success callback
    (frame) => {
      wsHelpers.log('Connected successfully', frame)
      if (onConnect) onConnect(frame)
    },
    // Error callback
    (error) => {
      wsHelpers.logError('Connection failed', error)
      if (onError) onError(error)
      
      // Auto-retry sau một khoảng thời gian
      setTimeout(() => {
        wsHelpers.log('Attempting to reconnect...')
        connectWebSocket({
          stompClient,
          onConnect,
          onError,
          retryDelay,
          headers
        })
      }, retryDelay)
    }
  )
}

/**
 * Ngắt kết nối WebSocket an toàn
 * @param {Object} stompClient - STOMP client instance
 * @param {string} componentName - Tên component để log
 */
export function disconnectWebSocket(stompClient, componentName = 'Unknown') {
  if (stompClient && typeof stompClient.disconnect === 'function') {
    try {
      stompClient.disconnect()
      console.log(`🚌 [WebSocket] Disconnected from ${componentName}`)
    } catch (error) {
      console.error(`❌ [WebSocket] Error disconnecting from ${componentName}:`, error)
    }
  }
}

/**
 * Subscribe to topic với error handling
 * @param {Object} stompClient - STOMP client instance
 * @param {string} topic - Topic để subscribe
 * @param {Function} handler - Message handler
 * @param {Object} headers - Headers cho subscription
 * @returns {Object} Subscription object
 */
export function subscribeToTopic(stompClient, topic, handler, headers = {}) {
  if (!stompClient || !stompClient.connected) {
    console.warn('⚠️ [WebSocket] Cannot subscribe - client not connected')
    return null
  }

  try {
    const subscription = stompClient.subscribe(topic, (response) => {
      try {
        const data = JSON.parse(response.body)
        console.log(`📨 [WebSocket] Received from ${topic}:`, data)
        handler(data)
      } catch (error) {
        console.error(`❌ [WebSocket] Failed to parse message from ${topic}:`, error)
      }
    }, headers)

    console.log(`✅ [WebSocket] Subscribed to ${topic}`)
    return subscription
  } catch (error) {
    console.error(`❌ [WebSocket] Failed to subscribe to ${topic}:`, error)
    return null
  }
}

/**
 * Gửi message đến server
 * @param {Object} stompClient - STOMP client instance
 * @param {string} destination - Destination endpoint
 * @param {Object} data - Data để gửi
 * @param {Object} headers - Headers cho message
 */
export function sendMessage(stompClient, destination, data, headers = {}) {
  if (!stompClient || !stompClient.connected) {
    console.warn('⚠️ [WebSocket] Cannot send message - client not connected')
    return false
  }

  try {
    stompClient.send(destination, headers, JSON.stringify(data))
    console.log(`📤 [WebSocket] Sent to ${destination}:`, data)
    return true
  } catch (error) {
    console.error(`❌ [WebSocket] Failed to send message to ${destination}:`, error)
    return false
  }
}

/**
 * WebSocket connection state checker
 * @param {Object} stompClient - STOMP client instance
 * @returns {boolean} True nếu connected
 */
export function isWebSocketConnected(stompClient) {
  return stompClient && stompClient.connected === true
}

/**
 * Tạo WebSocket connection hoàn chỉnh với tất cả config
 * @param {Object} config - Full configuration
 * @returns {Object} WebSocket connection với methods
 */
export function createWebSocketConnection(config = {}) {
  const {
    wsUrl = 'http://localhost:8080/ws',
    componentName = 'Unknown',
    onConnect,
    onError,
    retryDelay = 5000,
    headers = {}
  } = config

  const stompClient = createStompClient(wsUrl)
  let isConnected = false

  const connection = {
    client: stompClient,
    isConnected: () => isConnected,
    
    connect() {
      connectWebSocket({
        stompClient,
        onConnect: (frame) => {
          isConnected = true
          if (onConnect) onConnect(frame)
        },
        onError: (error) => {
          isConnected = false
          if (onError) onError(error)
        },
        retryDelay,
        headers
      })
    },
    
    disconnect() {
      disconnectWebSocket(stompClient, componentName)
      isConnected = false
    },
    
    subscribe(topic, handler, subscriptionHeaders = {}) {
      return subscribeToTopic(stompClient, topic, handler, subscriptionHeaders)
    },
    
    send(destination, data, messageHeaders = {}) {
      return sendMessage(stompClient, destination, data, messageHeaders)
    }
  }

  return connection
}

export default {
  createStompClient,
  connectWebSocket,
  disconnectWebSocket,
  subscribeToTopic,
  sendMessage,
  isWebSocketConnected,
  createWebSocketConnection
}
