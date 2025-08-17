import { ref, computed } from 'vue'
import { useAuthToken } from './useAuthToken'

/**
 * Enhanced useAuth using real JWT authentication
 * Replaces hardcoded values with real token-based auth
 */
export const useAuth = () => {
  const authToken = useAuthToken()
  
  // Real JWT-based state
  const isAuthenticated = computed(() => authToken.isAuthenticated.value)
  const user = computed(() => {
    if (!authToken.isAuthenticated.value || !authToken.userId.value) {
      return null
    }
    
    return {
      id: authToken.userId.value,
      name: 'User', // TODO: Get from JWT claims or API
      email: 'user@example.com' // TODO: Get from JWT claims or API
    }
  })
  const userRole = ref('BUS_SUPPLIER') // TODO: Get from JWT claims

  // Computed properties
  const isAdmin = computed(() => false) // TODO: Based on role from JWT
  const isBusAdmin = computed(() => isAuthenticated.value) // Any authenticated user can manage buses

  // Methods
  const checkAuth = async () => {
    return authToken.checkAuth()
  }

  const logout = () => {
    // Clear tokens and reset state
    const { clearToken } = require('@/services/TokenService')
    clearToken()
    userRole.value = null
  }

  return {
    // State (compatible with old useAuth)
    isAuthenticated,
    user,
    userRole,
    
    // Computed
    isAdmin,
    isBusAdmin,
    
    // Methods
    checkAuth,
    logout,
    
    // Enhanced methods from useAuthToken
    requireUserId: authToken.requireUserId,
    debugAuth: authToken.debugAuth
  }
} 