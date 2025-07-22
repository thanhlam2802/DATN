/**
 * Auth Guard Composable
 * Provides authentication and authorization logic for Vue components
 */

import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth, CurrentUser } from '@/utils/auth'

/**
 * Auth Guard Hook
 * @param {Object} options - Configuration options
 * @param {boolean} options.requireAuth - Component requires authentication
 * @param {string|string[]} options.requiredRoles - Required user roles
 * @param {boolean} options.redirectToLogin - Auto redirect to login if not authenticated
 * @param {boolean} options.showError - Show error message instead of redirect
 */
export const useAuthGuard = (options = {}) => {
  const {
    requireAuth = false,
    requiredRoles = [],
    redirectToLogin = true,
    showError = false
  } = options

  const router = useRouter()
  const auth = useAuth()
  const authError = ref(null)
  const isLoading = ref(true)

  // Computed properties
  const isAuthenticated = computed(() => auth.isLoggedIn.value)
  const currentUser = computed(() => auth.currentUser.value)
  const userId = computed(() => auth.userId.value)
  const userRole = computed(() => auth.userRole.value)
  const isAuthorized = computed(() => {
    if (!requireAuth) return true
    
    if (!isAuthenticated.value) {
      return false
    }

    if (requiredRoles.length === 0) {
      return true // Chỉ cần authenticated
    }

    const roles = Array.isArray(requiredRoles) ? requiredRoles : [requiredRoles]
    return roles.some(role => auth.hasRole(role))
  })

  // Auth checking logic
  const checkAuth = async () => {
    try {
      isLoading.value = true
      authError.value = null

      // Refresh auth state
      auth.refreshAuthState()

      if (requireAuth && !isAuthenticated.value) {
        authError.value = 'Bạn cần đăng nhập để truy cập trang này'
        
        if (redirectToLogin) {
          console.warn('🔐 [AuthGuard] Not authenticated, redirecting to login')
          router.push('/login')
          return false
        }
      }

      if (requireAuth && !isAuthorized.value) {
        authError.value = 'Bạn không có quyền truy cập trang này'
        
        if (showError) {
          console.warn('🚫 [AuthGuard] Insufficient permissions')
          return false
        }
      }

      return true
    } catch (error) {
      console.error('❌ [AuthGuard] Error checking auth:', error)
      authError.value = 'Lỗi xác thực. Vui lòng thử lại.'
      return false
    } finally {
      isLoading.value = false
    }
  }

  // Lifecycle
  onMounted(() => {
    checkAuth()
  })

  return {
    // Auth state
    isAuthenticated,
    isAuthorized,
    currentUser,
    userId,
    userRole,
    
    // Loading and error states
    isLoading,
    authError,
    
    // Methods
    checkAuth,
    logout: auth.logout,
    
    // Helper methods
    hasRole: auth.hasRole,
    isAdmin: computed(() => auth.isAdmin.value),
    isOwner: (resourceOwnerId) => {
      return userId.value === resourceOwnerId || resourceOwnerId === userId.value
    }
  }
}

/**
 * Owner-specific auth guard
 * Kiểm tra user có phải owner của resource không
 */
export const useOwnerGuard = () => {
  const authGuard = useAuthGuard({ requireAuth: true })
  
  const canManageResource = (resourceOwnerId) => {
    if (!authGuard.isAuthenticated.value) return false
    
    // Admin có thể manage tất cả
    if (authGuard.isAdmin.value) return true
    
    // Owner chỉ có thể manage resource của mình
    return authGuard.isOwner(resourceOwnerId)
  }

  const filterOwnedResources = (resources, ownerField = 'ownerId') => {
    if (!authGuard.isAuthenticated.value) return []
    
    // Admin có thể xem tất cả
    if (authGuard.isAdmin.value) return resources
    
    // User chỉ xem resource của mình
    return resources.filter(resource => {
      const resourceOwnerId = resource[ownerField] || resource.owner?.id
      return authGuard.isOwner(resourceOwnerId)
    })
  }

  return {
    ...authGuard,
    canManageResource,
    filterOwnedResources
  }
}

/**
 * Public component auth (không cần login nhưng có thể enhanced nếu logged in)
 */
export const usePublicAuth = () => {
  const auth = useAuth()
  
  return {
    isLoggedIn: computed(() => auth.isLoggedIn.value),
    currentUser: computed(() => auth.currentUser.value),
    userId: computed(() => auth.userId.value),
    
    // Enhanced features for logged users
    canBookTicket: computed(() => auth.isLoggedIn.value),
    canSaveSearch: computed(() => auth.isLoggedIn.value),
    canViewHistory: computed(() => auth.isLoggedIn.value),
    
    // Methods
    promptLogin: () => {
      // Show login modal or redirect
      console.log('💡 Please login to access this feature')
    }
  }
}

/**
 * Admin-only guard
 */
export const useAdminGuard = () => {
  return useAuthGuard({
    requireAuth: true,
    requiredRoles: ['ADMIN', 'admin'],
    redirectToLogin: true,
    showError: true
  })
}

/**
 * Bus Management Guard (cho bus owners và admins)
 */
export const useBusManagementGuard = () => {
  return useAuthGuard({
    requireAuth: true,
    requiredRoles: ['BUS_OWNER', 'ADMIN', 'admin'],
    redirectToLogin: true
  })
} 