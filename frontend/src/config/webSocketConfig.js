/**
 * 🔌 WebSocket Configuration
 * Centralized configuration cho tất cả WebSocket connections
 */

// ✅ Environment-based configuration
const getBaseUrl = () => {
  // Ưu tiên env variables trước
  if (import.meta.env.VITE_WS_BASE_URL) {
    return import.meta.env.VITE_WS_BASE_URL
  }
  
  const isDevelopment = import.meta.env.DEV || import.meta.env.MODE === 'development'
  const isProduction = import.meta.env.PROD || import.meta.env.MODE === 'production'
  
  if (isDevelopment) {
    return 'http://localhost:8080'
  }
  
  if (isProduction) {
    // Production domain - change này theo domain thật
    return 'https://your-production-domain.com'
  }
  
  // Fallback
  return 'http://localhost:8080'
}

const getEndpoint = () => {
  return import.meta.env.VITE_WS_ENDPOINT || '/ws'
}

// ✅ WebSocket Configuration
export const WS_CONFIG = {
  // Base URLs
  BASE_URL: getBaseUrl(),
  WS_ENDPOINT: getEndpoint(),
  
  // Full WebSocket URL
  get WS_URL() {
    return `${this.BASE_URL}${this.WS_ENDPOINT}`
  },
  
  // Connection settings
  CONNECTION: {
    RETRY_DELAY: parseInt(import.meta.env.VITE_WS_RETRY_DELAY) || 5000,
    MAX_RETRIES: parseInt(import.meta.env.VITE_WS_MAX_RETRIES) || 10,
    HEARTBEAT_INTERVAL: parseInt(import.meta.env.VITE_WS_HEARTBEAT_INTERVAL) || 30000,
    CONNECT_TIMEOUT: parseInt(import.meta.env.VITE_WS_CONNECT_TIMEOUT) || 10000,
  },
  
  // Debug settings
  DEBUG: {
    ENABLED: import.meta.env.VITE_WS_DEBUG === 'true' || import.meta.env.DEV || false,
    VERBOSE_LOGGING: import.meta.env.VITE_WS_VERBOSE_LOGGING === 'true' || false,
  }
}

// ✅ Topic Endpoints cho từng service
export const WS_TOPICS = {
  // Bus Management Topics
  BUS: {
    // Owner-specific topics
    OWNER_STATUS_UPDATES: (ownerId) => `/topic/bus/owner/${ownerId}/status-updates`,
    OWNER_NOTIFICATIONS: (ownerId) => `/topic/bus/owner/${ownerId}/notifications`,
    
    // Public topics
    SLOT_STATUS: (busSlotId) => `/topic/bus/slots/${busSlotId}/status`,
    
    // Admin topics
    ADMIN_UPDATES: '/topic/bus/admin/updates',
    SYSTEM_ALERTS: '/topic/bus/system/alerts'
  },
  
  // Hotel Management Topics  
  HOTEL: {
    // Hotel-specific topics
    ROOM_UPDATES: (hotelId) => `/topic/hotels/${hotelId}/room-updates`,
    BOOKING_NOTIFICATIONS: (hotelId) => `/topic/hotels/${hotelId}/booking-notifications`,
    VIEWER_NOTIFICATIONS: (hotelId) => `/topic/hotels/${hotelId}/viewer-notifications`,
    
    // Admin topics
    ADMIN_BOOKINGS: '/topic/admin/hotel/bookings',
    ADMIN_PAYMENTS: '/topic/admin/hotel/payments',
    ADMIN_REVIEWS: '/topic/admin/hotel/reviews',
    ADMIN_CANCELLATIONS: '/topic/admin/hotel/cancellations',
    ADMIN_SYSTEM: '/topic/admin/hotel/system',
    ADMIN_ACTIONS: '/topic/admin/hotel/actions',
    ADMIN_STATUS: '/topic/admin/hotel/status'
  },
  
  // Order Management Topics
  ORDER: {
    // Order-specific topics
    ORDER_UPDATES: (orderId) => `/topic/orders/${orderId}`,
    VOUCHER_UPDATES: '/topic/vouchers/updates',
    
    // Payment topics
    PAYMENT_STATUS: (orderId) => `/topic/payments/${orderId}/status`,
    PAYMENT_NOTIFICATIONS: '/topic/payments/notifications'
  },
  
  // Flight Management Topics
  FLIGHT: {
    BOOKING_UPDATES: (bookingId) => `/topic/flights/${bookingId}/updates`,
    FLIGHT_STATUS: (flightId) => `/topic/flights/${flightId}/status`,
    ADMIN_NOTIFICATIONS: '/topic/admin/flight/notifications'
  }
}

