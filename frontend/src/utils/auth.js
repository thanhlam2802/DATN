/**
 * Auth Utility Module
 * Quản lý token, user info và authentication state
 */

// Constants
const TOKEN_KEY = 'auth_token'
const USER_KEY = 'user_info'
const REFRESH_TOKEN_KEY = 'refresh_token'

// Temporary user ID for development (will be replaced with actual token parsing)
const TEMP_USER_ID = '11'

/**
 * Auth Storage Management
 */
export const AuthStorage = {
  // Token management
  setToken(token) {
    localStorage.setItem(TOKEN_KEY, token)
  },

  getToken() {
    return localStorage.getItem(TOKEN_KEY)
  },

  removeToken() {
    localStorage.removeItem(TOKEN_KEY)
  },

  // Refresh token management
  setRefreshToken(refreshToken) {
    localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken)
  },

  getRefreshToken() {
    return localStorage.getItem(REFRESH_TOKEN_KEY)
  },

  removeRefreshToken() {
    localStorage.removeItem(REFRESH_TOKEN_KEY)
  },

  // User info management
  setUser(userInfo) {
    localStorage.setItem(USER_KEY, JSON.stringify(userInfo))
  },

  getUser() {
    const userStr = localStorage.getItem(USER_KEY)
    return userStr ? JSON.parse(userStr) : null
  },

  removeUser() {
    localStorage.removeItem(USER_KEY)
  },

  // Clear all auth data
  clearAll() {
    this.removeToken()
    this.removeRefreshToken()
    this.removeUser()
  }
}

/**
 * JWT Token Parser (simplified for now)
 */
export const TokenParser = {
  /**
   * Parse JWT token to extract user info
   * @param {string} token - JWT token
   * @returns {object|null} - Parsed token data
   */
  parseToken(token) {
    if (!token) return null
    
    try {
      // Simple JWT parsing (in production, you might want to use a JWT library)
      const base64Url = token.split('.')[1]
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
      const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
      }).join(''))
      
      return JSON.parse(jsonPayload)
    } catch (error) {
      console.error('Error parsing token:', error)
      return null
    }
  },

  /**
   * Check if token is expired
   * @param {string} token - JWT token
   * @returns {boolean} - Is token expired
   */
  isTokenExpired(token) {
    const parsed = this.parseToken(token)
    if (!parsed || !parsed.exp) return true
    
    const currentTime = Date.now() / 1000
    return parsed.exp < currentTime
  },

  /**
   * Extract user ID from token
   * @param {string} token - JWT token
   * @returns {string|null} - User ID
   */
  getUserIdFromToken(token) {
    const parsed = this.parseToken(token)
    return parsed?.userId || parsed?.sub || parsed?.id || null
  }
}

/**
 * Current User Management
 */
export const CurrentUser = {
  /**
   * Get current user ID
   * Priority: token -> stored user -> temporary ID
   * @returns {string} - User ID
   */
  getId() {
    // Try to get from token first
    const token = AuthStorage.getToken()
    if (token && !TokenParser.isTokenExpired(token)) {
      const userId = TokenParser.getUserIdFromToken(token)
      if (userId) return userId
    }

    // Try to get from stored user info
    const user = AuthStorage.getUser()
    if (user?.id) return user.id

    // Fallback to temporary ID for development
    console.warn('Using temporary user ID for development:', TEMP_USER_ID)
    return TEMP_USER_ID
  },

  /**
   * Get current user info
   * @returns {object|null} - User information
   */
  getInfo() {
    return AuthStorage.getUser()
  },

  /**
   * Check if user is authenticated
   * @returns {boolean} - Is user logged in
   */
  isAuthenticated() {
    const token = AuthStorage.getToken()
    if (!token) return false
    
    return !TokenParser.isTokenExpired(token)
  },

  /**
   * Get user role from token or stored info
   * @returns {string|null} - User role
   */
  getRole() {
    const token = AuthStorage.getToken()
    if (token) {
      const parsed = TokenParser.parseToken(token)
      if (parsed?.role) return parsed.role
    }

    const user = AuthStorage.getUser()
    return user?.role || null
  },

  /**
   * Check if user has specific role
   * @param {string} role - Role to check
   * @returns {boolean} - Has role
   */
  hasRole(role) {
    const userRole = this.getRole()
    return userRole === role
  },

  /**
   * Check if user is admin
   * @returns {boolean} - Is admin user
   */
  isAdmin() {
    return this.hasRole('ADMIN') || this.hasRole('admin')
  },

  /**
   * Get authorization header for API calls
   * @returns {object} - Auth headers
   */
  getAuthHeaders() {
    const token = AuthStorage.getToken()
    if (token) {
      return {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    }
    return {
      'Content-Type': 'application/json'
    }
  }
}

/**
 * Auth State Management (reactive)
 */
import { ref, computed } from 'vue'

const isLoggedIn = ref(CurrentUser.isAuthenticated())
const currentUser = ref(CurrentUser.getInfo())

export const useAuth = () => {
  /**
   * Login user
   * @param {object} loginData - Login response data
   */
  const login = (loginData) => {
    const { token, refreshToken, user } = loginData
    
    if (token) AuthStorage.setToken(token)
    if (refreshToken) AuthStorage.setRefreshToken(refreshToken)
    if (user) AuthStorage.setUser(user)
    
    isLoggedIn.value = true
    currentUser.value = user
  }

  /**
   * Logout user
   */
  const logout = () => {
    AuthStorage.clearAll()
    isLoggedIn.value = false
    currentUser.value = null
  }

  /**
   * Refresh auth state
   */
  const refreshAuthState = () => {
    isLoggedIn.value = CurrentUser.isAuthenticated()
    currentUser.value = CurrentUser.getInfo()
  }

  return {
    // State
    isLoggedIn: computed(() => isLoggedIn.value),
    currentUser: computed(() => currentUser.value),
    userId: computed(() => CurrentUser.getId()),
    userRole: computed(() => CurrentUser.getRole()),
    isAdmin: computed(() => CurrentUser.isAdmin()),
    
    // Methods
    login,
    logout,
    refreshAuthState,
    
    // Utilities
    getAuthHeaders: CurrentUser.getAuthHeaders,
    hasRole: CurrentUser.hasRole
  }
}

/**
 * Default export for convenience
 */
export default {
  AuthStorage,
  TokenParser,
  CurrentUser,
  useAuth
} 