// ✅ Message Destinations cho sending messages
export const WS_DESTINATIONS = {
  // Bus destinations
  BUS: {
    STATUS_UPDATE: '/app/bus/status-update',
    OWNER_CONNECT: '/app/bus/owner-connect',
    ADMIN_ACTION: '/app/bus/admin/action'
  },
  
  // Hotel destinations
  HOTEL: {
    ROOM_BOOKING: '/app/hotel/room-booking',
    ROOM_UPDATE: '/app/hotel/room-update',
    VIEWER_NOTIFICATION: '/app/hotel/viewer-notification',
    ADMIN_STATUS: '/app/admin/hotel/status'
  },
  
  // Order destinations
  ORDER: {
    VOUCHER_ACTION: '/app/order/voucher-action',
    PAYMENT_ACTION: '/app/order/payment-action'
  }
}

// ✅ WebSocket Headers configuration
export const WS_HEADERS = {
  // Default headers
  DEFAULT: {
    'Content-Type': 'application/json'
  },
  
  // Authenticated headers (với token)
  getAuthHeaders: (token) => ({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  }),
  
  // Component-specific headers
  BUS_OWNER: (ownerId) => ({
    'Content-Type': 'application/json',
    'X-Owner-Id': ownerId.toString()
  }),
  
  HOTEL_ADMIN: (hotelId) => ({
    'Content-Type': 'application/json',
    'X-Hotel-Id': hotelId.toString()
  })
}

// ✅ Connection state management
export const WS_STATES = {
  CONNECTING: 'CONNECTING',
  CONNECTED: 'CONNECTED',
  DISCONNECTING: 'DISCONNECTING',
  DISCONNECTED: 'DISCONNECTED',
  ERROR: 'ERROR',
  RECONNECTING: 'RECONNECTING'
}

// ✅ Error codes
export const WS_ERRORS = {
  CONNECTION_FAILED: 'CONNECTION_FAILED',
  CONNECTION_TIMEOUT: 'CONNECTION_TIMEOUT',
  AUTHENTICATION_FAILED: 'AUTHENTICATION_FAILED',
  SUBSCRIPTION_FAILED: 'SUBSCRIPTION_FAILED',
  MESSAGE_SEND_FAILED: 'MESSAGE_SEND_FAILED',
  UNKNOWN_ERROR: 'UNKNOWN_ERROR'
}

// ✅ Helper functions
export const wsHelpers = {
  /**
   * Get environment-specific WebSocket URL
   */
  getWsUrl: () => WS_CONFIG.WS_URL,
  
  /**
   * Check if running in development
   */
  isDevelopment: () => WS_CONFIG.DEBUG.ENABLED,
  
  /**
   * Get connection config for specific component
   */
  getConnectionConfig: (componentName, customConfig = {}) => ({
    wsUrl: WS_CONFIG.WS_URL,
    componentName,
    retryDelay: WS_CONFIG.CONNECTION.RETRY_DELAY,
    maxRetries: WS_CONFIG.CONNECTION.MAX_RETRIES,
    connectTimeout: WS_CONFIG.CONNECTION.CONNECT_TIMEOUT,
    ...customConfig
  }),
  
  /**
   * Log WebSocket events (only in development)
   */
  log: (message, data = null) => {
    if (WS_CONFIG.DEBUG.ENABLED) {
      if (data) {
        console.log(`🔌 [WebSocket] ${message}:`, data)
      } else {
        console.log(`🔌 [WebSocket] ${message}`)
      }
    }
  },
  
  /**
   * Log WebSocket errors (always log errors)
   */
  logError: (message, error = null) => {
    if (error) {
      console.error(`❌ [WebSocket] ${message}:`, error)
    } else {
      console.error(`❌ [WebSocket] ${message}`)
    }
  },
  
  /**
   * Validate topic format
   */
  isValidTopic: (topic) => {
    return typeof topic === 'string' && topic.startsWith('/topic/')
  },
  
  /**
   * Validate destination format
   */
  isValidDestination: (destination) => {
    return typeof destination === 'string' && destination.startsWith('/app/')
  }
}

// ✅ Default export
export default {
  WS_CONFIG,
  WS_TOPICS,
  WS_DESTINATIONS,
  WS_HEADERS,
  WS_STATES,
  WS_ERRORS,
  wsHelpers
}
